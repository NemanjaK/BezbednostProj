package xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class AsymmetricKeyEncryption {
	
	private static final String KEY_STORE_FILE = "./data/usera.jks";

	static {
		// staticka inicijalizacija
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
	}
	/**
	 * Kreira DOM od XML dokumenta
	 */
	public static Document loadDocument(String senderEmail) {
		
		String inFile = "./data/" + senderEmail + "_signed.xml";
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new File(inFile));

			return document;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}

	/**
	 * Ucitava sertifikat is KS fajla alias primer
	 */
	public static Certificate readCertificate() {
		try {

			// kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");

			// ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
			ks.load(in, "123".toCharArray());

			if (ks.isKeyEntry("usera")) {
				Certificate cert = ks.getCertificate("userb");
				
				return cert;
			} else
				return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}

	/**
	 * Snima DOM u XML fajl
	 */
	public static void saveDocument(Document doc, String senderEmail) {
		String outFile = "./data/" + senderEmail + "_signed_enc.xml";

		try {
			File outFileF = new File(outFile);
			FileOutputStream f = new FileOutputStream(outFileF);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(f);

			transformer.transform(source, result);

			f.close();
			System.out.println("Saved");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Generise tajni kljuc
	 */
	public static SecretKey generateDataEncryptionKey() {

		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede"); // Triple
																			// DES
			return keyGenerator.generateKey();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Kriptuje sadrzaj prvog elementa odsek
	 */
	public static Document encrypt(Document doc, SecretKey key, Certificate certificate) {

		try {

			// cipher za kriptovanje XML-a
			XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.TRIPLEDES);
			
			// inicijalizacija za kriptovanje
			xmlCipher.init(XMLCipher.ENCRYPT_MODE, key);

			// cipher za kriptovanje tajnog kljuca,
			// Koristi se Javni RSA kljuc za kriptovanje
			XMLCipher keyCipher = XMLCipher.getInstance(XMLCipher.RSA_v1dot5);
			
			// inicijalizacija za kriptovanje tajnog kljuca javnim RSA kljucem
			keyCipher.init(XMLCipher.WRAP_MODE, certificate.getPublicKey());
			
			// kreiranje EncryptedKey objekta koji sadrzi  enkriptovan tajni (session) kljuc
			EncryptedKey encryptedKey = keyCipher.encryptKey(doc, key);
			
			// u EncryptedData element koji se kriptuje kao KeyInfo stavljamo
			// kriptovan tajni kljuc
			// ovaj element je koreni elemnt XML enkripcije
			EncryptedData encryptedData = xmlCipher.getEncryptedData();
			
			// kreira se KeyInfo element
			KeyInfo keyInfo = new KeyInfo(doc);
			
			// postavljamo naziv 
			keyInfo.addKeyName("Kriptovani tajni kljuc");
			
			// postavljamo kriptovani kljuc
			keyInfo.add(encryptedKey);
			
			// postavljamo KeyInfo za element koji se kriptuje
			encryptedData.setKeyInfo(keyInfo);

			// trazi se element ciji sadrzaj se kriptuje
			NodeList emails = doc.getElementsByTagName("email");
			Element email = (Element) emails.item(0);
			

			xmlCipher.doFinal(doc, doc.getDocumentElement(), true); // kriptuje sa sadrzaj

			return doc;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
}
