package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.maps.android.ui.IconGenerator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_pointer.distance
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.PlaygroundForMap
import ru.radixit.letsplay.data.network.response.MapsResponse
import ru.radixit.letsplay.databinding.BottomSheetDialogBinding
import ru.radixit.letsplay.databinding.FragmentMapsBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.inVisible
import ru.radixit.letsplay.utils.showToast
import ru.radixit.letsplay.utils.visible
import java.util.*

@AndroidEntryPoint
class MapsFragment : BaseFragment(), OnMapReadyCallback {
    private var isFirstLocation = false
    private lateinit var binding: FragmentMapsBinding
    private val viewModel: MapsViewModel by viewModels()
    private var googleMapMain: GoogleMap? = null
    private var latLng: LatLng? = null
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var client: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            constTopBar.notifBellConstPlayg.setOnClickListener {
                findNavController().navigate(MapsFragmentDirections.actionMapsFragmentToNotificationsFragment3())
            }
            listPlaygroundsBtn.setOnClickListener {
                findNavController().navigate(MapsFragmentDirections.actionMapsFragmentToListPlaygroundsFragment())
            }
            constTopBar.searchFieldsPlayg.gone()

            viewModel.newNotifCount.observe(viewLifecycleOwner) {
                if (it > 0) {
                    binding.constTopBar.notifCountTvPlayg.visible()
                    binding.constTopBar.notifCountTvPlayg.text = it.toString()
                } else {
                    binding.constTopBar.notifCountTvPlayg.gone()
                }
            }
            viewModel.newNotifCount()
        }
        mapFragment =
            (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        client = LocationServices.getFusedLocationProviderClient(requireContext())
        mapFragment.getMapAsync(this)
        goneSystemGpsBtn()
        settigGps()
        binding.myGpsImg.setOnClickListener {
            settigGps()
        }

        binding.constTopBar.addPlaygroundsPlayg.setOnClickListener {
            findNavController().navigate(MapsFragmentDirections.actionMapsFragmentToCreatePlaygRedesFrag())
        }
    }


    private fun settigGps() {
        if (isGpsEnabled()) {
            // GPS включен


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation()
                }
            }

//            getCurrentLocation()
            binding.myGpsImg.setImageResource(R.drawable.circle_white)
            binding.gpsProgressBar.visible()
        } else {
            // GPS выключен, делаем запрос на включение
            showEnableGpsDialog()
        }
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

    private fun locationRequest() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10 * 1000 // 10 seconds
        locationRequest.fastestInterval = 5 * 1000 // 5 seconds

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // GPS включен, можно выполнять операции с геолокацией
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Показываем пользователю диалоговое окно для включения GPS
                    exception.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ошибка открытия диалогового окна
                }
            } else {
                // GPS выключен и его нельзя включить программно
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 44) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()

            }
        }
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
                latLng = LatLng(location.result.latitude, location.result.longitude)
                latLng?.let { latLng ->
                    it.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12F))
                    binding.gpsProgressBar.gone()
                    binding.myGpsImg.setImageResource(R.drawable.gps_btn)
                }
            }
            mapFragment.getMapAsync(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                44
            )
        }
    }

    fun isGpsEnabled(): Boolean {
        val locationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
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

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap) {
        googleMapMain = googleMap
        if(!isFirstLocation){
            val regionLatLng = LatLng(43.057222, 46.915278)

            val zoomLevel = 8f
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(regionLatLng, zoomLevel))
            isFirstLocation = !isFirstLocation
        }
        viewModel.maps()
        if (view != null) {
            viewModel.maps.observe(viewLifecycleOwner) {
                for (i in it.geocodes) {
                    val position = LatLng(i.lat.toDouble(), i.lng.toDouble())
                    Log.d(
                        "position2",
                        "latitude = ${i.lat.toDouble()}, longitude = ${i.lng.toDouble()}"
                    )
                    var marker: Marker? = null
                    latLng?.let { latLng ->
                        val iconGenerator = IconGenerator(context)
                        val result = FloatArray(1)
                        Location.distanceBetween(
                            latLng.latitude,
                            latLng.longitude,
                            position.latitude,
                            position.longitude,
                            result
                        )
                        val inflatedView =
                            View.inflate(context, R.layout.custom_pointer, null)
                        val distance = result[0] / 1000
                        inflatedView.findViewById<TextView>(R.id.distance).text =
                            String.format("%.1f", distance).replace(",", ".")
                        iconGenerator.setContentView(inflatedView)
                        iconGenerator.setBackground(null)
                        marker = googleMap.addMarker(
                            MarkerOptions().position(position).icon(
                                BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
                            ).snippet(String.format("%.1f", distance) + " " + i.id)
                        )
                    } ?: run {
                        val iconGenerator = IconGenerator(context)
                        val inflatedView =
                            View.inflate(context, R.layout.custom_point_map_redes, null)
                        iconGenerator.setContentView(inflatedView)
                        iconGenerator.setBackground(null)
                        marker = googleMap.addMarker(
                            MarkerOptions().position(position).icon(
                                BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
                            ).snippet(String.format("%.1f", distance) + " " + i.id)
                        )
                    }
                    marker?.let {
                        viewModel.getPlaygroundForMap(marker?.snippet!!.split(" ")[1].toInt())
                        googleMap.setOnMarkerClickListener { marker ->
                            viewModel.getPlayground(marker.snippet!!.split(" ")[1])
                            viewModel.getPlaygroundForMap(marker.snippet!!.split(" ")[1].toInt())
                            var binding: BottomSheetDialogBinding =
                                BottomSheetDialogBinding.inflate(
                                    LayoutInflater.from(context)
                                )
                            val bottomSheetDialog = BottomSheetDialog(requireContext())
                            bottomSheetDialog.setContentView(binding.root)
//                    bottomSheetDialog.findViewById<TextView>(R.id.bottom_sheet_distance)!!.text =
//                        "Расстояние: ${marker.snippet!!.split(" ")[0]} км"
                            viewModel.playgroundForMap.observe(viewLifecycleOwner) { playground ->
                                binding.priceContentScroll.text = "от ${playground.price} ₽"
                                if (playground.ratingsCount == 0) {
                                    binding.ratingBarToolBar.inVisible()
                                    binding.nullStartsImg.visible()
                                } else {
                                    binding.ratingBarToolBar.visible()
                                    binding.nullStartsImg.inVisible()
                                    binding.ratingBarToolBar.rating =
                                        playground.ratingsCount.toFloat()
                                }
                                binding.titleReviewsCount.text = playground.ratingsCount.toString()
                                binding.nameField.text =
                                    playground.title
                                binding.botttomSheetAddress.text =
                                    playground.address

                                if (playground.photos.isNotEmpty()) {
                                    binding.eventPhoto.isVisible =
                                        true
                                    Glide.with(bottomSheetDialog.context)
                                        .load(playground.photos[0].url)
                                        .into(binding.eventPhoto)
                                } else {
                                    binding.eventPhoto.isVisible =
                                        false
                                }

                            }
                            if (marker.position != latLng) {
                                bottomSheetDialog.show()
                            }
//                    bottomSheetDialog.findViewById<ImageView>(R.id.route_intent)!!
//                        .setOnClickListener {
//                            val intent = Intent(
//                                Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=${marker.position.latitude},${marker.position.longitude}")
//                            )
//                            startActivity(intent)
//                        }
                            bottomSheetDialog.findViewById<ConstraintLayout>(R.id.item_bottom_sheet)!!
                                .setOnClickListener {
                                    Log.d(
                                        "position1",
                                        "latitude = ${marker.position.latitude}, longitude = ${marker.position.longitude}"
                                    )
                                    bottomSheetDialog.dismiss()
                                    findNavController().navigate(
                                        MapsFragmentDirections.actionMapsFragmentToPlaygInfoRedesignFrag(
                                            marker.position, marker.snippet!!.split(" ")[1]
                                        )
                                    )
                                }
                            false
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val REQUEST_CHECK_SETTINGS = 100
    }
}