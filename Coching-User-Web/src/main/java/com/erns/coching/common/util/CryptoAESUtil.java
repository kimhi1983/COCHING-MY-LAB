package com.erns.coching.common.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoAESUtil {

	public static String alg = "AES/CBC/PKCS5Padding";
    //private final String key = "01234567890123456789012345678901";
    //private final String iv = key.substring(0, 16); // 16byte

    public static String encrypt(String key, String text) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        String iv = key.substring(0, 16);

        SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String key, String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        String iv = key.substring(0, 16);

        SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, "UTF-8");
    }

	public static void main(String[] args) throws Exception {
		String key = "COCHING20231128";

		String[][] testArr = {
		{"114", "MjAyMzkwMDE2NTpNQTAxOkJGMUI5OTVDMjlCNDQ4MUYyRjRBQTM3RTA0OTdBRTg0"},
		};


		for(String[] row : testArr) {
			String ct = CryptoAESUtil.encrypt(key, row[1]);
			String sql = String.format("UPDATE T_FRANCHISE_INFO SET PG_API_KEY = '%s' WHERE fr_seq = %s ;", ct, row[0]);

			System.out.println(sql);
		}

//		try {
//			String ciphertext = CryptoAESUtil.encrypt(key, password);
//			log.info("ciphertext:{}",ciphertext);
//
//
//			LOGGER.info("decrypt:{}",decrypt(key, ciphertext));
//		}catch(Exception e) {
//			e.printStackTrace();
//		}


	}
}
