package security.ws.signature;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class SignatureManager {
	
	private String _keystorePassword;
	private String _keyPassword;
	private String _caCert;
	
	public SignatureManager(String keystorePass, String keyPass, String caCert) {
		_keystorePassword = keystorePass;
		_keyPassword = keyPass;
		_caCert = caCert;
	}

	public String sign (String orig, String text2Sign) throws Exception{
		String _origin = orig;
		String _text2Sign = text2Sign;
		// get signature stuff
		DigitalSignatureX509 Sign = new DigitalSignatureX509();
		String keyStorePath = "keys//" + _origin + ".jks";
		char[] keyStorePass = _keystorePassword.toCharArray();
		String kAlias = _origin;
		char[] kPass = _keyPassword.toCharArray();
		PrivateKey privateKey = Sign.getPrivateKeyFromKeystore(keyStorePath, keyStorePass, kAlias, kPass);

		// sign with counter, destination, sender and bodytext

		byte[] bytesToSign = _text2Sign.getBytes();
		byte[] digitalSignature = Sign.makeDigitalSignature(bytesToSign, privateKey);
		String _signatureText = printBase64Binary(digitalSignature);

		return _signatureText;
	}

	public boolean verify (String send, String signatureValue, String text2verify) throws Exception	{

		// verify signature
		DigitalSignatureX509 Sign = new DigitalSignatureX509();
		byte[] signatureBytes = parseBase64Binary(signatureValue);

		String certificateFile = "keys//" + send + ".cer";
		
		X509CertificateCheck caCheck = new X509CertificateCheck();
		// get the sender's public certificate file
		Certificate certificate = Sign.readCertificateFile(certificateFile);
		// get the CA's certificate and public key
		Certificate caCertificate = caCheck.readCertificateFile(_caCert);
		PublicKey caPublicKey = caCertificate.getPublicKey();

		// check if the certificate is valid (signed by CA)
		if (caCheck.verifySignedCertificate(certificate,caPublicKey)) {
			System.out.println("The signed certificate is valid");
		} else {
			System.err.println("The signed certificate is not valid");
			return false;
		}				


		PublicKey publicKey = certificate.getPublicKey();

		// verify the signature
		System.out.println("Verifying ...");
		byte[] bytesToVerify = text2verify.getBytes();

		boolean isValid = Sign.verifyDigitalSignature(signatureBytes, bytesToVerify, publicKey);

		if (isValid) {
			System.out.println("The digital signature is valid");
			return true;
		} else {
			System.out.println("The digital signature is NOT valid");
			return false;
		}

	}
}
