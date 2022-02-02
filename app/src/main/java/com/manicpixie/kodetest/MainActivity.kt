package com.manicpixie.kodetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.manicpixie.kodetest.domain.model.User
import com.manicpixie.kodetest.presentation.main.MainScreen
import com.manicpixie.kodetest.presentation.profile.ProfileScreen
import com.manicpixie.kodetest.presentation.util.Screen
import com.manicpixie.kodetest.ui.theme.KODETestTheme
import dagger.hilt.android.AndroidEntryPoint



@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KODETestTheme {


                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.MainScreen.route
                    ) {
                        composable(route = Screen.MainScreen.route) {
                            MainScreen(navController = navController)
                        }
                        composable(route = Screen.ProfileScreen.route, arguments = listOf(
                            navArgument("user") {
                                type = UserNavType
                            }
                        )) {

                            val userObject = it.arguments?.getParcelable<User>("user")

                            ProfileScreen(
                                navController = navController,
                                user = userObject!!
                            )
                        }
                    }
                }
            }
    }
     val UserNavType: NavType<User> = object : NavType<User>(false) {
        override val name: String
            get() = "uniqueUser"

        override fun get(bundle: Bundle, key: String): User? {
            return bundle.getParcelable(key)
        }

        override fun parseValue(value: String): User {
            return Gson().fromJson(value, object : TypeToken<User>() {}.type)
        }

        override fun put(bundle: Bundle, key: String, value: User) {
            bundle.putParcelable(key, value)
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KODETestTheme {
        Greeting("Android")
    }

}