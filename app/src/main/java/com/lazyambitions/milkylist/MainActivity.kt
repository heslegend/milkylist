package com.lazyambitions.milkylist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.lazyambitions.milkylist.ui.main.LoginFragment
import com.lazyambitions.milkylist.ui.main.LoginViewModel
import com.lazyambitions.milkylist.ui.main.MainFragment
import javax.security.auth.callback.Callback


//TODO make DataServiceObject Mutable Live Data
//TODO implement Data Binding
//TODO make options menu work


class MainActivity : AppCompatActivity(), LoginFragment.LoginActivityCallback, MainFragment.MainActivityCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commitNow()
        }

    }

    // Callback Method
    override fun openList(){
        Log.d("Blubb", "openList() called")
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitNow()
    }

    // Callback Method
    override fun openLogin(){
        Log.d("Blubb", "openLogin() called")
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, LoginFragment.newInstance())
            .commitNow()
    }

}
