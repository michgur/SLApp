package com.klmn.slapp.ui

import android.Manifest.permission.READ_CONTACTS
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.klmn.slapp.R
import com.klmn.slapp.common.hideKeyboard
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.messaging.fcm.MessagingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    @Inject lateinit var userPreferences: UserPreferences
    @Inject lateinit var repository: SlappRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        validateGooglePlayServices()

        if (Firebase.auth.currentUser == null) goToAuthActivity()
        else userPreferences.phoneNumber.observe(this) {
            if (it.isNullOrBlank()) {
                Firebase.auth.signOut()
                goToAuthActivity()
            }
        }

        lifecycleScope.launch {
            userPreferences.saveHasReadContactsPermission(
                ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    READ_CONTACTS
                ) == PERMISSION_GRANTED
            )
        }

        // todo:
        //      -firestore optimization
        //      -notifications
        //      cleanup
        //      -splash screen

        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.fragment_container_view)
        navController.addOnDestinationChangedListener { _, _, _ -> hideKeyboard() }

        startService(Intent(this, MessagingService::class.java))

        userPreferences.registrationToken.observe(this) { t ->
            CoroutineScope(Dispatchers.IO).launch {
                userPreferences.phoneNumber.value?.let { repository.refreshToken(t, it) }
            }
        }
    }

    fun requestReadContactsPermission() = ActivityCompat.requestPermissions(
        this,
        arrayOf(READ_CONTACTS),
        PERMISSION_REQUEST_CODE
    )

    companion object {
        const val AUTH_REQUEST_CODE = 123
        const val PERMISSION_REQUEST_CODE = 456
    }
    private fun goToAuthActivity() = startActivityForResult(
        Intent(
            this,
            PhoneAuthActivity::class.java
        ), AUTH_REQUEST_CODE
    )

    private fun validateGooglePlayServices() = GoogleApiAvailability.getInstance().let {
        if (it.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS)
            it.makeGooglePlayServicesAvailable(this)
    }

    override fun onResume() {
        super.onResume()
        validateGooglePlayServices()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != AUTH_REQUEST_CODE)
            super.onActivityResult(requestCode, resultCode, data)
        else {
            if (data?.getBooleanExtra("success", false) != true) finish()
            else if (userPreferences.hasReadContactsPermission.value != true)
                AlertDialog.Builder(this)
                    .setMessage(R.string.permission_dialog_message)
                    .setPositiveButton(R.string.permission_dialog_pos) { dialog, _ ->
                        requestReadContactsPermission()
                        dialog.dismiss()
                    }.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != PERMISSION_REQUEST_CODE)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        lifecycleScope.launch {
            userPreferences.saveHasReadContactsPermission(
                grantResults[0] == PERMISSION_GRANTED
            )
        }
    }

    override fun onSupportNavigateUp() =
        navController.navigateUp() || super.onSupportNavigateUp()
}