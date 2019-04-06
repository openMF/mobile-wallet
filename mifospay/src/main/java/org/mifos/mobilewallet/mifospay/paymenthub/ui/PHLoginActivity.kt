package org.mifos.mobilewallet.mifospay.paymenthub.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_ph_login.*
import org.json.JSONObject
import org.mifos.mobilewallet.core.data.paymenthub.entity.User
import org.mifos.mobilewallet.core.utils.IOUtils
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.utils.Toaster
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PHLoginActivity: AppCompatActivity() {

    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ph_login)

        val users = JSONObject(IOUtils.loadJSONFromAsset(this,
                "paymenthub_users.json")).getJSONArray("users")

        btn_login.setOnClickListener {

            //TODO this is temp implementation that stores the registered users locally till a
            // login API is implemented on the payment hub
            val userName = et_username.text.toString()

            var registered = false

            for (i in 0..users.length()) {
                if (userName == users.getJSONObject(i).getString("username")) {
                    registered = true
                    val registeredUser = users.getJSONObject(i)

                    val userType = object : TypeToken<User>() {}.type
                    currentUser = Gson().fromJson(registeredUser.toString(), userType)

                    break
                }
            }

            if (registered) {
                startActivity(Intent(this, PaymentHubActivity::class.java))
            } else {
                Toaster.showToast(this, "Error logging in")
            }
        }
    }
}