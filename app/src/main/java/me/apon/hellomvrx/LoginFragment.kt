package me.apon.hellomvrx

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.*
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.concurrent.TimeUnit

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/2/23.
 */

data class Account(val name: String, val password: String)
//定义async LoginRequest
data class LoginState(val LoginRequest: Async<Account> = Uninitialized) : MvRxState

class LoginViewModel(init: LoginState) : BaseMvRxViewModel<LoginState>(init) {
    fun login(name: String, password: String) {
        Single.fromCallable {
            if (name.isEmpty() || password.isEmpty()) {
                throw Throwable("empty name or password")
            }
        }
        .map { Account(name, password) } //模拟网络请求
        .delay(10, TimeUnit.SECONDS)//将时间改60秒，调用login后，退出登录页，会看到doOnDispose被调用，
                //也就是说ViewModel会在Lifecycel结束时自动丢弃(dispose)subscription,因此不用手动dispose，不用担心内存泄漏问题
        .doOnDispose {
            println("--------doOnDispose---------")
        }
        .execute { copy(LoginRequest = it) }//异步请求需做两件事：1.调用execute，自动subscribe2.更新state
    }

    override fun onCleared() {
        super.onCleared()
        println("--------onCleared---------")
    }
}

class LoginFragment : BaseMvRxFragment() {

    private val viewModel by fragmentViewModel(LoginViewModel::class)

    private lateinit var dialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginBT.setOnClickListener { login() }

        val builder = AlertDialog.Builder(activity!!)
        builder.setView(R.layout.progress)
        dialog = builder.create()
    }

    private fun login() {
        viewModel.login(userNameET.text.toString(), passwordET.text.toString())
    }

    override fun invalidate() {
        //获取state，根据async状态显示loading
        withState(viewModel) {
            when (it.LoginRequest) {
                is Loading -> {
                    dialog.show()
                }
                is Success -> {
                    var account = it.LoginRequest.invoke()
                    Toast.makeText(activity, "Login Success ${account.name}:${account.password}", Toast.LENGTH_LONG)
                        .show()
                    dialog.dismiss()
                    activity!!.finish()
                }
                is Fail -> {
                    var message = it.LoginRequest.error.message
                    Toast.makeText(activity, "error: $message", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
            }
        }
    }

}