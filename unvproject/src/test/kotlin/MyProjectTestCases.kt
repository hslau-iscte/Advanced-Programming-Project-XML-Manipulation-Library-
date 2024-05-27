import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class XMLDocumentTest {
    @Test
    fun testFind() {
        val root = XMLElement("plano")
        val document = XMLDocument(root)

        val curso = XMLElement("curso")
        curso.addAttribute("value", "Mestrado em Engenharia Informática")
        root.addChild(curso)

        val fuc = XMLElement("fuc")
        fuc.addAttribute("codigo", "M4310")
        root.addChild(fuc)

        val matchingElements = document.find("//fuc")
        assertEquals(1, matchingElements.size)
        assertEquals("fuc", matchingElements[0].name)
        assertEquals("M4310", matchingElements[0].attributes["codigo"])
    }

    @Test
    fun testWriteToFile() {
        // Test case for writing XML document to a file
        val root = XMLElement("plano")
        val document = XMLDocument(root)

        val filePath = "test.xml"
        document.writeToFile(filePath)

        val file = File(filePath)
        val fileContent = file.readText()
        assertEquals("<plano />", fileContent.trim()) // Adjusted expected output

        // Clean up
        file.delete()
    }


    @Test
    fun testAddGlobalAttribute() {
        val root = XMLElement("plano")
        val document = XMLDocument(root)

        val curso = XMLElement("curso")
        curso.addAttribute("value", "Mestrado em Engenharia Informática")
        root.addChild(curso)

        document.addGlobalAttribute("curso", "attributeName", "attributeValue")

        val cursoWithAttribute = root.children[0]
        assertEquals("attributeValue", cursoWithAttribute.attributes["attributeName"])
    }

    @Test
    fun testRenameGlobalEntity() {
        val root = XMLElement("plano")
        val document = XMLDocument(root)

        val curso = XMLElement("curso")
        curso.addAttribute("value", "Mestrado em Engenharia Informática")
        root.addChild(curso)

        document.renameGlobalEntity("curso", "newName")

        val renamedEntity = root.children[0]
        assertEquals("newName", renamedEntity.name)
    }

    @Test
    fun testRenameGlobalAttribute() {
        val root = XMLElement("plano")
        val document = XMLDocument(root)

        val curso = XMLElement("curso")
        curso.addAttribute("value", "Mestrado em Engenharia Informática")
        root.addChild(curso)

        document.renameGlobalAttribute("curso", "value", "newName")

        val cursoWithRenamedAttribute = root.children[0]
        assertEquals("Mestrado em Engenharia Informática", cursoWithRenamedAttribute.attributes["newName"])
        assertEquals(null, cursoWithRenamedAttribute.attributes["value"])
    }

    @Test
    fun testRemoveGlobalEntity() {
        val root = XMLElement("plano")
        val document = XMLDocument(root)

        val curso = XMLElement("curso")
        curso.addAttribute("value", "Mestrado em Engenharia Informática")
        root.addChild(curso)

        document.removeGlobalEntity("curso")

        assertEquals(0, root.children.size)
    }

    @Test
    fun testRemoveGlobalAttribute() {
        val root = XMLElement("plano")
        val document = XMLDocument(root)

        val curso = XMLElement("curso")
        curso.addAttribute("value", "Mestrado em Engenharia Informática")
        root.addChild(curso)

        document.removeGlobalAttribute("curso", "value")

        assertEquals(0, curso.attributes.size)
    }

    @Test
    fun testFUCAdapter() {
            // Setup: Create an instance of XMLElement representing a FUC entity
            val fucElement = XMLElement("fuc").apply {
                addAttribute("codigo", "M4310")
                addAttribute("nome", "Programação Avançada")
                addAttribute("ects", "6.0")
                addChild(XMLElement("avaliacao").apply {
                    addChild(XMLElement("componente").apply {
                        addAttribute("nome", "Quizzes")
                        addAttribute("peso", "20")
                    })
                    addChild(XMLElement("componente").apply {
                        addAttribute("nome", "Projeto")
                        addAttribute("peso", "80")
                    })
                })
            }

            // Invocation: Call the adapt function of FUCAdapter with the prepared XMLElement
            FUCAdapter.adapt(fucElement)

            // Assertion: Verify that the XMLElement has been adapted as expected
            assertEquals("M4310", fucElement.attributes["codigo"])
            assertEquals("advanced", fucElement.attributes["type"])
            assertEquals("This course covers advanced programming topics.", fucElement.children.firstOrNull { it.name == "description" }?.attributes?.get("value"))
        }
}
