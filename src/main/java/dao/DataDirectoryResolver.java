package dao;

import java.io.File;

final class DataDirectoryResolver {
    private DataDirectoryResolver() {
    }

    static File resolve(String basePath) {
        if (basePath != null && !basePath.trim().isEmpty()) {
            File runtimeDirectory = new File(basePath);
            File projectDataDirectory = findProjectDataDirectory(runtimeDirectory);
            if (projectDataDirectory != null) {
                if (!projectDataDirectory.exists()) {
                    projectDataDirectory.mkdirs();
                }
                return projectDataDirectory;
            }

            if (!runtimeDirectory.exists()) {
                runtimeDirectory.mkdirs();
            }
            return runtimeDirectory;
        }

        File fallbackDirectory = new File("src/main/webapp/data");
        if (!fallbackDirectory.exists()) {
            fallbackDirectory.mkdirs();
        }
        return fallbackDirectory;
    }

    private static File findProjectDataDirectory(File runtimeDirectory) {
        File current = runtimeDirectory;
        while (current != null) {
            File pomFile = new File(current, "pom.xml");
            File sourceDataDirectory = new File(current, "src/main/webapp/data");
            if (pomFile.exists() && sourceDataDirectory.getParentFile().exists()) {
                return sourceDataDirectory;
            }
            current = current.getParentFile();
        }
        return null;
    }
}
