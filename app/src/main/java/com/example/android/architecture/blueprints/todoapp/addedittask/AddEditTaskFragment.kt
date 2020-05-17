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

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.devwu.jetpack.architecture.BaseMvvmFragment
import com.devwu.jetpack.architecture.hint.IntHint
import com.devwu.jetpack.architecture.hint.StringHint
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.AddtaskFragBinding
import com.example.android.architecture.blueprints.todoapp.util.getViewModelFactory
import com.example.android.architecture.blueprints.todoapp.util.setupRefreshLayout
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
class AddEditTaskFragment : BaseMvvmFragment<AddtaskFragBinding, AddEditTaskViewModel>() {

  override val layoutId = R.layout.addtask_frag

  private val args: AddEditTaskFragmentArgs by navArgs()

  override val vm: AddEditTaskViewModel by viewModels { getViewModelFactory() }

  private fun setupSnackbar() {
    vm.queryModel.hintEvent.observe(viewLifecycleOwner, Observer {
      val msg = when (it) {
        is StringHint -> it.message
        is IntHint -> getString(it.messageId)
      }
      Snackbar.make(view!!, msg, Snackbar.LENGTH_LONG).show()
    })
  }

  private fun setupNavigation() {
    vm.taskUpdatedEvent.observe(this, EventObserver {
      val action = AddEditTaskFragmentDirections
          .actionAddEditTaskFragmentToTasksFragment(it)
      findNavController().navigate(action)
      vm.commandModel.emitGoNext("zzz")
    })
    vm.commandModel.emitGoNext("xxx")
    vm.queryModel.goNextEvent.observe(this, Observer {
      Timber.e("dddd"+it.toString())
//      val action = AddEditTaskFragmentDirections
//          .actionAddEditTaskFragmentToTasksFragment(it as Int)
//      findNavController().navigate(action)
    })
    vm.commandModel.emitGoNext("yyyy")
  }

  override fun initView() {
    setupSnackbar()
    setupNavigation()
    this.setupRefreshLayout(binding.refreshLayout)
    vm.start(args.taskId)
  }
}
