package com.gls.reportagls.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.gls.reportagls.ui.admin.AdminScreen
import com.gls.reportagls.ui.detail.ReportDetailScreen
import com.gls.reportagls.ui.home.HomeScreen
import com.gls.reportagls.ui.login.LoginScreen
import com.gls.reportagls.ui.myreports.MyReportsScreen
import com.gls.reportagls.ui.report.ReportFormScreen
import com.gls.reportagls.viewmodel.ReportaGlsViewModel

private sealed interface AppScreen {
    data object Main : AppScreen
    data object CreateReport : AppScreen
    data class Detail(val reportId: String) : AppScreen
}

private data class NavItem(val label: String, val icon: ImageVector)

@Composable
fun ReportaGlsApp(vm: ReportaGlsViewModel) {
    val state by vm.uiState.collectAsState()

    if (state.currentUser == null) {
        LoginScreen(vm = vm)
        return
    }

    var screen by remember { mutableStateOf<AppScreen>(AppScreen.Main) }
    var tab by rememberSaveable { mutableStateOf(0) }
    val isAdmin = state.currentUser?.isAdmin == true

    BackHandler(enabled = screen != AppScreen.Main) {
        screen = AppScreen.Main
    }

    when (val current = screen) {
        AppScreen.Main -> MainScaffold(
            vm = vm,
            isAdmin = isAdmin,
            tab = tab,
            onSelectTab = { tab = it },
            onOpenReport = { screen = AppScreen.Detail(it) },
            onCreateReport = { screen = AppScreen.CreateReport }
        )

        AppScreen.CreateReport -> ReportFormScreen(
            vm = vm,
            onBack = { screen = AppScreen.Main },
            onSaved = { screen = AppScreen.Main }
        )

        is AppScreen.Detail -> {
            val report = state.reports.firstOrNull { it.id == current.reportId }
            if (report == null) {
                LaunchedEffect(current.reportId) { screen = AppScreen.Main }
            } else {
                ReportDetailScreen(
                    report = report,
                    isAdmin = isAdmin,
                    onBack = { screen = AppScreen.Main },
                    onStatusChange = { vm.updateStatus(report.id, it) }
                )
            }
        }
    }
}

@Composable
private fun MainScaffold(
    vm: ReportaGlsViewModel,
    isAdmin: Boolean,
    tab: Int,
    onSelectTab: (Int) -> Unit,
    onOpenReport: (String) -> Unit,
    onCreateReport: () -> Unit
) {
    val items = buildList {
        add(NavItem("Inicio", Icons.Filled.Home))
        add(NavItem("Mis reportes", Icons.Filled.List))
        if (isAdmin) add(NavItem("Admin", Icons.Filled.Settings))
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateReport) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo reporte")
            }
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = { onSelectTab(index) },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (tab) {
                0 -> HomeScreen(vm = vm, onOpenReport = onOpenReport)
                1 -> MyReportsScreen(vm = vm, onOpenReport = onOpenReport)
                else -> if (isAdmin) {
                    AdminScreen(vm = vm, onOpenReport = onOpenReport)
                } else {
                    HomeScreen(vm = vm, onOpenReport = onOpenReport)
                }
            }
        }
    }
}
