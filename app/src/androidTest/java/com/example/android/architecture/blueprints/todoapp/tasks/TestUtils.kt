/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.tasks

import android.app.Activity
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import com.example.android.architecture.blueprints.todoapp.R

/**
 * 获取当前 Activity 下 toolbar 中导航按钮对应的内容描述字符串
 * 通常来讲，toolbar 导航按钮的内容描述对应以下字符串
 * <string name="nav_app_bar_navigate_up_description">Navigate up</string> 表示返回按钮
 * <string name="nav_app_bar_open_drawer_description">Open navigation drawer</string> 表示打开抽屉栏按钮
 **/
fun <T : Activity> ActivityScenario<T>.getToolbarNavigationContentDescription()
    : String {
    var description = ""
    onActivity {
        description =
            it.findViewById<Toolbar>(R.id.toolbar).navigationContentDescription as String
    }
    return description
}
