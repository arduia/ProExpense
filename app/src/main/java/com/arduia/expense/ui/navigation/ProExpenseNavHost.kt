package com.arduia.expense.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun ProExpenseNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Any = SplashRoute,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<SplashRoute> {
            // TODO: SplashScreen(navController = navController)
        }

        composable<OnboardingRoute> {
            // TODO: OnboardingScreen(navController = navController)
        }

        composable<HomeRoute> {
            // TODO: HomeScreen(navController = navController)
        }

        composable<ExpenseEntryRoute> {
            // TODO: ExpenseEntryScreen(navController = navController)
        }

        composable<ExpenseLogRoute> {
            // TODO: ExpenseLogScreen(navController = navController)
        }

        composable<StatisticsRoute> {
            // TODO: StatisticsScreen(navController = navController)
        }

        composable<SettingsRoute> {
            // TODO: SettingsScreen(navController = navController)
        }

        composable<BackupRoute> {
            // TODO: BackupScreen(navController = navController)
        }

        composable<FeedbackRoute> {
            // TODO: FeedbackScreen(navController = navController)
        }

        composable<AboutRoute> {
            // TODO: AboutScreen(navController = navController)
        }

        composable<WebRoute> {
            // TODO: WebScreen(navController = navController)
        }
    }
}
