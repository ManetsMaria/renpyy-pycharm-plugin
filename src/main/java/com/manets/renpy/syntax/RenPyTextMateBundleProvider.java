package com.manets.renpy.syntax;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class RenPyTextMateBundleProvider implements TextMateBundleProvider {

    private static final Logger LOG = Logger.getInstance(RenPyTextMateBundleProvider.class);
    private static final String RESOURCE_PATH = "/syntax/textmate";
    private static final String BUNDLE_NAME = "renpy";

    @NotNull
    @Override
    public List<PluginBundle> getBundles() {
        Path bundlePath = getBundlePath();
        PluginBundle pluginBundle = new PluginBundle(BUNDLE_NAME, bundlePath);
        return List.of(pluginBundle);
    }

    private Path getBundlePath() {
        try {
            IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(PluginId.getId("com.manets.renpy.RenPy"));
            String version = plugin.getVersion();
            String path = plugin.getPluginPath() + "/bundles/" + version;
            return copyResourceDirectory(path, List.of("renpy.tmLanguage", "renpy.screen.tmLanguage", "renpy.atl.tmLanguage"));
        } catch (IOException ex) {
            LOG.error("Bundles error: " + ex);
            throw new UncheckedIOException(ex);
        }
    }

    private Path copyResourceDirectory(@NotNull String target, List<String> fileNames) throws IOException {
        Path targetPath = Paths.get(target);
        if (Files.notExists(targetPath)) {
            Files.createDirectories(targetPath);
        }
        for (String fileName: fileNames) {
            Path pathToCopyFile = new File(target + "/" + fileName).toPath();
            if (Files.notExists(pathToCopyFile)) {
                Files.copy(
                        Objects.requireNonNull(RenPyTextMateBundleProvider.class.getResourceAsStream(RESOURCE_PATH + "/" + fileName)),
                        pathToCopyFile);
            }
        }
        return targetPath;
    }
}
