package com.klmn.slapp.ui

import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.klmn.slapp.R
import com.klmn.slapp.common.hideKeyboard
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.domain.BuyNotification
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.messaging.fcm.MessagingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    /*
    * Responsibilities:
    *   -hosting the fragment container + navController
    *   -determining whether should authenticate & starting PhoneAuthActivity if necessary
    *   -determining whether app has READ_CONTACTS permission & send request if necessary
    *   -validating GooglePlayServices exist for firebase calls
    *   -determining whether should start at viewItemsFragment (notification) or homeFragment
    * */
    @Inject lateinit var userPreferences: UserPreferences

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        validateGooglePlayServices()

        if (Firebase.auth.currentUser == null) goToAuthActivity()
        userPreferences.phoneNumber.observe(this) {
            if (it.isNullOrBlank()) {
                Firebase.auth.signOut()
                goToAuthActivity()
            }
        }

        lifecycleScope.launchWhenStarted {
            userPreferences.saveHasReadContactsPermission(
                ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    READ_CONTACTS
                ) == PERMISSION_GRANTED
            )
        }

        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.fragment_container_view)
        navController.addOnDestinationChangedListener { _, _, _ -> hideKeyboard() }

        intent.getParcelableExtra<BuyNotification>("notification")?.let { notification ->
            navController.navInflater.inflate(R.navigation.slapp_nav).let { graph ->
                graph.startDestination = R.id.viewItemsFragment
                graph.addArgument(
                    "notification",
                    NavArgument.Builder()
                        .setType(NavType.ParcelableType(BuyNotification::class.java))
                        .setDefaultValue(notification)
                        .setIsNullable(false)
                        .build()
                )
                navController.graph = graph
            }
        }
    }

    companion object {
        // request code for PhoneAuthActivity
        const val AUTH_REQUEST_CODE = 123
        // request code for READ_CONTACTS permission
        const val PERMISSION_REQUEST_CODE = 456
    }

    fun requestReadContactsPermission() = ActivityCompat.requestPermissions(
        this,
        arrayOf(READ_CONTACTS),
        PERMISSION_REQUEST_CODE
    )

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

    // PhoneAuthActivity ended
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

    // READ_CONTACTS permission granted
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != PERMISSION_REQUEST_CODE)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        lifecycleScope.launch {
            userPreferences.saveHasReadContactsPermission(
                grantResults[0] == PERMISSION_GRANTED)
        }
    }

    override fun onSupportNavigateUp() =
        navController.navigateUp() || super.onSupportNavigateUp()
}