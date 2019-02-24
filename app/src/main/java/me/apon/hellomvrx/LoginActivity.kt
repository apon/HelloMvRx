package me.apon.hellomvrx

import android.os.Bundle
import com.airbnb.mvrx.BaseMvRxActivity

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/2/23.
 */
class LoginActivity : BaseMvRxActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}