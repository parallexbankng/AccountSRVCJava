package com.parallex.accountopening.utils;


import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Component
public class Encrypter {
	private final Cipher ecipher;
	private final Cipher dcipher;

	private final String phrase;

	private final byte[] salt;

	public Encrypter() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, InvalidKeyException {
		this.phrase = "adig-dot@prod.com@@2$%%&*;.,4321";
		this.salt = new byte[] { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35,
				(byte) 0xE3, (byte) 0x03 }; // 8-byte salt
		int iterationCount = 19;

		KeySpec keySpec = new PBEKeySpec(phrase.toCharArray(), salt, iterationCount);

		// create key
		SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

		ecipher = Cipher.getInstance(key.getAlgorithm());
		dcipher = Cipher.getInstance(key.getAlgorithm());

		// Prepare the parameter to the ciphers
		AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

		// Create the ciphers
		ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
		dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
	}

	public String encrypt(String str) {
		return doEncrypt(doEncrypt(str));
	}

	private String doEncrypt(String token) {
		try {
			// Encode the string into bytes using utf-8
			byte[] utf8 = token.getBytes("UTF8");

			// Encrypt
			byte[] enc = ecipher.doFinal(utf8);

			// Encode bytes to base64 to get a string
			return Base64.getEncoder().encodeToString(enc);
		} catch (BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
			throw new EncrypterException("Error during encryption", e);
		}
	}

	private String doDecrypt(String token) {
		try {
			// Decode base64 to get bytes
			byte[] dec = Base64.getDecoder().decode(token);
			byte[] utf8 = dcipher.doFinal(dec);
			return new String(utf8, "UTF8");
		} catch (BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
			// throw new EncrypterException("Error during decryption", e);
			// log.info("Error decrypting "+token+" Error: "+e.getMessage());
			return token;
		}
	}

	final public String decrypt(String str) {
		return doDecrypt(doDecrypt(str));
	}
	
	public static void main(String[] args) {
		Encrypter encrypter;
		try {
			encrypter = new Encrypter();
			System.out.println("Encrypter >>>> "+encrypter.encrypt("sapassword"));
			System.out.println("DEcrypter >>>> "+encrypter.decrypt("kFCRv5g0o/IYe1qU5XUfFshuEOtomNWfI3O4GxyuPyk="));
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
