package org.dotwebstack.framework.frontend.soap.wsdlreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class was extracted from the soapUI code base by centeractive ag in October 2011.
 * The main reason behind the extraction was to separate the code that is responsible
 * for the generation of the SOAP messages from the rest of the soapUI's code that is
 * tightly coupled with other modules, such as soapUI's graphical user interface, etc.
 * The goal was to create an open-source java project whose main responsibility is to
 * handle SOAP message generation and SOAP transmission purely on an XML level.
 * <br/>
 * centeractive ag would like to express strong appreciation to SmartBear Software and
 * to the whole team of soapUI's developers for creating soapUI and for releasing its
 * source code under a free and open-source licence. centeractive ag extracted and
 * modifies some parts of the soapUI's code in good faith, making every effort not
 * to impair any existing functionality and to supplement it according to our
 * requirements, applying best practices of software design.
 *
 * <p>Changes done:
 * - changing location in the package structure
 * - removal of dependencies and code parts that are out of scope of SOAP message generation
 * - minor fixes to make the class compile out of soapUI's code base
 * - adding utility method to handle XML-String from/to XML-Source transformation
 */

/**
 * General XML-related utilities
 */
@SuppressWarnings("deprecation")
final class XmlUtils {
  private static final Logger LOG = LoggerFactory.getLogger(XmlUtils.class);
  private static DocumentBuilder documentBuilder;

  public static synchronized Document parse(InputStream in) {
    try {
      return ensureDocumentBuilder().parse(in);
    } catch (Exception e) {
      LOG.error("Error parsing InputStream; " + e.getMessage());
    }

    return null;
  }

  public static synchronized Document parse(String fileName) throws IOException {
    try {
      return ensureDocumentBuilder().parse(fileName);
    } catch (SAXException e) {
      LOG.error("Error parsing fileName [" + fileName + "]; " + e.getMessage());
    }

    return null;
  }


  public static synchronized Document parse(InputSource inputSource) throws IOException {
    try {
      return ensureDocumentBuilder().parse(inputSource);
    } catch (SAXException e) {
      throw new IOException(e.toString());
    }
  }

  private static final class ElementNodeList implements NodeList {
    private final List<Element> list;

    public ElementNodeList(List<Element> list) {
      this.list = list;
    }

    public int getLength() {
      return list.size();
    }

    public Node item(int index) {
      return list.get(index);
    }
  }

  public static String getChildElementText(Element elm, String name) {
    Element child = getFirstChildElement(elm, name);
    return child == null ? null : getElementText(child);
  }

  public static String getChildElementText(Element elm, String name, String defaultValue) {
    String result = getChildElementText(elm, name);
    return result == null ? defaultValue : result;
  }

  public static Element getFirstChildElement(Element elm) {
    return getFirstChildElement(elm, null);
  }

  public static Element getFirstChildElement(Element elm, String name) {
    if (elm == null) {
      return null;
    }

    NodeList nl = elm.getChildNodes();
    for (int c = 0; c < nl.getLength(); c++) {
      Node node = nl.item(c);
      if (node.getNodeType() == Node.ELEMENT_NODE
          && (name == null || node.getNodeName().equals(name))) {
        return (Element) node;
      }
    }

    return null;
  }

  public static String getElementText(Element elm) {
    Node node = elm.getFirstChild();
    if (node != null && node.getNodeType() == Node.TEXT_NODE) {
      return node.getNodeValue();
    }

    return null;
  }

  public static String getFragmentText(DocumentFragment elm) {
    Node node = elm.getFirstChild();
    if (node != null && node.getNodeType() == Node.TEXT_NODE) {
      return node.getNodeValue();
    }

    return null;
  }

  public static String getNodeValue(Node node) {
    if (node == null) {
      return null;
    }

    if (node.getNodeType() == Node.ELEMENT_NODE) {
      return getElementText((Element) node);
    } else if (node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
      return getFragmentText((DocumentFragment) node);
    } else {
      return node.getNodeValue();
    }
  }

  public static String removeUnneccessaryNamespaces(String xml) {
    if (StringUtils.isBlank(xml)) {
      return xml;
    }

    XmlObject xmlObject = null;
    XmlCursor cursor = null;
    try {
      xmlObject = XmlObject.Factory.parse(xml);

      cursor = xmlObject.newCursor();
      while (cursor.currentTokenType() != XmlCursor.TokenType.START
          && cursor.currentTokenType() != XmlCursor.TokenType.ENDDOC) {
        cursor.toNextToken();
      }

      if (cursor.currentTokenType() == XmlCursor.TokenType.START) {
        Map<?, ?> nsMap = new HashMap<Object, Object>();

        cursor.getAllNamespaces(nsMap);
        nsMap.remove(cursor.getDomNode().getPrefix());

        NamedNodeMap attributes = cursor.getDomNode().getAttributes();
        for (int c = 0; attributes != null && c < attributes.getLength(); c++) {
          nsMap.remove(attributes.item(c).getPrefix());
        }

        if (cursor.toFirstChild()) {
          while (cursor.getDomNode() != xmlObject.getDomNode()) {
            attributes = cursor.getDomNode().getAttributes();
            for (int c = 0; attributes != null && c < attributes.getLength(); c++) {
              nsMap.remove(attributes.item(c).getPrefix());
            }

            nsMap.remove(cursor.getDomNode().getPrefix());
            cursor.toNextToken();
          }
        }

        xml = xmlObject.xmlText(new XmlOptions().setSaveOuter().setSavePrettyPrint()
            .setSaveImplicitNamespaces(nsMap));
      }
    } catch (XmlException e) {
      LOG.error("Caught XmlException in XmlUtils.removeUnneccessaryNamespaces");
    } finally {
      if (cursor != null) {
        cursor.dispose();
      }
    }

    return xml;
  }

  private static DocumentBuilder ensureDocumentBuilder() {
    if (documentBuilder == null) {
      try {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        documentBuilder = dbf.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
        LOG.error("Error creating DocumentBuilder; " + e.getMessage());
      }
    }

    return documentBuilder;
  }


  public static XmlObject createXmlObject(String input, XmlOptions xmlOptions)
      throws XmlException {
    return XmlObject.Factory.parse(input, xmlOptions);
  }

  public static XmlObject createXmlObject(URL input, XmlOptions xmlOptions) throws XmlException {
    try {
      return XmlObject.Factory.parse(input, xmlOptions);
    } catch (Exception e) {
      throw new XmlException(e.toString());
    }
  }


  public static XmlObject createXmlObject(String input) throws XmlException {
    return XmlObject.Factory.parse(input);
  }

  public static XmlObject createXmlObject(URL input) throws XmlException {
    try {
      return XmlObject.Factory.parse(input);
    } catch (Exception e) {

      throw new XmlException(e);
    }
  }


  public static void setElementText(Element elm, String text) {
    Node node = elm.getFirstChild();
    if (node == null) {
      if (text != null) {
        elm.appendChild(elm.getOwnerDocument().createTextNode(text));
      }
    } else if (node.getNodeType() == Node.TEXT_NODE) {
      if (text == null) {
        node.getParentNode().removeChild(node);
      } else {
        node.setNodeValue(text);
      }
    } else if (text != null) {
      Text textNode = node.getOwnerDocument().createTextNode(text);
      elm.insertBefore(textNode, elm.getFirstChild());
    }
  }

  public static QName getQName(XmlObject contentElement) {
    return contentElement == null ? null : getQName(contentElement.getDomNode());
  }

  public static QName getQName(Node node) {
    if (node == null) {
      return null;
    } else if (node.getNamespaceURI() == null) {
      return new QName(node.getNodeName());
    } else {
      return new QName(node.getNamespaceURI(), node.getLocalName());
    }
  }

  public static boolean setNodeValue(Node domNode, String string) {
    if (domNode == null) {
      return false;
    }

    short nodeType = domNode.getNodeType();

    switch (nodeType) {
      case Node.ELEMENT_NODE: {
        setElementText((Element) domNode, string);
        break;
      }
      case Node.ATTRIBUTE_NODE:
      case Node.TEXT_NODE: {
        domNode.setNodeValue(string);
        break;
      }
      case Node.PROCESSING_INSTRUCTION_NODE: {
        ((ProcessingInstruction) domNode).setData(string);
        break;
      }
      case Node.CDATA_SECTION_NODE: {
        ((CDATASection) domNode).setData(string);
        break;
      }
      default: {
        return false;
      }
    }

    return true;
  }

  public static void serializePretty(XmlObject xmlObject, Writer writer) throws IOException {
    XmlOptions options = new XmlOptions();
    options.setSavePrettyPrint();
    options.setSavePrettyPrintIndent(3);
    options.setSaveNoXmlDecl();
    options.setSaveAggressiveNamespaces();
    // StringToStringMap map = new StringToStringMap();
    // map.put( SoapVersion.Soap11.getEnvelopeNamespace(), "SOAPENV" );
    // map.put( SoapVersion.Soap12.getEnvelopeNamespace(), "SOAPENV" );
    //
    // options.setSaveSuggestedPrefixes( map );
    xmlObject.save(writer, options);
  }

}