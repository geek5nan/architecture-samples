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

import android.view.Gravity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions.navigateTo
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.util.DataBindingIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.monitorActivity
import com.example.android.architecture.blueprints.todoapp.util.saveTaskBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the [DrawerLayout] layout component in [TasksActivity] which manages
 * navigation within the app.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {

    private lateinit var tasksRepository: TasksRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        //初始化虚假数据仓库
        tasksRepository = ServiceLocator.provideTasksRepository(getApplicationContext())
    }

    @After
    fun reset() {
        // 重置数据仓库
        ServiceLocator.resetRepository()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        //注册 Espresso 资源
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        //注销 Espresso 资源
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun drawerNavigationFromTasksToStatistics() {
        // start up Tasks screen
        // 开启任务列表 Activity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        // 断言抽屉布局处于关闭状态后打开抽屉栏
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.
            .perform(open()) // Open Drawer

        // Start statistics screen.
        onView(withId(R.id.nav_view))
            .perform(navigateTo(R.id.statistics_fragment_dest))

        // Check that statistics screen was opened.
        // 断言统计 Fragment 已正常显示
        onView(withId(R.id.statistics_layout)).check(matches(isDisplayed()))

        // 断言抽屉栏关闭后打开抽屉栏
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.
            .perform(open()) // Open Drawer

        // Start tasks screen.
        // 开启任务列表 Fragment
        onView(withId(R.id.nav_view))
            .perform(navigateTo(R.id.tasks_fragment_dest))

        // Check that tasks screen was opened.
        // 断言任务列表 layout 已正常显示
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun tasksScreen_clickOnAndroidHomeIcon_OpensNavigation() {
        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Check that left drawer is closed at startup
        // 断言抽屉栏处于关闭状态
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.

        // Open Drawer
        // 获取导航按钮并点击
        onView(
            // 通过内容描述字符串获取 Matcher 对象
            withContentDescription(
                // 获取 Toolbar 导航内容描述字符串
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check if drawer is open
        // 断言抽屉栏已打开
        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen(Gravity.START))) // Left drawer is open open.
    }

    @Test
    fun statsScreen_clickOnAndroidHomeIcon_OpensNavigation() {
        // start up Tasks screen
        // 开启任务列表 Activity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // When the user navigates to the stats screen
        // 跳转至统计 Fragment
        activityScenario.onActivity {
            it.findNavController(R.id.nav_host_fragment).navigate(R.id.statistics_fragment_dest)
        }

        // Then check that left drawer is closed at startup
        // 断言抽屉栏处于关闭状态
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.

        // When the drawer is opened
        // 通过 toolbar 上的导航按钮打开抽屉栏
        onView(
            withContentDescription(
//                activityScenrio
////                    .getToolbarNavigationContentDescription()a
                    R.string.nav_app_bar_open_drawer_description
            )
        ).perform(click())

        // Then check that the drawer is open
        // 断言抽屉栏已打开
        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen(Gravity.START))) // Left drawer is open open.
    }

    @Test
    fun taskDetailScreen_doubleUIBackButton() {
        // 向数据仓库新增 Task 对象
        val task = Task("UI <- button", "Description")
        tasksRepository.saveTaskBlocking(task)

        // start up Tasks screen
        // 开启并监控任务列表 Activity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        // 通过字符串找到 Task 对象并点击
        onView(withText("UI <- button")).perform(click())
        // Click on the edit task button
        // 通过 id 找到 fab 按钮并点击
        onView(withId(R.id.edit_task_fab)).perform(click())

        // Confirm that if we click "<-" once, we end up back at the task details page
        // 点击 toolbar 返回按钮
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())
        // 断言当前处于任务详情页面
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))

        // 点击返回按钮
        // Confirm that if we click "<-" a second time, we end up back at the home screen
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())
        // 断言当前处于任务列表页面
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun taskDetailScreen_doubleBackButton() {
        // 向数据仓库新增 Task 对象
        val task = Task("Back button", "Description")
        tasksRepository.saveTaskBlocking(task)

        // start up Tasks screen
        // 开启 任务列表 Activity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        // 通过文字找到 View 并点击
        onView(withText("Back button")).perform(click())
        // Click on the edit task button
        // 点击 fab 按钮
        onView(withId(R.id.edit_task_fab)).perform(click())

        // Confirm that if we click back once, we end up back at the task details page
        // 触发 back 按钮
        pressBack()
        // 断言当前处于任务详情页面
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))

        // Confirm that if we click back a second time, we end up back at the home screen
        // 触发 back 事件，并断言当前处于任务列表页面
        pressBack()
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))
    }
}
