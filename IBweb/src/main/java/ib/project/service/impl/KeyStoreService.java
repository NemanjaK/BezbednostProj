package ib.project.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bouncycastle.cert.X509CRLHolder;
import org.springframework.stereotype.Service;

import ib.project.certificate.model.IssuerData;
import ib.project.certificate.model.SubjectData;
import ib.project.certificate.utils.CRLManager;
import ib.project.certificate.utils.CertificateGenerator;
import ib.project.keystore.KeyStoreWriter;
import ib.project.model.User;

@Service
public class KeyStoreService {

	// Kreiranje self-signed sertifikata i kreiranje keystore-a u korisnički
	// direktorijum
	public boolean generateKeyStore(User user) {
		CertificateGenerator cg = new CertificateGenerator();

		try {
			SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = iso8601Formater.parse("2019-01-01");
			Date endDate = iso8601Formater.parse("2024-01-01");
			KeyPair keyPairCA = cg.generateKeyPair();

			IssuerData issuerData = new IssuerData("FTN", "Fakultet tehnickih nauka", "Katedra za informatiku", "RS",
					"ftnmail@uns.ac.rs", "123445", keyPairCA.getPrivate());
			SubjectData subjectData1 = new SubjectData(keyPairCA.getPublic(), issuerData.getX500name(), "1", startDate,
					endDate);

			X509Certificate certCA = cg.generateCertificate(issuerData, subjectData1);
			X509CRLHolder crlHolder = CRLManager.createCRL(certCA, keyPairCA.getPrivate());

			// Kreiranje sertifikata potpisanog od strane CA
			startDate = iso8601Formater.parse("2021-02-07");
			endDate = iso8601Formater.parse("2022-10-7");

			KeyPair keyPair2 = cg.generateKeyPair();

			String email = user.getEmail();
			String shortMail = email.substring(0, email.indexOf("@"));

			char[] password = shortMail.toCharArray();

			SubjectData subjectData2 = new SubjectData(keyPair2.getPublic(), "FTN", email, "Katedra za informatiku",
					"RS", email, "12345", "1", startDate, endDate);
			X509Certificate cert = cg.generateCertificate(issuerData, subjectData2);

			// kreiranje i čuvanje keystore-a
			KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
			keyStoreWriter.loadKeyStore(null, password);
			keyStoreWriter.write(email, keyPair2.getPrivate(), password, cert);
			keyStoreWriter.saveKeyStore("data/" + shortMail + ".jks", password);

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// Kreiranje korisnickog direktorijuma
	public void createDirectory(String id) {

		Path userDir = Paths.get("data/" + id);

		try {
			Files.createDirectory(userDir);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static byte[] readBytesFromFile(String filePath) {

		FileInputStream fileInputStream = null;
		byte[] bytesArray = null;
		try {

			File file = new File(filePath);
			bytesArray = new byte[(int) file.length()];

			// read file into bytes[]
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bytesArray);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return bytesArray;
	}
}
