package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.BasicConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import model.mailclient.MailBody;
import support.MailHelper;
import support.MailReader;
import util.Base64;
import util.DataUtil;
import util.GzipUtil;
import xml.AsymmetricKeyDecryption;
import xml.AsymmetricKeyEncryption;
import xml.SignEnveloped;
import xml.VerifySignatureEnveloped;

public class ReadMailClient extends MailClient {

	public static long PAGE_SIZE = 2;
	public static boolean ONLY_FIRST_PAGE = true;
	
	public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, MessagingException, NoSuchPaddingException, InvalidAlgorithmParameterException, KeyStoreException, CertificateException, UnrecoverableKeyException {

		// Build a new authorized API client service.
        Gmail service = getGmailService();
        ArrayList<MimeMessage> mimeMessages = new ArrayList<MimeMessage>();

        String user = "me";
        String query = "is:unread label:INBOX";
        
        String sender = "";
        String reciever = "";
        
        List<Message> messages = MailReader.listMessagesMatchingQuery(service, user, query, PAGE_SIZE, ONLY_FIRST_PAGE);
        for(int i=0; i<messages.size(); i++) {
        	Message fullM = MailReader.getMessage(service, user, messages.get(i).getId());
        	
        	MimeMessage mimeMessage;
			try {
				
				mimeMessage = MailReader.getMimeMessage(service, user, fullM.getId());
				
				sender = mimeMessage.getHeader("From", null);
				reciever = mimeMessage.getHeader("To", null);
				
				System.out.println("\nMessage number " + i);
				System.out.println("From: " + sender);
				System.out.println("Subject: " + mimeMessage.getSubject());
				System.out.println("Body: " + MailHelper.getText(mimeMessage));
				System.out.println("\n");
				
				mimeMessages.add(mimeMessage);
	        
			} catch (MessagingException e) {
				e.printStackTrace();
			}	
        }
        
        System.out.println("Select a message to decrypt:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	        
	    String answerStr = reader.readLine();
	    Integer answer = Integer.parseInt(answerStr);
	    
		MimeMessage chosenMessage = mimeMessages.get(answer);
		
    	Document doc = AsymmetricKeyDecryption.loadDocument(sender);
		PrivateKey pk = AsymmetricKeyDecryption.readPrivateKey();           
		System.out.println("Decrypting....");
		doc = AsymmetricKeyDecryption.decrypt(doc, pk);
		AsymmetricKeyDecryption.saveDocument(doc, sender);
		System.out.println("Decryption done\n");

		Document doc2 = VerifySignatureEnveloped.loadDocument(sender);
		boolean res = VerifySignatureEnveloped.verifySignature(doc2);
		System.out.println("Verification = " + res + "\n");

		// opet proverava potpis, ali je dokument menjan
		System.out.println("");
		System.out.println("<-----TEST CASE FOR CHANGED MESSAGE CONTENT-irregular signature------>");
		
		System.out.println("Changing message content....");
		Node fc = doc2.getFirstChild();
		NodeList list = fc.getChildNodes();
		for (int i = 0; i <list.getLength(); i++) {
			Node node = list.item(i);
			if("subject".equals(node.getNodeName())) {
				node.setTextContent("changed subject");
			}
		}
		boolean res1 = VerifySignatureEnveloped.verifySignature(doc2);
		System.out.println("Verification = " + res1 + "\n");
		
		
		System.out.println("From: " + sender);
		Document docum = AsymmetricKeyEncryption.loadDocument(sender);
		MailHelper.printEmail(docum);
	}
}
