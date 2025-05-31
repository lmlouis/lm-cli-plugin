package dev.lmlouis;

/*
 * LM Louis CLI plugin.
 * https://github.com/lmlouis
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Mojo(name = "install-lm-cli", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class InstallCliMojo extends AbstractMojo {

    @Parameter(property = "lm.cli.version", defaultValue = "latest")
    private String version;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            String tag = version;
            if ("latest".equals(version)) {
                getLog().info("Fetching latest version from GitHub API...");
                HttpURLConnection conn = (HttpURLConnection) new URL("https://api.github.com/repos/lmlouis/lm-cli/releases/latest").openConnection();
                conn.setRequestProperty("Accept", "application/vnd.github+json");

                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject json = new JSONObject(response.toString());
                    tag = json.getString("tag_name");
                }
            }

            getLog().info("Downloading lm-cli source code version: " + tag);
            String url = "https://github.com/lmlouis/lm-cli/archive/refs/tags/" + tag + ".zip";
            File targetDir = new File(outputDirectory, "lm-cli-source");
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            File archive = new File(targetDir, "lm-cli-" + tag + ".zip");

            // Télécharger le ZIP
            try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(archive)) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }

            getLog().info("lm-cli source code downloaded to: " + archive.getAbsolutePath());

            // Extraire le ZIP dans le dossier targetDir
            unzip(archive, targetDir);

            // Optionnel : supprimer l'archive après extraction
            if (archive.delete()) {
                getLog().info("Deleted archive after extraction.");
            } else {
                getLog().warn("Could not delete archive after extraction.");
            }

            getLog().info("lm-cli source code extracted to: " + targetDir.getAbsolutePath());

        } catch (IOException e) {
            throw new MojoExecutionException("Failed to download or extract lm-cli source code", e);
        }
    }

    private void unzip(File zipFile, File targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = newFile(targetDir, entry);
                if (entry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // Créer les dossiers parents si nécessaire
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // Copier le contenu du fichier
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    // Sécurité : éviter les attaques Zip Slip (extraction hors dossier cible)
    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
