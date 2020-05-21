package com.devwu.jetpack.architecture.datafetch

import com.devwu.jetpack.architecture.dispose.IAutoDispose
import io.reactivex.rxjava3.core.Observable

/**
 * Created by WuNan at 2020/4/29 5:01 PM
 *
 * contact: geek5nan@gmail.com
 */
interface IDataFetchHelper : IAutoDispose, IDataFetchHook {

  /**
   * RxJava 订阅便捷函数, 仅供数据流处理的末端使用，中间的转换过程请使用 RxJava 内置操作符完成。
   *
   * 内置 [onSuccess] 、[onError] 预处理函数
   *   1. 当网络请求成功(200) && 服务端业务正常([BaseResponseBean.code] == 0)时，调用 [successCallback] lambda 表达式进行成功回调
   *   2. 当网络请求成功(200) && 服务端业务异常([BaseResponseBean.code] != 0)时，直接使用 Toast 显示服务端响应中的 [BaseResponseBean.msg] 字段
   *   3. 当网络请求失败时(4xx、5xx)，调用 [onError] 进行异常处理
   * 使用 [autoClean] 函数管理 [io.reactivex.rxjava3.disposables.Disposable] 的释放工作
   *
   *
   * @receiver Observable<T>
   * @param isStrictMode Boolean 是否开启严格模式，默认开启。
   *    若开启，则只在服务端响应为真时，使用 successCallback 进行回调
   *    若未开启，则无论服务度响应内容是否成功，均使用 successCallback 进行回调
   * @param successCallback Function1<T, Unit>  请求成功回调的 Lambda 表达式
   */
  fun <T> Observable<T>.subscribeOnSuccess(isStrictMode: Boolean = true, successCallback: (T) -> Unit) {
    this
      /**
       * 当 [Observable] 被订阅时，触发 [IDataFetchHook.onDataFetchBegin] 函数
       */
      .doOnSubscribe { onDataFetchBegin() }
      /**
       * 当 [Observable] 结束时( onCompleted / onError 均可)，触发 [IDataFetchHook.onDataFetchEnd] 函数
       */
      .doFinally { onDataFetchEnd() }
      .subscribe(
//            onSuccess(isStrictMode) { successCallback(it) },
//            onError()
      ).autoClean()
  }

}