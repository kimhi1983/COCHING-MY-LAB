package com.erns.coching.config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.erns.coching.common.exception.JweException;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.SiteType;
import com.erns.coching.common.util.CipherUtil;
import com.erns.coching.common.util.DateUtil;
import com.erns.coching.common.util.JweUtil;
import com.erns.coching.login.domain.AclTokenVO;
import com.erns.coching.login.domain.IUser;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.login.service.AclTokenService;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>JWT 토큰처리</p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtConfig {

	@Value("${jwe.access-expire-time:1800000}")
    private long accessExpireTime;

    @Value("${jwe.refresh-expire-time:7200000}")
    private long refreshExpireTime;

    @Value("${jwe.refresh-remain-expire-time:604800000}")
    private long refreshRemainExpireTime;

    @Value("${jwe.iss:https://www.coching.com}" )
    private String jwtIss;

    @Value("${jwe.private-key.path:classpath:/key/erns_coching_private_key.pem}" )
    private String privateKeyPath;

    @Value("${jwe.public-key.path:classpath:/key/erns_coching_public_key.pem}" )
  	private String publicKeyPath;

    @Autowired
	private ResourceLoader resourceLoader;

    private PrivateKey privateKey;

	private RSAPublicKey publicKey;

	@SuppressWarnings("unused")
	private final UserDetailsService userDetailsService;

	@Autowired
	protected AclTokenService aclTokenService;

    @PostConstruct
    protected void init() throws NoSuchAlgorithmException, IOException {
    	log.info("JwtConfig is started");

    	//개인키 로드
		log.info("Load JWE private key...");
		Resource privateRes = resourceLoader.getResource(privateKeyPath);
		privateKey = CipherUtil.getPrivateKey(privateRes.getInputStream());
		log.info("Loaded JWE private key");

		//공용키 로드
		log.info("Load JWE public key...");
		Resource publicRes = resourceLoader.getResource(publicKeyPath);
		publicKey = CipherUtil.getPublicKey(publicRes.getInputStream());
		log.info("Loaded JWE public key");

		//secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public boolean isAllowAccessToken(String token) {
    	AclTokenVO ret = aclTokenService.getAccesslist(token);
		return ret != null;
    }

    public boolean isBlacklistToken(String token) {
		AclTokenVO ret = aclTokenService.getBlacklist(token);
		return ret != null;
	}


    private Claims genClaims(IUser userInfo) {
    	Claims claims = Jwts.claims().setSubject(userInfo.getId()); // JWT payload 에 저장되는 정보단위

    	claims.put("id", userInfo.getId());
        claims.put("userSeq", userInfo.getSeq());
		claims.put("ptnSeq", userInfo.getPtnSeq());
        claims.put("userName", userInfo.getUserName());

        return claims;
    }

    /**
     * AccessToken 토큰생성
     * @param userInfo
     * @param roleList
     * @return
     * @throws JOSEException
     */
    public AclTokenVO createAccessToken(IUser userInfo, List<String> roleList) throws JOSEException {
    	Claims claims = genClaims(userInfo);
        claims.put("roles", roleList); // 정보는 key / value 쌍으로 저장된다.

        Date issueDate = new Date();
        Date expDate = new Date(issueDate.getTime() + accessExpireTime);
        log.debug("accessKey expDate:{}", DateUtil.SDF_DATEDDASH_HHMMSS.format(expDate));
        String token = generateToken(claims, expDate);

        return AclTokenVO.builder()
        		.siteType(SiteType.USER_WEB.getCode())
        		.token(token)
        		.userSeq(userInfo.getSeq())
        		.expDate(expDate)
        		.regDttm(issueDate)
        		.build();
    }

    /**
     * RefreshToken 생성
     * @param userInfo
     * @param roleList
     * @param remainLogin 로그인 유지 여부
     * @return
     * @throws JOSEException
     */
    public AclTokenVO createRefreshToken(IUser userInfo, List<String> roleList, boolean remainLogin) throws JOSEException {
    	//로그인 유지일 경우
    	long refreshExpend = remainLogin ? refreshRemainExpireTime : refreshExpireTime;

    	Claims claims = genClaims(userInfo);
        claims.put("roles", roleList); // 정보는 key / value 쌍으로 저장된다.
        claims.put("remainLogin", remainLogin); // 정보는 key / value 쌍으로 저장된다.


        Date issueDate = new Date();
        Date expDate = new Date(issueDate.getTime() + refreshExpend);
        log.debug("refreshKey expDate:{}", DateUtil.SDF_DATEDDASH_HHMMSS.format(expDate));
        String token = generateToken(claims, expDate);

        return AclTokenVO.builder()
        		.siteType(SiteType.USER_WEB.getCode())
        		.token(token)
        		.userSeq(userInfo.getSeq())
        		.expDate(expDate)
        		.regDttm(issueDate)
        		.build();
    }

    /**
     * JWE 토큰생성
     * @param claims claimsMap
     * @param expDate 유효기간
     * @return JWE 토큰
     * @throws JOSEException
     */
    private String generateToken(Claims claims, Date expDate) throws JOSEException {

    	final Date today = new Date(new Date().getTime() / 1000 * 1000);
		Date exp = expDate;
		Date nbf = today;
		Date iat = today;
		String jti = UUID.randomUUID().toString(); //JWT ID

		Builder jwtBilder = new JWTClaimsSet.Builder();

		//추가 Claim 넣기
		if(claims != null && !claims.isEmpty()) {
			for(String key : claims.keySet()) {
				log.debug("key:{},val:{}", key, claims.get(key));
				jwtBilder.claim(key, claims.get(key));
			}
		}

		//JWE 토큰 생성
		JWTClaimsSet jwtClaims = jwtBilder.
				issuer(jwtIss).
	            issueTime(today).
	            issueTime(iat).
	            notBeforeTime(nbf).
	            expirationTime(exp).
	            jwtID(jti)
	            .build();


		//JWT -> JWE 토큰암호화
		JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM);
		EncryptedJWT jwt = new EncryptedJWT(header, jwtClaims);
		RSAEncrypter encrypter = new RSAEncrypter(publicKey);
		encrypter.getJCAContext().setProvider(BouncyCastleProviderSingleton.getInstance());

		jwt.encrypt(encrypter);
		String jwtString = jwt.serialize();

		log.debug(jwtClaims.toJSONObject().toString());
		log.debug("jwtString : {}", jwtString);

		return jwtString;
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    // JWE 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String jwtToken) throws ParseException, JweException {
    	EncryptedJWT jwe = null;

    	try {
			jwe = JweUtil.decrypt(privateKey, jwtToken);
			//log.debug("JweUtil.decrypt : {}", jwtToken);
		}catch(ParseException | JOSEException e) {
			log.error("JWE Token parsing error!", e);
			throw new JweException(ApiResultError.ERROR_ERROR_JWT);
		}

    	ApiResultError retTokenVerify = verifyIdentityToken(jwe, false);
		if (ApiResultError.NO_ERROR != retTokenVerify) {
			log.warn("JWE 토근 검증실패 : {} : {}", retTokenVerify.getMessage(), jwtToken);
			throw new JweException(retTokenVerify);
		}

		JWTClaimsSet claimsSet = jwe.getJWTClaimsSet();

		LoginUserDTO userDetails = LoginUserDTO.FromJweBuilder()
				.jweClaims(claimsSet.getClaims())
				.build();

		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) throws JweException {
    	EncryptedJWT jwe = null;

    	try {
			jwe = JweUtil.decrypt(privateKey, jwtToken);
		}catch(ParseException | JOSEException e) {
			log.error("JWE Token parsing error!", e);
			throw new JweException(ApiResultError.ERROR_ERROR_JWT);
		}

    	ApiResultError retTokenVerify = verifyIdentityToken(jwe, true);
		if (ApiResultError.NO_ERROR != retTokenVerify) {
			log.warn("JWE 토근 검증실패 : {} : {}", retTokenVerify.getMessage(), jwtToken);
			throw new JweException(retTokenVerify);
		}

		return true;
    }

    /**
     * Refresh 만료시간이 30% 아래이면 갱신필요
     * @param claims
     * @return
     */
    public boolean isNeedNewRefreshJwToken(JWTClaimsSet claims) {
    	long reSec = refreshExpireTime;
    	long diff = claims.getExpirationTime().getTime() - System.currentTimeMillis();
    	long checkTime = (long) (reSec * 300); //30%
    	if(diff > 0 && diff < checkTime) {
    		return true;
    	}

    	return false;
    }

    /**
	 * 토큰을 privateKey를 이용하여 복호화
	 * @param jweToken
	 * @return
	 * @throws JweException
	 */
	public EncryptedJWT decrypt(String jweToken) throws JweException {
		EncryptedJWT jwe = null;
		try {
			jwe = JweUtil.decrypt(privateKey, jweToken);
		}catch(ParseException | JOSEException e) {
			log.error("JWE Token parsing error!", e);
			throw new JweException(ApiResultError.ERROR_ERROR_JWT);
		}

		return jwe;
	}

	/**
	 * 토큰 유효성 검증
	 * @param jwe
	 * @return
	 */
	public ApiResultError verifyIdentityToken(EncryptedJWT jwe, boolean checkExpTime) {

		try {
			JWTClaimsSet payload = jwe.getJWTClaimsSet();

            // ext 만료시간
            Date currentTime = new Date(System.currentTimeMillis());
            if (!currentTime.before(payload.getExpirationTime())) {
            	LocalDateTime localDateTime =
            			payload.getExpirationTime().toInstant() // Date -> Instant
            			.atZone(ZoneId.systemDefault()) 		// Instant -> ZonedDateTime
            			.toLocalDateTime(); 					// ZonedDateTime -> LocalDateTime

            	log.warn("토큰만료 ext:{} {}", localDateTime, payload);
                return ApiResultError.ERROR_EXPIRED_JWT;
            }

            //iss 발급처
            if (!jwtIss.equals(payload.getIssuer())) {
            	log.warn("발급처 미스매칭 iss:{} {}", payload.getIssuer(), payload);
                return ApiResultError.ERROR_INVALID_ISS;
            }

            //iat 발급시간
            //Date issueTime = payload.getIssueTime();

            //Jti 고유식별자
            String jwtID = payload.getJWTID();
            //log.debug("jwtID(Jti) {}", jwtID);
            if(!StringUtils.hasText(jwtID)) {
            	log.warn("Jti 누락:{} {}", jwtID, payload);
                return ApiResultError.ERROR_INVALID_NO_JTI;
            }


        } catch (ParseException e) {
            e.printStackTrace();
            return ApiResultError.ERROR_INVALID_JWT;
        }

        return ApiResultError.NO_ERROR;
	}

}
