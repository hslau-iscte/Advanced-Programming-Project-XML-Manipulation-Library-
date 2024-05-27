package week2.kotlin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class TestFileComposite {
    private val rootDirectory = File("/path/to/your/root/directory") // Change this to your root directory
    private val directoryTree = rootDirectory.toDirectoryTree()

    @Test
    fun testDepth() {
        assertEquals(1, directoryTree.depth)
        assertEquals(2, directoryTree.children[0].depth)
        assertEquals(3, directoryTree.children[0].children[0].depth)
        // Add more assertions as needed for deeper levels
    }

    @Test
    fun testDeepElementCount() {
        assertEquals(5, directoryTree.deepElementCount) // Adjust this based on your directory structure
    }

    @Test
    fun testToText() {
        val expectedText = """
            artists
                beatles
                    help
                    let it be
        """.trimIndent()
        assertEquals(expectedText, directoryTree.toText().trim())
    }
}
