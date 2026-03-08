package com.erns.es.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>RSA 암호화 유틸</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Slf4j
public class CipherUtil {
	
	/**
	 * 1024비트 RSA 키쌍을 생성합니다.
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair genRSAKeyPair() throws NoSuchAlgorithmException {
		SecureRandom secureRandom = new SecureRandom();
		KeyPairGenerator gen;
		gen = KeyPairGenerator.getInstance("RSA");
		gen.initialize(1024, secureRandom);
		KeyPair keyPair = gen.genKeyPair();
		return keyPair;
	}

	/**
	 * Public Key로 RSA 암호화를 수행합니다.
	 * @param plainText	암호화할 평문입니다.
	 * @param publicKey publicKey 공개키 입니다.
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static String encryptRSA(String plainText, PublicKey publicKey) throws NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] bytePlain = cipher.doFinal(plainText.getBytes());
		String encrypted = Base64.getEncoder().encodeToString(bytePlain);
		return encrypted;
	}

	/**
	 * RAS 복호화를 수행합니다.
	 * @param encrypted 암호화된 이진데이터를 base64 인코딩한 문자열 입니다.
	 * @param privateKey 복호화를 위한 개인키 입니다.
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws UnsupportedEncodingException
	 */
	public static String decryptRSA(String encrypted, PrivateKey privateKey)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, UnsupportedEncodingException {
		Cipher cipher = Cipher.getInstance("RSA");
		byte[] byteEncrypted = Base64.getDecoder().decode(encrypted.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] bytePlain = cipher.doFinal(byteEncrypted);
		String decrypted = new String(bytePlain, "utf-8");
		return decrypted;
	}

	/**
	 * Base64 엔코딩된 개인키 문자열로부터 PrivateKey객체를 얻는다.
	 * @param keyString
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PrivateKey getPrivateKeyFromBase64String(final String keyString)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		final String privateKeyString = keyString.replaceAll("\\n", "").replaceAll("-{5}[ a-zA-Z]*-{5}", "");
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));
		return keyFactory.generatePrivate(keySpecPKCS8);
	}

	/**
	 * Base64 엔코딩된 공용키키 문자열로부터 PublicKey객체를 얻는다.
	 * @param keyString
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey getPublicKeyFromBase64String(final String keyString)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		final String publicKeyString = keyString.replaceAll("\\n", "").replaceAll("-{5}[ a-zA-Z]*-{5}", "");
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));
		return keyFactory.generatePublic(keySpecX509);
	}
	
	/**
	 * pem 파일로 부터 PKCS8 형식의 개인키를 읽어온다
	 * @param inputStream
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(InputStream is) throws NoSuchAlgorithmException {
		KeyFactory factory = KeyFactory.getInstance("RSA");

		try (
			InputStreamReader keyReader = new InputStreamReader(is);
			PemReader pemReader = new PemReader(keyReader)) 
		{
			PemObject pemObject = pemReader.readPemObject();
			byte[] content = pemObject.getContent();
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
			return factory.generatePrivate(privKeySpec);

		} catch (InvalidKeySpecException | IOException e) {
			log.error("Error, Cannot getPrivateKey", e);
			return null;
		}
	}
	
	public static RSAPublicKey getPublicKey(InputStream is) throws NoSuchAlgorithmException {
		KeyFactory factory = KeyFactory.getInstance("RSA");

		try (
			InputStreamReader keyReader = new InputStreamReader(is);
			PemReader pemReader = new PemReader(keyReader))
		{
			PemObject pemObject = pemReader.readPemObject();
			byte[] content = pemObject.getContent();
			RSAPublicKey publicKey = (RSAPublicKey) factory
					.generatePublic(new X509EncodedKeySpec(content));
			
			return publicKey;

		} catch (InvalidKeySpecException | IOException e) {
			log.error("Error, Cannot getPublicKey", e);
			return null;
		}
	}
	
	/**
	 * Util 테스용
	 * @param args
	 * @throws NoSuchAlgorithmException
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException {
		//test key
		
		KeyPair keys = CipherUtil.genRSAKeyPair();
		
		PublicKey publicKey = keys.getPublic();
		PrivateKey privateKey = keys.getPrivate();		
		
		// 공개키를 Base64 인코딩한 문자일을 만듭니다. 
		byte[] bytePublicKey = publicKey.getEncoded(); 
		String base64PublicKey = Base64.getEncoder().encodeToString(bytePublicKey); 
		System.out.println("Base64 Public Key : " + base64PublicKey); 
		
		// 개인키를 Base64 인코딩한 문자열을 만듭니다. 
		byte[] bytePrivateKey = privateKey.getEncoded(); 
		String base64PrivateKey = Base64.getEncoder().encodeToString(bytePrivateKey); 
		System.out.println("Base64 Private Key : " + base64PrivateKey);
	}
}
