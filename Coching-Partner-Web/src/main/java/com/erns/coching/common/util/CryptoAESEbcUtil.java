package com.erns.coching.common.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoAESEbcUtil {

	public static String alg = "AES/ECB/PKCS5Padding";
    //private final String key = "01234567890123456789012345678901";
    //private final String iv = key.substring(0, 16); // 16byte

    public static String encrypt(String key, String text) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        String iv = key.substring(0, 16);
        SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String key, String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        String iv = key.substring(0, 16);
        SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, "UTF-8");
    }

	public static void main(String[] args) throws Exception {
		String key = "COCHING20250101_WOW";

		String[][] testArr = {
		{"114", "MjAyMzkwMDE2NTpNQTAxOkJGMUI5OTVDMjlCNDQ4MUYyRjRBQTM3RTA0OTdBRTg0"},
		};


		for(String[] row : testArr) {
			String ct = CryptoAESEbcUtil.encrypt(key, row[1]);
			String sql = String.format("UPDATE T_FRANCHISE_INFO SET PG_API_KEY = '%s' WHERE fr_seq = %s ;", ct, row[0]);

			System.out.println(sql);
		}

		try {
			String ciphertext = CryptoAESEbcUtil.encrypt(key, testArr[0][1]);
			log.info("ciphertext:{}",ciphertext);


			log.info("decrypt:{}",decrypt(key, ciphertext));

            String test2 = "PvIfGDuvYQDPD/SbGQgUXD25nPJ3rR2mbs9ACMee9FyvieF20vk4vFjMgElIXy0DXWFOvdHSrAa7LIgu7gmnOQ==";
            log.info("{} => decrypt:{}", test2, decrypt(key, test2));
            
		}catch(Exception e) {
			e.printStackTrace();
		}


	}
}
