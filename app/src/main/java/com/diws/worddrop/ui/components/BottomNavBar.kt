package com.diws.worddrop.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.diws.worddrop.ui.theme.DarkSurface
import com.diws.worddrop.ui.theme.DarkSurfaceHigh
import com.diws.worddrop.ui.theme.PrimaryPurple
import com.diws.worddrop.ui.theme.TextPrimary
import com.diws.worddrop.ui.theme.TextSecondary

sealed class NavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : NavItem("home", "Home", Icons.Rounded.Home)
    object Vocabulary : NavItem("vocabulary", "Vocabulary", Icons.Rounded.Book)
    object Progress : NavItem("progress", "Progress", Icons.Rounded.BarChart)
    object Settings : NavItem("settings", "Settings", Icons.Rounded.Settings)
}

val bottomNavItems = listOf(
    NavItem.Home,
    NavItem.Vocabulary,
    NavItem.Progress,
    NavItem.Settings
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        contentColor = TextPrimary
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DarkSurface,
                    selectedTextColor = PrimaryPurple,
                    indicatorColor = PrimaryPurple,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}
