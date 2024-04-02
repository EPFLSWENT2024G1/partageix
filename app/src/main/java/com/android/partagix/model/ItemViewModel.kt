/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.partagix.model

import Category
import Item
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ItemViewModel(item: Item) : ViewModel() {

  private val database = Database()

  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(ItemUIState(item))
  val uiState: StateFlow<ItemUIState> = _uiState

  init {
    update()
  }

  /*  fun getItem(itemId: String) {
    viewModelScope.launch {
      database.getItems {
        for (i in it) {
          if (i.id == itemId) {
            //update(i)
          }
        }
      }
    }
  }*/

  fun updateUiState(new: Item) {
    _uiState.value =
        _uiState.value.copy(
            item = new,
        )
  }

  private fun update() {
    viewModelScope.launch {
      database.getItems {
        for (i in it) {
          if (i.id == _uiState.value.item.id) {
            updateUiState(i)
          }
        }
      }
    }
  }

  fun saveWithUiState() {
    if (_uiState.value.item.id == "") {
      database.createItem(
          "Yp5cetHh3nLGMsjYY4q9" /* todo get the correct userId */, _uiState.value.item)
    } else {
      database.setItem(_uiState.value.item)
    }
  }

  /* fun createItem() {

  viewModelScope.launch {
    val newItem =
        Item(
            "", // no itemId exists at this moment, it will be generated
            // and overwritten by the database
            _uiState.value.item.category,
            _uiState.value.item.name,
            _uiState.value.item.description)

    database.createItem("Yp5cetHh3nLGMsjYY4q9" */
  /* todo get the correct userId */
  /*, newItem)
    }
  }

  fun saveItem() {
    viewModelScope.launch {
        database.setItem(_uiState.value.item) }
  }*/
}

fun testAll() {
  val itemName = "testCreateItem"
  val itemDescription = "testing create"
  val iCategory = Category("0", "idCategory")
  val i = Item("", iCategory, itemName, itemDescription)
  val view = ItemViewModel(i)
  view.saveWithUiState()

  val itName = "testEditItem"
  val itDescription = "testing edit"
  val itCategory = Category("0", "idCategory")
  val it = Item("", iCategory, itemName, itemDescription)
  val vit = ItemViewModel(it)
  vit.saveWithUiState()
  val newName = "Edited"
  val newDescription = "successfully edited"
  val newit = Item("", iCategory, itemName, itemDescription)
  vit.updateUiState(newit)
  vit.saveWithUiState()
}

data class ItemUIState(val item: Item)
