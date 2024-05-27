//import org.junit.Assert.assertEquals
import org.junit.Assert
import org.junit.Test
import java.io.File

class FileExtensionsTest {

    private val fileList = listOf(
        File("random"),
        File("Test.kt"),
        File("Example.kt"),
        File("Script.kts")
    )

    @Test
    fun testCountExtensions() {
        val countMap = fileList.countExtensions()
        assertEquals(2, countMap["kt"])
        assertEquals(1, countMap["kts"])
        assertEquals(1, countMap[""])
    }

    @Test
    fun testFilterByExtension() {
        val filteredList = fileList.filterByExtension("kt")
        assertEquals(2, filteredList.size)
        assertEquals("Test.kt", filteredList[0].name)
        assertEquals("Example.kt", filteredList[1].name)
    }

    @Test
    fun testExtensions() {
        val extensions = fileList.extensions
        assertEquals(setOf("kt", "kts", ""), extensions)
    }

    @Test
    fun testDepth() {
        val file1 = File("/usr/local/bin/script.sh")
        assertEquals(4, file1.depth)
        val file2 = File("/usr/local/")
        assertEquals(2, file2.depth)
        val file3 = File("/usr/local/bin/")
        assertEquals(3, file3.depth)
    }

    @Test
    fun testExtensionName() {
        val file1 = File("example.txt")
        assertEquals("txt", file1.extensionName)
        val file2 = File("/usr/local/bin/script.sh")
        assertEquals("sh", file2.extensionName)
        val file3 = File("/usr/local/")
        assertEquals("", file3.extensionName)
    }
}
