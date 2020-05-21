package com.devwu.jetpack.architecture.hint

import androidx.annotation.StringRes

/**
 * Created by WuNan at 2020/5/17 10:28 PM
 *
 * contact: geek5nan@gmail.com
 */
sealed class Hint
data class StringHint(val message: String) : Hint()
data class IntHint(@StringRes val messageId: Int) : Hint()
