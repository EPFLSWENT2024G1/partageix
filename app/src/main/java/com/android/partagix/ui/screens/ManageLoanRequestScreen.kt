package com.android.partagix.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.ItemListColumn
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageLoanRequest(
    manageLoanViewModel: ManageLoanViewModel,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier
) {
  val uiState by manageLoanViewModel.uiState.collectAsStateWithLifecycle()
  Scaffold(
      modifier = modifier,
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("homeScreenTopAppBar"),
            title = { Text(text = "Partagix") },
        )
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.HOME,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = modifier.testTag("manageScreenBottomNavBar"))
      }) { innerPadding ->
        if (uiState.items.isEmpty()) {
          Column(modifier = modifier.fillMaxSize().padding(innerPadding)) {
            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            Box(
                modifier =
                    modifier.padding(innerPadding).fillMaxSize().testTag("manageScreenNoItemBox")) {
                  Text(
                      text = "There is no loan request.",
                      modifier = modifier.align(Alignment.Center).testTag("manageScreenNoItemText"))
                }
          }
        } else {
          Column(
              verticalArrangement = Arrangement.Center,
              modifier =
                  modifier.fillMaxSize().padding(innerPadding).testTag("manageScreenMainContent")) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color(0xffcac4d0))

                ItemListColumn(
                    list = uiState.items,
                    users = uiState.users,
                    loan = uiState.loans,
                    title = "Borrowing requests",
                    corner = uiState.items.size.toString(),
                    isCornerClickable = false,
                    expandable = true,
                    expanded = uiState.expanded,
                    onClick = { /* isnt usefull for this column */},
                    onClickCorner = { /* isnt usefull for this column */},
                    manageLoanViewModel = manageLoanViewModel,
                    modifier = Modifier.testTag("manageLoanScreenItemListColumn"))
              }
        }
      }
}
