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

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.R.string
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.util.DataBindingIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.deleteAllTasksBlocking
import com.example.android.architecture.blueprints.todoapp.util.monitorActivity
import com.example.android.architecture.blueprints.todoapp.util.saveTaskBlocking
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Large End-to-End test for the tasks module.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksActivityTest {

    private lateinit var repository: TasksRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        repository = ServiceLocator.provideTasksRepository(getApplicationContext())
        repository.deleteAllTasksBlocking()
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    // 测试编辑任务功能
    fun editTask() {
        // 向数据仓库新增 Task 对象
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION"))

        // start up Tasks screen
        // 开启 任务列表 Activity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list and verify that all the data is correct
        // 点击标题为 TITLE1 的 TASK
        onView(withText("TITLE1")).perform(click())
        // 断言 TASK 的内容已在任务详情页正常显示
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("TITLE1")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("DESCRIPTION")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

        // Click on the edit button, edit, and save
        // 点击 fab 按钮
        onView(withId(R.id.edit_task_fab)).perform(click())
        // 编辑 TASK 标题
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
        // 标记 TASK 描述
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
        // 点击 fab 按钮 保存修改
        onView(withId(R.id.save_task_fab)).perform(click())
        // 断言 修改后的 TASK 对象内容正常显示
        // Verify task is displayed on screen in the task list.
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        // Verify previous task is not displayed
        onView(withText("TITLE1")).check(doesNotExist())
    }

    @Test
    // 测试删除任务功能
    fun createOneTask_deleteTask() {

        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Add active task
        // 添加任务
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text))
            .perform(typeText("TITLE1"), closeSoftKeyboard())
        onView(withId(R.id.add_task_description_edit_text)).perform(typeText("DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // 删除任务
        // Open it in details view
        onView(withText("TITLE1")).perform(click())
        // Click delete task in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        // 断言任务已删除（在任务列表中不存在 TITLE1 对应的任务）
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
    }

    @Test
    fun createTwoTasks_deleteOneTask() {
        // 添加任务
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION"))
        repository.saveTaskBlocking(Task("TITLE2", "DESCRIPTION"))

        // start up Tasks screen
        // 开启 任务列表 Activity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Open the second task in details view
        // 打开 TITLE2 详情
        onView(withText("TITLE2")).perform(click())
        // Click delete task in menu
        // 删除 TITLE2
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify only one task was deleted
        // 断言 TITLE2 删除成功（任务列表只能显示 TITLE1 不显示 TITLE2）
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(doesNotExist())
    }

    @Test
    fun markTaskAsCompleteOnDetailScreen_taskIsCompleteInList() {
        // Add 1 active task
        // 添加任务
        val taskTitle = "COMPLETED"
        repository.saveTaskBlocking(Task(taskTitle, "DESCRIPTION"))

        // start up Tasks screen
        // 开启 任务列表 Activity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        // 点击 COMPLETED 对应的任务，进入任务详情
        onView(withText(taskTitle)).perform(click())
        // Click on the checkbox in task details screen
        // 点击完成按钮
        onView(withId(R.id.task_detail_complete_checkbox)).perform(click())

        // Click on the navigation up button to go back to the list
        // 点击导航按钮，回到任务列表页面
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check that the task is marked as completed
        // 断言 COMPLETE 标题对应的 TASK 中的 checkbox 已勾选
        onView(allOf(withId(R.id.complete_checkbox), hasSibling(withText(taskTitle))))
            .check(matches(isChecked()))
    }

    @Test
    fun markTaskAsActiveOnDetailScreen_taskIsActiveInList() {
        // Add 1 completed task
        // 添加标题为 ACTIVE 的任务
        val taskTitle = "ACTIVE"
        repository.saveTaskBlocking(Task(taskTitle, "DESCRIPTION", true))

        // start up Tasks screen
        // 开启任务列表 Activity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        // 进入标题为 ACTIVE 任务的详情
        onView(withText(taskTitle)).perform(click())
        // Click on the checkbox in task details screen
        // 勾选完成按钮
        onView(withId(R.id.task_detail_complete_checkbox)).perform(click())

        // Click on the navigation up button to go back to the list
        // 返回任务列表页面
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check that the task is marked as active
        // 断言 ACTIVE 对应任务的 checkbox 未勾选
        onView(allOf(withId(R.id.complete_checkbox), hasSibling(withText(taskTitle))))
            .check(matches(not(isChecked())))
    }

    @Test
    fun markTaskAsCompleteAndActiveOnDetailScreen_taskIsActiveInList() {
        // Add 1 active task
        // 添加任务
        val taskTitle = "ACT-COMP"
        repository.saveTaskBlocking(Task(taskTitle, "DESCRIPTION"))

        // start up Tasks screen
        // 开启任务列表 Activity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        // Click on the task on the list
        // 进入 ACT-COMP 任务详情
        onView(withText(taskTitle)).perform(click())
        // Click on the checkbox in task details screen
        // 勾选
        onView(withId(R.id.task_detail_complete_checkbox)).perform(click())
        // Click again to restore it to original state
        // 取消勾选
        onView(withId(R.id.task_detail_complete_checkbox)).perform(click())

        // Click on the navigation up button to go back to the list
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check that the task is marked as active
        // 断言 未勾选
        onView(allOf(withId(R.id.complete_checkbox), hasSibling(withText(taskTitle))))
            .check(matches(not(isChecked())))
    }

    @Test
    fun markTaskAsActiveAndCompleteOnDetailScreen_taskIsCompleteInList() {
        // Add 1 completed task
        // 添加任务, 初始状态为 已完成
        val taskTitle = "COMP-ACT"
        repository.saveTaskBlocking(Task(taskTitle, "DESCRIPTION", true))

        // start up Tasks screen
        // 开启任务列表 Activity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        // 进入任务详情
        onView(withText(taskTitle)).perform(click())
        // Click on the checkbox in task details screen
        // 勾选  ，状态变为 未勾选
        onView(withId(R.id.task_detail_complete_checkbox)).perform(click())
        // Click again to restore it to original state
        // 反勾选，状态变为 已勾选
        onView(withId(R.id.task_detail_complete_checkbox)).perform(click())

        // Click on the navigation up button to go back to the list
        // 返回任务列表
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())
        // Check that the task is marked as active
        // 断言 COMP-ACT 对应任务的状态为已勾选
        onView(allOf(withId(R.id.complete_checkbox), hasSibling(withText(taskTitle))))
            .check(matches(isChecked()))
    }

    @Test
    fun createTask() {
        // start up Tasks screen
        // 开启任务列表 Activity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the "+" button, add details, and save
        // 点击 fab
        onView(withId(R.id.add_task_fab)).perform(click())
        // 新建 TASK
        onView(withId(R.id.add_task_title_edit_text))
            .perform(typeText("title"), closeSoftKeyboard())
        onView(withId(R.id.add_task_description_edit_text)).perform(typeText("description"))
        onView(withId(R.id.save_task_fab)).perform(click())
        // 断言新建的 TASK 已显示
        // Then verify task is displayed on screen
        onView(withText("title")).check(matches(isDisplayed()))
    }
}
