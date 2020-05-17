package com.devwu.jetpack.architecture.loading

import android.app.ProgressDialog
import android.content.Context
import com.devwu.jetpack.architecture.BaseActivity
import java.lang.ref.WeakReference

/**
 * Created by WuNan at 2020/5/17 11:10 AM
 *
 * contact: wunan@ybm100.com
 *
 * 默认数据加载指示器.
 */

@Suppress("DEPRECATION")
class DefaultDataLoadingIndicator(context: Context) : IDataLoadingIndicator {

  private val dialog = ProgressDialog(context).also { it.setCancelable(false) }

  /**
   * 显示加载进度框
   *
   * @param msg 加载进度框中的内容，默认为 null
   */
  override fun show(msg: String?) {
    if (dialog.isShowing) {
      dialog.dismiss()
    }
    dialog.setMessage(msg)
    dialog.show()
  }

  /**
   * 隐藏加载进度框
   */
  override fun dismiss() {
    dialog.dismiss()
  }

}
