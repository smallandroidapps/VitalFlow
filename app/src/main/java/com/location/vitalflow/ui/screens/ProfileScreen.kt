package com.location.vitalflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.location.vitalflow.data.repository.AuthResult

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val userEmail by viewModel.userEmail.collectAsState()
    val signInState by viewModel.signInState.collectAsState()
    var isSyncing by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(signInState) {
        when (val result = signInState) {
            is AuthResult.Error -> snackbarHostState.showSnackbar(result.message)
            is AuthResult.Success -> snackbarHostState.showSnackbar("Signed in as ${result.displayName}")
            else -> {}
        }
        viewModel.clearSignInState()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile & Backup",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Cloud Backup", style = MaterialTheme.typography.titleMedium)
                    if (userEmail == null) {
                        Text(
                            text = "Backup your wellness data to Google Drive AppData.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.signIn(context) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Sign in with Google")
                        }
                    } else {
                        Text(text = "Signed in as: $userEmail", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { isSyncing = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSyncing
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Text("Sync Now to Drive")
                            }
                        }
                        TextButton(onClick = { viewModel.signOut(); isSyncing = false }) {
                            Text("Sign Out")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Integrations", style = MaterialTheme.typography.titleMedium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Google Health Connect")
                        Switch(checked = false, onCheckedChange = { /* Health Connect Sync */ })
                    }
                }
            }
        }
    }
}
