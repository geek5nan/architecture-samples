package com.devwu.jetpack.architecture

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devwu.jetpack.architecture.datafetch.IDataFetchHelper
import com.devwu.jetpack.architecture.dispose.IAutoDispose
import com.devwu.jetpack.architecture.hint.Hint
import com.devwu.jetpack.architecture.hint.IntHint
import com.devwu.jetpack.architecture.hint.StringHint
import com.devwu.jetpack.architecture.validate.IDataValidateRule
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * Created by WuNan at 2020/4/29 12:52 PM
 *
 *
 * contact: geek5nan@gmail.com
 */
abstract class BaseViewModel : ViewModel(), IAutoDispose, IDataValidateRule, IDataFetchHelper {

  /**
   * [QueryModel] 查询模型，提供给外部订阅。
   *
   * 意图1. 维护可供外部订阅的 LiveData 事件
   * 意图2. 使用 Kotlin 抽象属性语法糖🍬，使得子类实现时，可使用 lazy 方式延迟创建，详见 [IAutoDispose.compositeDisposable] 中的描述。
   */
  abstract val queryModel: QueryModel

  /**
   * [CommandModel] 命令模型，提供给外部调用。
   *
   * 意图1. V/VC 中触发的事件，由 [commandModel] 代为触发
   * 意图2. 使用 Kotlin 抽象属性语法糖🍬，使得子类实现时，可使用 lazy 方式延迟创建，详见 [IAutoDispose.compositeDisposable] 中的描述。
   */
  abstract val commandModel: CommandModel


  /**
   * [DataFetcher] 数据获取器，仅供 VM 内部访问。
   *
   * 意图1. 封装数据请求过程，无论数据的来源是 Retrofit 提供的，还是本地数据库 Room 提供的，均在此处处理，对上层使用方「透明」。
   * Ref：「透明」概念可参考计算机组成原理中的相关定义。
   */
  protected open val dataFetcher: DataFetcher? = null

  /**
   * [IAutoDispose] Implementations
   *   BaseViewModel 负责维护 CompositeDisposable 。Activity、Fragment 将使用 ViewModel 中创建的 CompositeDisposable 实例对象，避免重复创建。
   */
  override val compositeDisposable by lazy { CompositeDisposable() }

  /**
   * ViewModel 销毁时，释放 compositeDisposable
   */
  override fun onCleared() {
    disposeAll()
  }

  override fun onDataFetchBegin() {
    commandModel.emitDataLoading(true)
  }

  override fun onDataFetchEnd() {
    commandModel.emitDataLoading(false)
  }


  /**
   * 查询模型 负责数据的获取，采用观察者模式，外部调用方提前订阅此模型提供的事件源。当有新的事件发生时，查询模型将依次通知所有当订阅者。
   */
  inner class QueryModel {
    val goNextEvent: LiveData<Any?> by lazy { commandModel.goNextEvent }
    val goBackEvent: LiveData<Unit> by lazy { commandModel.goBackEvent }
    val dataLoadingEvent: LiveData<Boolean> by lazy { commandModel.dataLoadingEvent }
    val hintEvent: LiveData<Hint> by lazy { commandModel.hintEvent }
  }

  /**
   * 命令模型，负责接受外部发送当命令，封装命令的实际行为，当用户命令完成时，异步通知 QueryModel ，从而通知到命令发送方
   */
  inner class CommandModel {
    val goNextEvent = MutableLiveData<Any?>()
    val goBackEvent = MutableLiveData<Unit>()
    val dataLoadingEvent = MutableLiveData<Boolean>()
    val hintEvent = MutableLiveData<Hint>()

    fun emitGoNext(params: Any? = null) {
      goNextEvent.postValue(params)
    }

    fun emitGoBack() {
      goBackEvent.postValue(Unit)
    }

    fun emitDataLoading(isLoading: Boolean = false) {
      dataLoadingEvent.postValue(isLoading)
    }

    fun emitHintEvent(msg: String) {
      hintEvent.postValue(StringHint(msg))
    }

    fun emitHintEvent(@StringRes msgId: Int) {
      hintEvent.postValue(IntHint(msgId))
    }

  }


  /**
   * 数据获取内部类, 封装数据获取行为。
   *
   * 意图1. 避免将数据获取与用户交互等行为及函数混乱的放置到 ViewModel 中，提升代码的可读性及可维护性。
   * 意图2. 减少潜在的文件拆分成本，避免当 ViewModel 代码膨胀时，维护性差的问题。
   * 意图3. 通过受保护的访问修饰符，隐藏其内部实现，同时避免在 ViewModel 类的外部直接获取 [DataFetcher] 及实例化，
   */
  protected inner class DataFetcher
}