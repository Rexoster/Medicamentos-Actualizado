package com.luisangel.calculadoramedicamentos.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.luisangel.calculadoramedicamentos.BuildConfig
import java.io.BufferedInputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class UpdateManifest(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val apkFileName: String = "HerramientasClinicas.apk",
    val releaseUrl: String = "",
    val notes: String = ""
)

sealed class UpdateCheckResult {
    data class Available(
        val manifest: UpdateManifest
    ) : UpdateCheckResult()

    data class UpToDate(
        val manifest: UpdateManifest
    ) : UpdateCheckResult()

    data object NotConfigured : UpdateCheckResult()

    data class Error(
        val message: String
    ) : UpdateCheckResult()
}

class AppUpdateManager(
    private val context: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    val manifestUrl: String
        get() = BuildConfig.UPDATE_MANIFEST_URL

    fun currentVersionLabel(): String =
        "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

    fun canInstallDownloadedApk(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun openInstallPermissionSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:${context.packageName}")
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent)
        }
    }

    suspend fun checkForUpdates(): UpdateCheckResult {
        val url = manifestUrl.trim()

        if (url.isBlank()) {
            return UpdateCheckResult.NotConfigured
        }

        return try {
            val text = downloadText(url)
            val manifest = json.decodeFromString<UpdateManifest>(text)

            if (
                manifest.versionCode >
                BuildConfig.VERSION_CODE
            ) {
                UpdateCheckResult.Available(manifest)
            } else {
                UpdateCheckResult.UpToDate(manifest)
            }
        } catch (error: Exception) {
            UpdateCheckResult.Error(
                error.message
                    ?: "No se pudo consultar la actualización."
            )
        }
    }

    suspend fun downloadApk(
        manifest: UpdateManifest,
        onProgress: (Int) -> Unit
    ): File {
        val updatesDir = File(
            context.cacheDir,
            "updates"
        ).apply {
            mkdirs()
        }

        updatesDir.listFiles()
            ?.forEach(File::delete)

        val safeName = manifest.apkFileName
            .ifBlank {
                "HerramientasClinicas-${manifest.versionName}.apk"
            }
            .replace("/", "_")
            .replace("\\", "_")

        val outputFile = File(
            updatesDir,
            safeName
        )

        val connection = openConnection(
            manifest.apkUrl
        )

        try {
            val length = connection.contentLengthLong
            val response = connection.responseCode

            if (response !in 200..299) {
                throw IllegalStateException(
                    "Descarga rechazada por el servidor: HTTP $response"
                )
            }

            BufferedInputStream(connection.inputStream).use { input ->
                outputFile.outputStream().use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var downloaded = 0L

                    while (true) {
                        val read = input.read(buffer)

                        if (read <= 0) {
                            break
                        }

                        output.write(buffer, 0, read)
                        downloaded += read

                        if (length > 0) {
                            onProgress(
                                ((downloaded * 100) / length)
                                    .toInt()
                                    .coerceIn(0, 100)
                            )
                        }
                    }
                }
            }

            if (outputFile.length() <= 0L) {
                throw IllegalStateException(
                    "La APK descargada quedó vacía."
                )
            }

            onProgress(100)
            return outputFile
        } finally {
            connection.disconnect()
        }
    }

    fun installApk(apkFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(
                uri,
                "application/vnd.android.package-archive"
            )
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        context.startActivity(intent)
    }

    private fun downloadText(url: String): String {
        val connection = openConnection(url)

        return try {
            val response = connection.responseCode

            if (response !in 200..299) {
                throw IllegalStateException(
                    "HTTP $response al consultar actualizaciones."
                )
            }

            connection.inputStream
                .bufferedReader()
                .use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun openConnection(url: String): HttpURLConnection {
        val connection = URL(url)
            .openConnection() as HttpURLConnection

        connection.connectTimeout = 12000
        connection.readTimeout = 25000
        connection.instanceFollowRedirects = true
        connection.setRequestProperty(
            "Accept",
            "application/json, application/octet-stream"
        )
        connection.setRequestProperty(
            "User-Agent",
            "HerramientasClinicas/${BuildConfig.VERSION_NAME}"
        )

        return connection
    }
}
