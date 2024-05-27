import kotlin.reflect.KType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.*


data class Student(
    val number: Int,
    val name: String,
    val type: StudentType? = null
)

enum class StudentType {
    Bachelor, Master, Doctoral
}

// Get a list of attributes in the order of the primary constructor
val KClass<*>.dataClassFields: List<KProperty<*>>
    get() {
        require(isData) { "instance must be data class" }
        return primaryConstructor!!.parameters.map { p ->
            declaredMemberProperties.find { it.name == p.name }!!
        }
    }

// Check if a KClass is an enum
val KClass<*>.isEnum: Boolean
    get() = java.isEnum

// Obtain a list of constants of an enum type
val KClass<*>.enumConstants: List<Enum<*>>
    get() {
        require(isEnum) { "instance must be enum" }
        return java.enumConstants.toList().filterIsInstance<Enum<*>>()
    }

// Function to create CREATE TABLE SQL statement
fun createTable(clazz: KClass<*>): String {
    val tableName = clazz.simpleName ?: error("Class name cannot be determined")
    val columns = clazz.dataClassFields.joinToString(", ") { property ->
        "${property.name} ${mapType(property.returnType)}"
    }
    return "CREATE TABLE $tableName ($columns);"
}

// Function to map Kotlin types to MySQL data types
fun mapType(type: KType): String {
    return when (type.classifier) {
        Int::class -> "INT"
        String::class -> "CHAR"
        Boolean::class -> "BOOLEAN"
        Double::class -> "DOUBLE"
        // Add support for StudentType (enum)
        StudentType::class -> "ENUM('Bachelor', 'Master', 'Doctoral')"
        else -> throw IllegalArgumentException("Type not supported: ${type.classifier}")
    }
}

// Function to generate INSERT INTO SQL statement
fun insertInto(obj: Any): String {
    val clazz = obj::class
    val tableName = clazz.simpleName ?: error("Class name cannot be determined")
    val values = clazz.dataClassFields.joinToString(", ") { property ->
        val propertyName = property.name
        val propertyValue = property.getter.call(obj)
        "'$propertyValue'"
    }
    val columns = clazz.dataClassFields.joinToString(", ") { property ->
        property.name
    }
    return "INSERT INTO $tableName ($columns) VALUES ($values);"
}


// Example usage
fun main() {
//    val student = Student(1, "John Doe", StudentType.Bachelor)
//    val clazz = student::class as KClass<Student>
//    println(createTable(clazz))
    val s = Student(7, "Cristiano", StudentType.Doctoral)
    val sql: String = insertInto(s)
    println(sql)
}
