/**
 * JVM specific API implementations using JAXB and so forth which would not be used when compiling to JS
 */
package kotlin.dom

import java.io.File
import java.io.InputStream
import java.io.StringWriter
import java.io.Writer
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.*
import org.xml.sax.InputSource

val Node.nodeName: String
get() = getNodeName() ?: ""

val Node.nodeValue: String
get() = getNodeValue() ?: ""

val Node.nodeType: Short
get() = getNodeType()

val Node.parentNode: Node?
get() = getParentNode()

val Node.childNodes: NodeList
get() = getChildNodes()!!

val Node.firstChild: Node?
get() = getFirstChild()

val Node.lastChild: Node?
get() = getLastChild()

val Node.nextSibling: Node?
get() = getNextSibling()

val Node.previousSibling: Node?
get() = getPreviousSibling()

val Node.attributes: NamedNodeMap?
get() = getAttributes()

val Node.ownerDocument: Document?
get() = getOwnerDocument()

val Document.documentElement: Element?
get() = if (this != null) this.getDocumentElement() else null

val Node.namespaceURI: String
get() = getNamespaceURI() ?: ""

val Node.prefix: String
get() = getPrefix() ?: ""

val Node.localName: String
get() = getLocalName() ?: ""

val Node.baseURI: String
get() = getBaseURI() ?: ""

var Node.textContent: String
get() = getTextContent() ?: ""
set(value) {
    setTextContent(value)
}

val DOMStringList.length: Int
get() = this.getLength()

val NameList.length: Int
get() = this.getLength()

val DOMImplementationList.length: Int
get() = this.getLength()

val NodeList.length: Int
get() = this.getLength()

val CharacterData.length: Int
get() = this.getLength()

val NamedNodeMap.length: Int
get() = this.getLength()


/**
 * Returns the HTML representation of the node
 */
public val Node.outerHTML: String
get() = toXmlString()

/**
 * Returns the HTML representation of the node
 */
public val Node.innerHTML: String
get() = childNodes.outerHTML

/**
 * Returns the HTML representation of the nodes
 */
public val NodeList.outerHTML: String
get() = toList().map<Node, String> { it.innerHTML }.makeString("")

/** Returns an [[Iterator]] of all the next [[Element]] siblings */
fun Node.nextElements(): Iterator<Element> = nextSiblings().filterIsInstance<Node, Element>(javaClass<Element>())

/** Returns an [[Iterator]] of all the previous [[Element]] siblings */
fun Node.previousElements(): Iterator<Element> = previousSiblings().filterIsInstance<Node, Element>(javaClass<Element>())


var Element.classSet : Set<String>
get() {
    val answer = LinkedHashSet<String>()
    val array = this.classes.split("""\s""")
    for (s in array) {
        if (s != null && s.size > 0) {
            answer.add(s)
        }
    }
    return answer
}
set(value) {
    this.classes = value.makeString(" ")
}

/** Adds the given CSS class to this element's 'class' attribute */
fun Element.addClass(cssClass: String): Boolean {
    val classSet = this.classSet
    val answer = classSet.add(cssClass)
    if (answer) {
        this.classSet = classSet
    }
    return answer
}

/** Removes the given CSS class to this element's 'class' attribute */
fun Element.removeClass(cssClass: String): Boolean {
    val classSet = this.classSet
    val answer = classSet.remove(cssClass)
    if (answer) {
        this.classSet = classSet
    }
    return answer
}



/** Creates a new document with the given document builder*/
public fun createDocument(builder: DocumentBuilder): Document {
    return builder.newDocument().sure()
}

/** Creates a new document with an optional DocumentBuilderFactory */
public fun createDocument(builderFactory: DocumentBuilderFactory = defaultDocumentBuilderFactory()): Document {
    return createDocument(builderFactory.newDocumentBuilder()!!)
}

/**
 * Returns the default [[DocumentBuilderFactory]]
 */
public fun defaultDocumentBuilderFactory(): DocumentBuilderFactory {
    return DocumentBuilderFactory.newInstance()!!
}

/**
 * Returns the default [[DocumentBuilder]]
 */
public fun defaultDocumentBuilder(builderFactory: DocumentBuilderFactory = defaultDocumentBuilderFactory()): DocumentBuilder {
    return builderFactory.newDocumentBuilder()!!
}

/**
 * Parses the XML document using the given *file*
 */
public fun parseXml(file: File, builder: DocumentBuilder = defaultDocumentBuilder()): Document {
    return builder.parse(file)!!
}

/**
 * Parses the XML document using the given *uri*
 */
public fun parseXml(uri: String, builder: DocumentBuilder = defaultDocumentBuilder()): Document {
    return builder.parse(uri)!!
}

/**
 * Parses the XML document using the given *inputStream*
 */
public fun parseXml(inputStream: InputStream, builder: DocumentBuilder = defaultDocumentBuilder()): Document {
    return builder.parse(inputStream)!!
}

/**
 * Parses the XML document using the given *inputSource*
 */
public fun parseXml(inputSource: InputSource, builder: DocumentBuilder = defaultDocumentBuilder()): Document {
    return builder.parse(inputSource)!!
}


/** Creates a new TrAX transformer */
public fun createTransformer(source: Source? = null, factory: TransformerFactory = TransformerFactory.newInstance().sure()): Transformer {
    val transformer = if (source != null) {
        factory.newTransformer(source)
    } else {
        factory.newTransformer()
    }
    return transformer.sure()
}

/** Converts the node to an XML String */
public fun Node.toXmlString(): String = toXmlString(false)

/** Converts the node to an XML String */
public fun Node.toXmlString(xmlDeclaration: Boolean): String {
    val writer = StringWriter()
    writeXmlString(writer, xmlDeclaration)
    return writer.toString().sure()
}

/** Converts the node to an XML String and writes it to the given [[Writer]] */
public fun Node.writeXmlString(writer: Writer, xmlDeclaration: Boolean): Unit {
    val transformer = createTransformer()
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, if (xmlDeclaration) "no" else "yes")
    transformer.transform(DOMSource(this), StreamResult(writer))
}
