package com.example.mambappv2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mambappv2.data.sync.SyncManager
import com.example.mambappv2.di.AppContainer
import com.example.mambappv2.workers.SyncWorker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncConfigScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val syncManager = remember { AppContainer.getInstance(context).syncManager }
    
    var spreadsheetId by remember { mutableStateOf("") }
    var isConfiguring by remember { mutableStateOf(false) }
    var configurationResult by remember { mutableStateOf<String?>(null) }
    var autoSyncEnabled by remember { mutableStateOf(false) }
    
    val syncStatus by syncManager.syncStatus.collectAsState()
    val lastSyncTime by syncManager.lastSyncTime.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = "Configuración de Sincronización",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Información general
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Sincronización con Google Sheets",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Conecta tu archivo Excel/Google Sheets para mantener tus datos sincronizados entre la app y tu PC.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Estado actual
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Estado Actual",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (syncStatus) {
                        is SyncManager.SyncStatus.Idle -> {
                            Icon(Icons.Default.Circle, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                            Text("En espera")
                        }
                        is SyncManager.SyncStatus.InProgress -> {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            Text("Sincronizando...")
                        }
                        is SyncManager.SyncStatus.Success -> {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("Última sincronización exitosa")
                        }
                        is SyncManager.SyncStatus.Error -> {
                            Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Text("Error: ${syncStatus.message}")
                        }
                    }
                }
                
                if (lastSyncTime > 0) {
                    Text(
                        text = "Última sincronización: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(lastSyncTime))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Configuración de Google Sheets
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Configurar Google Sheets",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                OutlinedTextField(
                    value = spreadsheetId,
                    onValueChange = { spreadsheetId = it },
                    label = { Text("ID de Google Sheets") },
                    placeholder = { Text("1ABC123...") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isConfiguring,
                    supportingText = {
                        Text("Encuentra el ID en la URL de tu Google Sheets")
                    }
                )
                
                Button(
                    onClick = {
                        scope.launch {
                            isConfiguring = true
                            configurationResult = null
                            try {
                                syncManager.setupSync(spreadsheetId)
                                configurationResult = "✅ Configuración exitosa"
                            } catch (e: Exception) {
                                configurationResult = "❌ Error: ${e.message}"
                            } finally {
                                isConfiguring = false
                            }
                        }
                    },
                    enabled = spreadsheetId.isNotBlank() && !isConfiguring,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isConfiguring) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Configurar Sincronización")
                }
                
                configurationResult?.let { result ->
                    Text(
                        text = result,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (result.startsWith("✅")) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
        
        // Opciones de sincronización
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Opciones de Sincronización",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sincronización Automática")
                        Text(
                            "Sincronizar cada 30 minutos en segundo plano",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = autoSyncEnabled,
                        onCheckedChange = { enabled ->
                            autoSyncEnabled = enabled
                            if (enabled) {
                                SyncWorker.schedulePeriodicSync(context)
                            } else {
                                SyncWorker.cancelAllSyncWork(context)
                            }
                        }
                    )
                }
                
                Divider()
                
                // Botones de sincronización manual
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                syncManager.performQuickSync()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = syncStatus !is SyncManager.SyncStatus.InProgress
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sync Rápido")
                    }
                    
                    Button(
                        onClick = {
                            scope.launch {
                                syncManager.performFullSync()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = syncStatus !is SyncManager.SyncStatus.InProgress
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sync Completo")
                    }
                }
            }
        }
        
        // Instrucciones
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Cómo configurar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                val instructions = listOf(
                    "1. Sube tu archivo Excel a Google Drive",
                    "2. Abre el archivo en Google Sheets",
                    "3. Copia el ID de la URL (entre /d/ y /edit)",
                    "4. Pega el ID arriba y configura la sincronización",
                    "5. ¡Listo! Tus datos se mantendrán sincronizados"
                )
                
                instructions.forEach { instruction ->
                    Text(
                        text = instruction,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
} 