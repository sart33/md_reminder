package solutions.mobiledev.reminder.presentation.contactSms

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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.data.ContactRepositoryImpl
import solutions.mobiledev.reminder.databinding.FragmentContactsSmsBinding
import solutions.mobiledev.reminder.domain.entity.ContactItem
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.reminder.fragment.AddSmsReminderFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderAddFragment


class ContactsSmsFragment : Fragment() {

    private lateinit var viewModel: ReminderViewModel
    private lateinit var contactSmsListAdapter: ContactSmsListAdapter
    private lateinit var smsMessage: String
    private lateinit var contacts: MutableList<ContactItem>
    private lateinit var contactItem: ContactItem
    private lateinit var names: MutableList<ContactItem>
    private lateinit var contactList: MutableLiveData<List<ContactItem>>

    private val REQUEST_CODE_READ_CONTACTS = 1
    private var READ_CONTACTS_GRANTED = false
    private var reminderItemId: Int = UNDEFINED_ID


    private lateinit var animatorSetCancel: AnimatorSet


    private var _binding: FragmentContactsSmsBinding? = null
    private val binding: FragmentContactsSmsBinding
        get() = _binding ?: throw RuntimeException("FragmentContactsSmsBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContactsSmsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupRecyclerView()
        viewModel = ViewModelProvider(this@ContactsSmsFragment)[ReminderViewModel::class.java]
        initTouchCancelAnimation()
        super.onViewCreated(view, savedInstanceState)

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
            getContactSmsList().observe(viewLifecycleOwner) { item ->
                /**
                 * При вызове submitList - внутри адаптера - запускается новый поток, -
                 * который и выполняет - все вычисления.
                 */
                viewModel.contactList.observe(viewLifecycleOwner) {
                    contactSmsListAdapter.submitList(it)

                }


                viewModel.contactItem.observe(viewLifecycleOwner) {
                    contactItem = it
                }
                contactSmsListAdapter.submitList(item)
            }
            laCancel.setOnClickListener {
                animatorSetCancel.start()
                goToPrev()
            }
        }
    }

    private fun goToPrev() {
        if (this@ContactsSmsFragment::smsMessage.isInitialized) launchAddSmsReminderFragment(
            smsMessage = smsMessage, reminderItemId = reminderItemId
        )
        else launchAddSmsReminderFragment(reminderItemId = reminderItemId)
    }

    private fun initTouchCancelAnimation() = with(binding) {


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
        contactSmsListAdapter.onContactItemClickListener = {
            contactItem = it
            viewModel.chooseContact(it)

            if (this@ContactsSmsFragment::smsMessage.isInitialized) launchAddSmsReminderFragment(
                contactItem,
                smsMessage, reminderItemId
            )
            else launchAddSmsReminderFragment(contactItem, reminderItemId)
        }
    }


    @SuppressLint("Range")
    fun getContactSmsList(): MutableLiveData<List<ContactItem>> {
        val columnIndex = ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID
        val phoneNumber = ContactsContract.CommonDataKinds.Phone.NUMBER
        val phoneDisplayName = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        val eMail = ContactsContract.CommonDataKinds.Email.ADDRESS
        val userAva = ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI
        val cr = requireContext().contentResolver
        if (this@ContactsSmsFragment::contactList.isInitialized) {
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
                    if (this@ContactsSmsFragment::names.isInitialized) {
                        names.add(ContactItem(id, name, number, email, avatar))
                    } else {
                        names = MutableList(
                            1,
                            init = { _ -> ContactItem(id, name, number, email, avatar) })
                    }


                }
                names.forEach {
                    it.number = Regex("[ ]{1,2}|[-]{1,2}").replace(it.number, "")
                }
//
                val distinctNumber = names.distinctBy { it.number }
                val distinctNumberTwo = distinctNumber.groupBy { it.name }
                distinctNumberTwo.forEach {

                    if (this@ContactsSmsFragment::contacts.isInitialized) {
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
                }

                val sortTwo = contacts.sortedBy { it.name }



                ContactRepositoryImpl.contactListLD.value = sortTwo
                if (this::contactItem.isInitialized) {
                    ContactRepositoryImpl.contactListLD.value!!.forEach {
                        if (it.number == contactItem.number) {
                            it.item = contactItem.item
                        }
                    }
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
            contactSmsListAdapter = ContactSmsListAdapter()
            adapter = contactSmsListAdapter
        }
        setupClickListener()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun launchAddSmsReminderFragment(
        contactItem: ContactItem,
        smsMessage: String,
        reminderItemId: Int
    ) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(
                R.id.main_container,
                AddSmsReminderFragment.newInstanceThree(
                    contactItem = contactItem,
                    smsMessage = smsMessage,
                    reminderItemId = reminderItemId
                )
            ).addToBackStack(null)
            .commit()
    }

    private fun launchAddSmsReminderFragment(contactItem: ContactItem, reminderItemId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(
                R.id.main_container,
                AddSmsReminderFragment.newInstanceTwo(
                    contactItem = contactItem,
                    reminderItemId = reminderItemId
                )
            )
            .addToBackStack(null).commit()
    }


    private fun launchAddSmsReminderFragment(smsMessage: String, reminderItemId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(
                R.id.main_container,
                AddSmsReminderFragment.newInstanceFour(
                    smsMessage = smsMessage,
                    reminderItemId = reminderItemId
                )
            ).addToBackStack(null)
            .commit()
    }

    private fun launchAddSmsReminderFragment(reminderItemId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(
                R.id.main_container,
                AddSmsReminderFragment.newInstance(reminderItemId = reminderItemId)
            )
            .addToBackStack(null).commit()
    }

    private fun parseArgs() {
        requireArguments().getString("smsMessage")
            ?.let {
                smsMessage = it
            }
        if (!requireArguments().containsKey(AddSmsReminderFragment.REMINDER_ITEM_ID)) {
            reminderItemId = 0
        } else {
            reminderItemId = requireArguments().getInt(AddSmsReminderFragment.REMINDER_ITEM_ID)
        }
    }

    companion object {
        const val NAME = "ContactsSmsFragment"
        const val REMINDER_ITEM_ID = "reminder_item_id"
        const val UNDEFINED_ID = 0

        fun newInstance(reminderItemId: Int): ContactsSmsFragment {
            return ContactsSmsFragment().apply {
                arguments = Bundle().apply {
                    putInt(REMINDER_ITEM_ID, reminderItemId)
                }
            }
        }

        fun newInstanceSmsMessage(smsMessage: String, reminderItemId: Int): ContactsSmsFragment {
            return ContactsSmsFragment().apply {
                arguments = Bundle().apply {
                    putString("smsMessage", smsMessage)
                    putInt(REMINDER_ITEM_ID, reminderItemId)
                }
            }

        }
    }
}