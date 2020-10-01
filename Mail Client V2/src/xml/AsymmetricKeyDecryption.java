package xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.encryption.XMLCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class AsymmetricKeyDecryption {

	//private static final String IN_FILE = "./data/univerzitet_enc2.xml";
	//private static final String OUT_FILE = "./data/univerzitet_dec2.xml";
	private static final String KEY_STORE_FILE = "./data/userb.jks";

	static {
		// staticka inicijalizacija
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
	}

	public static void testIt(String senderEmail, String recieverEmail) {
		String inFile = "./data/" + senderEmail + "_enc.xml";
		String outFile = "./data/" + senderEmail + "_dec.xml";
		
		Document doc = loadDocument(inFile);

		PrivateKey pk = readPrivateKey();

		System.out.println("Decrypting....");
		doc = decrypt(doc, pk);

		saveDocument(doc, outFile);
		System.out.println("Decryption done\n");
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
	 * Ucitava privatni kljuc is KS fajla alias primer
	 */
	private static PrivateKey readPrivateKey() {
		try {

			// kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");

			// ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
			ks.load(in, "123".toCharArray());

			if (ks.isKeyEntry("userb")) {
				PrivateKey pk = (PrivateKey) ks.getKey("userb", "123".toCharArray());
				return pk;
			} else
				return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Kriptuje sadrzaj prvog elementa odsek
	 */
	private static Document decrypt(Document doc, PrivateKey privateKey) {

		try {
			// cipher za dekritpovanje XML-a
			XMLCipher xmlCipher = XMLCipher.getInstance();

			// inicijalizacija za dekriptovanje
			xmlCipher.init(XMLCipher.DECRYPT_MODE, null);

			// postavlja se kljuc za dekriptovanje tajnog kljuca
			xmlCipher.setKEK(privateKey);

			// trazi se prvi EncryptedData element
			NodeList encDataList = doc.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
			Element encData = (Element) encDataList.item(0);

			// dekriptuje se
			// pri cemu se prvo dekriptuje tajni kljuc, pa onda njime podaci
			xmlCipher.doFinal(doc, encData);

			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
