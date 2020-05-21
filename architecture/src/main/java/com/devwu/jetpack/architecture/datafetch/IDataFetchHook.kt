package com.devwu.jetpack.architecture.datafetch

/**
 * Created by WuNan at 2020/5/6 4:26 PM
 *
 *
 * contact: geek5nan@gmail.com
 */
interface IDataFetchHook {
  /**
   * 数据获取触发时执行的钩子函数
   */
  fun onDataFetchBegin()

  /**
   * 数据获取结束时执行的钩子函数
   */
  fun onDataFetchEnd()
}