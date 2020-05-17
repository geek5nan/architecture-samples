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

package com.example.android.architecture.blueprints.todoapp.addedittask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devwu.jetpack.architecture.BaseViewModel
import com.devwu.jetpack.architecture.validate.IDataValidateRule
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.ADD_EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.tasks.EDIT_RESULT_OK
import kotlinx.coroutines.launch

/**
 * ViewModel for the Add/Edit screen.
 */
class AddEditTaskViewModel(
    private val tasksRepository: TasksRepository
) : BaseViewModel(), IDataValidateRule {

  // Two-way databinding, exposing MutableLiveData
  val title = MutableLiveData<String>()

  // Two-way databinding, exposing MutableLiveData
  val description = MutableLiveData<String>()

  private val _taskUpdatedEvent = MutableLiveData<Event<Int>>()
  val taskUpdatedEvent: LiveData<Event<Int>> = _taskUpdatedEvent

  private var taskId: String? = null

  private var isNewTask: Boolean = false

  private var isDataLoaded = false

  private var taskCompleted = false



  fun start(taskId: String?) {


    this.taskId = taskId
    if (taskId == null) {
      // No need to populate, it's a new task
      isNewTask = true
      return
    }
    if (isDataLoaded) {
      // No need to populate, already have data.
      return
    }

    isNewTask = false
    commandModel.emitDataLoading(true)

    viewModelScope.launch {
      tasksRepository.getTask(taskId).let { result ->
        if (result is Success) {
          onTaskLoaded(result.data)
        } else {
          onDataNotAvailable()
        }
      }
    }
  }

  private fun onTaskLoaded(task: Task) {
    title.value = task.title
    description.value = task.description
    taskCompleted = task.isCompleted
    commandModel.emitDataLoading(false)
    isDataLoaded = true
  }

  private fun onDataNotAvailable() {
    commandModel.emitDataLoading(false)
  }


  override fun verifyData(isShowHint: Boolean): Boolean {
    if (title.value.isNullOrEmpty() || description.value.isNullOrEmpty()) {
      if (isShowHint) commandModel.emitHintEvent(R.string.empty_task_message)
      return false
    }
    return true
  }

  // Called when clicking on fab.
  fun saveTask() {
    val currentTitle = title.value
    val currentDescription = description.value
    if (!verifyData(true)) {
      return
    }
    val currentTaskId = taskId
    if (isNewTask || currentTaskId == null) {
      createTask(Task(currentTitle!!, currentDescription!!))
    } else {
      val task = Task(currentTitle!!, currentDescription!!, taskCompleted, currentTaskId)
      updateTask(task)
    }
  }

  private fun createTask(newTask: Task) = viewModelScope.launch {
    tasksRepository.saveTask(newTask)
    _taskUpdatedEvent.value = Event(ADD_EDIT_RESULT_OK)
//    commandModel.emitGoNext(ADD_EDIT_RESULT_OK)
  }

  private fun updateTask(task: Task) {
    if (isNewTask) {
      throw RuntimeException("updateTask() was called but task is new.")
    }
    viewModelScope.launch {
      tasksRepository.saveTask(task)
//      commandModel.emitGoNext(EDIT_RESULT_OK)
      _taskUpdatedEvent.value = Event(EDIT_RESULT_OK)
    }
  }

  override val queryModel =  QueryModel()
  override val commandModel=  CommandModel()
}
