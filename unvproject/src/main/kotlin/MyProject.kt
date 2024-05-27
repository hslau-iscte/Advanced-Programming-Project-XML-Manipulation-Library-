import java.io.File
import kotlin.reflect.KClass



@Target(AnnotationTarget.PROPERTY)
annotation class XmlString(val transformer: KClass<out Transformer>)

@Target(AnnotationTarget.CLASS)
annotation class XmlAdapter(val adapter: KClass<out Adapter>)

interface Transformer {
    fun transform(value: Any): String
}

object AddPercentage : Transformer {
    override fun transform(value: Any): String {
        return "$value%"
    }
}

interface Adapter {
    fun adapt(entity: XMLElement)
}

object FUCAdapter : Adapter {
    override fun adapt(entity: XMLElement) {
        // Custom logic to adapt FUC entity
        // reordering attributes, modifying values, etc.
        val codigoAttribute = entity.attributes.remove("codigo")
        if (codigoAttribute != null) {
            entity.attributes["codigo"] = codigoAttribute
        }
        // Add additional attributes
        entity.addAttribute("type", "advanced")

        // Add additional elements
        entity.addChild(XMLElement("description").apply {
            addAttribute("lang", "en")
            addAttribute("value", "This course covers advanced programming topics.")
        })
    }
}
/**
 * Represents a visitor interface for visiting XML elements.
 */
interface Visitor {
    /**
     * Visits an XML element.
     * @param element The XML element to visit.
     */
    fun visit(element: XMLElement)
}

// Concrete visitor class for printing element names
class PrintVisitor : Visitor {
    /**
     * Visits an XML element and prints its name.
     * @param element The XML element to visit.
     */
    override fun visit(element: XMLElement) {
        println(element.name)
        element.children.forEach { it.accept(this) }
    }
}

// Concrete visitor class for printing attributes
class AttributeVisitor : Visitor {
    /**
     * Visits an XML element and prints its attributes.
     * @param element The XML element to visit.
     */
    override fun visit(element: XMLElement) {
        println("Attributes of ${element.name}: ${element.attributes}")
        element.children.forEach { it.accept(this) }
    }
}

// Concrete visitor class for adding attributes globally
class AddAttributeVisitor(private val entityName: String, private val attributeName: String, private val attributeValue: String) : Visitor {
    /**
     * Visits an XML element and adds the specified attribute globally.
     * @param element The XML element to visit.
     */
    override fun visit(element: XMLElement) {
        if (element.name == entityName) {
            element.attributes[attributeName] = attributeValue
        }
        element.children.forEach { it.accept(this) }
    }
}

// Concrete visitor class for renaming entities globally
class RenameEntityVisitor(private val oldName: String, private val newName: String) : Visitor {
    /**
     * Visits an XML element and renames entities globally.
     * @param element The XML element to visit.
     */
    override fun visit(element: XMLElement) {
        if (element.name == oldName) {
            element.name = newName
        }
        element.children.forEach { it.accept(this) }
    }
}

// Concrete visitor class for renaming attributes globally
class RenameAttributeVisitor(private val entityName: String, private val oldAttributeName: String, private val newAttributeName: String) : Visitor {
    /**
     * Visits an XML element and renames attributes globally.
     * @param element The XML element to visit.
     */
    override fun visit(element: XMLElement) {
        if (element.name == entityName) {
            val attributeValue = element.attributes.remove(oldAttributeName)
            if (attributeValue != null) {
                element.attributes[newAttributeName] = attributeValue
            }
        }
        element.children.forEach { it.accept(this) }
    }
}

// Concrete visitor class for removing entities globally
class RemoveEntityVisitor(private val entityName: String) : Visitor {
    /**
     * Visits an XML element and removes entities globally.
     * @param element The XML element to visit.
     */
    override fun visit(element: XMLElement) {
        element.children.removeIf { it.name == entityName }
        element.children.forEach { it.accept(this) }
    }
}

// Concrete visitor class for removing attributes globally
class RemoveAttributeVisitor(private val entityName: String, private val attributeName: String) : Visitor {
    /**
     * Visits an XML element and removes attributes globally.
     * @param element The XML element to visit.
     */
    override fun visit(element: XMLElement) {
        if (element.name == entityName) {
            element.attributes.remove(attributeName)
        }
        element.children.forEach { it.accept(this) }
    }
}

// Define your XML entity class
/**
 * Represents an XML element.
 *
 * @property name The name of the XML element.
 * @property attributes The attributes associated with the XML element.
 * @property children The child elements of the XML element.
 * @property parent The parent element of the XML element.
 * Creates an XML element with the given name, attributes, children, and parent.
 */
data class XMLElement(
    var name: String,
    val attributes: MutableMap<String, String> = mutableMapOf(),
    val children: MutableList<XMLElement> = mutableListOf(),
    var parent: XMLElement? = null
) {
    /**
     * Adds a single attribute to the XML element.
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     */
    fun addAttribute(name: String, value: String) {
        attributes[name] = value
    }

    /**
     * Adds multiple attributes to the XML element.
     * @param pairs The pairs of attribute name and value.
     */
    fun addAttributes(vararg pairs: Pair<String, String>) {
        pairs.forEach { (key, value) -> attributes[key] = value }
    }

    /**
     * Removes child elements with the given name recursively.
     * @param entityName The name of the child elements to remove.
     */
    fun removeEntitiesByName(entityName: String) {
        val iterator = children.iterator()
        while (iterator.hasNext()) {
            val child = iterator.next()
            if (child.name == entityName) {
                iterator.remove()
            } else {
                child.removeEntitiesByName(entityName)
            }
        }
    }

    /**
     * Renames child elements with the old name to the new name recursively.
     * @param oldName The old name of the child elements.
     * @param newName The new name of the child elements.
     */
    fun renameEntitiesByName(oldName: String, newName: String) {
        if (name == oldName) {
            name = newName
        }
        children.forEach { it.renameEntitiesByName(oldName, newName) }
    }

    /**
     * Adds a child element to the XML element.
     * @param child The child element to add.
     */
    fun addChild(child: XMLElement) {
        child.parent = this
        children.add(child)
    }

    /**
     * Pretty prints the XML structure.
     * @param depth The depth of indentation.
     * @return The XML structure string.
     */
    fun prettyPrint(depth: Int = 0): String {
        val indent = "  ".repeat(depth)
        val attributeStr = attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }
        val childrenStr = children.joinToString("\n") { it.prettyPrint(depth + 1) }
        return if (children.isNotEmpty()) {
            "$indent<$name $attributeStr>\n$childrenStr\n$indent</$name>"
        } else {
            "$indent<$name $attributeStr/>"
        }
    }

    override fun toString(): String {
        return prettyPrint()
    }
    /**
     * Accepts a visitor and visits the XML element and its children.
     * @param visitor The visitor to accept.
     */
    fun accept(visitor: Visitor) {
        visitor.visit(this)
        children.forEach { it.accept(visitor) }
    }

    /**
     * Gets the parent element of the XML element.
     * @return The parent element.
     */
    fun getParentElements(): XMLElement? {
        return parent
    }

    /**
     * Gets the child elements of the XML element.
     * @return The list of child elements.
     */
    fun getChildElements(): List<XMLElement> {
        return children
    }
}

/**
 * Represents an XML document.
 *
 * @property rootElement The root element of the XML document.
 * @constructor Creates an XML document with the given root element.
 */
class XMLDocument(private val rootElement: XMLElement) {
    /**
     * Finds XML fragments matching the XPath expression.
     * @param xpath The XPath expression.
     * @return The list of XML fragments matching the expression.
     */
    fun find(xpath: String): List<XMLElement> {
        val elements = mutableListOf<XMLElement>()
        // Remove leading and trailing slashes
        val trimmedXpath = xpath.trim('/')
        if (trimmedXpath == "//") {
            // XPath expression is just "//", return all elements
            getAllElements(rootElement, elements)
        } else {
            // Otherwise, treat as single expression
            findElements(rootElement, trimmedXpath, elements)
        }
        return elements
    }

    /**
     * Finds XML elements matching the single XPath expression.
     * @param element The current XML element.
     * @param expression The XPath expression.
     * @param elements The list to store found elements.
     */
    private fun findElements(element: XMLElement, expression: String, elements: MutableList<XMLElement>) {
        if (element.name == expression) {
            elements.add(element)
        }
        element.children.forEach { child ->
            findElements(child, expression, elements)
        }
    }

    /**
     * Gets all XML elements.
     * @param element The current XML element.
     * @param elements The list to store found elements.
     */
    private fun getAllElements(element: XMLElement, elements: MutableList<XMLElement>) {
        elements.add(element)
        element.children.forEach { child ->
            getAllElements(child, elements)
        }
    }

    /**
     * Writes the XML document to a file.
     * @param filePath The path of the file to write.
     */
    fun writeToFile(filePath: String) {
        File(filePath).writeText(rootElement.prettyPrint())
    }

    /**
     * Adds a global attribute to the XML document.
     * @param entityName The name of the elements to which the attribute will be added.
     * @param attributeName The name of the attribute.
     * @param attributeValue The value of the attribute.
     */
    fun addGlobalAttribute(entityName: String, attributeName: String, attributeValue: String) {
        val visitor = AddAttributeVisitor(entityName, attributeName, attributeValue)
        rootElement.accept(visitor)
    }

    /**
     * Renames global entities in the XML document.
     * @param oldName The old name of the entities.
     * @param newName The new name of the entities.
     */
    fun renameGlobalEntity(oldName: String, newName: String) {
        val visitor = RenameEntityVisitor(oldName, newName)
        rootElement.accept(visitor)
    }

    /**
     * Renames global attributes in the XML document.
     * @param entityName The name of the elements to which the attribute belongs.
     * @param oldAttributeName The old name of the attribute.
     * @param newAttributeName The new name of the attribute.
     */
    fun renameGlobalAttribute(entityName: String, oldAttributeName: String, newAttributeName: String) {
        val visitor = RenameAttributeVisitor(entityName, oldAttributeName, newAttributeName)
        rootElement.accept(visitor)
    }

    /**
     * Removes global entities from the XML document.
     * @param entityName The name of the entities to remove.
     */
    fun removeGlobalEntity(entityName: String) {
        val visitor = RemoveEntityVisitor(entityName)
        rootElement.accept(visitor)
    }

    /**
     * Removes global attributes from the XML document.
     * @param entityName The name of the elements from which the attribute will be removed.
     * @param attributeName The name of the attribute to remove.
     */
    fun removeGlobalAttribute(entityName: String, attributeName: String) {
        val visitor = RemoveAttributeVisitor(entityName, attributeName)
        rootElement.accept(visitor)
    }

    /**
     * Accepts a visitor and visits the XML document.
     * @param visitor The visitor to accept.
     */
    fun accept(visitor: Visitor) {
        rootElement.accept(visitor)
    }

}
data class ComponenteAvaliacao(
    val nome: String,
    @XmlString(AddPercentage::class)
    val peso: Int
)

/**
 * The entry point of the program.
 * This function demonstrates the usage of the XMLDocument and XMLElement classes
 * to create and manipulate an XML document structure. It creates an XML document
 * with various elements and attributes, performs operations such as printing,
 * adding, renaming, and removing elements and attributes globally, and finally
 * writes the modified XML document to a file.
 */
fun main() {

    // Create root element
    val root = XMLElement("plano")
    val document = XMLDocument(root)
    // Add @XmlAdapter annotation to FUC class
    @XmlAdapter(FUCAdapter::class)
    class FUC(
        val codigo: String,
        val nome: String,
        val ects: Double,
        val observacoes: String,
        val avaliacao: List<ComponenteAvaliacao>
    )

    // Create an instance of FUC
    val f = FUC("M4310", "Programação Avançada", 6.0, "la la...",
        listOf(
            ComponenteAvaliacao("Quizzes", 20),
            ComponenteAvaliacao("Projeto", 80)
        )
    )

    // Now, convert the FUC instance to XML
    val fucElement = convertToXMLElement(f)
    root.addChild(fucElement)
    // Add <curso> element
    root.addChild(XMLElement("curso").apply {
        addAttribute("value", "Mestrado em Engenharia Informática")
        // Add child elements here
        addChild(XMLElement("curso2"))
        addChild(XMLElement("curso3"))
    })

    // Data for fuc elements
    val fucDataList = listOf(
        mapOf(
            "codigo" to "M4310",
            "nome" to "Programação Avançada",
            "ects" to "6.0",
            "componentes" to listOf(
                mapOf("nome" to "Quizzes", "peso" to "20%"),
                mapOf("nome" to "Projeto", "peso" to "80%")
            )
        ),
        mapOf(
            "codigo" to "03782",
            "nome" to "Dissertação",
            "ects" to "42.0",
            "componentes" to listOf(
                mapOf("nome" to "Dissertação", "peso" to "60%"),
                mapOf("nome" to "Apresentação", "peso" to "20%"),
                mapOf("nome" to "Discussão", "peso" to "20%")
            )
        )
    )

    // Add <fuc> elements using loop
    for (fucData in fucDataList) {
        val fuc = XMLElement("fuc").apply {
            addAttribute("codigo", fucData["codigo"].toString().trim())
            addChild(XMLElement("nome").apply { addAttribute("value", fucData["nome"].toString().trim()) })
            addChild(XMLElement("ects").apply { addAttribute("value", fucData["ects"].toString().trim()) })
            val avaliacao = XMLElement("avaliacao")
            fucData["componentes"]?.let { componentes ->
                for (componenteData in componentes as List<Map<String, String>>) {
                    val trimmedComponenteData = componenteData.mapValues { it.value.trim() }
                    avaliacao.addChild(XMLElement("componente").apply {
                        addAttributes("nome" to trimmedComponenteData["nome"]!!, "peso" to trimmedComponenteData["peso"]!!)
                    })
                }
            }
            addChild(avaliacao)
        }
        root.addChild(fuc)
    }

    // Pretty print the XML structure
    println(root.prettyPrint())
    // Traversal with different visitors
    val printVisitor = PrintVisitor()
    val attributeVisitor = AttributeVisitor()
//
    println("\nPrintVisitor Element names:")
    document.accept(printVisitor)
//
    println("\nAttributeVisitor:")
    document.accept(attributeVisitor)

    // 6. Add attributes globally to the document
    document.addGlobalAttribute("funcionario", "attributeName", "attributeValue")

    // 7. Renaming entities globally in the document
    document.renameGlobalEntity("fuc", "funcionario")

    // 8. Renaming attributes globally in the document
    document.renameGlobalAttribute("curso", "value", "descricao")
    document.renameGlobalAttribute("funcionario", "codigo", "code")

    // 9. Removing entities globally from the document
    document.removeGlobalEntity("avaliacao")

    // 10. Removing attributes globally from the document
    document.removeGlobalAttribute("ects", "value")

    // Write to file
    document.writeToFile("output.xml")

    // Accessing parent and child entities
    val childElement = root.children.firstOrNull()
    childElement?.let {
        val parentElement = it.getParentElements()
        println("Parent of ${it.name}: $parentElement")
        val childrenOfChildElement = it.getChildElements()
        println("Children of ${it.name}: $childrenOfChildElement")
    }

    // Assuming xmlDoc is an instance of XMLDocument
    val xmlDoc = XMLDocument(root) // Initialize xmlDoc with the rootElement

// Now you can use xmlDoc to call the find function xpah
    val matchingElements = xmlDoc.find("//componente")
    matchingElements.forEach { element ->
        println(element.prettyPrint())
    }



}

fun convertToXMLElement(obj: Any): XMLElement {
    val clazz = obj::class.java
    val properties = clazz.declaredFields
    val element = XMLElement(clazz.simpleName)

    properties.forEach { property ->
        property.isAccessible = true
        val value = property.get(obj)
        if (value != null) {
            val attributeName = property.name
            val stringValue = if (property.isAnnotationPresent(XmlString::class.java)) {
                val transformerClass = property.getAnnotation(XmlString::class.java).transformer
                val transformer = transformerClass.objectInstance as Transformer
                transformer.transform(value)
            } else {
                value.toString()
            }
            element.addAttribute(attributeName, stringValue)
        }
    }

    if (clazz.isAnnotationPresent(XmlAdapter::class.java)) {
        val adapterClass = clazz.getAnnotation(XmlAdapter::class.java).adapter
        val adapter = adapterClass.objectInstance as Adapter
        adapter.adapt(element)
    }

    return element
}
