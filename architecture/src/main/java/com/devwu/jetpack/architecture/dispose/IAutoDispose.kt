package com.devwu.jetpack.architecture.dispose

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * Author:    WuNan
 * Created:   2019/7/23 15:54
 * Description:
 **/
interface IAutoDispose {
  /**
   * Kotlin 语法🍬。
   *
   * 在 Kotlin 中，属性 val/var 是对变量 field 的封装，凡是使用 val/var 声明的属性 xxx，均会生成对应的 setXXX（仅 var ）/getXXX 函数。
   * 因此，在接口中声明属性 val （或在抽象类中声明抽象属性 val）时，仅会生成其对应的 getter 函数，并不存在实际的变量 field。
   *
   * 意图1. 提供统一的函数签名，凡是实现/继承 [IAutoDispose] 的类/子接口，均可访问 [compositeDisposable] 属性。
   * 意图2. 基于 Kotlin 的抽象属性语法🍬，有利于在实现类中使用 Kotlin 的 by 关键字（即代理模式）完成实例变量的初始化过程。
   *   例如, 在实现类中使用 by lazy { xxx } 进行延迟初始化，详见 [com.devwu.jetpack.architecture.BaseViewModel.compositeDisposable]
   */
  val compositeDisposable: CompositeDisposable //语法🍬。效果等同于 fun compositeDisposable(): CompositeDisposable

  /**
   * Kotlin 语法🍬。
   *
   * 意图1. 为 Disposable 添加扩展函数，封装 compositeDisposable.add(disposable) 的行为。
   *    将原调用方式由外部包裹式 compositeDisposable.add(disposable) 展开，改为链式调用 disposable.autoClean()，简化外部使用成本。
   * 意图2. 在 [IAutoDispose] 接口内声明的扩展函数，仅在 [IAutoDispose] 的子接口或实现类中可见，借此「约束」了 autoClean() 函数的可见性，避免全局污染。
   */
  fun Disposable.autoClean() {
    compositeDisposable.add(this)
  }

  /**
   * 便捷函数，封装 [compositeDisposable.dispose()] 行为。
   *
   * 意图1. 更便捷的取消 [compositeDisposable] 集合中的全部任务。
   * 意图2. 简化外部调用，使用场景见 [com.devwu.jetpack.architecture.BaseViewModel.compositeDisposable]
   */
  fun disposeAll() {
    compositeDisposable.dispose()
  }
}