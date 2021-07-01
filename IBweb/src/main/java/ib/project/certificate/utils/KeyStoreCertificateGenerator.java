package ib.project.certificate.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bouncycastle.cert.X509CRLHolder;

import ib.project.certificate.model.IssuerData;
import ib.project.certificate.model.SubjectData;
import ib.project.keystore.KeyStoreWriter;
import ib.project.model.User;

public class KeyStoreCertificateGenerator {

	// Kreiranje self-signed sertifikata i kreiranje keystore-a u korisnički
	// direktorijum
	private boolean generateKeyStore(User user) {
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
			startDate = iso8601Formater.parse("2021-02-02");
			endDate = iso8601Formater.parse("2021-12-12");

			KeyPair keyPair2 = cg.generateKeyPair();

			String email = user.getEmail();
			char[] password = email.toCharArray();
			String id = Long.toString(user.getId());

			SubjectData subjectData2 = new SubjectData(keyPair2.getPublic(), "FTN", email, "Katedra za informatiku",
					"RS", email, "12345", "1", startDate, endDate);
			X509Certificate cert = cg.generateCertificate(issuerData, subjectData2);

			// kreiranje i čuvanje keystore-a
			KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
			keyStoreWriter.loadKeyStore(null, password);
			keyStoreWriter.write(email, keyPair2.getPrivate(), password, cert);
			keyStoreWriter.saveKeyStore("data/" + id + "/keystore.jks", password);

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// Kreiranje korisnickog direktorijuma
	private void createDirectories(String id) {

		Path userDir = Paths.get("data/" + id);
		Path userImagesDir = Paths.get("data/" + id + "/img");

		try {
			Files.createDirectory(userDir);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			Files.createDirectory(userImagesDir);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
