import java.io.File

sealed interface Element {
    val name: String
    val parent: DirectoryElement?

    val depth: Int
        get() = parent?.depth?.plus(1) ?: 1

    val path: String
        get() = buildString {
            append(parent?.path ?: "")
            if (parent != null && parent.path != "/") {
                append("/")
            }
            append(name)
        }


    val deepElementCount: Int
        get() = if (this is DirectoryElement) {
            1 + children.sumBy { it.deepElementCount }
        } else {
            1
        }

    fun toText(): String
}

data class DirectoryElement(
    override val name: String,
    override val parent: DirectoryElement? = null,
    val children: MutableList<Element> = mutableListOf()
) : Element {
    override fun toText(): String {
        val prefix = "\t".repeat(depth - 1)
        val stringBuilder = StringBuilder()
        stringBuilder.append("$prefix$name\n")
        for (child in children) {
            stringBuilder.append(child.toText())
        }
        return stringBuilder.toString()
    }
}

fun File.toDirectoryTree(): Element {
    require(this.isDirectory) { "Provided file is not a directory." }

    fun buildTree(file: File, parent: DirectoryElement? = null): Element {
        val directoryElement = DirectoryElement(file.name, parent)
        file.listFiles()?.forEach { child ->
            if (child.isDirectory) {
                directoryElement.children.add(buildTree(child, directoryElement))
            } else {
                // Assuming files are leaf nodes
                directoryElement.children.add(FileElement(child.name, directoryElement))
            }
        }
        return directoryElement
    }

    return buildTree(this)
}

data class FileElement(
    override val name: String,
    override val parent: DirectoryElement
) : Element {
    override fun toText(): String {
        val prefix = "\t".repeat(depth)
        return "$prefix$name\n"
    }
}
