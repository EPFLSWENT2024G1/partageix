package com.android.partagix.model

import android.location.Location
import androidx.lifecycle.ViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.visibility.Visibility
import java.util.concurrent.CountDownLatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FinishedLoansViewModel(db: Database = Database(), latch: CountDownLatch = CountDownLatch(1)) :
    ViewModel() {
  private val database = db

  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(FinishedUIState(emptyList()))
  val uiState: StateFlow<FinishedUIState> = _uiState
  private val _uiItem =
      MutableStateFlow(
          Item(
              "",
              Category("GpWpDVqb1ep8gm2rb1WL", "Others"),
              "",
              "",
              Visibility.PRIVATE,
              0,
              Location("")))
  val uiItem: StateFlow<Item> = _uiItem

  init {
    getFinishedLoan(latch)
  }

  fun getFinishedLoan(latch: CountDownLatch = CountDownLatch(1)) {
    val user = Authentication.getUser()

    if (user == null) {
      _uiState.value = _uiState.value.copy(loans = emptyList())
      // TODO: Handle error
      latch.countDown()
      return
    } else {
      database.getLoans {
        _uiState.value = _uiState.value.copy(loans = emptyList())
        it.filter { loan ->
              loan.state == LoanState.FINISHED &&
                  (loan.idLender == user.uid || loan.idBorrower == user.uid)
            }
            .forEach { loan -> updateLoans(loan) }

        latch.countDown()
      }
    }
  }

  fun getItem(itemId: String) {
    database.getItem(itemId) { _uiItem.value = it }
  }

  private fun updateLoans(new: Loan) {
    _uiState.value = _uiState.value.copy(loans = _uiState.value.loans.plus(new))
  }
}

data class FinishedUIState(val loans: List<Loan>)
