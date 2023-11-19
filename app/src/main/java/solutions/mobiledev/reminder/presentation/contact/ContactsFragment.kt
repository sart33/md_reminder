package solutions.mobiledev.reminder.presentation.contact

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.data.ContactRepositoryImpl.contactListLD
import solutions.mobiledev.reminder.databinding.FragmentContactsBinding
import solutions.mobiledev.reminder.domain.entity.ContactItem
import solutions.mobiledev.reminder.domain.entity.SaveContactItem
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderAddFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderEditFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListFragment


class ContactsFragment : Fragment() {

    private lateinit var viewModel: ReminderViewModel
    private lateinit var contactListAdapter: ContactListAdapter
    private lateinit var contactItem: ContactItem
    private lateinit var saveContactItem: SaveContactItem
    private lateinit var selectedContacts: MutableList<ContactItem>
    private lateinit var saveContactsList: List<SaveContactItem>
    private lateinit var names: MutableList<ContactItem>
    private lateinit var contacts: MutableList<ContactItem>
    private lateinit var contactList: MutableLiveData<List<ContactItem>>
    private var reminderItemId: Int = UNDEFINED_ID

    private val REQUEST_CODE_READ_CONTACTS = 1
    private var READ_CONTACTS_GRANTED = false

    private lateinit var animatorSetSave: AnimatorSet
    private lateinit var animatorSetCancel: AnimatorSet

    private var _binding: FragmentContactsBinding? = null
    private val binding: FragmentContactsBinding
        get() = _binding ?: throw RuntimeException("FragmentContactsBinding == null")


    override fun onCreate(savedInstanceState: Bundle?) {
        parseParams()
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {

        setupRecyclerView()
        viewModel = ViewModelProvider(this@ContactsFragment)[ReminderViewModel::class.java]
        super.onViewCreated(view, savedInstanceState)
        initTouchAnimation()


        val hasReadContactPermission =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            )
        // если устройство до API 23, устанавливаем разрешение
        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            READ_CONTACTS_GRANTED = true
        } else {
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                REQUEST_CODE_READ_CONTACTS
            )
        }
        // если разрешение установлено, загружаем контакты
        if (READ_CONTACTS_GRANTED) {
            getContactList().observe(viewLifecycleOwner) {
                /**
                 * При вызове submitList - внутри адаптера - запускается новый поток, -
                 * который и выполняет - все вычисления.
                 */

                getSelectList()

                viewModel.contactList.observe(viewLifecycleOwner) {
                    contactListAdapter.submitList(it)
                }
                viewModel.contactItem.observe(viewLifecycleOwner) {
                    contactItem = it
                }
                contactListAdapter.submitList(it)
                laCancel.setOnClickListener {
                    animatorSetCancel.start()
                    goToPrev()
//                    launchReminderAddFragment()
                }
                laSave.setOnClickListener {
                    animatorSetSave.start()
                    viewModel.selectedContacts.observe(viewLifecycleOwner) {
                        it.forEach {
                            if (this@ContactsFragment::saveContactsList.isInitialized) {
//                            if (this@ContactsFragment::saveContactsList.isInitialized) {
                                viewModel.addSaveContactItem(
                                    saveContactItem
                                )

//                            }
                            } else {
                                if (it.avatar != null) {
                                    saveContactItem = SaveContactItem(
                                        it.id, it.name, it.number, it.avatar, remId = reminderItemId
                                    )
                                } else {
                                    saveContactItem = SaveContactItem(
                                        it.id, it.name, it.number, "null", remId = reminderItemId
                                    )
                                }
                                viewModel.addSaveContactItem(
                                    saveContactItem
                                )
                            }
                        }
                        goToPrev()
//                        launchReminderAddFragment()
                    }
                }
            }
        }
    }


    private fun goToPrev() {
        if (reminderItemId == 0) {
            launchReminderAddFragment()
        } else {
            launchReminderEditFragment()
        }
    }


    private fun initTouchAnimation() = with(binding) {
        val bgColorAnimatorSave = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorSave = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        bgColorAnimatorSave.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvSaveButton.setBackgroundColor(color)
        }
        textColorAnimatorSave.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvSaveButton.setTextColor(color)
        }

        animatorSetSave = AnimatorSet()
        animatorSetSave.playTogether(bgColorAnimatorSave, textColorAnimatorSave)
        animatorSetSave.duration = ReminderAddFragment.DEFAULT_DURATION // 500мс

        val bgColorAnimatorCancel = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorCancel = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        bgColorAnimatorCancel.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvCancelButton.setBackgroundColor(color)
        }
        textColorAnimatorCancel.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvCancelButton.setTextColor(color)
        }

        animatorSetCancel = AnimatorSet()
        animatorSetCancel.playTogether(bgColorAnimatorCancel, textColorAnimatorCancel)
        animatorSetCancel.duration = ReminderAddFragment.DEFAULT_DURATION // 500мс
    }


    private fun setupClickListener() {
        contactListAdapter.onContactItemClickListener = {
            viewModel.chooseContact(it)
            it.item = !it.item
            if (this@ContactsFragment::selectedContacts.isInitialized) {
                if (it.item) {
//                    if (!selectedContacts.contains(it)) {
//                        selectedContacts.add(it)
//                    }
                    selectedContacts.remove(it)
                    selectedContacts.add(it)
                } else {
                    selectedContacts.remove(it)
                }

            } else {
                if (it.item) {
                    selectedContacts = MutableList(
                        1,
                        init = { index ->
                            ContactItem(
                                it.id,
                                it.name,
                                it.number,
                                it.email,
                                it.avatar,
                                it.item
                            )
                        })
                } else {
                    selectedContacts.remove(it)
                }
            }
            viewModel.chooseSelectContacts(selectedContacts)
            getSelectList()
            viewModel.contactList.observe(viewLifecycleOwner) {
                contactListAdapter.submitList(it)

            }
        }
    }


    private fun getSelectList() {
        viewModel.selectedContacts.observe(viewLifecycleOwner) {
            selectedContacts = it as MutableList<ContactItem>
        }
    }





    @SuppressLint("Range")
    fun getContactList(): MutableLiveData<List<ContactItem>> {
        val columnIndex = ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID
        val phoneNumber = ContactsContract.CommonDataKinds.Phone.NUMBER
        val phoneDisplayName = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        val eMail = ContactsContract.CommonDataKinds.Email.ADDRESS
        val userAva = ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI
        val cr = requireContext().contentResolver
        if (this@ContactsFragment::contactList.isInitialized) {
            return contactList
        }
        if (cr != null) {
            val cur =
                cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
            if (cur != null && cur.count > 0) {
                while (cur.moveToNext()) {
                    val id = cur.getInt(cur.getColumnIndex(columnIndex))
                    val name = cur.getString(cur.getColumnIndex(phoneDisplayName))
                    val number = cur.getString(cur.getColumnIndex(phoneNumber))
                    val email = cur.getString(cur.getColumnIndex(eMail))
                    val avatar = cur.getString(cur.getColumnIndex(userAva))
                    if (avatar != null) {
//                        if (Build.VERSION.SDK_INT < 28) {
//                            bitmap = MediaStore.Images.Media.getBitmap(
//                                cr, parse(avatar)
//                            )
////                            ivAvatar.setImageBitmap(bitmap)
//                        } else {
//                            val source = ImageDecoder.createSource(cr, parse(avatar))
//                            bitmap = ImageDecoder.decodeBitmap(source)
////                            iv.setImageBitmap(bitmap)
//                        }
//                    val image = MediaStore.Images.Media.getBitmap(cr, parse(avatar))
                    } else {
//                        BitmapFactory.decodeResource(resources, solutions.mobiledev.reminder.R.drawable.avatar_anime_girl)
//                        bitmap = BitmapFactory.decodeResource(resources, solutions.mobiledev.reminder.R.drawable.contact)
//                        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
//                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.contact)
//                        val mPhotoWidth = bitmap.width
//                        val mPhotoHeight = bitmap.height
// присваиваем ImageView
// присваиваем ImageView
//                        imageView.setImageBitmap(bitmap)
                    }

                    if (this@ContactsFragment::names.isInitialized) {
                        names.add(ContactItem(id, name, number, email, avatar))
                    } else {
                        names = MutableList(
                            1,
                            init = { index ->
                                ContactItem(
                                    id,
                                    name,
                                    number,
                                    email,
                                    avatar
                                )
                            })
                    }
                }
                names.forEach {
                    it.number = Regex("[ ]{1,2}|[-]{1,2}").replace(it.number, "")
                }
                var distinctNumber = names.distinctBy { it.number }
                var distinctNumberTwo = distinctNumber.groupBy { it.name }
                distinctNumberTwo.forEach {

                    if (this@ContactsFragment::contacts.isInitialized) {
                        if (it.value.size == 1) {
                            contacts.add(
                                ContactItem(
                                    it.value[0].id,
                                    it.value[0].name,
                                    it.value[0].number,
                                    it.value[0].email,
                                    it.value[0].avatar,
                                    it.value[0].item,
                                    lastNumber = true
                                )
                            )
                        }
                        if (it.value.size == 2) {
                            contacts.add(
                                ContactItem(
                                    it.value[0].id,
                                    it.value[0].name,
                                    it.value[0].number,
                                    it.value[0].email,
                                    it.value[0].avatar,
                                    it.value[0].item,
                                    lastNumber = false
                                )
                            )
                            contacts.add(
                                ContactItem(
                                    it.value[1].id,
                                    it.value[1].name,
                                    it.value[1].number,
                                    it.value[1].email,
                                    it.value[1].avatar,
                                    it.value[1].item,
                                    true
                                )
                            )
                        }
                        if (it.value.size == 3) {
                            contacts.add(
                                ContactItem(
                                    it.value[0].id,
                                    it.value[0].name,
                                    it.value[0].number,
                                    it.value[0].email,
                                    it.value[0].avatar,
                                    it.value[0].item,
                                    lastNumber = false
                                )
                            )
                            contacts.add(
                                ContactItem(
                                    it.value[1].id,
                                    it.value[1].name,
                                    it.value[1].number,
                                    it.value[1].email,
                                    it.value[1].avatar,
                                    it.value[1].item,
                                    lastNumber = false
                                )
                            )
                            contacts.add(
                                ContactItem(
                                    it.value[2].id,
                                    it.value[2].name,
                                    it.value[2].number,
                                    it.value[2].email,
                                    it.value[2].avatar,
                                    it.value[2].item,
                                    lastNumber = true
                                )
                            )

                        }
                    } else {
                        if (it.value.size == 1) {
                            contacts = MutableList(
                                1,
                                init = { index ->
                                    ContactItem(
                                        it.value[0].id,
                                        it.value[0].name,
                                        it.value[0].number,
                                        it.value[0].email,
                                        it.value[0].avatar,
                                        it.value[0].item,
                                        true
                                    )
                                })
                        } else {
                            contacts = MutableList(
                                1,
                                init = { index ->
                                    ContactItem(
                                        it.value[0].id,
                                        it.value[0].name,
                                        it.value[0].number,
                                        it.value[0].email,
                                        it.value[0].avatar,
                                        it.value[0].item,
                                        false
                                    )
                                })
                        }
                    }
                }

                var sortTwo = contacts.sortedBy { it.name }

                contactListLD.value = sortTwo
                if (this::contactItem.isInitialized) {
                    contactListLD.value!!.forEach {
                        if (it.number == contactItem.number) {
                            it.item = contactItem.item
                        }
                    }
                }
                if (this::selectedContacts.isInitialized) {
                    selectedContacts.forEach {
                        if (it.item != contactListLD.value!![it.id].item) {
                            contactListLD.value!![it.id].item = it.item
                        }
                    }
                }

                cur.close()
            }
            return contactListLD
        } else {
            return contactListLD
        }
    }

    private fun setupRecyclerView() = with(binding) {
        with(rvContactList) {
            contactListAdapter = ContactListAdapter()
            adapter = contactListAdapter
        }
        setupClickListener()
    }


//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(REMINDER_ITEM_ID)) {
            reminderItemId = 0
        }
        reminderItemId = args.getInt(REMINDER_ITEM_ID)
//        Log.d("ContactsFragment", "reminderItemId: $reminderItemId")
    }

    private fun launchReminderAddFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderAddFragment.newInstance()).addToBackStack(null)
            .commit()
    }

    private fun launchReminderEditFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderEditFragment.newInstance(reminderItemId)).addToBackStack(null)
            .commit()
    }

    companion object {
        const val NAME = "ContactsFragment"
        const val REMINDER_ITEM_ID = "reminder_item_id"
        const val UNDEFINED_ID = 0
        const val DEFAULT_DURATION = 500.toLong()


        fun newInstance(): ContactsFragment {
            return ContactsFragment().apply {
                arguments = Bundle().apply {}
            }
        }


        fun newInstance(remainderId: Int): ContactsFragment {
            return ContactsFragment().apply {
                arguments = Bundle().apply {
                    putInt(REMINDER_ITEM_ID, remainderId)
                }
            }
        }

        fun newInstanceTwo(selectedContacts: MutableList<ContactItem>): ReminderAddFragment {
            return ReminderAddFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(
                        "selectContactList",
                        selectedContacts as ArrayList<ContactItem>
                    )
                }
            }
        }


    }
}