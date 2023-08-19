package ru.radixit.letsplay.presentation.ui

import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.databinding.ActivityMainBinding
import ru.radixit.letsplay.presentation.global.BaseActivity
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_LetsPlay)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_activity_main
        ) as NavHostFragment
        val navController = navHostFragment.navController
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController)
        if (checkGooglePlayServices()) {
            FirebaseMessaging
                .getInstance()
                .token
                .addOnCompleteListener(OnCompleteListener { task ->

                    if (!task.isSuccessful) {
                        Log.w(TAG, "getInstanceId не удалось ", task.exception)
                        return@OnCompleteListener
                    }

                    Log.d(TAG, task.result)
                    sessionManager.saveFirebaseToken(task.result)
                })

        } else
            Log.w(TAG, "На устройстве нет сервисов Google Play")
        Log.d(TAG, intent.extras.toString())

    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
    }

    private fun checkGooglePlayServices(): Boolean {

        val status = GoogleApiAvailability
            .getInstance()
            .isGooglePlayServicesAvailable(this)

        return if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Пожалуйста обновите Google Play")
            false
        } else {
            Log.i(TAG, "Все ок с Google Play")
            true
        }
    }


    companion object {
        private const val TAG = "MainActivity"
    }
}