package com.devwu.jetpack.architecture.validate

/**
 * Created by WuNan at 2020/5/6 4:12 PM
 *
 * contact: wunan@ybm100.com
 */
interface IDataValidateRule {
  /**
   * 表单数据校验，若子类有数据校验的行为，可通过重载此函数完成
   *
   * 意图: 「模版方法模式」，提供统一的函数签名
   *
   * @param isShowHint Boolean 数据校验失败时，是否显示错误提示信息。默认为 false，即不展示错误提示信息
   * @return Boolean 数据校验结果，默认为 true
   */
  fun verifyData(isShowHint: Boolean = false) = true
}