package com.diws.worddrop.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.diws.worddrop.ui.theme.DarkBackground
import com.diws.worddrop.ui.theme.TextSecondaryDark

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
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val unselectedColor = if (isDark) TextSecondaryDark else Color(0xFF1C1917)

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (selected) MaterialTheme.colorScheme.onPrimary else unselectedColor
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selected) MaterialTheme.colorScheme.primary else unselectedColor,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor
                )
            )
        }
    }
}
