package com.klmn.slapp.ui

import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.klmn.slapp.R
import com.klmn.slapp.common.hideKeyboard
import com.klmn.slapp.data.datastore.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    @Inject lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userPreferences.uid.observe(this, ::println)

        lifecycleScope.launch {
            userPreferences.saveUID("Michael")
        }

        navController = findNavController(R.id.fragment_container_view)
        navController.addOnDestinationChangedListener { _, _, _ -> hideKeyboard() }

        if (ContextCompat.checkSelfPermission(this, READ_CONTACTS) != PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != 1)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Toast.makeText(this,
            "permission granted: ${grantResults[0] == PERMISSION_GRANTED}",
            Toast.LENGTH_SHORT).show()
    }

    /*
    * NEXT:
    *   implement the foundation for integrating user operations:
    *       keep track of user in fragments, show only lists that the user is a part of,
    *           pass the user id when creating lists & items
    *       implement the 'users' tab in ListFragment
    *       check how to access phone contacts and display names from phone numbers
    *       consider using unit tests, as this shit is complicated to test manually by yourself
    *   FIREBASE
    *   Some last features
    *   Move on with your life
    * */

    override fun onSupportNavigateUp() =
        navController.navigateUp() || super.onSupportNavigateUp()
}