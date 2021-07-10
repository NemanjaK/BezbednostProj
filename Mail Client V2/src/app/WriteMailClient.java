package app;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;

import javax.crypto.SecretKey;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;

import com.google.api.services.gmail.Gmail;


import xml.AsymmetricKeyEncryption;
import xml.SignEnveloped;
import support.MailHelper;
import support.MailWritter;
import util.DataUtil;

public class WriteMailClient extends MailClient {
	
	public static void main(String[] args) {
		//BasicConfigurator.configure();
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
		
        try {
        	Gmail service = getGmailService();
            
        	System.out.println("Your email:");
        	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String from = reader.readLine();
                
        	System.out.println("Insert a reciever:");
            String to = reader.readLine();
        	
            System.out.println("Insert a subject:");
            String subject = reader.readLine();
            
            System.out.println("Insert body:");
            String body = reader.readLine();
            
            //Kreiramo XML file
			DataUtil.generateXML(from,subject, body);		
			
			//Potpisujemo  XML file
        	Document doc = SignEnveloped.loadDocument(from);
			PrivateKey pk = SignEnveloped.readPrivateKey();           
        	Certificate certFrom = SignEnveloped.readCertificate();
        	
    		System.out.println("Signing....");
        	doc = SignEnveloped.signDocument(doc, pk, certFrom);
            SignEnveloped.saveDocument(doc, from);
    		System.out.println("Signed");

    		// Enkrtiptovanje
            Document doc2 = AsymmetricKeyEncryption.loadDocument(from);

            SecretKey secretKey = AsymmetricKeyEncryption.generateDataEncryptionKey();     
            Certificate cert = AsymmetricKeyEncryption.readCertificate();
            doc2 = AsymmetricKeyEncryption.encrypt(doc2, secretKey, cert);
            AsymmetricKeyEncryption.saveDocument(doc2, from);
            
	        // Konvertovanje enkriptovanog XML dokumenta u DOM
	        Document encrDoc = convertXMLFileToXMLDocument(from);
	        
	        // XML document to String
	        String xmlString = DataUtil.XmlDocumentToString(encrDoc);
	        System.out.println(xmlString);
	        
	        // Slanje enkriptovanog XML-a u body-ju poruke	        
            MimeMessage mimeMessage = MailHelper.createMimeMessage(to,"Encrypted message!", xmlString);
            MailWritter.sendMessage(service, "me", mimeMessage);
           
        	
        }catch (Exception e) {
        	e.printStackTrace();
		}
	}
    public static Document convertXMLFileToXMLDocument(String senderEmail) 
    {
    	
		String outFile = "./data/" + senderEmail + "_signed_enc.xml";
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         
        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();
             
            //Parse the content to Document object
            Document xmlDocument = builder.parse(new File(outFile));
             
            return xmlDocument;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return null;
    }

}
