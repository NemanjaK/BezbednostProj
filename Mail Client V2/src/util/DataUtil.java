package util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.apache.log4j.BasicConfigurator;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DataUtil {

	static {

		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
	}

	public static void generateXML(String emailSender, String subject, String body) {
		final String xmlFilePath = "./data/" + emailSender + ".xml";
		
		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder icBuilder;
	    
	    try {
	    	icBuilder = icFactory.newDocumentBuilder();
	    	Document doc = icBuilder.newDocument();
	    	Element mainRootElement = doc.createElement("email");
	    	doc.appendChild(mainRootElement);
	    	
	    	mainRootElement.appendChild(getEmailElements(doc, "subject", subject));
	    	mainRootElement.appendChild(getEmailElements(doc, "body", body));
	    	
	    	Transformer transformer = TransformerFactory.newInstance().newTransformer();
    		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
	        DOMSource source = new DOMSource(doc);
	         
	        StreamResult streamResult = new StreamResult(new File(xmlFilePath));
	        transformer.transform(source, streamResult);
	    } catch (Exception e) {
	         e.printStackTrace();
	    }
	}
	
	public static Document convertStringToXMLDocument(String xmlString) 
    {
		Document doc = null;
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         
        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();
             
            //Parse the content to Document object
            doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return doc;
    }

    // Konvertovanje XML dokumenta u String
    public static String XmlDocumentToString(Document xmlDocument) {
    	
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        String xmlString = null;
        
        try {
            transformer = tf.newTransformer();
             
            // Uncomment if you do not require XML declaration
            //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
             
            //A character stream that collects its output in a string buffer, 
            //which can then be used to construct a string.
            StringWriter writer = new StringWriter();
     
            //transform document to string 
            transformer.transform(new DOMSource(xmlDocument), new StreamResult(writer));
     
            xmlString = writer.getBuffer().toString();   
            System.out.println(xmlString);                      //Print to console or logs
        } 
        catch (TransformerException e) 
        {
            e.printStackTrace();
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return xmlString;
    }

	private static Node getEmailElements(Document doc, String name, String value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}

	public static Document loadDocument() {

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new File("./data/message.xml"));

			return document;
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static boolean verifySiganture(Document doc, X509Certificate cer) {

		try {

			NodeList nodes = doc.getElementsByTagName("ds:Signature");

			if (nodes == null || nodes.getLength() == 0) {
				throw new Exception("Can't find signature in document.");
			}

			DOMValidateContext ctx = new DOMValidateContext(cer.getPublicKey(), nodes.item(0));
			XMLSignatureFactory sigF = XMLSignatureFactory.getInstance("DOM");
			javax.xml.crypto.dsig.XMLSignature xmlSignature = sigF.unmarshalXMLSignature(ctx);

			return xmlSignature.validate(ctx);

		} catch (Exception ex) {

			ex.printStackTrace();
			return false;
		}
	}

}
