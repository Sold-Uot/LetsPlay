package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.editProfile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.network.request.EditProfileRedesRequest
import ru.radixit.letsplay.databinding.FragEditProfileRedesignBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.adapter.DropDownRvRedesAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.adapter.SpinnerCreatePlaygAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter.CustomSpinnerAdapter
import ru.radixit.letsplay.utils.getFileName
import ru.radixit.letsplay.utils.gone
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.roundToInt


@AndroidEntryPoint
class EditProfileRedesFrag : Fragment() {

    private var _binding: FragEditProfileRedesignBinding? = null
    private var uri: Uri? = null
    private val binding get() = _binding!!
    private val viewModel: EditProfileViewModel by viewModels()
    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                uri = it
                viewModel.saveUri(uri.toString())
                viewModel.uri.observe(viewLifecycleOwner) {
                    binding.profileImage.visibility = View.VISIBLE
                    Glide.with(binding.root).load(Uri.parse(it)).into(binding.profileImage)
                }
            }
        }
    private var position = 1
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragEditProfileRedesignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
//        setupPosition()
//        changePosition()
//        setupSex()
//        changeSex()
        onBack()
//        binding.editProfileBirthday.maskForBirthDate()

        binding.settingMatCard.setOnClickListener {
            selectImageFromGallery()
        }
        binding.addPhoto.setOnClickListener {
            selectImageFromGallery()
        }

        binding.heightSlider.setLabelFormatter(object : LabelFormatter {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })
        binding.heightSlider.addOnChangeListener(object : Slider.OnChangeListener {
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                slider.value = value.roundToInt().toFloat()
            }
        })
        binding.weightSlider.setLabelFormatter(object : LabelFormatter {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })
        binding.weightSlider.addOnChangeListener(object : Slider.OnChangeListener {
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                slider.value = value.roundToInt().toFloat()
            }
        })

        binding.checkSaveToolbar.setOnClickListener {
            uploadPhoto()
            val name = binding.nameUserEdTv.text.toString().ifEmpty {
                binding.nameUserEdTv.hint.toString()
            }
            val gender = if (binding.day2Rb.isChecked) {
                0
            } else {
                1
            }
            val surname = binding.surnameUserEdTv.text.toString().ifEmpty {
                binding.surnameUserEdTv.hint.toString()
            }
//            val email: String = if (binding.editProfilePost.text.isEmpty()) {
//                binding.editProfilePost.hint.toString()
//            } else {
//                binding.editProfilePost.text.toString()
//            }
            val birthDate = binding.birthYear.text.toString().ifEmpty {
                binding.birthYear.hint.toString().split(" ")[2]
            }
            val weight = if (binding.weightSlider.value.toString().isEmpty()) {
                0
            } else {
                binding.weightSlider.value.toInt()
            }
            val height = if (binding.heightSlider.value.toString().isEmpty()) {
                0
            } else {
                binding.heightSlider.value.toInt()
            }

            viewModel.edit(
                EditProfileRedesRequest(
                    name, surname, weight, height, birthDate,
                    position, gender
                )
            ) {
                if (it) {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun setupView() {
        viewModel.fetchProfile()
        viewModel.photo.observe(viewLifecycleOwner) {
            binding.profileImage.visibility = View.VISIBLE
            Glide.with(binding.root).load(it.url).into(binding.profileImage)
        }
        val slots = UnderscoreDigitSlotsParser().parseSlots("__-__-____")
        val mask = MaskImpl.createTerminated(slots)
        mask.isHideHardcodedHead = true
        val formatWatcher: FormatWatcher = MaskFormatWatcher(
            mask
        )
        formatWatcher.installOn(binding.birthYear)
        binding.birthYear.doAfterTextChanged {
            if (it.toString().length != 10) {
                binding.materialCardView4.endIconMode = TextInputLayout.END_ICON_NONE
                binding.materialCardView4.error = "Дата не действительна"
            } else {
                binding.materialCardView4.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding.materialCardView4.setEndIconTintList(ContextCompat.getColorStateList(requireContext(), R.color.main_gray))
                binding.materialCardView4.endIconDrawable = ResourcesCompat.getDrawable(resources,
                    R.drawable.baseline_calendar_month,null)
                binding.materialCardView4.error = null
                try {
                    val sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    LocalDate.parse(it.toString(), sdf)
                } catch (e: DateTimeParseException) {
                    binding.materialCardView4.error = "Дата не действительна"
                }
            }
        }
        viewModel.profile.observe(viewLifecycleOwner) {
            val items = arrayListOf<String>()
            it.data.position?.let {posArgs->
                it.option.position.forEach { option ->
                    items.add(option.text)
                }

                binding.positionRv.layoutManager = LinearLayoutManager(requireContext(),
                    LinearLayoutManager.VERTICAL,false)
                binding.positionRv.setHasFixedSize(true)
                val adapterStuddedBoots = DropDownRvRedesAdapter(positionSelected = posArgs-1){obj,pos,str->
                    str?.let {
                        hideKeyboard()
                    }
                    position = pos
                    obj.updateData()
                    binding.positionSpinner.setSelection(pos)
                    binding.positionRv.gone()
                }
                binding.positionRv.adapter = adapterStuddedBoots
                adapterStuddedBoots.submitList(items)
                val adapterSpinner = SpinnerCreatePlaygAdapter(requireContext(), items,"Позиция", true){
                    binding.positionRv.isVisible = !binding.positionRv.isVisible
                }
                binding.positionSpinner.adapter = adapterSpinner
                binding.positionSpinner.setOnItemSelectedListener(object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        val pos = p2 + 1
                        position = pos
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                })
                binding.positionSpinner.setSelection(posArgs - 1)
            }
            binding.nameUserEdTv.setText(it.data.name ?: "Имя не указано")
            if (it.data.gender == 1) {
                binding.day1Rb.isChecked = true
            } else {
                binding.day2Rb.isChecked = true
            }
            binding.surnameUserEdTv.setText(it.data.surname ?: "Фамилия не указана")
//            binding.editProfilePosition.hint =
//                if (it.data.position != null) "Позиция: ${it.option.position[it.data.position - 1].text}" else "Позиция на поле не указана"
            binding.birthYear.setText(if (it.data.birthday != null) "${it.data.birthday}" else "")

            binding.heightSlider.value =
                if (it.data.height != null) it.data.height.toFloat() else 0.toFloat()
            binding.weightSlider.value =
                if (it.data.height != null) it.data.weight!!.toFloat() else 0.toFloat()
            binding.progressBar.gone()
//            binding.editProfilePost.hint = (it.data.email ?: "Почта: Не указана").toString()
        }
    }
    private fun hideKeyboard() {
        val inputManager: InputMethodManager? =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputManager?.hideSoftInputFromWindow(
            activity?.currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun uploadPhoto() {
        uri?.let { uri ->
            if (Build.VERSION.SDK_INT < 28) {
                val file = File(
                    requireActivity().cacheDir,
                    requireActivity().contentResolver.getFileName(uri)
                )
                val bitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver,
                    Uri.fromFile(file)
                )
                viewModel.uploadAvatar(bitmap)
                viewModel.uploadingPhoto.observe(viewLifecycleOwner) {
                    progressUploadAvatar(it)
                }
            } else {
                val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                viewModel.uploadAvatar(bitmap)
                viewModel.uploadingPhoto.observe(viewLifecycleOwner) {
                    progressUploadAvatar(it)
                }
            }
        }
    }

    //
    private fun progressUploadAvatar(it: Boolean) {
        if (it) {
            binding.progressBarLoad.visibility = View.VISIBLE
            binding.checkSaveToolbar.isEnabled = false
        } else {
            binding.progressBarLoad.visibility = View.GONE
            binding.checkSaveToolbar.isEnabled = true
        }
    }

    //
    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")
//
//
//    private fun setupPosition() {
//        viewModel.textPosition.observe(viewLifecycleOwner) {
//            binding.editProfilePosition.text = it
//        }
//    }
//
//    private fun changePosition() {
//        binding.editProfilePosition.setOnClickListener {
//            viewModel.changePositionOnField()
//            setupPosition()
//        }
//    }
//
//    private fun setupSex() {
//        viewModel.textSex.observe(viewLifecycleOwner) {
//            binding.editProfileSex.text = it
//        }
//    }
//
//    private fun changeSex() {
//        binding.editProfileSex.setOnClickListener {
//            viewModel.changeSex()
//            setupSex()
//        }
//    }

    private fun onBack() {
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}