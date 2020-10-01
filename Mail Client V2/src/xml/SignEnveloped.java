package xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//Potpisuje dokument, koristi se enveloped tip
public class SignEnveloped {
	
	//private static final String IN_FILE = "./data/univerzitet.xml";
	//private static final String OUT_FILE = "./data/univerzitet_signed1.xml";
	private static final String KEY_STORE_FILE = "./data/usera.jks";
	
  static {
  	//staticka inicijalizacija
      Security.addProvider(new BouncyCastleProvider());
      org.apache.xml.security.Init.init();
  }
	
	public static void testIt(String senderEmail) {
		
		String inFile = "./data/" + senderEmail + ".xml";
		String outFile = "./data/" + senderEmail + "_signed.xml";
		
		Document doc = loadDocument(inFile);
		
		PrivateKey pk = readPrivateKey();

		Certificate cert = readCertificate();
		
		System.out.println("Signing....");
		doc = signDocument(doc, pk, cert);
		
		//snima se dokument
		saveDocument(doc, outFile);
		System.out.println("Signing of document done");
	}
	
	/**
	 * Kreira DOM od XML dokumenta
	 */
	private static Document loadDocument(String file) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new File(file));

			return document;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * Snima DOM u XML fajl 
	 */
	private static void saveDocument(Document doc, String fileName) {
		try {
			File outFile = new File(fileName);
			FileOutputStream f = new FileOutputStream(outFile);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(f);
			
			transformer.transform(source, result);

			f.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Ucitava sertifikat is KS fajla
	 * alias primer
	 */
	private static Certificate readCertificate() {
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			
			//ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
			ks.load(in, "123".toCharArray());
			
			if(ks.isKeyEntry("usera")) {
				Certificate cert = ks.getCertificate("usera");
				return cert;
				
			}
			else
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * Ucitava privatni kljuc is KS fajla
	 * alias primer
	 */
	private static PrivateKey readPrivateKey() {
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			
			//ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
			ks.load(in, "123".toCharArray());
			
			if(ks.isKeyEntry("usera")) {
				PrivateKey pk = (PrivateKey) ks.getKey("usera", "123".toCharArray());
				return pk;
			}
			else
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static Document signDocument(Document doc, PrivateKey privateKey, Certificate cert) {
      
      try {
			Element rootEl = doc.getDocumentElement();
			
			//kreira se signature objekat
			XMLSignature sig = new XMLSignature(doc, null, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
			
			//kreiraju se transformacije nad dokumentom
			Transforms transforms = new Transforms(doc);
			    
			//iz potpisa uklanja Signature element
			//Ovo je potrebno za enveloped tip po specifikaciji
			transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
			
			//normalizacija
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
			    
			//potpisuje se citav dokument (URI "")
			sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
			    
			//U KeyInfo se postavalja Javni kljuc samostalno i citav sertifikat
			sig.addKeyInfo(cert.getPublicKey());
			sig.addKeyInfo((X509Certificate) cert);
			    
			//poptis je child root elementa
			rootEl.appendChild(sig.getElement());
			
			//potpisivanje
			sig.sign(privateKey);
			
			return doc;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
}
