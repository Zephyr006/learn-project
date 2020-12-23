package learn.light4j.constants;

import learn.light4j.util.LightConfigValues;
import org.jose4j.jwt.ReservedClaimNames;

/**
 * @author Zephyr
 * @date 2020/12/4.
 */
public class JwtConstants {

    /**
     * token有效时间(单位：秒)
     */
    public static final Long JWT_EXP_TIME = LightConfigValues.getAsLong("jwt.exp");
    /**
     * jwt 加密用的 key
     */
    public static final String JWT_KEY = LightConfigValues.getAsString("jwt.key");


    public static final String APP_ID_KEY = "applicationId";
    public static final String DATA_CENTER_ID_KEY = "dataCenterId";
    public static final String SERVICE_NAME_KEY = "serviceName";

    // key:"exp" , Unit:second
    public static final String EXPIRATION_TIME_KEY = ReservedClaimNames.EXPIRATION_TIME;

}
