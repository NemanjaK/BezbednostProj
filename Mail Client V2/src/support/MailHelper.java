package support;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MailHelper {
	/**
     * Return the primary text content of the message.
     * Source: https://gist.github.com/nutanc/c0d3fe354a8fdb75e1ef
     */
    public static String getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String) p.getContent();
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer HTML text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null) {
                        text = getText(bp);
                    }
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null) {
                        return s;
                    }
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null) {
                    return s;
                }
            }
        }

        return null;
    }
    
    
    public static MimeMessage createMimeMessage(String reciever,String subject, String  body) throws MessagingException {
    	
    	Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);
    	MimeMessage message = new MimeMessage(session);
    	
    	message.setRecipient(Message.RecipientType.TO, new InternetAddress(reciever));
    	message.setSubject(subject);
    	message.setText(body);
    	
    	return message;
    }
    
	public static void printEmail(Document doc) {
		Node fc = doc.getFirstChild();
		NodeList list = fc.getChildNodes();
		for (int i = 0; i <list.getLength(); i++) {
			Node node = list.item(i);
			if("subject".equals(node.getNodeName())) {
				System.out.println("Subject: " + node.getTextContent());
			}
			if("body".equals(node.getNodeName())) {
				System.out.println("Body: " + node.getTextContent());
			}
		}
	}
    
}
