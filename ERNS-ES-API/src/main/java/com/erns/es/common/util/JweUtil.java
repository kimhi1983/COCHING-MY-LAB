package com.erns.es.common.util;

import java.security.PrivateKey;
import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jwt.EncryptedJWT;

/**
 *
 * <p>JWE 유틸</p>
 *
 * @author Hunwoo Park
 *
 */
public class JweUtil {

	/**
	 * JWT을 JWE로 암호화
	 * @param privateKey
	 * @param jwtString
	 * @return
	 * @throws ParseException
	 * @throws JOSEException
	 */
	public static final EncryptedJWT decrypt(PrivateKey privateKey, String jwtString) throws ParseException, JOSEException {
		EncryptedJWT jwt = EncryptedJWT.parse(jwtString);
		decrypt(privateKey, jwt);
		return jwt;
	}

	/**
	 * JWE를 복호화
	 * @param privateKey
	 * @param jwt
	 * @throws JOSEException
	 */
	public static final void decrypt(PrivateKey privateKey, EncryptedJWT jwt) throws JOSEException {

		RSADecrypter decrypter = new RSADecrypter(privateKey);
        decrypter.getJCAContext().setProvider(BouncyCastleProviderSingleton.getInstance());

        // Decrypt
        jwt.decrypt(decrypter);
	}

}
