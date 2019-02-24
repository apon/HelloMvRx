package me.apon.hellomvrx

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.*
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/2/23.
 */


//定义State/继承MvRxState
data class MainState(@PersistState val name: String = "apon", val age: Int = 18) : MvRxState
//定义ViewModel/继承BaseMvRxViewModel
class MainViewModel(init: MainState) : BaseMvRxViewModel<MainState>(init,true) {

    init {
        logStateChanges()
    }

    fun showName(name: String) {
        //获取state
        withState {
            println("name: ${it.name}")
            //运行在后台线程中
            println("viewModel-Thread: ${Thread.currentThread().name}")
        }
        //更新state
        setState { copy(name = name) }
    }

    fun showAge(age: Int) {
        withState {
            println("age: ${it.age}")
            println("viewModel-Thread: ${Thread.currentThread().name}")
        }
        setState { copy(age = age) }
    }
}
//继承BaseMvRxFragment
class MainFragment : BaseMvRxFragment() {
    //获取ViewModel
    private val viewModel by fragmentViewModel(MainViewModel::class)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //调用viewModel的方法
        buttonShowName.setOnClickListener { viewModel.showName("yaopeng") }
        buttonShowAge.setOnClickListener { viewModel.showAge(28) }
        buttonNextCase.setOnClickListener { startActivity(Intent(activity, LoginActivity::class.java)) }
    }
    //实现MvRxView接口
    override fun invalidate() {
        //获取state
        withState(viewModel) {
            ageText.text = it.age.toString()
            nameText.text = it.name
            println("view-Thread: ${Thread.currentThread().name}")
        }
    }

}