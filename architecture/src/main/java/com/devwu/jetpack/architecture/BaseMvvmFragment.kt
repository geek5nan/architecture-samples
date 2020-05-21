package com.devwu.jetpack.architecture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.devwu.jetpack.architecture.dispose.IAutoDispose
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * Created by WuNan at 2020/5/17 10:12 PM
 *
 * contact: geek5nan@gmail.com
 */
abstract class BaseMvvmFragment<V : ViewDataBinding, VM : BaseViewModel> : BaseFragment(), IAutoDispose {

  abstract val vm: VM

  lateinit var binding: V

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    setupDataBinding(inflater, container)
    return binding.root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    initView()
  }

  private fun setupDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
    binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
    binding.setVariable(BR.vm, vm)
    binding.lifecycleOwner = this
  }

  abstract fun initView()

  override val compositeDisposable: CompositeDisposable by lazy { vm.compositeDisposable }
}