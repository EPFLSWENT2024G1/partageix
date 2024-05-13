package com.android.partagix.login

import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.registerForActivityResult
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.MainActivity
import com.android.partagix.model.Database
import com.android.partagix.model.auth.Authentication
import com.android.partagix.screens.LoginScreen
import com.android.partagix.screens.LoginScreen2
import com.android.partagix.ui.App
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.LoginScreen
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseUser
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest {
  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var authentication: Authentication
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun setup() {
    authentication = mockk<Authentication>()
    mockNavActions = mockk<NavigationActions>()

    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs
    every { authentication.isAlreadySignedIn() } returns true

    composeTestRule.setContent { LoginScreen(authentication) }
  }

  @Test
  fun basicDisplayed() {
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      loginTitle { assertIsDisplayed() }

      popUpLoginButton {
        assertIsDisplayed()
        performClick()
      }
    }
    ComposeScreen.onComposeScreen<LoginScreen2>(composeTestRule) {
      loginButton { assertIsDisplayed() }
    }
  }

  @Test
  fun testOnSignInSuccess() {

    val mockMainActivity = mockk<MainActivity>()
    val mockPackageManager = mockk<PackageManager>()
    val mockAuthentication = mockk<Authentication>()
    val mockDatabase = mockk<Database>()
    val mockActivityResultLauncher = mockk<ActivityResultLauncher<Intent>>()
    val mockFirebaseUser = mockk<FirebaseUser>()

    every {
      mockMainActivity.registerForActivityResult(any<FirebaseAuthUIActivityResultContract>(), any())
    } returns mockActivityResultLauncher

    every { mockMainActivity.applicationContext } returns mockMainActivity
    every { mockMainActivity.attributionTag } returns "tag"
    every { mockPackageManager.hasSystemFeature(any()) } returns true
    every { mockPackageManager.getPackageInfo(any<String>(), any<Int>()) } returns null
    every { mockMainActivity.packageManager } returns mockPackageManager
    every { mockMainActivity.packageName } returns "com.android.partagix"

    every { mockDatabase.getItemsWithImages(any()) } just Runs
    every { mockDatabase.getUserInventory(any(), any()) } just Runs
    every { mockDatabase.getLoans(any()) } just Runs
    every { mockDatabase.getUser(any(), any(), any()) } just Runs
    every { mockDatabase.createUser(any()) } just Runs

    every { mockFirebaseUser.uid } returns "1234"
    every { mockFirebaseUser.displayName } returns "name"
    every { mockFirebaseUser.email } returns "email"

    val app = App(mockMainActivity, mockAuthentication, mockDatabase)

    app.onSignInSuccess(mockFirebaseUser)

    coVerify { mockDatabase.getUser("1234", any(), any()) }
  }

  companion object {
    const val SLEEP_TIME = 2000L
  }
}
