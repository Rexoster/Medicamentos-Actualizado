import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GradleBootstrap {
    private static final String VERSION = "8.11.1";
    private static final URI DISTRIBUTION = URI.create(
        "https://services.gradle.org/distributions/gradle-" + VERSION + "-bin.zip"
    );

    public static void main(String[] args) throws Exception {
        if (args.length < 1) throw new IllegalArgumentException("Falta la ruta del proyecto.");
        Path projectRoot = Path.of(args[0]).toAbsolutePath().normalize();
        String configuredHome = System.getenv("GRADLE_USER_HOME");
        Path gradleHome = configuredHome == null || configuredHome.isBlank()
            ? Path.of(System.getProperty("user.home"), ".gradle")
            : Path.of(configuredHome);
        Path bootstrapRoot = gradleHome.resolve("calculator-native-bootstrap").resolve(VERSION);
        Path distributionRoot = bootstrapRoot.resolve("gradle-" + VERSION);
        Path executable = distributionRoot.resolve("bin").resolve(isWindows() ? "gradle.bat" : "gradle");

        if (!Files.exists(executable)) {
            Files.createDirectories(bootstrapRoot);
            Path zip = bootstrapRoot.resolve("gradle-" + VERSION + "-bin.zip");
            if (!Files.exists(zip)) download(zip);
            unzip(zip, bootstrapRoot);
        }

        List<String> command = new ArrayList<>();
        command.add(executable.toString());
        for (int i = 1; i < args.length; i++) command.add(args[i]);
        Process process = new ProcessBuilder(command)
            .directory(projectRoot.toFile())
            .inheritIO()
            .start();
        System.exit(process.waitFor());
    }

    private static void download(Path destination) throws IOException, InterruptedException {
        System.out.println("Descargando Gradle " + VERSION + "...");
        HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(30))
            .build();
        HttpRequest request = HttpRequest.newBuilder(DISTRIBUTION)
            .timeout(Duration.ofMinutes(10)).GET().build();
        Path temporary = destination.resolveSibling(destination.getFileName() + ".part");
        HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(temporary));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            Files.deleteIfExists(temporary);
            throw new IOException("No se pudo descargar Gradle. HTTP " + response.statusCode());
        }
        Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void unzip(Path zip, Path destination) throws IOException {
        System.out.println("Preparando Gradle...");
        try (InputStream input = Files.newInputStream(zip); ZipInputStream archive = new ZipInputStream(input)) {
            ZipEntry entry;
            while ((entry = archive.getNextEntry()) != null) {
                Path output = destination.resolve(entry.getName()).normalize();
                if (!output.startsWith(destination.normalize())) throw new IOException("Entrada ZIP no válida");
                if (entry.isDirectory()) Files.createDirectories(output);
                else {
                    Files.createDirectories(output.getParent());
                    Files.copy(archive, output, StandardCopyOption.REPLACE_EXISTING);
                }
                archive.closeEntry();
            }
        }
        if (!isWindows()) destination.resolve("gradle-" + VERSION).resolve("bin/gradle").toFile().setExecutable(true);
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
