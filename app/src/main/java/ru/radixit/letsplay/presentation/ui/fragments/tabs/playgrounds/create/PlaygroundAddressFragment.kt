package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.create

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.maps.android.ui.IconGenerator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.network.request.DaDataRequest
import ru.radixit.letsplay.databinding.FragmentPlaygroundAddressBinding
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.showToast
import ru.radixit.letsplay.utils.visible


@AndroidEntryPoint
class PlaygroundAddressFragment : DialogFragment() {

    private var _binding: FragmentPlaygroundAddressBinding? = null
    private val binding get() = _binding!!
    private var _latLng: LatLng? = null
    private val latLng get() = _latLng!!
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var client: FusedLocationProviderClient
    private lateinit var viewModel: CreatePlaygroundsViewModel
    private var googleMapMain:GoogleMap? = null

    @SuppressLint("SetTextI18n")
    private val callback = OnMapReadyCallback { googleMap ->
        googleMapMain = googleMap
        googleMap.setOnMapClickListener { position ->
            googleMapMain = googleMap
            googleMap.clear()
            val iconGenerator = IconGenerator(context)
            val inflatedView =
                View.inflate(context, R.layout.custom_point_map_redes, null)
            iconGenerator.setContentView(inflatedView)
            iconGenerator.setBackground(null)
            googleMap.addMarker(
                MarkerOptions().position(position).icon(
                    BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
                ).snippet("")
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12F))

            binding.cardView15.visibility = View.VISIBLE
            binding.constraintLayout7.visibility = View.VISIBLE
            binding.scrollView.visibility = View.GONE
            val gCoder = Geocoder(requireContext())
            val addresses: ArrayList<Address> =
                gCoder.getFromLocation(
                    position.latitude,
                    position.longitude,
                    1
                ) as ArrayList<Address>
            binding.city.text = addresses[0].locality
            val address = addresses[0].thoroughfare + " " + addresses[0].subThoroughfare
            if(!address.contains("null")){
                binding.address.text = address
            }

            binding.saveDataBtn.setOnClickListener {
                viewModel.saveAddress(
                    (addresses?.get(0)?.locality ?: "") + ", " + (addresses?.get(0)?.thoroughfare
                        ?: "") + " " + (addresses?.get(0)?.subThoroughfare ?: ""),
                    position.latitude.toString(),
                    position.longitude.toString()
                )
                requireContext().showToast("Адрес сохранён")
                findNavController().popBackStack()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaygroundAddressBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CreatePlaygroundsViewModel::class.java]
        onBack()
        binding.cancel.setOnClickListener {
            binding.cardView15.visibility = View.GONE
            binding.constraintLayout7.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE

            val params = binding.scrollView.layoutParams as ConstraintLayout.LayoutParams
            params.topToBottom = binding.toolbar2.id
            binding.scrollView.layoutParams = params
        }
        val recyclerView = binding.recyclerView
        val adapter = SearchAddressAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        viewModel.addresses.observe(viewLifecycleOwner) { list ->
            adapter.setData(list.suggestions)

            val params = binding.scrollView.layoutParams as ConstraintLayout.LayoutParams
            params.topToBottom = binding.toolbar2.id
            binding.scrollView.layoutParams = params
        }
        binding.searchAddresses.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                binding.searchAddresses.clearFocus()
                return false
            }
        })
        binding.searchAddresses.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getAddresses(DaDataRequest(query = query.toString()))
                    binding.cancelTxt.visibility = View.VISIBLE
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        adapter.selectItem { item ->
            binding.constraintLayout7.visible()
            binding.cardView15.visible()
            binding.scrollView.gone()
            binding.city.text = item.value
            item.location?.let { location ->
                googleMapMain?.clear()
                val gCoder = Geocoder(requireContext())
                val iconGenerator = IconGenerator(context)
                val inflatedView =
                    View.inflate(context, R.layout.custom_point_map_redes, null)
                iconGenerator.setContentView(inflatedView)
                iconGenerator.setBackground(null)
                val position = LatLng(location.geoLat.toDouble(),location.geoLon.toDouble())
                googleMapMain?.addMarker(
                    MarkerOptions().position(position).icon(
                        BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
                    ).snippet("")
                )
                googleMapMain?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12F))
                val addresses: ArrayList<Address>? =
                    gCoder.getFromLocation(
                        location.geoLat.toDouble(),
                        location.geoLon.toDouble(),
                        1
                    ) as ArrayList<Address>?
                if (addresses != null && addresses.isNotEmpty()) {
                    binding.city.text = addresses[0].locality
                    val address = addresses[0].thoroughfare + " " + addresses[0].subThoroughfare
                    if(!address.contains("null")){
                        binding.address.text = address
                    }
                }
                (addresses?.get(0)?.locality ?: "") + ", " + (addresses?.get(0)?.thoroughfare
                    ?: "") + " " + (addresses?.get(0)?.subThoroughfare ?: "")
                binding.saveDataBtn.setOnClickListener {
                    viewModel.saveAddress(
                        (addresses?.get(0)?.locality ?: "") + ", " + (addresses?.get(0)?.thoroughfare
                            ?: "") + " " + (addresses?.get(0)?.subThoroughfare ?: ""),
                        position.latitude.toString(),
                        position.longitude.toString()
                    )
//                    viewModel.saveAddress(item.value, location.geoLat, location.geoLat)
                    requireContext().showToast("Адрес сохранён")
                    findNavController().popBackStack()
                }
            }

        }
        binding.scrollView.setOnClickListener{
            Toast.makeText(context, "scrollView", Toast.LENGTH_SHORT).show()
        }
        binding.cancelTxt.setOnClickListener {
            binding.searchAddresses.setQuery("", true)
            binding.cancelTxt.visibility = View.GONE
            binding.scrollView.visibility = View.GONE
            binding.cardView15.visibility = View.VISIBLE
            binding.constraintLayout7.visibility = View.VISIBLE
        }
        return binding.root
    }

    private fun onBack() {
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        client = LocationServices.getFusedLocationProviderClient(requireContext())

        goneSystemGpsBtn()
        binding.myGpsImg.setOnClickListener {
            settigGps()
        }
    }
    private fun settigGps() {
        if (isGpsEnabled()) {
            // GPS включен
            getCurrentLocation()
            binding.myGpsImg.setImageResource(R.drawable.circle_white)
            binding.gpsProgressBar.visible()
        } else {
            // GPS выключен, делаем запрос на включение
            showEnableGpsDialog()
        }
    }

    fun showEnableGpsDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Включить GPS")
        dialogBuilder.setMessage("Для полноценного использования карты, рекомендуется включить GPS")
        dialogBuilder.setPositiveButton("Включить") { dialog: DialogInterface, _: Int ->
            // Переход в настройки для включения GPS
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context?.startActivity(intent)
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Отмена") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }
    private fun goneSystemGpsBtn() {
        val locationButton =
            (mapFragment.view?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(
                Integer.parseInt("2")
            )
        val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        layoutParams.setMargins(0, 0, 60, -850)
        locationButton.layoutParams = layoutParams
        locationButton.visibility = View.GONE
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val cancellationTokenSource = CancellationTokenSource()
        val currentLocationTask: Task<Location> = client.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )
        currentLocationTask.addOnCompleteListener { location ->
            mapFragment.getMapAsync {
                it.isMyLocationEnabled = true
                _latLng = LatLng(location.result.latitude, location.result.longitude)
                latLng?.let { latLng ->
                    it.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12F))
                    binding.gpsProgressBar.gone()
                    binding.myGpsImg.setImageResource(R.drawable.gps_btn)
                }
            }
            mapFragment.getMapAsync(callback)
        }
    }

    fun isGpsEnabled(): Boolean {
        val locationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onResume() {
        super.onResume()
        mapFragment.getMapAsync(callback)
    }
}