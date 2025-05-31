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
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Télécharge le binaire lm-cli depuis GitHub Releases.
 *
 *
 */
@Mojo( name = "install-lm-cli", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class InstallCliMojo
    extends AbstractMojo
{
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
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains("\"tag_name\"")) {
                        tag = inputLine.split(":")[1].replace("\"", "").replace(",", "").trim();
                        break;
                    }
                }
                in.close();
            }

            getLog().info("Downloading lm-cli version: " + tag);
            String url = "https://github.com/lmlouis/lm-cli/releases/download/" + tag + "/lm-cli";
            File targetDir = new File(outputDirectory, "lm-cli-bin");
            targetDir.mkdirs();
            File binary = new File(targetDir, "lm-cli");

            try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(binary)) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }

            if (!binary.setExecutable(true)) {
                getLog().warn("Could not make the file executable");
            }

            getLog().info("lm-cli downloaded to: " + binary.getAbsolutePath());

        } catch (IOException e) {
            throw new MojoExecutionException("Failed to download lm-cli", e);
        }
    }
}
