package io.quarkus.devtools.project;

import static com.google.common.base.Preconditions.checkNotNull;

import io.quarkus.devtools.buildfile.BuildFile;
import io.quarkus.devtools.writer.FileProjectWriter;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import java.nio.file.Path;

public final class QuarkusProject {

    private final Path projectFolderPath;
    private final BuildFile buildFile;
    private final QuarkusPlatformDescriptor descriptor;

    private QuarkusProject(final Path projectFolderPath, final QuarkusPlatformDescriptor descriptor,
            final BuildFile buildFile) {
        this.projectFolderPath = checkNotNull(projectFolderPath, "projectFolderPath is required");
        this.descriptor = checkNotNull(descriptor, "descriptor is required");
        this.buildFile = checkNotNull(buildFile, "buildFile is required");
    }

    public static QuarkusProject of(final Path projectFolderPath, final QuarkusPlatformDescriptor descriptor,
            final BuildTool buildTool) {
        final BuildFile buildFile = buildTool.createBuildFile(new FileProjectWriter(projectFolderPath.toFile()));
        return of(projectFolderPath, descriptor, buildFile);
    }

    public static QuarkusProject of(final Path projectFolderPath, final QuarkusPlatformDescriptor descriptor,
            final BuildFile buildFile) {
        return new QuarkusProject(projectFolderPath, descriptor, buildFile);
    }

    public static QuarkusProject resolveExistingProject(final Path projectFolderPath,
            final QuarkusPlatformDescriptor descriptor) {
        final BuildTool buildTool = resolveExistingProjectBuildTool(projectFolderPath);
        if (buildTool == null) {
            throw new IllegalStateException("This is neither a Maven or Gradle project");
        }
        return of(projectFolderPath, descriptor, buildTool);
    }

    public Path getProjectFolderPath() {
        return projectFolderPath;
    }

    public BuildFile getBuildFile() {
        return buildFile;
    }

    public BuildTool getBuildTool() {
        return buildFile.getBuildTool();
    }

    public QuarkusPlatformDescriptor getDescriptor() {
        return descriptor;
    }

    private static BuildTool resolveExistingProjectBuildTool(Path projectFolderPath) {
        if (projectFolderPath.resolve("pom.xml").toFile().exists()) {
            return BuildTool.MAVEN;
        } else if (projectFolderPath.resolve("build.gradle").toFile().exists()
                || projectFolderPath.resolve("build.gradle.kts").toFile().exists()) {
            return BuildTool.GRADLE;
        }
        return null;
    }
}
