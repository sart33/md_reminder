package solutions.mobiledev.reminder.presentation.contactsEmail

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.data.ContactRepositoryImpl
import solutions.mobiledev.reminder.databinding.FragmentContactsEmailBinding
import solutions.mobiledev.reminder.domain.entity.ContactItem
import solutions.mobiledev.reminder.presentation.contactSms.ContactsSmsFragment
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.reminder.fragment.AddEmailReminderFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.AddSmsReminderFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderAddFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListFragment


class ContactsEmailFragment : Fragment() {

    private lateinit var viewModel: ReminderViewModel
    private lateinit var contactEmailListAdapter: ContactEmailListAdapter
    private lateinit var emailMessage: String
    private lateinit var contactItem: ContactItem
    private lateinit var contacts: MutableList<ContactItem>
    private lateinit var selectedContacts: MutableList<ContactItem>
    private lateinit var names: MutableList<ContactItem>
    private lateinit var contactList: MutableLiveData<List<ContactItem>>


    private val REQUEST_CODE_READ_CONTACTS = 1
    private var READ_CONTACTS_GRANTED = false
    private var reminderItemId: Int = UNDEFINED_ID

    private var _binding: FragmentContactsEmailBinding? = null
    private val binding: FragmentContactsEmailBinding
        get() = _binding ?: throw RuntimeException("FragmentContactsEmailBinding == null")

    private lateinit var animatorSetSave: AnimatorSet
    private lateinit var animatorSetCancel: AnimatorSet


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContactsEmailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupRecyclerView()
        viewModel = ViewModelProvider(this@ContactsEmailFragment)[ReminderViewModel::class.java]

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
            getContactEmailList().observe(viewLifecycleOwner) { item ->
                /**
                 * При вызове submitList - внутри адаптера - запускается новый поток, -
                 * который и выполняет - все вычисления.
                 */
                getSelectList()

                viewModel.contactList.observe(viewLifecycleOwner) {
                    contactEmailListAdapter.submitList(it)
                }

                viewModel.contactItem.observe(viewLifecycleOwner) {
                    contactItem = it
                }
                contactEmailListAdapter.submitList(item)
                laCancel.setOnClickListener {
                    animatorSetCancel.start()
                    goToPrev()
                }
                laSave.setOnClickListener {
                    animatorSetSave.start()
                    if (this@ContactsEmailFragment::selectedContacts.isInitialized &&
                        this@ContactsEmailFragment::emailMessage.isInitialized
                    ) {
                        launchAddEmailReminderFragment(
                            reminderItemId = reminderItemId,
                            selectedContacts = selectedContacts,
                            emailMessage = emailMessage
                        )
                    }
//                    if (!this@ContactsEmailFragment::selectedContacts.isInitialized &&
//                        this@ContactsEmailFragment::emailMessage.isInitialized
//                    ) {
//                        launchAddEmailReminderFragment(
//                            reminderItemId = reminderItemId,
//                            emailMessage = emailMessage
//                        )
//                    }
                    if (this@ContactsEmailFragment::selectedContacts.isInitialized &&
                        !this@ContactsEmailFragment::emailMessage.isInitialized
                    ) {
                        launchAddEmailReminderFragment(
                            reminderItemId = reminderItemId,
                            selectedContacts = selectedContacts
                        )
                    } else {}
                }
            }
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

    private fun goToPrev() {
        if (
            this@ContactsEmailFragment::emailMessage.isInitialized
        ) {
            launchAddEmailReminderFragment(
                emailMessage = emailMessage,
                reminderItemId = reminderItemId
            )
        }
        if (!this@ContactsEmailFragment::emailMessage.isInitialized
        ) {
            launchAddEmailReminderFragment(reminderItemId = reminderItemId)
        }
    }

    private fun setupClickListener() {
        contactEmailListAdapter.onContactItemClickListener = {
            viewModel.chooseContact(it)
            it.item = !it.item
            if (this@ContactsEmailFragment::selectedContacts.isInitialized) {
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
                        init = { _ ->
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
            viewModel.contactList.observe(viewLifecycleOwner) { item ->
                contactEmailListAdapter.submitList(item)

            }
        }
    }


    private fun getSelectList() {
        viewModel.selectedContacts.observe(viewLifecycleOwner) {
            selectedContacts = it as MutableList<ContactItem>
        }
    }


    @SuppressLint("Range")
    fun getContactEmailList(): MutableLiveData<List<ContactItem>> {
        val columnIndex = ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID
        val phoneNumber = ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER
        val eMail = ContactsContract.CommonDataKinds.Email.ADDRESS
        val userAva = ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI
        val phoneDisplayName = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME

        val cr = requireContext().contentResolver
        if (this@ContactsEmailFragment::contactList.isInitialized) {
            return contactList
        }
        if (cr != null) {
            val cur =
                cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null)

            if (cur != null && cur.count > 0) {
                while (cur.moveToNext()) {
                    val id = cur.getInt(cur.getColumnIndex(columnIndex))
                    val name = cur.getString(cur.getColumnIndex(phoneDisplayName))
                    val number = cur.getString(cur.getColumnIndex(phoneNumber))
                    val email = cur.getString(cur.getColumnIndex(eMail))
                    val avatar = cur.getString(cur.getColumnIndex(userAva))
                    if (this@ContactsEmailFragment::names.isInitialized) {
                        names.add(ContactItem(id, name, number, email, avatar))
                    } else {
                        names = MutableList(
                            1,
                            init = { _ -> ContactItem(id, name, number, email, avatar) })
                    }
                }

                val distinctNumber = names.distinctBy { it.email }
                val distinctNumberTwo = distinctNumber.groupBy { it.name }
                distinctNumberTwo.forEach {

                    if (this@ContactsEmailFragment::contacts.isInitialized) {
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
                        if (it.value.size == 4) {
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
                            contacts.add(
                                ContactItem(
                                    it.value[3].id,
                                    it.value[3].name,
                                    it.value[3].number,
                                    it.value[3].email,
                                    it.value[3].avatar,
                                    it.value[3].item,
                                    lastNumber = true
                                )
                            )

                        }
                        if (it.value.size == 5) {
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
                            contacts.add(
                                ContactItem(
                                    it.value[3].id,
                                    it.value[3].name,
                                    it.value[3].number,
                                    it.value[3].email,
                                    it.value[3].avatar,
                                    it.value[3].item,
                                    lastNumber = true
                                )
                            )
                            contacts.add(
                                ContactItem(
                                    it.value[4].id,
                                    it.value[4].name,
                                    it.value[4].number,
                                    it.value[4].email,
                                    it.value[4].avatar,
                                    it.value[4].item,
                                    lastNumber = true
                                )
                            )

                        }
                        if (it.value.size == 6) {
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
                            contacts.add(
                                ContactItem(
                                    it.value[3].id,
                                    it.value[3].name,
                                    it.value[3].number,
                                    it.value[3].email,
                                    it.value[3].avatar,
                                    it.value[3].item,
                                    lastNumber = true
                                )
                            )
                            contacts.add(
                                ContactItem(
                                    it.value[4].id,
                                    it.value[4].name,
                                    it.value[4].number,
                                    it.value[4].email,
                                    it.value[4].avatar,
                                    it.value[4].item,
                                    lastNumber = true
                                )
                            )

                            contacts.add(
                                ContactItem(
                                    it.value[5].id,
                                    it.value[5].name,
                                    it.value[5].number,
                                    it.value[5].email,
                                    it.value[5].avatar,
                                    it.value[5].item,
                                    lastNumber = true
                                )
                            )

                        }
                    } else {
                        if (it.value.size == 1) {
                            contacts = MutableList(
                                1,
                                init = { _ ->
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
                                init = { _ ->
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

                    val sortTwo = contacts.sortedBy { item -> item.name }



                    ContactRepositoryImpl.contactListLD.value = sortTwo
                    if (this::contactItem.isInitialized) {
                        ContactRepositoryImpl.contactListLD.value!!.forEach { item ->
                            if (item.email == contactItem.email) {
                                item.item = contactItem.item
                            }
                        }
                    }
//                    if (this::selectedContacts.isInitialized) {
//                        selectedContacts.forEach {
                    /**
                     * при возврате после выбора ящиков с помощью теугольника
                     */
//                            if (it.item != ContactRepositoryImpl.contactListLD.value!![it.id].item) {
//                                ContactRepositoryImpl.contactListLD.value!![it.id].item = it.item
//                            }
//                        }
//                    }
                }
                cur.close()
            }

            return ContactRepositoryImpl.contactListLD
        } else {
            return ContactRepositoryImpl.contactListLD
        }
    }


    private fun setupRecyclerView() = with(binding) {
        with(rvContactList) {
            contactEmailListAdapter = ContactEmailListAdapter()
            adapter = contactEmailListAdapter
        }
        setupClickListener()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun launchAddEmailReminderFragment(reminderItemId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(
                R.id.main_container,
                AddEmailReminderFragment.newInstance(reminderItemId = reminderItemId)
            )
            .addToBackStack(null)
            .commit()
    }


    private fun launchAddEmailReminderFragment(
        reminderItemId: Int,
        selectedContacts: MutableList<ContactItem>
    ) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(
                R.id.main_container,
                AddEmailReminderFragment.newInstanceTwo(
                    selectedContacts = selectedContacts,
                    reminderItemId = reminderItemId
                )
            )
            .addToBackStack(null).commit()
    }


    private fun launchAddEmailReminderFragment(
        reminderItemId: Int,
        selectedContacts: MutableList<ContactItem>,
        emailMessage: String
    ) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(
                R.id.main_container,
                AddEmailReminderFragment.newInstanceThree(
                    selectedContacts = selectedContacts,
                    emailMessage = emailMessage,
                    reminderItemId = reminderItemId
                )
            )
            .addToBackStack(null).commit()
    }


    private fun launchAddEmailReminderFragment(
        reminderItemId: Int,
        emailMessage: String
    ) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(
                R.id.main_container,
                AddEmailReminderFragment.newInstanceFour(
                    emailMessage = emailMessage,
                    reminderItemId = reminderItemId
                )
            )
            .addToBackStack(null).commit()
    }

    private fun parseArgs() {
        requireArguments().getString("emailMessage")?.let {
            emailMessage = it
        }
        reminderItemId = if (!requireArguments().containsKey(REMINDER_ITEM_ID)) {
            0
        } else {
            requireArguments().getInt(REMINDER_ITEM_ID)
        }
    }


    companion object {
        const val NAME = "ContactsEmailFragment"
        const val REMINDER_ITEM_ID = "reminder_item_id"
        const val UNDEFINED_ID = 0


        fun newInstance(reminderItemId: Int): ContactsEmailFragment {
            return ContactsEmailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ContactsSmsFragment.REMINDER_ITEM_ID, reminderItemId)
                }
            }
        }

        fun newInstanceEmailMessage(
            emailMessage: String,
            reminderItemId: Int
        ): ContactsEmailFragment {
            return ContactsEmailFragment().apply {
                arguments = Bundle().apply {
                    putString("emailMessage", emailMessage)
                    putInt(ContactsSmsFragment.REMINDER_ITEM_ID, reminderItemId)
                }
            }

        }

    }
}