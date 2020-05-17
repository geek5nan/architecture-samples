package com.devwu.jetpack.architecture

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devwu.jetpack.architecture.loading.DefaultDataLoadingIndicator
import com.devwu.jetpack.architecture.loading.IDataLoadingIndicator

/**
 * Created by WuNan at 2020/4/29 1:06 PM
 *
 * contact: wunan@ybm100.com
 *
 * Activity 基类，作为最基础的 VC ，仅维护 Activity 语义下必要的、通用的函数及行为。
 * 与 Activity 关联较弱的功能及行为，放置在其他接口中实现。
 *   例如：用于管理加载动画的 [IDataLoadingIndicator]
 */
abstract class BaseActivity : AppCompatActivity() {

  private val defaultDataLoadingIndicator by lazy { DefaultDataLoadingIndicator(this) }

  /**
   * 数据加载指示器 [IDataLoadingIndicator] ，若需使用不同的指示器，可通过参照 [DefaultDataLoadingIndicator] 新建实现类完成。
   */
  protected open val dataLoadingIndicator: IDataLoadingIndicator? = null
    get() = field ?: defaultDataLoadingIndicator


  /**
   * 工厂模式：创建型模式，将 layoutId 的获取延迟到子类中，子类必须实现此函数的行为。
   * @see <https://www.runoob.com/design-pattern/factory-pattern.html>
   *
   * 获取当前 layout 的布局ID
   *
   * 意图1. 抽象函数，提供统一的函数签名，强制要求子类必须实现。
   * 意图2. 不在 [BaseActivity] 中消费 layoutId，而是由具体的 [BaseMvvmActivity] 或潜在的 BaseMvpActivity 进行消费，详见 [BaseMvvmActivity.setupBinding]。
   * @return layout Id
   */
  protected abstract val layoutId: Int

  /**
   * 模版模式：行为型模式，为子类提供统一的函数签名，可选实现。
   * @see <https://www.runoob.com/design-pattern/template-pattern.html>
   *
   * 意图💡： 若子 Activity 需要从 Intent 获取数据时，可重载此函数，以此将不同实现类的相似行为固定到 [fetchIntentData] 同名函数中。
   *
   * 注意⚠️:
   *   1. [fetchIntentData] 将在 [BaseActivity.onCreate] 函数中调用，请勿在此函数那进行额外的计算，以免影响 Activity 的启动速度。
   *   2. [fetchIntentData]
   */
  protected open fun fetchIntentData() {}


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    fetchIntentData()
  }

}