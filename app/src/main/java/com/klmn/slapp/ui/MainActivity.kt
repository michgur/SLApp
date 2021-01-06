package com.klmn.slapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.klmn.slapp.R
import com.klmn.slapp.common.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.fragment_container_view)
        navController.addOnDestinationChangedListener { _, _, _ -> hideKeyboard() }
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