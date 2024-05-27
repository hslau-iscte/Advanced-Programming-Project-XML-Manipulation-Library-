# XML Manipulation Project

## Project Structure
├── build.gradle.kts
├── settings.gradle.kts
├── src
│ ├── main
│ │ └── kotlin
│ │ ├── Main.kt
│ │ ├── XMLElement.kt
│ │ ├── XMLDocument.kt
│ │ ├── Visitors.kt
│ │ ├── Transformers.kt
│ │ ├── Adapters.kt
│ │ └── MicroXPath.kt
│ └── test
│ └── kotlin
│ └── XMLDocumentTest.kt
├── README.md
└── LICENSE


### Files Description

- `Main.kt`: The entry point of the application (if needed).
- `XMLElement.kt`: Defines the `XMLElement` class, representing an XML element.
- `XMLDocument.kt`: Defines the `XMLDocument` class, representing an XML document and providing methods to manipulate it.
- `Visitors.kt`: Contains visitor patterns for traversing XML structures.
- `Transformers.kt`: Contains transformers to modify XML structures.
- `Adapters.kt`: Contains adapters to adapt XML elements for specific use cases.
- `MicroXPath.kt`: Provides simplified XPath functionalities to find XML elements.
- `MyProjectTestCases.kt`: Contains unit tests to verify the functionality of the `XMLDocument` and related classes.

## Installation

### Prerequisites

- Ensure you have Java Development Kit (JDK) installed (version 8 or higher).
- Ensure you have Gradle installed.

### Dependencies

The project uses the following dependencies:
- `org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0`
- `org.junit.jupiter:junit-jupiter:5.10.2`
- `org.jetbrains.kotlin:kotlin-reflect:1.9.23`
- `com.bluecatcode.junit:junit-4.10-extended:1.0.4`

These dependencies are specified in the `build.gradle.kts` file.

### Setup

1. Clone the repository:
   ```bash
    https://github.com/hslau-iscte/Advanced-Programming-Project-XML-Manipulation-Library-.git
2. Open the project in your favorite IDE (e.g., IntelliJ IDEA).

3. Sync the Gradle project to download dependencies.

Usage
XMLElement Class
Represents an XML element with attributes and child elements.

XMLDocument Class

Represents an XML document and provides methods to manipulate it.
Methods

    find(xpath: String): Finds XML elements matching the XPath expression.
    writeToFile(filePath: String): Writes the XML document to a file.
    addGlobalAttribute(entityName: String, attributeName: String, attributeValue: String): Adds an attribute to all elements with the specified name.
    renameGlobalEntity(entityName: String, newName: String): Renames all elements with the specified name.
    renameGlobalAttribute(entityName: String, oldAttributeName: String, newAttributeName: String): Renames attributes for all elements with the specified name.
    removeGlobalEntity(entityName: String): Removes all elements with the specified name.
    removeGlobalAttribute(entityName: String, attributeName: String): Removes attributes for all elements with the specified name.

MicroXPath Object

Provides methods to find XML elements using a simplified XPath.
Methods

    find(root: XMLElement, xpath: String): Finds XML elements matching the XPath expression.
    
Test Cases

Test cases are provided in the MyProtectTestCases.kt file using JUnit 5. Run the test suite to ensure the correctness of the library. Use the provided Gradle task ./gradlew test to execute the tests.
Dependencies

This project has the following dependencies:

    Kotlin Standard Library (kotlin-stdlib-jdk8:1.8.0)
    JUnit Jupiter (org.junit.jupiter:junit-jupiter:5.10.2)
    Kotlin Reflection (org.jetbrains.kotlin:kotlin-reflect:1.9.23)
