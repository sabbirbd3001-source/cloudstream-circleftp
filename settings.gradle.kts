rootProject.name = "CloudstreamPlugins"

// Automatically include all plugin directories that contain build.gradle.kts
File(rootDir, ".").eachDir { dir ->
    if (dir.name != "build" && dir.name != "gradle" && File(dir, "build.gradle.kts").exists()) {
        include(dir.name)
    }
}

fun File.eachDir(block: (File) -> Unit) {
    listFiles()?.filter { it.isDirectory }?.forEach { block(it) }
}
