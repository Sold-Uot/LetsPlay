package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.create

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragCreatePlaygRedesignBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.adapter.DropDownRvRedesAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.adapter.SpinnerCreatePlaygAdapter
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.maskForNumberPhone
import ru.radixit.letsplay.utils.showToast


@AndroidEntryPoint
class CreatePlaygRedesFrag : Fragment(), OnMapReadyCallback {
    private lateinit var mapFragment: SupportMapFragment
    private var _binding: FragCreatePlaygRedesignBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreatePlaygroundsViewModel
    private var latLng: LatLng? = null
    private lateinit var client: FusedLocationProviderClient
    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uri: List<Uri> ->
            uploadPhoto(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragCreatePlaygRedesignBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CreatePlaygroundsViewModel::class.java]

        mapFragment =
            (childFragmentManager.findFragmentById(ru.radixit.letsplay.R.id.map_frag_create_playg) as SupportMapFragment?)!!
        client = LocationServices.getFusedLocationProviderClient(requireContext())
        binding.workSchedule.setOnClickListener {
            findNavController().navigate(CreatePlaygRedesFragDirections.actionCreatePlaygRedesFragToPlaygWorkSchedulerFragment())
        }
        viewModel.saveCoverage(1)
        viewModel.savePayment(true)
//        val things =
//            listOf(binding.shirtsIs, binding.ballsIs, binding.lightIs, binding.changeClothesIs)
//        for (thing in things) {
//            thing.setOnClickListener {
//                val split = thing.hint!!.split(":")[0]
//                Log.d("split", "split = ${split}")
//                if (findNavController().currentDestination?.id == R.id.createPlaygroundFragment) {
//                    findNavController().navigate(
//                        CreatePlaygroundFragmentDirections.actionCreatePlaygroundFragmentToAvailabilityDialogFragment(
//                            split
//                        )
//                    )
//                }
//            }
//        }
        viewModel.address.observe(viewLifecycleOwner) {
            binding.addressMapTv.text = it
        }
        viewModel.latLngObj.observe(viewLifecycleOwner) {
            latLng = it
            mapFragment.getMapAsync(this)
        }
        binding.editAddressMapTv.setOnClickListener {
            findNavController().navigate(CreatePlaygRedesFragDirections.actionCreatePlaygRedesFragToPlaygroundAddressFragment())
        }
//        viewModel.availability.observe(viewLifecycleOwner) {
//            val sb: StringBuilder = StringBuilder(it)
//            when {
//                it.contains("Мячи") -> {
//                    binding.ballsIs.setText(sb.deleteRange(0, 6))
//                }
//                it.contains("Раздевалки") -> {
//                    binding.changeClothesIs.setText(sb.deleteRange(0, 12))
//                }
//                it.contains("Манишики") -> {
//                    binding.shirtsIs.setText(sb.deleteRange(0, 10))
//                }
//                it.contains("Освещение") -> {
//                    binding.lightIs.setText(sb.deleteRange(0, 11))
//                }
//            }
//        }
        val listFieldSize =
            listOf("Покрытие: Искусственное", "Покрытие: Асфальт", "Покрытие: Натуральное")

        binding.coatingRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.coatingRv.setHasFixedSize(true)
        val adapterCoating = DropDownRvRedesAdapter{obj,pos,str ->
            str?.let {
                hideKeyboard()
            }
            when (pos) {
                0 -> {
                    viewModel.saveCoverage(2)
                }
                1 -> {
                    viewModel.saveCoverage(0)
                }
                2 -> {
                    viewModel.saveCoverage(1)
                }
            }
            obj.updateData()
            binding.coatingSpinner.setSelection(pos)
            binding.coatingRv.gone()
        }
        binding.coatingRv.adapter = adapterCoating
        adapterCoating.submitList(listFieldSize)
        val adapterSpinner = SpinnerCreatePlaygAdapter(requireContext(), listFieldSize, "Покрытие") {
                binding.coatingRv.isVisible = !binding.coatingRv.isVisible
            }
        binding.coatingSpinner.adapter = adapterSpinner
        binding.coatingSpinner.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })
        binding.coatingSpinner.setSelection(0)
        val listPay = listOf(
            "Оплата переводом: Нет",
            "Оплата переводом: Есть"
        )
        binding.payRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.payRv.setHasFixedSize(true)
        val adapterPay = DropDownRvRedesAdapter{obj,pos,str ->
            str?.let {
                hideKeyboard()
            }
            when (pos) {
                0 -> {
                    viewModel.savePayment(true)
                }
                1 -> {
                    viewModel.savePayment(false)
                }
            }
            obj.updateData()
            binding.paySpinner.setSelection(pos)
            binding.payRv.gone()
        }
        binding.payRv.adapter = adapterPay
        adapterPay.submitList(listPay)
        val adapterSpinnerPay =
            SpinnerCreatePlaygAdapter(requireContext(), listPay, "Оплата переводом", true) {
                binding.payRv.isVisible = !binding.payRv.isVisible
            }
        binding.paySpinner.adapter = adapterSpinnerPay
        binding.paySpinner.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })
        binding.paySpinner.setSelection(0)
//        binding.editProfilePay.setOnClickListener {
//            if (pay) {
//                binding.editProfilePay.hint = "Оплата переводом: Есть"
//                pay = false
//                viewModel.savePayment(false)
//            } else {
//                binding.editProfilePay.hint = "Оплата переводом: Нет"
//                pay = true
//                viewModel.savePayment(true)
//            }
//        }
        val listStuddedBoots = listOf("Шипованные бутсы: Можно", "Шипованные бутсы: Нельзя")
        val adapterStuddedBootsSpinner =
            SpinnerCreatePlaygAdapter(requireContext(), listStuddedBoots, "Шиповые бутсы") {
                binding.studdedBootsRv.isVisible = !binding.studdedBootsRv.isVisible
            }
        binding.studdedBootsRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.studdedBootsRv.setHasFixedSize(true)
        val adapterStuddedBoots = DropDownRvRedesAdapter{obj,pos,str->
            str?.let {
                hideKeyboard()
            }
            when (pos) {
                0 -> {
                    viewModel.saveShoes(false)
                }
                1 -> {
                    viewModel.saveShoes(true)
                }
            }
            obj.updateData()
            binding.studdedBootsSpinner.setSelection(pos)
            binding.studdedBootsRv.gone()
        }
        binding.studdedBootsRv.adapter = adapterStuddedBoots
        adapterStuddedBoots.submitList(listStuddedBoots)
        binding.studdedBootsSpinner.adapter = adapterStuddedBootsSpinner
        binding.studdedBootsSpinner.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })
        binding.studdedBootsSpinner.setSelection(0)

        val list = listOf("Нет", "Есть", "Есть (за оплату)")
        binding.ballsRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.ballsRv.setHasFixedSize(true)
        val adapterBallsRv = DropDownRvRedesAdapter(true){obj,pos,str->
            Log.d("value","uri = ${viewModel.uriList.value}")
            if(!str.equals("")) {
                hideKeyboard()
                viewModel.setBalls(pos,str)
            }else{
                viewModel.setBalls(pos,null)
            }
            obj.updateData()
            binding.ballsSpinner.setSelection(pos)
            binding.ballsRv.gone()
        }
        binding.ballsRv.adapter = adapterBallsRv
        adapterBallsRv.submitList(list)

        val adapterSpinnerBalls = SpinnerCreatePlaygAdapter(requireContext(), list, "Мячи", true) {
            binding.ballsRv.isVisible = !binding.ballsRv.isVisible
        }
        binding.ballsSpinner.adapter = adapterSpinnerBalls
        binding.ballsSpinner.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })
        binding.ballsSpinner.setSelection(0)
        binding.clothesRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.clothesRv.setHasFixedSize(true)
        val adapterClothes = DropDownRvRedesAdapter(true){obj,pos,str ->
            if(!str.equals("")) {
                hideKeyboard()
                viewModel.setChanging(pos,str)
            }else {
                viewModel.setChanging(pos,null)
            }
            obj.updateData()
            binding.clothesSpinner.setSelection(pos)
            binding.clothesRv.gone()
        }
        binding.clothesRv.adapter = adapterClothes
        adapterClothes.submitList(list)


        val adapterSpinnerClothes =
            SpinnerCreatePlaygAdapter(requireContext(), list, "Раздевалки", true) {
                binding.clothesRv.isVisible = !binding.clothesRv.isVisible
            }
        binding.clothesSpinner.adapter = adapterSpinnerClothes
        binding.clothesSpinner.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })
        binding.clothesSpinner.setSelection(0)
        binding.shirtsRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.shirtsRv.setHasFixedSize(true)
        val adapterShirts = DropDownRvRedesAdapter(true){obj,pos,str->
            obj.updateData()
            binding.shirtsSpinner.setSelection(pos)
            binding.shirtsRv.gone()
            if(!str.equals("")) {
                hideKeyboard()
                viewModel.setShirts(pos,str)
            }else{
                viewModel.setShirts(pos,null)
            }
        }
        binding.shirtsRv.adapter = adapterShirts
        adapterShirts.submitList(list)


        val adapterSpinnerShirts =
            SpinnerCreatePlaygAdapter(requireContext(), list, "Манишки", true) {
                binding.shirtsRv.isVisible = !binding.shirtsRv.isVisible
            }
        binding.shirtsSpinner.adapter = adapterSpinnerShirts
        binding.shirtsSpinner.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })
        binding.shirtsSpinner.setSelection(0)



        binding.lightRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.lightRv.setHasFixedSize(true)
        val adapterLight = DropDownRvRedesAdapter(true){obj,pos,str->
            obj.updateData()
            binding.lightSpinner.setSelection(pos)
            binding.lightRv.gone()
            if(!str.equals("")) {
                hideKeyboard()
                viewModel.setLights(pos,str)
            }else{
                viewModel.setLights(pos,null)
            }
        }
        binding.lightRv.adapter = adapterLight
        adapterLight.submitList(list)


        val adapterSpinnerLight =
            SpinnerCreatePlaygAdapter(requireContext(), list, "Освещение", true) {
                binding.lightRv.isVisible = !binding.lightRv.isVisible
            }
        binding.lightSpinner.adapter = adapterSpinnerLight
        binding.lightSpinner.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding.showerRoomRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
        binding.showerRoomRv.setHasFixedSize(true)
        val adapterCheap = DropDownRvRedesAdapter(true){
            obj,pos,str ->
            obj.updateData()
            binding.showerroomSpinner.setSelection(pos)
            binding.showerRoomRv.gone()
            if(!str.equals("")){
                hideKeyboard()
                viewModel.setShower(pos,str)

            }
            else{
                viewModel.setShower(pos ,null) }

        }

        binding.showerRoomRv.adapter = adapterCheap
        adapterCheap.submitList(list)



        val adapterSpinnerCeaproom = SpinnerCreatePlaygAdapter(requireContext(),list,"Душевая",true){
            binding.showerRoomRv.isVisible = !binding.showerRoomRv.isVisible
        }
        binding.showerroomSpinner.adapter = adapterSpinnerCeaproom
        binding.showerroomSpinner.setOnItemSelectedListener(object  :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })





        binding.lightSpinner.setSelection(0)
        binding.contactNumber.maskForNumberPhone()
        binding.continueButton.setOnClickListener {
            if (binding.nameField.text!!.isNotEmpty() || binding.contactNumber.text!!.isNotEmpty() || binding.price.text!!.isNotEmpty()) {
                viewModel.createPlayground(
                    binding.nameField.text.toString(),
                    binding.contactNumber.text.toString()
                        .filterNot { it == '-' || it == ' ' || it == '(' || it == ')' }
                        .replace("+7", ""),
                    binding.price.text.toString(),
                )
            } else {
                requireContext().showToast("Заполните все поля")
            }
        }
        viewModel.playSuccessCreated.observe(viewLifecycleOwner){
                findNavController().popBackStack()
        }
        onBack()
        binding.addPhoto.setOnClickListener {
            selectImageFromGallery()
        }
        return binding.root
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager? =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputManager?.hideSoftInputFromWindow(
            activity?.currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        latLng?.let { latLng ->
            val iconGenerator = IconGenerator(context)
            val inflatedView =
                View.inflate(context, ru.radixit.letsplay.R.layout.custom_point_map_redes, null)
            iconGenerator.setContentView(inflatedView)
            iconGenerator.setBackground(null)
            googleMap.addMarker(
                MarkerOptions().position(latLng).icon(
                    BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
                ).snippet("")
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12F))
        }
    }

    private fun uploadPhoto(uri: List<Uri>) {
        viewModel.saveUri(uri)
        binding.photoProfile.visibility = View.VISIBLE
        viewModel.uriList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Glide.with(binding.root).load(it[it.lastIndex]).into(binding.photoProfile)
            }
        }
    }


    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun onBack() {
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun toggle(show: Boolean) {
        val transition: Transition = Slide(Gravity.TOP)
        transition.setDuration(600)
        transition.addTarget(binding.clothesRv)
        TransitionManager.beginDelayedTransition(binding.clothesSpinner, transition)
        binding.clothesRv.visibility = if (show) View.VISIBLE else View.GONE
    }
    override fun onDestroy() {
        _binding = null
        viewModel.saveMondayEnd(null)
        viewModel.saveTuesdayEnd(null)
        viewModel.saveWednesdayEnd(null)
        viewModel.saveThursdayEnd(null)
        viewModel.saveFridayEnd(null)
        viewModel.saveSaturdayEnd(null)
        viewModel.saveSundayEnd(null)
        viewModel.saveMondayStart(null)
        viewModel.saveTuesdayStart(null)
        viewModel.saveWednesdayStart(null)
        viewModel.saveThursdayStart(null)
        viewModel.saveFridayStart(null)
        viewModel.saveSaturdayStart(null)
        viewModel.saveSundayStart(null)
        super.onDestroy()
    }
}