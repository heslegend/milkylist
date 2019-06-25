package com.lazyambitions.milkylist.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lazyambitions.milkylist.R
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

import java.util.*

class LoginFragment : Fragment() {

    var activityCallback: LoginActivityCallback? = null
    val MY_REQUEST_CODE: Int = 190
    private lateinit var viewModel: LoginViewModel
    private lateinit var providers: List<AuthUI.IdpConfig>


    interface LoginActivityCallback{
        fun openList()
    }

    companion object {
        fun newInstance() = LoginFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        providers = Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        showSignInOptions()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==MY_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(activity, "${user!!.email}", Toast.LENGTH_SHORT).show()
                activityCallback?.openList()
            } else {
                Toast.makeText(activity, "${response!!.error}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSignInOptions() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.AppTheme)
                .build(), MY_REQUEST_CODE)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activityCallback = context as LoginActivityCallback
    }

    override fun onDetach() {
        super.onDetach()
        activityCallback = null
    }
}


