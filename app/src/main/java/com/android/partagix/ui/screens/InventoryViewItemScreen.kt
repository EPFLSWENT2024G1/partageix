package com.android.partagix.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.border
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.partagix.model.BorrowViewModel
import com.android.partagix.model.ItemViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.LabeledText
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.utils.stripTime
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Screen to view an item.
 *
 * @param navigationActions a NavigationActions instance to navigate between screens.
 * @param itemViewModel an ItemViewModel which handles functionality.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryViewItemScreen(
    navigationActions: NavigationActions,
    itemViewModel: ItemViewModel,
    borrowViewModel: BorrowViewModel,
    viewOthersItem: Boolean = false
) {

  val uiState = itemViewModel.uiState.collectAsState()
  var isCalendarVisible by remember { mutableStateOf(false) }
  val unavailableDates = uiState.value.unavailableDates
  val datePickerState =
      DatePickerState(
        locale = Locale.getDefault(),
        selectableDates = object : SelectableDates {
          override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val currentDate = stripTime(Date(utcTimeMillis))
            return unavailableDates.none { stripTime(it) == currentDate }
          }

          override fun isSelectableYear(year: Int): Boolean {
            return true
          }
        }
      )

  var actualUser = FirebaseAuth.getInstance().currentUser?.uid ?: ""
  var item = uiState.value.item
  val user = uiState.value.user

  var imgBitmap: Bitmap? = null
  val imgFile = item.imageId
  if (imgFile.exists()) {
    // on below line we are creating an image bitmap variable
    // and adding a bitmap to it from image file.
    imgBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
  }

  LaunchedEffect(key1 = uiState) { item = itemViewModel.uiState.value.item }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Back to selection") },
            modifier = Modifier.fillMaxWidth().testTag("inventoryViewItemTopBar"),
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("navigationIcon")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null)
                  }
            })
      },
      bottomBar = {
        BottomNavigationBar(
            modifier = Modifier.testTag("inventoryViewItemBottomBar"),
            selectedDestination = "Inventory",
            navigateToTopLevelDestination = { dest -> navigationActions.navigateTo(dest) })
      },
      modifier = Modifier.fillMaxWidth().testTag("inventoryViewItem")) {
        Column(
            modifier = Modifier.padding(it).fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Box(modifier = Modifier.fillMaxWidth().height(140.dp).padding(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                  Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()) {
                    AsyncImage(
                        model = item.imageId.absolutePath,
                        contentDescription = "fds",
                        contentScale = ContentScale.FillWidth,
                        modifier =
                            Modifier.fillMaxWidth(0.3f)
                                .border(1.dp, MaterialTheme.colorScheme.onBackground),
                        alignment = Alignment.Center)
                  }
                  Spacer(modifier = Modifier.width(8.dp))

                  Column {
                    LabeledText(
                        label = "Object Name",
                        text = item.name,
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f))

                    LabeledText(
                        label = "Author",
                        text = user.name,
                        modifier = Modifier.fillMaxWidth().fillMaxHeight())
                  }
                }
              }
              Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                LabeledText(label = "Description", text = item.description)

                Spacer(modifier = Modifier.height(8.dp))
                if (!viewOthersItem) {
                  LabeledText(label = "Category", text = item.category.name)

                  Spacer(modifier = Modifier.height(8.dp))

                  LabeledText(label = "Visibility", text = item.visibility.visibilityLabel)

                  Spacer(modifier = Modifier.height(8.dp))
                }

                LabeledText(label = "Quantity", text = item.quantity.toString())

                Spacer(modifier = Modifier.height(8.dp))

                LabeledText(
                    label = "Where",
                    text = item.location.extras?.getString("display_name", "Unknown") ?: "Unknown")

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.padding(8.dp)) {
                  Column {
                    Text(
                        text = "Availability",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                          Text(
                              text = "available", /*TODO: get item disponibility*/
                              style = MaterialTheme.typography.bodyMedium,
                              color = MaterialTheme.colorScheme.onBackground,
                              modifier = Modifier.padding(6.dp, 0.dp, 0.dp, 0.dp),
                          )

                          IconButton(
                              onClick = { isCalendarVisible = true },

                              content = {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                              })
                        }
                  }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                  Button(
                      onClick = { navigationActions.navigateTo("${Route.STAMP}/${item.id}") },
                      colors =
                          ButtonColors(
                              containerColor = MaterialTheme.colorScheme.onPrimary,
                              contentColor = MaterialTheme.colorScheme.onBackground,
                              disabledContentColor = MaterialTheme.colorScheme.onBackground,
                              disabledContainerColor = Color.Gray),
                      content = {
                        Text(
                            "Download QR code",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                      },
                      modifier = Modifier.fillMaxWidth(0.5f))

                  Spacer(modifier = Modifier.width(8.dp))
                  Button(
                      enabled = actualUser != user.id && actualUser != "" && user.id != "",
                      onClick = {
                        borrowViewModel.startBorrow(item, user)
                        navigationActions.navigateTo(Route.BORROW)
                      },
                      colors =
                          ButtonColors(
                              containerColor = MaterialTheme.colorScheme.onPrimary,
                              contentColor = MaterialTheme.colorScheme.onBackground,
                              disabledContentColor = MaterialTheme.colorScheme.onBackground,
                              disabledContainerColor = Color.Gray),
                      content = {
                        Text(
                            "Borrow item",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                      },
                      modifier = Modifier.fillMaxWidth())
                }

                Spacer(modifier = Modifier.width(8.dp))

                // This should be displayed only if the user is the owner of the item
                if (itemViewModel.compareIDs(
                    item.idUser, FirebaseAuth.getInstance().currentUser?.uid)) {
                  Button(
                      onClick = { navigationActions.navigateTo(Route.EDIT_ITEM) },
                      content = { Text("Edit") },
                      colors =
                          ButtonColors(
                              containerColor = MaterialTheme.colorScheme.onPrimary,
                              contentColor = MaterialTheme.colorScheme.onBackground,
                              disabledContentColor = MaterialTheme.colorScheme.onBackground,
                              disabledContainerColor = Color.Gray),
                      modifier = Modifier.fillMaxWidth().testTag("editItemButton"))
                }
              }
            }
    if (isCalendarVisible) {
      DatePickerDialog(
        modifier = Modifier.testTag("endDatePicker"),
        onDismissRequest = { isCalendarVisible = false },
        confirmButton = {
          TextButton(
            modifier = Modifier.testTag("endDateOk"),
            onClick = {

            }) {
            Text("OK")
          }
        },
        dismissButton = {
          TextButton(
            modifier = Modifier.testTag("endDateCancel"),
            onClick = { isCalendarVisible = false }) {
            Text("Cancel")
          }
        }) {
        DatePicker(datePickerState)
      }
    }

  }
}

