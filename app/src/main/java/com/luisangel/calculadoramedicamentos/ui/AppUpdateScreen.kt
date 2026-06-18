package com.luisangel.calculadoramedicamentos.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.luisangel.calculadoramedicamentos.BuildConfig
import com.luisangel.calculadoramedicamentos.update.AppUpdateManager
import com.luisangel.calculadoramedicamentos.update.UpdateCheckResult
import com.luisangel.calculadoramedicamentos.update.UpdateManifest
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppUpdateScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = rememberCoroutineScope()

    val updateManager = remember {
        AppUpdateManager(appContext)
    }

    var checking by remember {
        mutableStateOf(false)
    }
    var downloading by remember {
        mutableStateOf(false)
    }
    var progress by remember {
        mutableIntStateOf(0)
    }
    var statusText by remember {
        mutableStateOf("Presiona buscar para consultar la última versión publicada.")
    }
    var availableUpdate by remember {
        mutableStateOf<UpdateManifest?>(null)
    }
    var downloadedApk by remember {
        mutableStateOf<File?>(null)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .imePadding()
            .padding(18.dp),
        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {
        OutlinedCard(
            colors = CardDefaults.outlinedCardColors(
                containerColor =
                    MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape =
                            MaterialTheme.shapes.medium,
                        color =
                            MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.Download,
                            contentDescription = null,
                            tint =
                                MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(9.dp)
                        )
                    }

                    Column(
                        Modifier.weight(1f)
                    ) {
                        Text(
                            "Actualizaciones",
                            style =
                                MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "Instala nuevas versiones desde la app.",
                            style =
                                MaterialTheme.typography.labelMedium,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider()

                Text(
                    "Versión instalada",
                    style =
                        MaterialTheme.typography.labelSmall,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    updateManager.currentVersionLabel(),
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (
                    BuildConfig.UPDATE_MANIFEST_URL.isBlank()
                ) {
                    Surface(
                        color =
                            MaterialTheme.colorScheme.errorContainer,
                        shape =
                            MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp),
                            verticalAlignment =
                                Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint =
                                    MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                "La ruta de actualización no está configurada. " +
                                    "Compila desde GitHub Actions para que se use automáticamente el repositorio de esta app.",
                                color =
                                    MaterialTheme.colorScheme.onErrorContainer,
                                style =
                                    MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                } else {
                    Text(
                        "Fuente de actualización",
                        style =
                            MaterialTheme.typography.labelSmall,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        BuildConfig.UPDATE_MANIFEST_URL,
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        OutlinedCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Estado",
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )

                Text(
                    statusText,
                    style =
                        MaterialTheme.typography.bodyMedium
                )

                if (checking) {
                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier =
                                Modifier.height(24.dp)
                        )
                        Text("Consultando...")
                    }
                }

                if (downloading) {
                    LinearProgressIndicator(
                        progress = {
                            progress / 100f
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "$progress %",
                        style =
                            MaterialTheme.typography.labelSmall,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                availableUpdate?.let { update ->
                    HorizontalDivider()

                    Text(
                        "Disponible: ${update.versionName} (${update.versionCode})",
                        fontWeight = FontWeight.Bold,
                        color =
                            MaterialTheme.colorScheme.primary
                    )

                    if (update.notes.isNotBlank()) {
                        Text(
                            update.notes,
                            style =
                                MaterialTheme.typography.bodySmall
                        )
                    }
                }

                downloadedApk?.let {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text("APK descargada lista para instalar")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = null
                            )
                        }
                    )
                }

                FlowRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        enabled =
                            !checking && !downloading,
                        onClick = {
                            scope.launch {
                                checking = true
                                downloadedApk = null
                                availableUpdate = null
                                statusText =
                                    "Buscando actualización..."

                                val result =
                                    withContext(Dispatchers.IO) {
                                        updateManager.checkForUpdates()
                                    }

                                checking = false

                                when (result) {
                                    is UpdateCheckResult.Available -> {
                                        availableUpdate =
                                            result.manifest
                                        statusText =
                                            "Hay una actualización disponible."
                                    }

                                    is UpdateCheckResult.UpToDate -> {
                                        availableUpdate = null
                                        statusText =
                                            "Ya tienes la última versión disponible: ${result.manifest.versionName}."
                                    }

                                    UpdateCheckResult.NotConfigured -> {
                                        statusText =
                                            "La actualización automática no está configurada en esta compilación."
                                    }

                                    is UpdateCheckResult.Error -> {
                                        statusText =
                                            "No se pudo consultar: ${result.message}"
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Buscar")
                    }

                    val update = availableUpdate

                    Button(
                        enabled =
                            update != null &&
                                !checking &&
                                !downloading,
                        onClick = {
                            if (update == null) {
                                return@Button
                            }

                            scope.launch {
                                if (
                                    !updateManager
                                        .canInstallDownloadedApk()
                                ) {
                                    statusText =
                                        "Android necesita autorización para instalar actualizaciones desde esta app."
                                    updateManager
                                        .openInstallPermissionSettings()
                                    return@launch
                                }

                                downloading = true
                                progress = 0
                                statusText =
                                    "Descargando APK..."

                                try {
                                    val file =
                                        withContext(Dispatchers.IO) {
                                            updateManager.downloadApk(
                                                update
                                            ) {
                                                progress = it
                                            }
                                        }

                                    downloadedApk = file
                                    downloading = false
                                    statusText =
                                        "Descarga completa. Se abrirá el instalador."
                                    updateManager.installApk(file)
                                } catch (error: Exception) {
                                    downloading = false
                                    statusText =
                                        "No se pudo descargar o instalar: ${error.message}"
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Descargar e instalar")
                    }

                    OutlinedButton(
                        enabled =
                            !checking && !downloading,
                        onClick = {
                            if (
                                updateManager.canInstallDownloadedApk()
                            ) {
                                Toast.makeText(
                                    context,
                                    "Permiso de instalación activo.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                updateManager
                                    .openInstallPermissionSettings()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Permiso instalación")
                    }

                    downloadedApk?.let { file ->
                        OutlinedButton(
                            enabled =
                                !checking && !downloading,
                            onClick = {
                                if (
                                    updateManager
                                        .canInstallDownloadedApk()
                                ) {
                                    updateManager.installApk(file)
                                } else {
                                    updateManager
                                        .openInstallPermissionSettings()
                                }
                            }
                        ) {
                            Text("Instalar APK descargada")
                        }
                    }
                }
            }
        }

        Surface(
            color =
                MaterialTheme.colorScheme.tertiaryContainer.copy(
                    alpha = 0.55f
                ),
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                text =
                    "La app descarga solamente la APK publicada como versión firmada. " +
                        "No sube medicamentos, pesos, cálculos ni datos clínicos. " +
                        "Android siempre mostrará una pantalla de instalación para confirmar.",
                modifier = Modifier.padding(14.dp),
                style =
                    MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}
