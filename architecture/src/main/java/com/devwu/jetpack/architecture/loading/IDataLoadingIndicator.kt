package com.devwu.jetpack.architecture.loading

/**
 * Created by WuNan at 2020/4/29 1:23 PM
 *
 * [IDataLoadingIndicator] 接口内的函数及行为仅关注数据加载动画。
 *
 * contact: geek5nan@gmail.com
 */
interface IDataLoadingIndicator {


  /**
   * 显示加载进度框
   *
   * @param msg 加载进度框中的内容，默认为 null
   */
  fun show(msg: String? = null)

  /**
   * 隐藏加载进度框
   */
  fun dismiss()
}