/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import im.point.dotty.R
import im.point.dotty.common.NavActivity
import im.point.dotty.common.ViewModelFactory

class LoginActivity : NavActivity<LoginViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        with(supportFragmentManager) {
            if (backStackEntryCount == 0) {
                beginTransaction()
                        .replace(R.id.fragment_container, LoginFragment.netInstance()).commit()
            }
        }
    }

    override fun provideViewModel(): LoginViewModel =
            ViewModelProvider(this, ViewModelFactory(this))
                    .get(LoginViewModel::class.java)

    companion object {
        fun getIntent(context: Context?): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}