package com.example.theperfectionist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AccountScrn(navController: NavController) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppThemeState.backgroundColor)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ✅ FIXED BACK BUTTON
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AppThemeState.textColor
                    )
                }

                Text(
                    text = "Account",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AppThemeState.textColor
                )
            }

            Spacer(modifier = Modifier.height(70.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppThemeState.cardColor
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    AccountRow(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Change Password",
                                tint = AppThemeState.purpleColor
                            )
                        },
                        title = "Change Password",
                        subtitle = "Update your 6-digit PIN",
                        onClick = {
                            navController.navigate("change_password")
                        }
                    )

                    Divider(color = AppThemeState.subTextColor.copy(alpha = 0.25f))

                    AccountRow(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Dark Mode",
                                tint = AppThemeState.purpleColor
                            )
                        },
                        title = "Dark Mode",
                        subtitle = "Switch the app appearance",
                        trailing = {
                            Switch(
                                checked = AppThemeState.darkMode,
                                onCheckedChange = {
                                    AppThemeState.setDarkMode(context, it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF009688),
                                    checkedTrackColor = Color(0xFFEADCF8),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color(0xFFEADCF8)
                                )
                            )
                        },
                        onClick = {
                            AppThemeState.setDarkMode(
                                context,
                                !AppThemeState.darkMode
                            )
                        }
                    )

                    Divider(color = AppThemeState.subTextColor.copy(alpha = 0.25f))

                    AccountRow(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "User Manual",
                                tint = AppThemeState.purpleColor
                            )
                        },
                        title = "User Manual",
                        subtitle = "Read how to use the app",
                        onClick = {
                            navController.navigate("user_manual")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AccountRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier.width(40.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = AppThemeState.textColor
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = AppThemeState.subTextColor
            )
        }

        if (trailing != null) {
            trailing()
        }
    }
}