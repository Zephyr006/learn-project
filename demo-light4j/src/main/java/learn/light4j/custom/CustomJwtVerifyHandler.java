package learn.light4j.custom;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.networknt.config.Config;
import com.networknt.exception.ExpiredTokenException;
import com.networknt.handler.Handler;
import com.networknt.handler.MiddlewareHandler;
import com.networknt.oas.model.Operation;
import com.networknt.oas.model.Path;
import com.networknt.openapi.ApiNormalisedPath;
import com.networknt.openapi.JwtVerifyHandler;
import com.networknt.openapi.NormalisedPath;
import com.networknt.openapi.OpenApiHelper;
import com.networknt.openapi.OpenApiOperation;
import com.networknt.security.IJwtVerifyHandler;
import com.networknt.security.JwtVerifier;
import com.networknt.utility.Constants;
import com.networknt.utility.ModuleRegistry;
import learn.light4j.constants.AttachmentConstants;
import learn.light4j.constants.JwtConstants;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.ReservedClaimNames;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的 Jwt token 验证
 * @see com.networknt.openapi.JwtVerifyHandler
 *
 * @author Zephyr
 * @date 2020/12/1.
 */
public class CustomJwtVerifyHandler implements MiddlewareHandler, IJwtVerifyHandler {
    static final Logger logger = LoggerFactory.getLogger(JwtVerifyHandler.class);

    static final String DEV_ENV_PROFILE = "dev";

    static final String OPENAPI_SECURITY_CONFIG = "openapi-security";
    static final String ENABLE_VERIFY_SCOPE = "enableVerifyScope";
    static final String ENABLE_VERIFY_JWT_SCOPE_TOKEN = "enableExtractScopeToken";

    static final String ENABLE_VERIFY_JWT = "enableVerifyJwt";
    static final String ENABLE_JWT_CACHE = "enableJwtCache";
    static final String EXPIRATION_TIME = ReservedClaimNames.EXPIRATION_TIME;

    static Cache<String, JwtClaims> jwtClaimsCache;
    static final int CACHE_EXPIRED_IN_MINUTES = 15;

    final Boolean enableVerifyJwt = (Boolean) securityConfig.get(ENABLE_VERIFY_JWT);
    final Boolean enableJwtCache = (Boolean) securityConfig.get(ENABLE_JWT_CACHE);

    /** 无效的 token */
    static final String STATUS_INVALID_AUTH_TOKEN = "ERR10000";
    /** token 已过期 */
    static final String STATUS_AUTH_TOKEN_EXPIRED = "ERR10001";
    /** 缺少/未找到 token */
    static final String STATUS_MISSING_AUTH_TOKEN = "ERR10002";
    /** 无效的 scope token */
    static final String STATUS_INVALID_SCOPE_TOKEN = "ERR10003";
    /** scope token 已过期 */
    static final String STATUS_SCOPE_TOKEN_EXPIRED = "ERR10004";
    /** auth token 作用域不匹配 */
    static final String STATUS_AUTH_TOKEN_SCOPE_MISMATCH = "ERR10005";
    /** scope token 作用域不匹配 */
    static final String STATUS_SCOPE_TOKEN_SCOPE_MISMATCH = "ERR10006";
    /** 无效的请求路径 */
    static final String STATUS_INVALID_REQUEST_PATH = "ERR10007";
    /** 请求方法不允许 */
    static final String STATUS_METHOD_NOT_ALLOWED = "ERR10008";

    static Map<String, Object> securityConfig;
    // make this static variable public so that it can be accessed from the server-info module
    public static JwtVerifier jwtVerifier;
    static {
        // check if openapi-security.yml exist
        securityConfig = Config.getInstance().getJsonMapConfig(OPENAPI_SECURITY_CONFIG);
        // fallback to generic security.yml
        if(securityConfig == null) {
            securityConfig = Config.getInstance().getJsonMapConfig(JwtVerifier.SECURITY_CONFIG);
        }
        //jwtVerifier = new JwtVerifier(config);
    }

    private volatile HttpHandler next;

    public CustomJwtVerifyHandler() {
        if(Boolean.TRUE.equals(enableJwtCache)) {
            jwtClaimsCache = Caffeine.newBuilder()
                    // assuming that the clock screw time is less than 5 minutes
                    .expireAfterWrite(CACHE_EXPIRED_IN_MINUTES, TimeUnit.MINUTES)
                    .build();
        }
    }

    @Override
    public HttpHandler getNext() {
        return next;
    }

    @Override
    public MiddlewareHandler setNext(HttpHandler next) {
        Handlers.handlerNotNull(next);
        this.next = next;
        return this;
    }

    @Override
    public boolean isEnabled() {
        Object object = securityConfig.get(JwtVerifier.ENABLE_VERIFY_JWT);
        return object != null && Boolean.parseBoolean(object.toString()) ;
    }

    @Override
    public void register() {
        ModuleRegistry.registerModule(JwtVerifyHandler.class.getName(), securityConfig, null);
    }

    @Override
    public JwtVerifier getJwtVerifier() {
        return jwtVerifier;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HeaderMap headerMap = exchange.getRequestHeaders();
        String authorization = headerMap.getFirst(Headers.AUTHORIZATION);
        if(StringUtils.isNotBlank(authorization)) {
            try {
                JwtClaims claims = this.verifyJwt(authorization, true);

                // 自定义校验：token 中不包含用户的 id和 dataCenterId，token 无效
                if (claims == null || !claims.hasClaim(JwtConstants.APP_ID_KEY)
                        || !claims.hasClaim(JwtConstants.DATA_CENTER_ID_KEY)
                        || !claims.hasClaim(JwtConstants.SERVICE_NAME_KEY)) {
                    setExchangeStatus(exchange, STATUS_INVALID_AUTH_TOKEN);
                    return;
                } else {
                    exchange.putAttachment(AttachmentConstants.USER_INFO, claims.getClaimsMap());
                }

                Map<String, Object> auditInfo = exchange.getAttachment(AttachmentConstants.AUDIT_INFO);
                // In normal case, the auditInfo shouldn't be null as it is created by OpenApiHandler with
                // endpoint and swaggerOperation available. This handler will enrich the auditInfo.
                if(auditInfo == null) {
                    auditInfo = new HashMap<>();
                    exchange.putAttachment(AttachmentConstants.AUDIT_INFO, auditInfo);
                }
                if(securityConfig != null && OpenApiHelper.openApi3 != null) {
                    Operation operation;
                    OpenApiOperation openApiOperation = (OpenApiOperation)auditInfo.get(Constants.OPENAPI_OPERATION_STRING);
                    if(openApiOperation == null) {
                        final NormalisedPath requestPath = new ApiNormalisedPath(exchange.getRequestURI());
                        final Optional<NormalisedPath> maybeApiPath = OpenApiHelper.findMatchingApiPath(requestPath);
                        if (!maybeApiPath.isPresent()) {
                            setExchangeStatus(exchange, STATUS_INVALID_REQUEST_PATH);
                            return;
                        }

                        final NormalisedPath swaggerPathString = maybeApiPath.get();
                        final Path swaggerPath = OpenApiHelper.openApi3.getPath(swaggerPathString.original());

                        final String httpMethod = exchange.getRequestMethod().toString().toLowerCase();
                        operation = swaggerPath.getOperation(httpMethod);

                        if (operation == null) {
                            setExchangeStatus(exchange, STATUS_METHOD_NOT_ALLOWED);
                            return;
                        }
                        openApiOperation = new OpenApiOperation(swaggerPathString, swaggerPath, httpMethod, operation);
                        auditInfo.put(Constants.OPENAPI_OPERATION_STRING, openApiOperation);
                        auditInfo.put(Constants.ENDPOINT_STRING, swaggerPathString.normalised() + "@" + httpMethod);
                    } else {
                        operation = openApiOperation.getOperation();
                    }
                }
                Handler.next(exchange, next);
            } catch (InvalidJwtException e) {
                // only log it and unauthorized is returned.
                logger.error("InvalidJwtException: ", e);
                setExchangeStatus(exchange, STATUS_INVALID_AUTH_TOKEN);
            } catch (ExpiredTokenException e) {
                logger.error("ExpiredTokenException", e);
                setExchangeStatus(exchange, STATUS_AUTH_TOKEN_EXPIRED);
            }
        } else {
            setExchangeStatus(exchange, STATUS_MISSING_AUTH_TOKEN);
        }
    }

    /**
     * Get VerificationKeyResolver based on the configuration settings
     * @see JwtVerifier#verifyJwt(String, boolean, boolean, java.util.function.BiFunction)
     */
    public JwtClaims verifyJwt(String jwt, boolean ignoreExpiry)
            throws InvalidJwtException, ExpiredTokenException {
        JwtClaims claims;

        if(Boolean.TRUE.equals(enableJwtCache)) {
            claims = jwtClaimsCache.getIfPresent(jwt);
            if(claims != null) {
                if(!ignoreExpiry) {
                    try {
                        // Time Unit : second
                        if (NumericDate.now().getValue() >= claims.getExpirationTime().getValue()) {
                            logger.info("Cached jwt token is expired!");
                            throw new ExpiredTokenException("Token is expired");
                        }
                    } catch (MalformedClaimException e) {
                        // This is cached token and it is impossible to have this exception
                        logger.error("MalformedClaimException:", e);
                    }
                }
                // this claims object is signature verified already
                return claims;
            }
        }

        JwtConsumer consumer = new JwtConsumerBuilder()
                .setSkipAllValidators()
                .setDisableRequireSignature()
                .setSkipSignatureVerification()
                .build();

        JwtContext jwtContext = consumer.process(jwt);
        claims = jwtContext.getJwtClaims();

        if(Boolean.TRUE.equals(enableJwtCache)) {
            jwtClaimsCache.put(jwt, claims);
        }
        return claims;
    }


    protected boolean matchedScopes(List<String> jwtScopes, Collection<String> specScopes) {
        boolean matched = false;
        if(specScopes != null && specScopes.size() > 0) {
            if(jwtScopes != null && jwtScopes.size() > 0) {
                for(String scope: specScopes) {
                    if(jwtScopes.contains(scope)) {
                        matched = true;
                        break;
                    }
                }
            }
        } else {
            matched = true;
        }
        return matched;
    }
}
