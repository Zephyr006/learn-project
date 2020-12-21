package learn.base.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yiqischool.gaea.eventtracking.listener.constants.JwtConstants;
import com.yiqischool.gaea.eventtracking.listener.exception.UnauthorizedException;
import com.yiqischool.gaea.eventtracking.listener.model.UserIdModel;
import org.jose4j.jwt.ReservedClaimNames;

/**
 * @author Zephyr
 * @date 2020/12/1.
 */
public class JwtUtils {

    /**
     * 从 token 字符串中解析得到用户的 id 和 dataCenterId
     */
    public static UserIdModel decodeAsIdModel(String token, String key) throws UnauthorizedException {
        try {
            assertNotNull(token, key);
            JWTVerifier verifier = JWT.require(sign(key)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            Long id = decodedJWT.getClaim(JwtConstants.APP_ID_KEY).asLong();
            Long dataCenterId = decodedJWT.getClaim(JwtConstants.DATA_CENTER_ID_KEY).asLong();
            if (id == null || dataCenterId == null) {
                throw new UnauthorizedException();
            }
            return new UserIdModel(id, dataCenterId);
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Throwable e) {
            throw new UnauthorizedException();
        }
    }

    public static Claim decode(String token, String key, String claimKey) throws UnauthorizedException {
        try {
            JWTVerifier verifier = JWT.require(sign(key)).build();
            DecodedJWT verify = verifier.verify(token);
            return verify.getClaim(claimKey);
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Throwable e) {
            throw new UnauthorizedException();
        }
    }

    /**
     * 对用户的 id 和 dataCenterId 进行编码得到 token
     * @param hasExpire 是否设置过期时间
     * @return token
     */
    public static String encode(Long appId, Long dataCenterId, String key, boolean hasExpire) {
        if (hasExpire) {
            return JWT.create()
                    .withClaim(JwtConstants.APP_ID_KEY, appId)
                    .withClaim(JwtConstants.DATA_CENTER_ID_KEY, dataCenterId)
                    // ReservedClaimNames.EXPIRATION_TIME == "exp"
                    .withClaim(ReservedClaimNames.EXPIRATION_TIME, System.currentTimeMillis()/1000 + JwtConstants.JWT_EXP_TIME)
                    .sign(sign(key));
        } else {
            return JWT.create()
                    .withClaim(JwtConstants.APP_ID_KEY, appId)
                    .withClaim(JwtConstants.DATA_CENTER_ID_KEY, dataCenterId)
                    .sign(sign(key));
        }
    }

    private static Algorithm sign(String key) {
        return Algorithm.HMAC256(key);
    }

    private static void assertNotNull(Object... args) {
        if (args != null) {
            for (Object arg : args) {
                assert arg != null;
            }
        }
    }
}
