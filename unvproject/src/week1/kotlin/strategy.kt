import java.io.File

// Step 1: Recursive function to obtain file listing with subdirectories
fun deepListFiles(
    dir: File,
    filter: (File) -> Boolean = { true },
    listingStrategy: ListingStrategy = Ascending
): List<File> {
    val fileList = mutableListOf<File>()
    val children = dir.listFiles() ?: return emptyList()
    children.sort()
    for (child in children) {
        if (child.isDirectory) {
            fileList.addAll(deepListFiles(child, filter, listingStrategy))
        } else {
            if (filter(child)) {
                fileList.add(child)
            }
        }
    }
    listingStrategy.sort(fileList.toTypedArray())
    return fileList
}

// Step 2: Function to obtain distinct file extensions
fun distinctExtensions(fileList: List<File>): Set<String> {
    return fileList.map { it.extension }.toSet()
}

// Step 3: Extension function for File class
fun File.deepListFilesWithStrategy(
    filter: (File) -> Boolean = { true },
    listingStrategy: ListingStrategy = Ascending
): List<File> {
    return deepListFiles(this, filter, listingStrategy)
}

// Step 4: Listing strategy interface
interface ListingStrategy {
    fun sort(dirContents: Array<File>)
}

// Default strategy for sorting in ascending order
object Ascending : ListingStrategy {
    override fun sort(dirContents: Array<File>) {
        dirContents.sortBy { it.nameWithoutExtension }
    }
}

// Example usage
fun main() {
    val path = File(System.getProperty("user.dir")) // Current directory
    val kotlinFiles = path.deepListFilesWithStrategy(filter = { it.extension == "kt" })
    println("Kotlin files: $kotlinFiles")

    val distinctExtensions = distinctExtensions(kotlinFiles)
    println("Distinct extensions: $distinctExtensions")
}
