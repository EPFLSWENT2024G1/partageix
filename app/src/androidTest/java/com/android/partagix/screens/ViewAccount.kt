package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ViewAccount(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ViewAccount>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = {
          hasTestTag("viewAccount") // scaffold
        }) {

  val viewAccount: KNode = onNode { hasTestTag("viewAccount") } // scaffold
  val topBar: KNode = child { hasTestTag("topBar") } // topappbar
  val title: KNode = child { hasTestTag("title") } // text
  val backButton: KNode = child { hasTestTag("backButton") } // iconbutton
  val accountScreenBottomNavBar: KNode = child {
    hasTestTag("accountScreenBottomNavBar")
  } // BottomNavigationBar
  val mainContent: KNode = child { hasTestTag("mainContent") } // column
  val userImage: KNode = child { hasTestTag("userImage") } // image
  val username: KNode = child { hasTestTag("username") } // row
  val usernameText: KNode = child { hasTestTag("usernameText") } // text
  val address: KNode = child { hasTestTag("address") } // textfield
  val rating: KNode = child { hasTestTag("rating") } // textfield
  val actionButtons: KNode = child { hasTestTag("actionButtons") } // row
  val inventoryButton: KNode = child { hasTestTag("inventoryButton") } // button
  val editButton: KNode = child { hasTestTag("editButton") } // button
  val friendButton: KNode = child { hasTestTag("friendButton") } // button
}
