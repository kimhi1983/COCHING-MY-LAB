package com.erns.coching;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import com.erns.coching.common.util.CipherUtil;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>JWE 테스트 코드</p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
public class JweTests {

	//private String privateKeyPath = "classpath:/key/erns_coching_private_key.pem";
	private String privateKeyPath = "C:\\dev\\git_repo\\Coching-Partner-Web\\src\\main\\resources\\key\\erns_coching_private_key.pem";

	//private String publicKeyPath = "classpath:/key/erns_coching_public_key.pem";
	private String publicKeyPath = "C:\\dev\\git_repo\\Coching-Partner-Web\\src\\main\\resources\\key\\erns_coching_public_key.pem";

	private PrivateKey privateKey;

	private RSAPublicKey publicKey;

	/**
	 * 서비스 생성시 작업
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	@PostConstruct
	public void startup() throws NoSuchAlgorithmException, IOException
	{
		log.info("JweTests is started");

		log.info("Load JWE private key...");
		InputStream privateIs = new FileInputStream(Paths.get(privateKeyPath).toFile()); //개인키 로드
		privateKey = CipherUtil.getPrivateKey(privateIs);
		log.info("Loaded JWE private key");

		log.info("Load JWE public key...");
		InputStream publicIs = new FileInputStream(Paths.get(publicKeyPath).toFile()); //개인키 로드
		publicKey = CipherUtil.getPublicKey(publicIs);
		log.info("Loaded JWE public key");
	}

	/**
	 * 테스트용 JWE 생성(개발후 삭제예정)
	 * @param bizNo 사업자번호
	 * @param onoffChk 온오프라인구분
	 * @param appendClaimMap 추가데이터
	 * @return
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws JOSEException
	 */
	private String makeTestJwe(String bizNo, String onoffChk,
			Map<String, Object> appendClaimMap) throws InvalidKeySpecException, NoSuchAlgorithmException, JOSEException {
		String iss = "https://www.coching.co.kr";

		final Date today = new Date(new Date().getTime() / 1000 * 1000);
		//Date exp = new Date(today.getTime() + 1000 * 60 * 10);
		Date exp = new Date(today.getTime() + (1000 * 5 * 60)); //5 min : 현재시간으로 5분간 토큰 유효
		Date nbf = today;
		Date iat = today;
		String jti = UUID.randomUUID().toString(); //JWT ID

		String tBizNo = bizNo;
		String tSendDt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String tOnoffChk = onoffChk;
		int tPv = 0;
		{	//토큰 Request parameter 유효성 검증값
			String subBiz = tBizNo.substring(3);
			log.debug("subBiz:{}", subBiz);
			long lBizNo = Long.parseLong(subBiz, 10);
			long lSendDt = Long.parseLong(tSendDt, 10);
			long lOnOff = tOnoffChk.equals("ON") ? 11 : 13;
			tPv = (int)((lBizNo + lSendDt + lOnOff) % 997);

			log.debug("tPv:{}", tPv);
		}

		Builder jwtBilder = new JWTClaimsSet.Builder().
                issuer(iss).
                issueTime(iat).
                //claim("uid", "123465789").
                //claim("track_id", System.nanoTime() + "").
                claim("bizNo", tBizNo).
                claim("linkCd", "Coching").
                claim("onoffChk", tOnoffChk).
                claim("svcCls", "M").
                claim("sendDt", tSendDt).
                claim("iPv", tPv).
                //claim("detailYn", "Y").
                notBeforeTime(nbf).
                expirationTime(exp).
                notBeforeTime(today).
                issueTime(today).
                jwtID(jti);

		//추가 Claim 넣기
		if(appendClaimMap != null && !appendClaimMap.isEmpty()) {
			for(String key : appendClaimMap.keySet()) {
				log.debug("key:{},val:{}", key, appendClaimMap.get(key));
				jwtBilder.claim(key, appendClaimMap.get(key));
			}
		}


		//JWE 토큰 생성
		JWTClaimsSet jwtClaims = jwtBilder.build();

		log.debug(jwtClaims.toJSONObject().toString());

		JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM);

		EncryptedJWT jwt = new EncryptedJWT(header, jwtClaims);

		RSAEncrypter encrypter = new RSAEncrypter(publicKey);
		encrypter.getJCAContext().setProvider(BouncyCastleProviderSingleton.getInstance());

		jwt.encrypt(encrypter);

		String jwtString = jwt.serialize();

		log.debug("jwtString : {}", jwtString);

		return jwtString;
	}

	private EncryptedJWT decryptJwe(String jwe) throws ParseException, JOSEException {
		EncryptedJWT jwt = EncryptedJWT.parse(jwe);

		// Create an decrypter with the specified private RSA key
        RSADecrypter decrypter = new RSADecrypter(privateKey);
        decrypter.getJCAContext().setProvider(BouncyCastleProviderSingleton.getInstance());

        // Decrypt
        jwt.decrypt(decrypter);

        return jwt;
	}

	public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, JOSEException, ParseException, IOException {
		log.info("JweTests.main is started");

		JweTests test = new JweTests();
		test.startup();

		Map<String, Object> appendClaimMap = new HashMap<>();
		{	//추가 param
			appendClaimMap.put("erpId", "1234567");
		}

		String jwe = test.makeTestJwe("1234567890", "ON", appendClaimMap);

		log.info("JWE:{}", jwe);


		EncryptedJWT ejwt = test.decryptJwe(jwe);
		log.info("decrypt JWT:{}", ejwt.getJWTClaimsSet().toString());
	}
}
