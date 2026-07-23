package com.example.lost_foundmanagementsystem.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lost_foundmanagementsystem.data.AppContainer
import com.example.lost_foundmanagementsystem.ui.auth.LoginScreen
import com.example.lost_foundmanagementsystem.ui.auth.RegisterScreen
import com.example.lost_foundmanagementsystem.ui.claim.ClaimVerificationScreen
import com.example.lost_foundmanagementsystem.ui.claim.StaffClaimsScreen
import com.example.lost_foundmanagementsystem.ui.home.HomeScreen
import com.example.lost_foundmanagementsystem.ui.items.ItemDetailScreen
import com.example.lost_foundmanagementsystem.ui.items.ItemsScreen
import com.example.lost_foundmanagementsystem.ui.profile.ProfileScreen
import com.example.lost_foundmanagementsystem.ui.report.ReportFoundScreen
import com.example.lost_foundmanagementsystem.ui.report.ReportLostScreen
import com.example.lost_foundmanagementsystem.ui.viewmodel.AppViewModel
import com.example.lost_foundmanagementsystem.ui.viewmodel.AppViewModelFactory

@Composable
fun LostFoundApp(
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = viewModel(
        factory = AppViewModelFactory(AppContainer.repository)
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.currentUser) {
        val user = uiState.currentUser
        val current = navController.currentDestination?.route
        if (user == null && current != Routes.LOGIN && current != Routes.REGISTER) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        } else if (user != null && (current == Routes.LOGIN || current == Routes.REGISTER || current == null)) {
            navController.navigate(Routes.HOME) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (uiState.currentUser == null) Routes.LOGIN else Routes.HOME
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                error = uiState.error,
                message = uiState.message,
                onClearFeedback = viewModel::clearFeedback,
                onLogin = { studNumber, password ->
                    viewModel.login(
                        studNumber,
                        password
                    ) { success ->
                        // Nothing to do here.
                        // Navigation is handled automatically by LaunchedEffect.
                    }
                },
                onGoRegister = {
                    viewModel.clearFeedback()
                    navController.navigate(Routes.REGISTER)
                }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                error = uiState.error,
                message = uiState.message,
                onClearFeedback = viewModel::clearFeedback,
                onRegister = {
                        studNumber,
                        firstName,
                        lastName,
                        password,
                        confirmPassword,
                        asStaff ->

                    viewModel.register(
                        studNumber,
                        firstName,
                        lastName,
                        password,
                        confirmPassword,
                        asStaff
                    ) { success ->

                        if (success) {
                            navController.popBackStack()
                        }

                    }
                },
                onBack = {
                    viewModel.clearFeedback()
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.HOME) {
            val user = uiState.currentUser
            if (user != null) {
                HomeScreen(
                    user = user,
                    message = uiState.message,
                    error = uiState.error,
                    onClearFeedback = viewModel::clearFeedback,
                    onReportLost = {
                        viewModel.clearFeedback()
                        navController.navigate(Routes.REPORT_LOST)
                    },
                    onReportFound = {
                        viewModel.clearFeedback()
                        navController.navigate(Routes.REPORT_FOUND)
                    },
                    onBrowse = {
                        viewModel.clearFeedback()
                        navController.navigate(Routes.ITEMS)
                    },
                    onClaims = {
                        viewModel.clearFeedback()
                        navController.navigate(Routes.CLAIMS)
                    },
                    onProfile = {
                        viewModel.clearFeedback()
                        navController.navigate(Routes.PROFILE)
                    }
                )
            }
        }
        composable(Routes.REPORT_LOST) {
            ReportLostScreen(
                error = uiState.error,
                message = uiState.message,
                onClearFeedback = viewModel::clearFeedback,
                onSubmit = viewModel::reportLost,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.REPORT_FOUND) {
            ReportFoundScreen(
                error = uiState.error,
                message = uiState.message,
                onClearFeedback = viewModel::clearFeedback,
                onSubmit = viewModel::reportFound,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
        composable(Routes.ITEMS) {
            ItemsScreen(
                lostItems = uiState.filteredLostItems,
                foundItems = uiState.filteredFoundItems,
                searchQuery = uiState.searchQuery,
                onSearchChange = viewModel::setSearchQuery,
                onOpenLost = { id ->
                    navController.navigate(Routes.itemDetail(Routes.TYPE_LOST, id))
                },
                onOpenFound = { id ->
                    navController.navigate(Routes.itemDetail(Routes.TYPE_FOUND, id))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.ITEM_DETAIL,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("id") { type = NavType.IntType }
            )
        ) { entry ->
            val type = entry.arguments?.getString("type").orEmpty()
            val id = entry.arguments?.getInt("id") ?: -1
            ItemDetailScreen(
                type = type,
                lostItem = if (type == Routes.TYPE_LOST) viewModel.getLostItem(id) else null,
                foundItem = if (type == Routes.TYPE_FOUND) viewModel.getFoundItem(id) else null,
                canClaim = uiState.currentUser != null,
                onClaim = {
                    viewModel.clearFeedback()
                    navController.navigate(Routes.claimVerify(id))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.CLAIM_VERIFY,
            arguments = listOf(navArgument("foundItemId") { type = NavType.IntType })
        ) { entry ->
            val foundItemId = entry.arguments?.getInt("foundItemId") ?: -1
            ClaimVerificationScreen(
                foundItem = viewModel.getFoundItem(foundItemId),
                error = uiState.error,
                message = uiState.message,
                onClearFeedback = viewModel::clearFeedback,
                onSubmit = { location, date ->
                    viewModel.submitClaim(foundItemId, location, date)
                },
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.popBackStack(Routes.ITEMS, inclusive = false)
                }
            )
        }
        composable(Routes.CLAIMS) {
            StaffClaimsScreen(
                claims = uiState.claims,
                foundItems = uiState.foundItems,
                error = uiState.error,
                message = uiState.message,
                onClearFeedback = viewModel::clearFeedback,
                onApprove = { viewModel.resolveClaim(it, approve = true) },
                onReject = { viewModel.resolveClaim(it, approve = false) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.PROFILE) {
            val user = uiState.currentUser
            if (user != null) {
                ProfileScreen(
                    user = user,
                    onLogout = {
                        viewModel.logout()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
