package com.devwu.jetpack.architecture

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.devwu.jetpack.architecture.dispose.IAutoDispose
import com.devwu.jetpack.architecture.loading.DefaultDataLoadingIndicator
import com.devwu.jetpack.architecture.loading.IDataLoadingIndicator

/**
 * Created by WuNan at 2020/4/29 4:30 PM
 *
 * contact: wunan@ybm100.com
 */
abstract class BaseMvvmActivity<V : ViewDataBinding, VM : BaseViewModel> : BaseActivity(), IAutoDispose {

  /**
   * 严格模式下，binding 的可见性应为 private。
   */
  private lateinit var binding: V

  /**
   * 获取 ViewModel，为具体实现类提供统一的函数签名。
   *
   * 语法糖 🍬，详见 [IAutoDispose.compositeDisposable] 中的描述。
   */
  protected abstract val vm: VM

  /**
   * 初始化视图，为不同的 Activity 实现类提供统一的函数签名，将视图初始化行为函数集合到同名函数中。
   */
  protected abstract fun initView()

  /**
   * 使用 BaseViewModel 提供的 compositeDisposable，避免重复创建
   *
   * 语法糖 🍬，详见 [IAutoDispose.compositeDisposable] 中的描述。
   */
  override val compositeDisposable by lazy { vm.compositeDisposable }

  /**
   * BaseMvvmActivity 创建时，完成初步构建工作。
   * 内涵 DataBinding 关联、视图初始化、LoadingCommand 订阅 等行为。
   */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setupDataBinding()
    initView()
    subscribeLoadingCommand()
  }

  private fun setupDataBinding() {
    /**
     * 根据 [BaseMvvmActivity] 具体实现类的声明的具体类型 [V] 与 [layoutId] 提供的布局 ID，完成具体 [ViewDataBinding] 的创建
     */
    binding = DataBindingUtil.setContentView<V>(this, layoutId)
    /**
     * 将 [BaseMvvmActivity] 具体实现类的实例对象设置为 [binding] 的 [androidx.lifecycle.LifecycleOwner]
     */
    binding.lifecycleOwner = this
    /**
     * ⚠️⚠️⚠️ 注意：layout -> data 中声明 ViewModel 的变量名称必须为 vm ，方可享受自动关联。
     * ⚠️⚠️⚠️ 注意：layout -> data 中声明 ViewModel 的变量名称必须为 vm ，方可享受自动关联。
     * ⚠️⚠️⚠️ 注意：layout -> data 中声明 ViewModel 的变量名称必须为 vm ，方可享受自动关联。
     */
    binding.setVariable(BR.vm, vm)
  }

  /**
   * 在 Activity 基类中订阅及消费 [BaseViewModel.UserInteraction.loadingCommand] 数据加载命令。
   */
  private fun subscribeLoadingCommand() {
    vm.queryModel.goNextEvent.observe(this, Observer {

    })
    vm.queryModel.dataLoadingEvent.observe(this, Observer {
      if (it) {
        dataLoadingIndicator?.show("数据加载中...")
      } else {
        dataLoadingIndicator?.dismiss()
      }
    })
  }

}