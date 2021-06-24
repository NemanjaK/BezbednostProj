package app;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.BasicConfigurator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
          //  final String path = "./data/" + from + "_enc.xml";
                
        	System.out.println("Insert a reciever:");
            String to = reader.readLine();
        	
            System.out.println("Insert a subject:");
            String subject = reader.readLine();
            
            System.out.println("Insert body:");
            String body = reader.readLine();
            
            //kreiranje xml fajla
			DataUtil.generateXML(from,subject, body);		
			
        	Document doc = SignEnveloped.loadDocument(from);
			PrivateKey pk = SignEnveloped.readPrivateKey();           
        	Certificate certFrom = SignEnveloped.readCertificate();
        	
    		System.out.println("Signing....");
        	doc = SignEnveloped.signDocument(doc, pk, certFrom);
            SignEnveloped.saveDocument(doc, from);
    		System.out.println("Signed");

            Document doc2 = AsymmetricKeyEncryption.loadDocument(from);
            SecretKey secretKey = AsymmetricKeyEncryption.generateDataEncryptionKey();     
            Certificate certTo = AsymmetricKeyEncryption.readCertificate();
            doc2 = AsymmetricKeyEncryption.encrypt(doc2, secretKey, certTo);
            AsymmetricKeyEncryption.saveDocument(doc2, from);
            
	        Document encrDoc = DataUtil.convertXMLFileToXMLDocument(from);
	        
	        String xmlString = DataUtil.XmlDocumentToString(encrDoc);
	        System.out.println(xmlString);
	        
	        // Slanje enkriptovanog XML-a u body-ju poruke	        
            MimeMessage mimeMessage = MailHelper.createMimeMessage(to,"Encrypted message!", xmlString);
            MailWritter.sendMessage(service, "me", mimeMessage);
           
        	
        }catch (Exception e) {
        	e.printStackTrace();
		}
	}

}
