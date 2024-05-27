package com.android.partagix.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.R
import com.android.partagix.model.BorrowViewModel
import com.android.partagix.model.ItemViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.utils.stripTime
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowScreen(
    viewModel: BorrowViewModel,
    modifier: Modifier = Modifier,
    itemViewModel: ItemViewModel,
    navigationActions: NavigationActions
) {
  Scaffold(
      modifier = modifier
          .testTag("borrowScreen")
          .fillMaxWidth(),
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("topBar"),
            title = { Text("Back", modifier = Modifier.testTag("backText")) },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null)
                  }
            })
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.LOAN,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = modifier.testTag("LoanScreenBottomNavBar"))
      }) {
        val loanUiState = viewModel.loanUiState.collectAsStateWithLifecycle()
        val itemUIState = viewModel.itemUiState.collectAsStateWithLifecycle()
        val userUIState = viewModel.userUiState.collectAsStateWithLifecycle()
        val uiState = itemViewModel.uiState.collectAsState()
        val loan = loanUiState.value
        val item = itemUIState.value
        val user = userUIState.value

        var notAvailable = viewModel.itemAvailability.collectAsState().value
        val loanItemName by remember { mutableStateOf(item.name) }
        val loanItemOwnerName by remember { mutableStateOf(user.name) }
        var loanDescription by remember {
          mutableStateOf("")
        } // TODO: edit Loan type to include description
        val loanLocation by remember { mutableStateOf(item.location) }
        val loanQuantity by remember { mutableStateOf(item.quantity) }

        val unavailableDates = uiState.value.unavailableDates

        var isStartDatePickerVisible by remember { mutableStateOf(false) }
        val startDatePickerState =
            DatePickerState(
                locale = Locale.getDefault(),
                selectableDates =
                object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val currentDate = stripTime(Date(utcTimeMillis))
                        return unavailableDates.none { stripTime(it) == currentDate }
                    }

                    override fun isSelectableYear(year: Int): Boolean {
                        return true
                    }
                })
        startDatePickerState.selectedDateMillis = Calendar.getInstance().timeInMillis
        val loanStartDate by remember(loan, loanUiState) { mutableStateOf(loan.startDate) }
        val loanStartDateString by
            remember(loanStartDate) {
              mutableStateOf(DateFormat.getDateInstance().format(loanStartDate))
            }

        var isEndDatePickerVisible by remember { mutableStateOf(false) }
        val endDatePickerState = 
            DatePickerState(
          locale = Locale.getDefault(),
          selectableDates =
          object : SelectableDates {
              override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                  val currentDate = stripTime(Date(utcTimeMillis))
                  return unavailableDates.none { stripTime(it) == currentDate }
              }

              override fun isSelectableYear(year: Int): Boolean {
                  return true
              }
          })
        endDatePickerState.selectedDateMillis = Calendar.getInstance().timeInMillis
        val loanEndDate by remember(loan, loanUiState) { mutableStateOf(loan.endDate) }
        val loanEndDateString by
            remember(loanEndDate) {
              mutableStateOf(DateFormat.getDateInstance().format(loanEndDate))
            }

        Column(
            modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally) {
            
              if (notAvailable) {
                Dialog(onDismissRequest = { viewModel.updateItemAvailability(false) },
                    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
                ) {
                    Surface (shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight(0.2f)
                        .testTag("popup")){
                        Column (horizontalAlignment = Alignment.CenterHorizontally, modifier =
                        modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 0.dp, bottom = 16.dp, top = 0.dp)
                            .background(MaterialTheme.colorScheme.background)){
                            Text(text = "This item is not available for the selected dates",
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.2f).padding(top = 10.dp)
                                    )
                            Button(onClick = {viewModel.updateItemAvailability(false)},
                                colors = ButtonColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimary,
                                    contentColor = MaterialTheme.colorScheme.onBackground,
                                    disabledContentColor = MaterialTheme.colorScheme.onBackground,
                                    disabledContainerColor = Color.Gray),
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                            ){
                                Text(text = "OK",
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    color = MaterialTheme.colorScheme.onBackground,)
                            }
                        }
                    }
                }
              }
              Box(modifier = modifier
                  .fillMaxWidth()
                  .height(140.dp)
                  .padding(8.dp)) {
                Row(modifier = modifier.fillMaxWidth()) {
                  Box(
                      contentAlignment = Alignment.Center,
                      modifier = modifier
                          .fillMaxHeight()
                          .fillMaxWidth(.4f)
                          .testTag("itemImage")) {
                        Image(
                            painter =
                                painterResource(
                                    id =
                                        R.drawable
                                            .ic_launcher_background), // TODO replace with actual
                            // image
                            contentDescription = "Item image",
                            modifier = modifier.fillMaxSize())
                      }

                  Spacer(modifier = modifier.width(8.dp))

                  Column {
                    OutlinedTextField(
                        value = loanItemName,
                        onValueChange = {},
                        label = { Text("Item name") },
                        modifier = modifier
                            .testTag("itemName")
                            .fillMaxWidth(),
                        maxLines = 1, // Ensure only one line is displayed
                        readOnly = true)
                    OutlinedTextField(
                        value = loanItemOwnerName,
                        onValueChange = {},
                        label = { Text("Owner") },
                        modifier = modifier
                            .testTag("itemOwner")
                            .fillMaxWidth(),
                        readOnly = true)
                  }
                }
              }
              Column(
                  modifier
                      .fillMaxWidth()
                      .padding(horizontal = 8.dp)) {
                OutlinedTextField(
                    value = loanDescription,
                    onValueChange = { loanDescription = it },
                    label = { Text("Description") },
                    modifier = modifier
                        .fillMaxWidth()
                        .testTag("description"),
                    minLines = 5,
                    readOnly = true)

                Spacer(modifier = modifier.height(8.dp))

                OutlinedTextField(
                    value = loanLocation.toString(),
                    onValueChange = {},
                    label = { Text("Location") },
                    modifier = modifier
                        .fillMaxWidth()
                        .testTag("location"),
                    readOnly = true)

                Spacer(modifier = modifier.height(8.dp))

                OutlinedTextField(
                    modifier = modifier
                        .fillMaxWidth()
                        .testTag("startDate"),
                    value = loanStartDateString,
                    label = { Text("Start date") },
                    onValueChange = {},
                    readOnly = true,
                    suffix = {
                      IconButton(
                          modifier =
                          Modifier
                              .height(30.dp)
                              .padding(0.dp)
                              .testTag("startDateButton"),
                          onClick = { isStartDatePickerVisible = true },
                          content = { Icon(Icons.Default.DateRange, contentDescription = null) })
                    })

                if (isStartDatePickerVisible) {
                  DatePickerDialog(
                      modifier = Modifier.testTag("startDatePicker"),
                      onDismissRequest = { isStartDatePickerVisible = false },
                      confirmButton = {
                        TextButton(
                            modifier = Modifier.testTag("startDateOk"),
                            onClick = {
                              val selectedDate =
                                  Calendar.getInstance().apply {
                                    timeInMillis = startDatePickerState.selectedDateMillis!!
                                  }
                              viewModel.updateLoan(loan.copy(startDate = selectedDate.time))
                              isStartDatePickerVisible = false
                            }) {
                              Text("OK")
                            }
                      },
                      dismissButton = {
                        TextButton(
                            modifier = Modifier.testTag("startDateCancel"),
                            onClick = { isStartDatePickerVisible = false }) {
                              Text("Cancel")
                            }
                      }) {
                        DatePicker(state = startDatePickerState)
                      }
                }

                Spacer(modifier = modifier.height(8.dp))

                OutlinedTextField(
                    modifier = modifier
                        .fillMaxWidth()
                        .testTag("endDate"),
                    value = loanEndDateString,
                    label = { Text("End date") },
                    onValueChange = {},
                    readOnly = true,
                    suffix = {
                      IconButton(
                          modifier = Modifier
                              .height(30.dp)
                              .padding(0.dp)
                              .testTag("endDateButton"),
                          onClick = { isEndDatePickerVisible = true },
                          content = { Icon(Icons.Default.DateRange, contentDescription = null) })
                    })

                if (isEndDatePickerVisible) {
                  DatePickerDialog(
                      modifier = Modifier.testTag("endDatePicker"),
                      onDismissRequest = { isEndDatePickerVisible = false },
                      confirmButton = {
                        TextButton(
                            modifier = Modifier.testTag("endDateOk"),
                            onClick = {
                              val selectedDate =
                                  Calendar.getInstance().apply {
                                    timeInMillis = endDatePickerState.selectedDateMillis!!
                                  }
                              viewModel.updateLoan(loan.copy(endDate = selectedDate.time))
                              isEndDatePickerVisible = false
                            }) {
                              Text("OK")
                            }
                      },
                      dismissButton = {
                        TextButton(
                            modifier = Modifier.testTag("endDateCancel"),
                            onClick = { isEndDatePickerVisible = false }) {
                              Text("Cancel")
                            }
                      }) {
                        DatePicker(state = endDatePickerState)
                      }
                }
                Spacer(modifier = modifier.height(8.dp))

                OutlinedTextField(
                    value = loanQuantity.toString(),
                    onValueChange = {},
                    label = { Text("Quantity") },
                    modifier = modifier.fillMaxWidth(),
                    readOnly = false)
              }

              Button(
                  modifier = modifier
                      .fillMaxWidth()
                      .testTag("saveButton")
                      .padding(10.dp),
                  colors =
                      ButtonColors(
                          containerColor = MaterialTheme.colorScheme.onPrimary,
                          contentColor = MaterialTheme.colorScheme.onBackground,
                          disabledContentColor = MaterialTheme.colorScheme.onBackground,
                          disabledContainerColor = Color.Gray),
                  onClick = {
                    viewModel.createLoan { navigationActions.navigateTo(Route.LOAN) }
                  },
                  content = { Text(text = "Make request reservation") })
            }
      }
}
