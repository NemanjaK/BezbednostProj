package ib.project.certificate.utils;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

public class CRLManager {

	public static X509CRLHolder createCRL(X509Certificate pub, PrivateKey priv)
			throws CertificateParsingException, InvalidKeyException, NoSuchProviderException, SecurityException,
			SignatureException, CertificateEncodingException, CertIOException, NoSuchAlgorithmException,
			OperatorCreationException, FileNotFoundException {
		/*
		 * Kreira se prazna CRL lista potpisana privatnim kljucem
		 */
		Date now = new Date();
		X509v2CRLBuilder crlGen = new X509v2CRLBuilder(new X500Name(pub.getSubjectDN().getName()), now);

		Date nextUpdate = new Date(now.getTime() + 30 * 24 * 60 * 60 * 1000); // Trebalo bi da se azurira svakih 30 dana
		PrivateKey caCrlPrivateKey = priv;

		crlGen.setNextUpdate(nextUpdate);

		// Kreiramo novu CRL listu pa joj dajemo broj 1
		crlGen.addExtension(X509Extension.cRLNumber, false, new CRLNumber(BigInteger.valueOf(1)));

		// Potpisivanje privatnim kljucem CA
		ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC")
				.build(caCrlPrivateKey);
		X509CRLHolder crlholder = crlGen.build(contentSigner);

		return crlholder;
	}

	public static X509CRLHolder updateCRL(X509CRLHolder crl, X509Certificate pub, PrivateKey priv, BigInteger serial,
			int reason) {
		/*
		 * Azuriranje CRL liste. Prosledjuje se serijski broj sertifikata koji se
		 * povlaci
		 */
		Security.addProvider(new BouncyCastleProvider());
		try {
			Date now = new Date();
			X509v2CRLBuilder crlGen = new X509v2CRLBuilder(crl.getIssuer(), now);
			Date nextUpdate = new Date(now.getTime() + 30 * 24 * 60 * 60 * 1000);

			// Dodavanje postojece CRL liste u novu listu
			crlGen.addCRL(crl);

			// Dodavanje serijskog broja sertifikata koji se povlaci uz navodjenje razloga
			// povlacenja kao i trenutka povlacenja
			crlGen.addCRLEntry(serial, now, reason);

			crlGen.setNextUpdate(nextUpdate);

			Extension ex = crl.getExtension(X509Extension.cRLNumber);
			// Azuriranje broja CRL liste - inkrementiranje broja za 1
			BigInteger newnumber = new BigInteger(ex.getParsedValue().toString()).add(BigInteger.ONE);

			crlGen.addExtension(X509Extension.authorityKeyIdentifier, false,
					new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(pub));
			crlGen.addExtension(X509Extension.cRLNumber, false, new CRLNumber(newnumber));

			ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(priv);
			X509CRLHolder crlholder = crlGen.build(contentSigner);

			return crlholder;
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean isCRLValid(X509CRLHolder crl, X509Certificate caCert) {
		/*
		 * Provera da li je CRL lista ispravna, tj. da li je potpisana prosledjenim CA
		 * sertifikatom
		 */
		try {
			return crl.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider("BC").build(caCert));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static X509CRL CRLFromCrlHolder(X509CRLHolder crlh) {
		/*
		 * Kreiranje X509CRL liste iz X509CRLHolder-a
		 */
		Security.addProvider(new BouncyCastleProvider());
		JcaX509CRLConverter crlConverter = new JcaX509CRLConverter().setProvider("BC");
		try {
			return crlConverter.getCRL(crlh);
		} catch (CRLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
