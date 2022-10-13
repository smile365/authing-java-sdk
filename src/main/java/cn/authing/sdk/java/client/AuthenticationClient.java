package cn.authing.sdk.java.client;

import cn.authing.sdk.java.dto.*;
import cn.authing.sdk.java.dto.authentication.*;
import cn.authing.sdk.java.enums.AuthMethodEnum;
import cn.authing.sdk.java.enums.ProtocolEnum;
import cn.authing.sdk.java.model.AuthenticationClientOptions;
import cn.authing.sdk.java.model.AuthingRequestConfig;
import cn.authing.sdk.java.model.ClientCredentialInput;
import cn.authing.sdk.java.util.CommonUtils;
import cn.authing.sdk.java.util.HttpUtils;
import cn.authing.sdk.java.util.JsonUtils;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author ZKB
 */
@SuppressWarnings("all")
public class AuthenticationClient extends BaseClient {

    private final AuthenticationClientOptions options;
    private final String appId;
//    private final JWKSet jwks;

    public AuthenticationClient(AuthenticationClientOptions options) throws IOException, ParseException {
        super(options);
        // 必要参数校验
        this.options = options;
        appId = options.getAppId();

        if (!(options.getScope().contains("openid"))) {
            throw new IllegalArgumentException("scope 中必须包含 openid");
        }

//        if (options.getServerJWKS() != null && options.getServerJWKS().getKeys().size() > 0) {
//            this.jwks = options.getServerJWKS();
//        } else {
//            this.jwks = JWKSet.load(new URL(this.options.getHost() + "/oidc/.well-known/jwks.json"));
//        }
    }

    public AuthUrlResult buildAuthUrl(BuildAuthUrlParams buildOptionParam) {
        if (StrUtil.isBlank(buildOptionParam.getState())) {
            buildOptionParam.setState(CommonUtils.createRandomString(16));
        }

        if (StrUtil.isBlank(buildOptionParam.getNonce())) {
            buildOptionParam.setNonce(CommonUtils.createRandomString(16));
        }

        AuthUrlParams params = new AuthUrlParams(
                buildOptionParam.getRedirectUri(),
                "code",
                "query",
                appId,
                buildOptionParam.getState(),
                buildOptionParam.getNonce(),
                buildOptionParam.getScope(),
                null);

        if (buildOptionParam.getForced()) {
            params.setPrompt("login");
        }

        if (buildOptionParam.getScope().contains("offline_access")) {
            params.setPrompt("consent");
        }

        String url = HttpUtils.buildUrlWithQueryParams(options.getHost() + "/oidc/auth",
                JsonUtils.deserialize(JsonUtils.serialize(params), Map.class));
        return new AuthUrlResult(url, buildOptionParam.getState(), params.getNonce());
    }

    public OIDCTokenResponse getAccessTokenByCode(String code) throws Exception {
        if ((StrUtil.isBlank(this.options.getAppId()) || StrUtil.isBlank(this.options.getAppSecret()))
                && AuthMethodEnum.NONE.getValue().equals(this.options.getTokenEndPointAuthMethod())) {
            throw new Exception("请在初始化 AuthenticationClient 时传入 appId 和 secret 参数");
        }

        String url = "";
        if (ProtocolEnum.OAUTH.getValue().equals(this.options.getProtocol())) {
            url += "/oauth/token";
        } else {
            url += "/oidc/token";
        }

        CodeToTokenParams tokenParam = new CodeToTokenParams();
        tokenParam.setRedirectUri(this.options.getRedirectUri());
        tokenParam.setCode(code);
        tokenParam.setGrantType("authorization_code");

        AuthingRequestConfig config = new AuthingRequestConfig();

        config.setUrl(url);
        config.setMethod("UrlencodedPOST");

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");

        if (AuthMethodEnum.CLIENT_SECRET_POST.getValue().equals(this.options.getTokenEndPointAuthMethod())) {
            tokenParam.setClientId(this.options.getAppId());
            tokenParam.setClientSecret(this.options.getAppSecret());
        } else if (AuthMethodEnum.CLIENT_SECRET_BASIC.getValue().equals(this.options.getTokenEndPointAuthMethod())) {
            String basic64Str = "Basic " + Base64.getEncoder().encodeToString((this.options.getAppId() + ":" + this.options.getAppSecret()).getBytes());
            headerMap.put(Header.AUTHORIZATION.getValue(), basic64Str);
        } else {
            // AuthMethodEnum.NONE
            tokenParam.setClientId(this.options.getAppId());
        }

        config.setHeaders(headerMap);
        config.setBody(tokenParam);

        String response = request(config);

        OIDCTokenResponse deserializeOIDCResponse = deserialize(response, OIDCTokenResponse.class);

        return deserializeOIDCResponse;
    }

//    public LoginState getLoginStateByAuthCode(String code, String redirectUri) throws Exception {
//        CodeToTokenParams tokenParam = new CodeToTokenParams(code, this.options.getAppId(), this.options.getAppSecret(),
//                redirectUri, "authorization_code");
//
//        AuthingRequestConfig config = new AuthingRequestConfig();
//
//        config.setUrl("/oidc/token");
//
//        HashMap<String, String> headerMap = new HashMap<>();
//
//        headerMap.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");
//
//        config.setHeaders(headerMap);
//
//        config.setMethod("UrlencodedPOST");
//
//        config.setBody(tokenParam);
//
//        String response = request(config);
//
//        OIDCTokenResponse deserializeOIDCResponse = deserialize(response, OIDCTokenResponse.class);
//
//        return new LoginState(deserializeOIDCResponse.getAccessToken(),
//                deserializeOIDCResponse.getIdToken(),
//                deserializeOIDCResponse.getRefreshToken(),
//                deserializeOIDCResponse.getExpiresIn(),
//                this.parseIDToken(deserializeOIDCResponse.getIdToken()),
//                this.parseAccessToken(deserializeOIDCResponse.getAccessToken()));
//    }
//
//    public LoginState refreshLoginState(String token) throws Exception {
//        RefreshTokenParams tokenParam = new RefreshTokenParams(this.options.getAppId(), this.options.getAppSecret(),
//                token);
//        AuthingRequestConfig config = new AuthingRequestConfig();
//
//        config.setUrl("/oidc/token");
//
//        HashMap<String, String> headerMap = new HashMap<>();
//
//        headerMap.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");
//
//        config.setHeaders(headerMap);
//
//        config.setBody(tokenParam);
//
//        config.setMethod("UrlencodedPOST");
//
//        String response = request(config);
//
//        OIDCTokenResponse deserializeOIDCResponse = deserialize(response, OIDCTokenResponse.class);
//
//        return new LoginState(deserializeOIDCResponse.getAccessToken(),
//                deserializeOIDCResponse.getIdToken(),
//                deserializeOIDCResponse.getRefreshToken(),
//                deserializeOIDCResponse.getExpiresIn(),
//                this.parseIDToken(deserializeOIDCResponse.getIdToken()),
//                this.parseAccessToken(deserializeOIDCResponse.getAccessToken()));
//
//    }

//    private IDToken parseIDToken(String token) throws Exception {
//        JWSObject jwsObject = JWSObject.parse(token);
//        String payload;
//
//        if (jwsObject.getHeader().getAlgorithm() == JWSAlgorithm.HS256) {
//            JWSVerifier jwsVerifier = new MACVerifier(this.options.getAppSecret());
//            if (!jwsObject.verify(jwsVerifier)) {
//                throw new Exception("token 签名不合法");
//            }
//        } else {
//            RSAKey rsaKey = this.jwks.getKeys().get(0).toRSAKey();
//            RSASSAVerifier verifier = new RSASSAVerifier(rsaKey);
//            if (!jwsObject.verify(verifier)) {
//                throw new Exception("校验不通过");
//            }
//        }
//
//        payload = jwsObject.getPayload().toString();
//
//        return deserialize(payload, IDToken.class);
//    }
//
//    private AccessToken parseAccessToken(String token) throws Exception {
//        JWSObject jwsObject = JWSObject.parse(token);
//        String payload;
//        RSAKey rsaKey = this.jwks.getKeys().get(0).toRSAKey();
//        RSASSAVerifier verifier = new RSASSAVerifier(rsaKey);
//        if (!jwsObject.verify(verifier)) {
//            throw new Exception("校验不通过");
//        }
//        payload = jwsObject.getPayload().toString();
//        return deserialize(payload, AccessToken.class);
//    }

    public UserInfo getUserinfo(String accessToken) {
        AuthingRequestConfig config = new AuthingRequestConfig();

        Map<String, String> headerMap = new HashMap<>();

        headerMap.put("Authorization", "Bearer " + accessToken);

        config.setHeaders(headerMap);

        config.setMethod("GET");

        config.setUrl("/oidc/me");

        String response = request(config);

        return deserialize(response, UserInfo.class);

    }

    public String buildLogoutUrlWithHost(ILogoutParams param) {
        String host = this.options.getHost();

        String path = HttpUtils.buildUrlWithQueryParams("/oidc/session/end",
                JsonUtils.deserialize(JsonUtils.serialize(param), Map.class));

        return host+path;
    }

    public String buildLogoutUrl(LogoutUrlParams param) {
        String redirectUri = this.options.getRedirectUri();

        if (StrUtil.isBlank(redirectUri)) {
            param.setPostLogoutRedirectUri(param.getPostLogoutRedirectUri());
        }

        if (StrUtil.isBlank(param.getPostLogoutRedirectUri())) {
            param.setState(null);
        }

        return HttpUtils.buildUrlWithQueryParams("/oidc/session/end",
                JsonUtils.deserialize(JsonUtils.serialize(param), Map.class));
    }

    // ==== AUTO GENERATED AUTHENTICATION METHODS BEGIN ====
    /**
     * @summary 发起绑定 MFA 认证要素请求
     * @description 当用户未绑定某个 MFA 认证要素时，可以发起绑定 MFA 认证要素请求。不同类型的 MFA
     *              认证要素绑定请求需要发送不同的参数，详细见 profile 参数。发起验证请求之后，Authing
     *              服务器会根据相应的认证要素类型和传递的参数，使用不同的手段要求验证。此接口会返回
     *              enrollmentToken，你需要在请求「绑定 MFA 认证要素」接口时带上此
     *              enrollmentToken，并提供相应的凭证。
     **/
    public SendEnrollFactorRequestRespDto sendEnrollFactorRequest(SendEnrollFactorRequestDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/send-enroll-factor-request");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, SendEnrollFactorRequestRespDto.class);
    }

    /**
     * @summary 绑定 MFA 认证要素
     * @description 绑定 MFA 要素
     **/
    public EnrollFactorRespDto enrollFactor(EnrollFactorDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/enroll-factor");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, EnrollFactorRespDto.class);
    }

    /**
     * @summary 解绑 MFA 认证要素
     * @description 当前不支持通过此接口解绑短信、邮箱验证码类型的认证要素。如果需要，请调用「解绑邮箱」和「解绑手机号」接口。
     **/
    public ResetFactorRespDto resetFactor(RestFactorDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/reset-factor");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, ResetFactorRespDto.class);
    }

    /**
     * @summary 获取绑定的所有 MFA 认证要素
     * @description Authing 目前支持四种类型的 MFA 认证要素：手机短信、邮件验证码、OTP、人脸。如果用户绑定了手机号 /
     *              邮箱之后，默认就具备了手机短信、邮箱验证码的 MFA 认证要素。
     **/
    public ListEnrolledFactorsRespDto listEnrolledFactors() {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/list-enrolled-factors");
        config.setBody(new Object());
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, ListEnrolledFactorsRespDto.class);
    }

    /**
     * @summary 获取绑定的某个 MFA 认证要素
     * @description 根据 Factor ID 获取用户绑定的某个 MFA Factor 详情。
     **/
    public GetFactorRespDto getFactor(GetFactorDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-factor");
        config.setBody(reqDto);
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, GetFactorRespDto.class);
    }

    /**
     * @summary 获取可绑定的 MFA 认证要素
     * @description 获取所有应用已经开启、用户暂未绑定的 MFA 认证要素，用户可以从返回的列表中绑定新的 MFA 认证要素。
     **/
    public ListFactorsToEnrollRespDto listFactorsToEnroll() {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/list-factors-to-enroll");
        config.setBody(new Object());
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, ListFactorsToEnrollRespDto.class);
    }

    /**
     * @summary 绑定外部身份源
     * @description
     *
     *              由于绝大多数的外部身份源登录不允许在第三方系统直接输入账号密码进行登录，所以外部身份源的绑定总是需要先跳转到对方的登录页面进行认证。此端点会通过浏览器
     *              `302` 跳转的方式先跳转到第三方的登录页面，
     *              终端用户在第三方系统认证完成之后，浏览器再会跳转到 Authing 服务器，Authing
     *              服务器会将此外部身份源绑定到该用户身上。最终的结果会通过浏览器 Window Post Message 的方式传递给开发者。
     *              你可以在你的应用系统中放置一个按钮，引导用户点击之后，弹出一个 Window
     *              Popup，地址为此端点，当用户在第三方身份源认证完成之后，此 Popup 会通过 Window Post Message
     *              的方式传递给父窗口。
     *
     *              为此我们在 `@authing/browser` SDK 中封装了相关方法，为开发者省去了其中大量的细节：
     *
     *              ```typescript
     *              import { Authing } from "@authing/browser"
     *              const sdk = new Authing({
     *              // 应用的认证地址，例如：https://domain.authing.cn
     *              domain: "",
     *
     *              // Authing 应用 ID
     *              appId: "you_authing_app_id",
     *
     *              // 登录回调地址，需要在控制台『应用配置 - 登录回调 URL』中指定
     *              redirectUri: "your_redirect_uri"
     *              });
     *
     *
     *              // success 表示此次绑定操作是否成功；
     *              // errMsg 为如果绑定失败，具体的失败原因，如此身份源已被其他账号绑定等。
     *              // identities 为此次绑定操作具体绑定的第三方身份信息
     *              const { success, errMsg, identities } = await
     *              sdk.bindExtIdpWithPopup({
     *              "extIdpConnIdentifier": "my-wechat"
     *              })
     *
     *              ```
     *
     *              绑定外部身份源成功之后，你可以得到用户在此第三方身份源的信息，以绑定飞书账号为例：
     *
     *              ```json
     *              [
     *              {
     *              "identityId": "62f20932xxxxbcc10d966ee5",
     *              "extIdpId": "62f209327xxxxcc10d966ee5",
     *              "provider": "lark",
     *              "type": "open_id",
     *              "userIdInIdp": "ou_8bae746eac07cd2564654140d2a9ac61",
     *              "originConnIds": ["62f2093244fa5cb19ff21ed3"]
     *              },
     *              {
     *              "identityId": "62f726239xxxxe3285d21c93",
     *              "extIdpId": "62f209327xxxxcc10d966ee5",
     *              "provider": "lark",
     *              "type": "union_id",
     *              "userIdInIdp": "on_093ce5023288856aa0abe4099123b18b",
     *              "originConnIds": ["62f2093244fa5cb19ff21ed3"]
     *              },
     *              {
     *              "identityId": "62f72623e011cf10c8851e4c",
     *              "extIdpId": "62f209327xxxxcc10d966ee5",
     *              "provider": "lark",
     *              "type": "user_id",
     *              "userIdInIdp": "23ded785",
     *              "originConnIds": ["62f2093244fa5cb19ff21ed3"]
     *              }
     *              ]
     *              ```
     *
     *              可以看到，我们获取到了用户在飞书中的身份信息：
     *
     *              - `open_id`: ou_8bae746eac07cd2564654140d2a9ac61
     *              - `union_id`: on_093ce5023288856aa0abe4099123b18b
     *              - `user_id`: 23ded785
     *
     *              绑定此外部身份源之后，后续用户就可以使用此身份源进行登录了，见**登录**接口。
     *
     *
     **/
    public GenerateBindExtIdpLinkRespDto linkExtIdp(LinkExtidpDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/link-extidp");
        config.setBody(reqDto);
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, GenerateBindExtIdpLinkRespDto.class);
    }

    /**
     * @summary 生成绑定外部身份源的链接
     * @description
     *
     *              由于绝大多数的外部身份源登录不允许在第三方系统直接输入账号密码进行登录，所以外部身份源的绑定总是需要先跳转到对方的登录页面进行认证。此端点会通过浏览器
     *              `302` 跳转的方式先跳转到第三方的登录页面，
     *              终端用户在第三方系统认证完成之后，浏览器再会跳转到 Authing 服务器，Authing
     *              服务器会将此外部身份源绑定到该用户身上。最终的结果会通过浏览器 Window Post Message 的方式传递给开发者。
     *              你可以在你的应用系统中放置一个按钮，引导用户点击之后，弹出一个 Window
     *              Popup，地址为此端点，当用户在第三方身份源认证完成之后，此 Popup 会通过 Window Post Message
     *              的方式传递给父窗口。
     *
     *              为此我们在 `@authing/browser` SDK 中封装了相关方法，为开发者省去了其中大量的细节：
     *
     *              ```typescript
     *              import { Authing } from "@authing/browser"
     *              const sdk = new Authing({
     *              // 应用的认证地址，例如：https://domain.authing.cn
     *              domain: "",
     *
     *              // Authing 应用 ID
     *              appId: "you_authing_app_id",
     *
     *              // 登录回调地址，需要在控制台『应用配置 - 登录回调 URL』中指定
     *              redirectUri: "your_redirect_uri"
     *              });
     *
     *
     *              // success 表示此次绑定操作是否成功；
     *              // errMsg 为如果绑定失败，具体的失败原因，如此身份源已被其他账号绑定等。
     *              // identities 为此次绑定操作具体绑定的第三方身份信息
     *              const { success, errMsg, identities } = await
     *              sdk.bindExtIdpWithPopup({
     *              "extIdpConnIdentifier": "my-wechat"
     *              })
     *
     *              ```
     *
     *              绑定外部身份源成功之后，你可以得到用户在此第三方身份源的信息，以绑定飞书账号为例：
     *
     *              ```json
     *              [
     *              {
     *              "identityId": "62f20932xxxxbcc10d966ee5",
     *              "extIdpId": "62f209327xxxxcc10d966ee5",
     *              "provider": "lark",
     *              "type": "open_id",
     *              "userIdInIdp": "ou_8bae746eac07cd2564654140d2a9ac61",
     *              "originConnIds": ["62f2093244fa5cb19ff21ed3"]
     *              },
     *              {
     *              "identityId": "62f726239xxxxe3285d21c93",
     *              "extIdpId": "62f209327xxxxcc10d966ee5",
     *              "provider": "lark",
     *              "type": "union_id",
     *              "userIdInIdp": "on_093ce5023288856aa0abe4099123b18b",
     *              "originConnIds": ["62f2093244fa5cb19ff21ed3"]
     *              },
     *              {
     *              "identityId": "62f72623e011cf10c8851e4c",
     *              "extIdpId": "62f209327xxxxcc10d966ee5",
     *              "provider": "lark",
     *              "type": "user_id",
     *              "userIdInIdp": "23ded785",
     *              "originConnIds": ["62f2093244fa5cb19ff21ed3"]
     *              }
     *              ]
     *              ```
     *
     *              可以看到，我们获取到了用户在飞书中的身份信息：
     *
     *              - `open_id`: ou_8bae746eac07cd2564654140d2a9ac61
     *              - `union_id`: on_093ce5023288856aa0abe4099123b18b
     *              - `user_id`: 23ded785
     *
     *              绑定此外部身份源之后，后续用户就可以使用此身份源进行登录了，见**登录**接口。
     *
     *
     **/
    public GenerateBindExtIdpLinkRespDto generateLinkExtIdpUrl(GenerateLinkExtidpUrlDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/generate-link-extidp-url");
        config.setBody(reqDto);
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, GenerateBindExtIdpLinkRespDto.class);
    }

    /**
     * @summary 解绑外部身份源
     * @description 解绑外部身份源，此接口需要传递用户绑定的外部身份源 ID，**注意不是身份源连接 ID**。
     **/
    public CommonResponseDto unbindExtIdp(UnbindExtIdpDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/unlink-extidp");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, CommonResponseDto.class);
    }

    /**
     * @summary 获取绑定的外部身份源
     * @description
     *              如在**介绍**部分中所描述的，一个外部身份源对应多个外部身份源连接，用户通过某个外部身份源连接绑定了某个外部身份源账号之后，
     *              用户会建立一条与此外部身份源之间的关联关系。此接口用于获取此用户绑定的所有外部身份源。
     *
     *              取决于外部身份源的具体实现，一个用户在外部身份源中，可能会有多个身份 ID，比如在微信体系中会有 `openid` 和
     *              `unionid`，在非书中有
     *              `open_id`、`union_id` 和 `user_id`。在 Authing 中，我们把这样的一条 `open_id`
     *              或者 `unionid_` 叫做一条 `Identity`， 所以用户在一个身份源会有多条 `Identity` 记录。
     *
     *              以微信为例，如果用户使用微信登录或者绑定了微信账号，他的 `Identity` 信息如下所示：
     *
     *              ```json
     *              [
     *              {
     *              "identityId": "62f20932xxxxbcc10d966ee5",
     *              "extIdpId": "62f209327xxxxcc10d966ee5",
     *              "provider": "wechat",
     *              "type": "openid",
     *              "userIdInIdp": "oH_5k5SflrwjGvk7wqpoBKq_cc6M",
     *              "originConnIds": ["62f2093244fa5cb19ff21ed3"]
     *              },
     *              {
     *              "identityId": "62f726239xxxxe3285d21c93",
     *              "extIdpId": "62f209327xxxxcc10d966ee5",
     *              "provider": "wechat",
     *              "type": "unionid",
     *              "userIdInIdp": "o9Nka5ibU-lUGQaeAHqu0nOZyJg0",
     *              "originConnIds": ["62f2093244fa5cb19ff21ed3"]
     *              }
     *              ]
     *              ```
     *
     *
     *              可以看到他们的 `extIdpId` 是一样的，这个是你在 Authing 中创建的**身份源 ID**；`provider`
     *              都是 `wechat`；
     *              通过 `type` 可以区分出哪个是 `openid`，哪个是
     *              `unionid`，以及具体的值（`userIdInIdp`）；他们都来自于同一个身份源连接（`originConnIds`）。
     *
     *
     *
     **/
    public GetIdentitiesRespDto getIdentities() {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-identities");
        config.setBody(new Object());
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, GetIdentitiesRespDto.class);
    }

    /**
     * @summary 获取应用开启的外部身份源列表
     * @description 获取应用开启的外部身份源列表，前端可以基于此渲染外部身份源按钮。
     **/
    public GetExtIdpsRespDto getExtIdps() {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-extidps");
        config.setBody(new Object());
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, GetExtIdpsRespDto.class);
    }

    /**
     * @summary 注册
     * @description
     *              此端点目前支持以下几种基于的注册方式：
     *
     *              1. 基于密码（PASSWORD）：用户名 + 密码，邮箱 + 密码。
     *              2. 基于一次性临时验证码（PASSCODE）：手机号 + 验证码，邮箱 +
     *              验证码。你需要先调用发送短信或者发送邮件接口获取验证码。
     *
     *              社会化登录等使用外部身份源“注册”请直接使用**登录**接口，我们会在其第一次登录的时候为其创建一个新账号。
     *
     **/
    public UserSingleRespDto signUp(SignupDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/signup");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, UserSingleRespDto.class);
    }

    /**
     * @summary 解密微信小程序数据
     **/
    public Object decryptWechatMiniProgramData(DecryptWechatMiniProgramDataDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/decrypt-wechat-miniprogram-data");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, Object.class);
    }

    /**
     * @summary 获取小程序的手机号
     **/
    public Object getWechatMiniprogramPhone(GetWechatMiniProgramPhoneDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-wechat-miniprogram-phone");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, Object.class);
    }

    /**
     * @summary 获取 Authing 服务器缓存的微信小程序、公众号 Access Token
     **/
    public GetWechatAccessTokenRespDto getWechatMpAccessToken(GetWechatAccessTokenDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-wechat-access-token");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, GetWechatAccessTokenRespDto.class);
    }

    /**
     * @summary 获取登录日志
     * @description 获取登录日志
     **/
    public GetLoginHistoryRespDto decryptWechatMiniProgramData1(GetLoginHistoryDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-login-history");
        config.setBody(reqDto);
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, GetLoginHistoryRespDto.class);
    }

    /**
     * @summary 获取登录应用
     * @description 获取登录应用
     **/
    public GetLoggedInAppsRespDto getLoggedInApps() {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-logged-in-apps");
        config.setBody(new Object());
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, GetLoggedInAppsRespDto.class);
    }

    /**
     * @summary 获取具备访问权限的应用
     * @description 获取具备访问权限的应用
     **/
    public GetAccessibleAppsRespDto getAccessibleApps() {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-accessible-apps");
        config.setBody(new Object());
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, GetAccessibleAppsRespDto.class);
    }

    /**
     * @summary 获取部门列表
     * @description 此接口用于获取用户的部门列表，可根据一定排序规则进行排序。
     **/
    public UserDepartmentPaginatedRespDto getDepartmentList(GetDepartmentListDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-department-list");
        config.setBody(reqDto);
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, UserDepartmentPaginatedRespDto.class);
    }

    /**
     * @summary 获取被授权的资源列表
     * @description 此接口用于获取用户被授权的资源列表。
     **/
    public AuthorizedResourcePaginatedRespDto getAuthorizedResources(GetAuthorizedResourcesDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-authorized-resources");
        config.setBody(reqDto);
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, AuthorizedResourcePaginatedRespDto.class);
    }

    /**
     * @summary 文件上传
     **/
    public UploadRespDto upload(UploadDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v2/upload");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, UploadRespDto.class);
    }

    /**
     * @summary 使用用户凭证登录
     * @description
     *              此端点为基于直接 API 调用形式的登录端点，适用于你需要自建登录页面的场景。**此端点暂时不支持
     *              MFA、信息补全、首次密码重置等流程，如有需要，请使用 OIDC 标准协议认证端点。**
     *
     *              ### 错误码
     *
     *              此接口可能出现的异常类型：
     *
     *              | 请求状态码 `statusCode` | 业务状态码 `apiCode` | 含义 | 错误提示示例 |
     *              | --- | --- | --- | --- |
     *              | 400 | - | 请求参数错误 | Parameter passwordPayload must include
     *              account, email, username or phone when authenticationType is
     *              PASSWORD. |
     *              | 403 | 2333 | 账号或密码错误 | Account not exists or password is
     *              incorrect. |
     *              | 403 | 2006 | 密码错误 | Password is incorrect. |
     *              | 403 | 2000 |
     *              当用户池开启**登录失败次数限制**并且**登录安全策略**设置为**验证码**时，如果当前请求触发登录失败次数上限，要求用户输入图形验证码。见**生成图形验证码**接口了解如何生成图形验证码。图形验证码参数通过
     *              `options.captchaCode` 进行传递。 | Please enter captcha code for this
     *              login request. |
     *              | 404 | 2004 | 用户不存在 | User not exists. |
     *              | 499 | 2016 | 当 `passwordEncryptType` 不为 `none` 时，服务器尝试解密密码失败 |
     *              Decrypt password failed, please check your encryption
     *              configuration. |
     *
     *              错误示例：
     *
     *              ```json
     *              {
     *              "statusCode": 400,
     *              "message": "Parameter passwordPayload must include account,
     *              email, username or phone when authenticationType is PASSWORD."
     *              }
     *              ```
     *
     *
     **/
    public LoginTokenRespDto loginByCredentials(LoginByCredentialsDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/signin");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, LoginTokenRespDto.class);
    }

    /**
     * @summary 使用移动端社会化登录
     * @description
     *              此端点为移动端社会化登录接口，使用第三方移动社会化登录返回的临时凭证登录，并换取用户的 `id_token` 和
     *              `access_token`。请先阅读相应社会化登录的接入流程。
     *
     **/
    public LoginTokenRespDto signInByMobile(MobileSignInDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/signin-by-mobile");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, LoginTokenRespDto.class);
    }

    /**
     * @summary 获取支付宝 AuthInfo
     * @description 此接口用于获取发起支付宝认证需要的[初始化参数
     *              AuthInfo](https://opendocs.alipay.com/open/218/105325)。
     **/
    public GetAlipayAuthInfoRespDto getAlipayAuthInfo(GetAlipayAuthinfoDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-alipay-authinfo");
        config.setBody(reqDto);
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, GetAlipayAuthInfoRespDto.class);
    }

    /**
     * @summary 生成用于登录的二维码
     * @description 生成用于登录的二维码，目前支持生成微信公众号扫码登录、小程序扫码登录、自建移动 APP 扫码登录的二维码。
     **/
    public GeneQRCodeRespDto geneQrCode(GenerateQrcodeDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/gene-qrcode");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, GeneQRCodeRespDto.class);
    }

    /**
     * @summary 查询二维码状态
     * @description 按照用户扫码顺序，共分为未扫码、已扫码等待用户确认、用户同意/取消授权、二维码过期以及未知错误六种状态，前端应该通过不同的状态给到用户不同的反馈。你可以通过下面这篇文章了解扫码登录详细的流程：https://docs.authing.cn/v2/concepts/how-qrcode-works.html.
     **/
    public CheckQRCodeStatusRespDto checkQrCodeStatus(CheckQrcodeStatusDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/check-qrcode-status");
        config.setBody(reqDto);
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, CheckQRCodeStatusRespDto.class);
    }

    /**
     * @summary 使用二维码 ticket 换取 TokenSet
     **/
    public LoginTokenRespDto exchangeTokenSetWithQrCodeTicket() {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/exchange-tokenset-with-qrcode-ticket");

        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, LoginTokenRespDto.class);
    }

    /**
     * @summary 生成图形验证码
     * @description 当用户池开启**登录失败次数限制**并且**登录安全策略**设置为**验证码**时，如果当前请求触发登录失败次数上限，要求用户输入图形验证码。此接口用于在前端生成图形验证码，会返回一个
     *              `content-type` 为 `image/svg+xml` 的响应。
     **/
    public Object geneCaptchaCode(CaptchaCodeDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/captcha-code");
        config.setBody(reqDto);
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, Object.class);
    }

    /**
     * @summary 自建 APP 扫码登录：APP 端修改二维码状态
     * @description 此端点用于在自建 APP
     *              扫码登录中修改二维码状态，对应着在浏览器渲染出二维码之后，终端用户扫码、确认授权、取消授权的过程。**此接口要求具备用户的登录态**。
     **/
    public CommonResponseDto changeQrCodeStatus() {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/change-qrcode-status");

        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, CommonResponseDto.class);
    }

    /**
     * @summary 发送短信
     * @description 发送短信时必须指定短信 Channel，每个手机号同一 Channel 在一分钟内只能发送一次。
     **/
    public SendSMSRespDto sendSms(SendSMSDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/send-sms");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, SendSMSRespDto.class);
    }

    /**
     * @summary 发送邮件
     * @description 发送邮件时必须指定邮件 Channel，每个邮箱同一 Channel 在一分钟内只能发送一次。
     **/
    public SendEmailRespDto sendEmail(SendEmailDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/send-email");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, SendEmailRespDto.class);
    }

    /**
     * @summary 获取用户资料
     * @description 此端点用户获取用户资料，需要在请求头中带上用户的 `access_token`，Authing 服务器会根据用户
     *              `access_token` 中的 `scope` 返回对应的字段。
     **/
    public UserSingleRespDto user() {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-profile");
        config.setBody(new Object());
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, UserSingleRespDto.class);
    }

    /**
     * @summary 修改用户资料
     * @description 此接口用于修改用户的用户资料，包含用户的自定义数据。如果需要**修改邮箱**、**修改手机号**、**修改密码**，请使用对应的单独接口。
     **/
    public UserSingleRespDto updateUserProfile(UpdateUserProfileDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/update-profile");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, UserSingleRespDto.class);
    }

    /**
     * @summary 绑定邮箱
     * @description 如果用户还**没有绑定邮箱**，此接口可用于用户**自主**绑定邮箱。如果用户已经绑定邮箱想要修改邮箱，请使用**修改邮箱**接口。你需要先调用**发送邮件**接口发送邮箱验证码。
     **/
    public CommonResponseDto bindEmail(BindEmailDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/bind-email");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, CommonResponseDto.class);
    }

    /**
     * @summary 绑定手机号
     * @description 如果用户还**没有绑定手机号**，此接口可用于用户**自主**绑定手机号。如果用户已经绑定手机号想要修改手机号，请使用**修改手机号**接口。你需要先调用**发送短信**接口发送短信验证码。
     **/
    public CommonResponseDto bindPhone(BindPhoneDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/bind-phone");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, CommonResponseDto.class);
    }

    /**
     * @summary 获取密码强度和账号安全等级评分
     * @description 获取用户的密码强度和账号安全等级评分，需要在请求头中带上用户的 `access_token`。
     **/
    public GetSecurityInfoRespDto getSecurityLevel() {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-security-info");
        config.setBody(new Object());
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, GetSecurityInfoRespDto.class);
    }

    /**
     * @summary 修改密码
     * @description 此端点用于用户自主修改密码，如果用户之前已经设置密码，需要提供用户的原始密码作为凭证。如果用户忘记了当前密码，请使用**忘记密码**接口。
     **/
    public CommonResponseDto updatePassword(UpdatePasswordDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/update-password");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, CommonResponseDto.class);
    }

    /**
     * @summary 发起修改邮箱的验证请求
     * @description 终端用户自主修改邮箱时，需要提供相应的验证手段。此接口用于验证用户的修改邮箱请求是否合法。当前支持通过**邮箱验证码**的方式进行验证，你需要先调用发送邮件接口发送对应的邮件验证码。
     **/
    public VerifyUpdateEmailRequestRespDto updateEmailVerification(UpdateEmailVerifyRequestDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/veirfy-update-email-request");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, VerifyUpdateEmailRequestRespDto.class);
    }

    /**
     * @summary 修改邮箱
     * @description 终端用户自主修改邮箱，需要提供相应的验证手段，见[发起修改邮箱的验证请求](#tag/用户资料/修改邮箱/operation/ProfileV3Controller_updateEmailVerification)。
     *              此参数需要提供一次性临时凭证 `updateEmailToken`，此数据需要从**发起修改邮箱的验证请求**接口获取。
     **/
    public CommonResponseDto updateEmail(UpdateEmailDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/update-email");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, CommonResponseDto.class);
    }

    /**
     * @summary 发起修改手机号的验证请求
     * @description 终端用户自主修改手机号时，需要提供相应的验证手段。此接口用于验证用户的修改手机号请求是否合法。当前支持通过**短信验证码**的方式进行验证，你需要先调用发送短信接口发送对应的短信验证码。
     **/
    public VerifyUpdatePhoneRequestRespDto updatePhoneVerification(VerifyUpdatePhoneRequestDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/veirfy-update-phone-request");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, VerifyUpdatePhoneRequestRespDto.class);
    }

    /**
     * @summary 修改手机号
     * @description 终端用户自主修改手机号，需要提供相应的验证手段，见[发起修改手机号的验证请求](#tag/用户资料/修改邮箱/operation/ProfileV3Controller_updatePhoneVerification)。
     *              此参数需要提供一次性临时凭证 `updatePhoneToken`，此数据需要从**发起修改手机号的验证请求**接口获取。
     **/
    public CommonResponseDto updatePhone(UpdatePhoneDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/update-phone");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, CommonResponseDto.class);
    }

    /**
     * @summary 发起忘记密码请求
     * @description 当用户忘记密码时，可以通过此端点找回密码。用户需要使用相关验证手段进行验证，目前支持**邮箱验证码**和**手机号验证码**两种验证手段。
     **/
    public PasswordResetVerifyResp resetPasswordVerify(VerifyResetPasswordRequestDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/verify-reset-password-request");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, PasswordResetVerifyResp.class);
    }

    /**
     * @summary 忘记密码
     * @description 此端点用于用户忘记密码之后，通过**手机号验证码**或者**邮箱验证码**的方式重置密码。此接口需要提供用于重置密码的临时凭证
     *              `passwordResetToken`，此参数需要通过**发起忘记密码请求**接口获取。
     **/
    public IsSuccessRespDto resetPassword(ResetPasswordDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/reset-password");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, IsSuccessRespDto.class);
    }

    /**
     * @summary 发起注销账号请求
     * @description 当用户希望注销账号时，需提供相应凭证，当前支持**使用邮箱验证码**、使用**手机验证码**、**使用密码**三种验证方式。
     **/
    public VerifyDeleteAccountRequestRespDto deleteAccountVerify(VerifyDeleteAccountRequestDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/verify-delete-account-request");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, VerifyDeleteAccountRequestRespDto.class);
    }

    /**
     * @summary 注销账户
     * @description 此端点用于用户自主注销账号，需要提供用于注销账号的临时凭证
     *              passwordResetToken，此参数需要通过**发起注销账号请求**接口获取。
     **/
    public IsSuccessRespDto deleteAccount(DeleteAccounDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/delete-account");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, IsSuccessRespDto.class);
    }

    /**
     * @summary 获取应用公开配置
     **/
    public Object getApplicationPublicConfig(GetApplicationPublicConfigDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-application-public-config");
        config.setBody(reqDto);
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, Object.class);
    }

    /**
     * @summary 获取服务器公开信息
     * @description 可端点可获取服务器的公开信息，如 RSA256 公钥、SM2 公钥、Authing 服务版本号等。
     **/
    public SystemInfoResp getSystemInfo() {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/system");
        config.setBody(new Object());
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, SystemInfoResp.class);
    }

    /**
     * @summary 获取国家列表
     * @description 动态获取国家列表，可以用于前端登录页面国家选择和国际短信输入框选择，以减少前端静态资源体积。
     **/
    public GetCountryListRespDto getCountryList() {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/get-country-list");
        config.setBody(new Object());
        config.setMethod("GET");
        String response = request(config);
        return deserialize(response, GetCountryListRespDto.class);
    }

    /**
     * @summary 预检验验证码是否正确
     * @description 预检测验证码是否有效，此检验不会使得验证码失效。
     **/
    public PreCheckCodeRespDto preCheckCode(PreCheckCodeDto reqDto) {
        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/api/v3/pre-check-code");
        config.setBody(reqDto);
        config.setMethod("POST");
        String response = request(config);
        return deserialize(response, PreCheckCodeRespDto.class);
    }

    // ==== AUTO GENERATED AUTHENTICATION METHODS END ====


    public AuthenticationClientOptions getOptions() {
        return options;
    }
    /**
     * Client Credentials 模式获取 Access Token
     */
    public GetAccessTokenByClientCredentialsRespDto getAccessTokenByClientCredentials(String scope, ClientCredentialInput options) throws Exception {
        if (StrUtil.isEmpty(scope)) {
            throw new InvalidParameterException("请传入 scope 参数，请看文档：https://docs.authing.cn/v2/guides/authorization/m2m-authz.html");
        }

        if (options == null) {
            throw new InvalidParameterException("请在调用本方法时传入 { accessKey: string, accessSecret: string }，请看文档：https://docs.authing.cn/v2/guides/authorization/m2m-authz.html");
        }

        GetAccessTokenByClientCredentialsDto reqDto = new GetAccessTokenByClientCredentialsDto();
        reqDto.setScope(scope);
        reqDto.setClientId(options.getAccessKey());
        reqDto.setClientSecret(options.getAccessSecret());
        reqDto.setGrantType(TokenEndPointParams.Grant_type.CLIENT_CREDENTIALS.getValue());

        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");

        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/oidc/token");
        config.setBody(reqDto);
        config.setMethod("UrlencodedPOST");
        config.setHeaders(headers);

        String response = request(config);
        return deserialize(response, GetAccessTokenByClientCredentialsRespDto.class);
    }

    /**
     * accessToken 换取用户信息
     */
    public UserInfo getUserInfoByAccessToken(String accessToken) {
        AuthingRequestConfig config = new AuthingRequestConfig();

        if (ProtocolEnum.OAUTH.getValue().equals(options.getProtocol())) {
            config.setMethod("POST");
            config.setBody(new Object());
        } else {
            config.setMethod("GET");
        }

        config.setUrl("/oidc/me/?access_token=" + accessToken);

        String response = request(config);
        return deserialize(response, UserInfo.class);
    }

    /**
     * 拼接 OIDC、OAuth 2.0、SAML、CAS 协议授权链接
     */
    public String buildAuthorizeUrl(IOidcParams params) {
        if (options.getAppId() == null) {
            throw new InvalidParameterException("请在初始化 AuthenticationClient 时传入 appId");
        }

        if (ProtocolEnum.OIDC.getValue().equals(options.getProtocol())) {
            throw new InvalidParameterException("初始化 AuthenticationClient 传入的 protocol 应为 ProtocolEnum.OIDC 不应该为 $protocol");
        }

        if (StrUtil.isEmpty(options.getRedirectUri())) {
            throw new InvalidParameterException("redirectUri 不应该为空 解决方法：请在 AuthenticationClient 初始化时传入 redirectUri，或者调用 buildAuthorizeUrl 时传入 redirectUri");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("client_id", Optional.ofNullable(params.getAppId()).orElse(options.getAppId()));
        map.put("scope", Optional.ofNullable(params.getScope()).orElse("openid profile email phone address"));
        map.put("state", Optional.ofNullable(params.getState()).orElse(CommonUtils.createRandomString(12)));
        map.put("nonce", Optional.ofNullable(params.getNonce()).orElse(CommonUtils.createRandomString(12)));
        map.put("response_mode", Optional.ofNullable(params.getResponseMode()).orElse(null));
        map.put("response_type", Optional.ofNullable(params.getResponseType()).orElse("code"));
        map.put("redirect_uri", Optional.ofNullable(params.getRedirectUri()).orElse(options.getRedirectUri()));
        map.put("prompt", params.getScope() != null && params.getScope().contains("offline_access") ? "consent" : null);

        return HttpUtils.buildUrlWithQueryParams(options.getHost()+"/oidc/auth", map);
    }

    /**
     * 使用 Refresh token 获取新的 Access token
     */
    public GetNewAccessTokenByRefreshTokenRespDto getNewAccessTokenByRefreshToken(String refreshToken) {
        verificationProtocol();

        String tokenEndPointAuthMethod = options.getTokenEndPointAuthMethod();
        if (AuthMethodEnum.CLIENT_SECRET_POST.getValue().equals(tokenEndPointAuthMethod)) {
            return getNewAccessTokenByRefreshTokenWithClientSecretPost(refreshToken);
        } else if (AuthMethodEnum.CLIENT_SECRET_BASIC.getValue().equals(tokenEndPointAuthMethod)) {
            return getNewAccessTokenByRefreshTokenWithClientSecretBasic(refreshToken);
        } else {
            return getNewAccessTokenByRefreshTokenWithNone(refreshToken);
        }
    }

    private void verificationProtocol() {
        if (!(ProtocolEnum.OAUTH.getValue().equals(options.getProtocol()) || ProtocolEnum.OIDC.getValue().equals(options.getProtocol()))) {
            throw new InvalidParameterException("初始化 AuthenticationClient 时传入的 protocol 参数必须为 ProtocolEnum.OAUTH 或 ProtocolEnum.OIDC，请检查参数");
        }
        if (StrUtil.isEmpty(options.getAppSecret()) && !AuthMethodEnum.NONE.getValue().equals(options.getTokenEndPointAuthMethod())) {
            throw new InvalidParameterException("请在初始化 AuthenticationClient 时传入 appId 和 secret 参数");
        }
    }

    private GetNewAccessTokenByRefreshTokenRespDto getNewAccessTokenByRefreshTokenWithClientSecretPost(String refreshToken) {
        AuthingRequestConfig config = new AuthingRequestConfig();

        if (ProtocolEnum.OIDC.getValue().equals(options.getProtocol())) {
            config.setUrl("/oidc/token");
        } else {
            config.setUrl("/oauth/token");
        }

        config.setMethod("UrlencodedPOST");

        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");
        config.setHeaders(headers);

        GetNewAccessTokenByRefreshTokenDto reqDto = new GetNewAccessTokenByRefreshTokenDto();
        reqDto.setClientId(options.getAppId());
        reqDto.setClientSecret(options.getAppSecret());
        reqDto.setGrantType(TokenEndPointParams.Grant_type.REFRESH_TOKEN.getValue());
        reqDto.setRefreshToken(refreshToken);
        config.setBody(reqDto);

        String response = request(config);
        return deserialize(response, GetNewAccessTokenByRefreshTokenRespDto.class);
    }

    private GetNewAccessTokenByRefreshTokenRespDto getNewAccessTokenByRefreshTokenWithClientSecretBasic(String refreshToken) {
        String basic64Str = "Basic " + Base64Encoder.encode((options.getAppId() + ":" + options.getAppSecret()).getBytes());

        AuthingRequestConfig config = new AuthingRequestConfig();

        if (ProtocolEnum.OIDC.getValue().equals(options.getProtocol())) {
            config.setUrl("/oidc/token");
        } else {
            config.setUrl("/oauth/token");
        }

        config.setMethod("UrlencodedPOST");

        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");
        headers.put(Header.AUTHORIZATION.getValue(), basic64Str);
        config.setHeaders(headers);

        GetNewAccessTokenByRefreshTokenDto reqDto = new GetNewAccessTokenByRefreshTokenDto();
        reqDto.setGrantType(TokenEndPointParams.Grant_type.REFRESH_TOKEN.getValue());
        reqDto.setRefreshToken(refreshToken);
        config.setBody(reqDto);

        String resqonse = request(config);
        return deserialize(resqonse, GetNewAccessTokenByRefreshTokenRespDto.class);
    }

    private GetNewAccessTokenByRefreshTokenRespDto getNewAccessTokenByRefreshTokenWithNone(String refreshToken) {
        AuthingRequestConfig config = new AuthingRequestConfig();

        if (ProtocolEnum.OIDC.getValue().equals(options.getProtocol())) {
            config.setUrl("/oidc/token");
        } else {
            config.setUrl("/oauth/token");
        }

        config.setMethod("UrlencodedPOST");

        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");
        config.setHeaders(headers);

        GetNewAccessTokenByRefreshTokenDto reqDto = new GetNewAccessTokenByRefreshTokenDto();
        reqDto.setClientId(options.getAppId());
        reqDto.setGrantType(TokenEndPointParams.Grant_type.REFRESH_TOKEN.getValue());
        reqDto.setRefreshToken(refreshToken);
        config.setBody(reqDto);

        String response = request(config);
        return deserialize(response, GetNewAccessTokenByRefreshTokenRespDto.class);
    }

    /**
     * 检查 Access token 或 Refresh token 的状态
     */
    public IntrospectTokenWithClientSecretPostRespDto introspectToken(String token) {
        verificationProtocol();

        String introspectionEndPointAuthMethod = options.getIntrospectionEndPointAuthMethod();
        if (AuthMethodEnum.CLIENT_SECRET_POST.getValue().equals(introspectionEndPointAuthMethod)) {
            return introspectTokenWithClientSecretPost(token);
        } else if (AuthMethodEnum.CLIENT_SECRET_BASIC.getValue().equals(introspectionEndPointAuthMethod)) {
            return introspectTokenWithClientSecretBasic(token);
        } else {
            return introspectTokenWithNone(token);
        }
    }

    private IntrospectTokenWithClientSecretPostRespDto introspectTokenWithClientSecretPost(String token) {
        AuthingRequestConfig config = new AuthingRequestConfig();

        if (ProtocolEnum.OIDC.getValue().equals(options.getProtocol())) {
            config.setUrl("/oidc/token/introspection");
        } else {
            config.setUrl("/oauth/token/introspection");
        }

        config.setMethod("UrlencodedPOST");

        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");
        config.setHeaders(headers);

        IntrospectTokenDto reqDto = new IntrospectTokenDto();
        reqDto.setClientId(options.getAppId());
        reqDto.setClientSecret(options.getAppSecret());
        reqDto.setToken(token);
        config.setBody(reqDto);

        String response = request(config);
        return deserialize(response, IntrospectTokenWithClientSecretPostRespDto.class);
    }

    private IntrospectTokenWithClientSecretPostRespDto introspectTokenWithClientSecretBasic(String token) {
        String basic64Str = "Basic " + Base64Encoder.encode((options.getAppId() + ":" + options.getAppSecret()).getBytes());

        AuthingRequestConfig config = new AuthingRequestConfig();

        if (ProtocolEnum.OIDC.getValue().equals(options.getProtocol())) {
            config.setUrl("/oidc/token/introspection");
        } else {
            config.setUrl("/oauth/token/introspection");
        }

        config.setMethod("UrlencodedPOST");

        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");
        headers.put(Header.AUTHORIZATION.getValue(), basic64Str);
        config.setHeaders(headers);

        IntrospectTokenDto reqDto = new IntrospectTokenDto();
        reqDto.setToken(token);
        config.setBody(reqDto);

        String response = request(config);
        return deserialize(response, IntrospectTokenWithClientSecretPostRespDto.class);
    }

    private IntrospectTokenWithClientSecretPostRespDto introspectTokenWithNone(String token) {
        AuthingRequestConfig config = new AuthingRequestConfig();

        if (ProtocolEnum.OIDC.getValue().equals(options.getProtocol())) {
            config.setUrl("/oidc/token/introspection");
        } else {
            config.setUrl("/oauth/token/introspection");
        }

        config.setMethod("UrlencodedPOST");

        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");
        config.setHeaders(headers);

        IntrospectTokenDto reqDto = new IntrospectTokenDto();
        reqDto.setClientId(options.getAppId());
        reqDto.setToken(token);
        config.setBody(reqDto);

        String response = request(config);
        return deserialize(response, IntrospectTokenWithClientSecretPostRespDto.class);
    }

    /**
     * 效验Token合法性
     */
    public ValidateTokenRespDto validateToken(ValidateTokenParams params) {
        String idToken = params.getIdToken();
        String accessToken = params.getAccessToken();
        if (idToken == null && accessToken == null) {
            throw new InvalidParameterException("请在传入的参数对象中包含 accessToken 或 idToken 字段");
        }
        if (accessToken != null && idToken != null) {
            throw new InvalidParameterException("accessToken 和 idToken 只能传入一个，不能同时传入");
        }

        AuthingRequestConfig config = new AuthingRequestConfig();

        if (accessToken != null) {
            config.setUrl("/api/v2/oidc/validate_token?access_token=" + accessToken);
        } else {
            config.setUrl("/api/v2/oidc/validate_token?id_token=" + idToken);
        }

        config.setMethod("GET");

        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");
        config.setHeaders(headers);

        String response = request(config);
        return deserialize(response, ValidateTokenRespDto.class);
    }

    /**
     * 撤回 Access token 或 Refresh token
     */
    public Boolean revokeToken(String token) {
        verificationProtocol();

        String introspectionEndPointAuthMethod = options.getIntrospectionEndPointAuthMethod();
        if (AuthMethodEnum.CLIENT_SECRET_POST.getValue().equals(introspectionEndPointAuthMethod)) {
            return revokeTokenWithClientSecretPost(token);
        } else if (AuthMethodEnum.CLIENT_SECRET_BASIC.getValue().equals(introspectionEndPointAuthMethod)) {
            return revokeTokenWithClientSecretBasic(token);
        } else {
            return revokeTokenWithNone(token);
        }
    }

    private Boolean revokeTokenWithClientSecretPost(String token) {
        AuthingRequestConfig config = new AuthingRequestConfig();

        if (ProtocolEnum.OIDC.getValue().equals(options.getProtocol())) {
            config.setUrl("/oidc/token/revocation");
        } else {
            config.setUrl("/oauth/token/revocation");
        }

        config.setMethod("UrlencodedPOST");

        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");
        config.setHeaders(headers);

        RevokeTokenDto reqDto = new RevokeTokenDto();
        reqDto.setClientId(options.getAppId());
        reqDto.setClientSecret(options.getAppSecret());
        reqDto.setToken(token);
        config.setBody(reqDto);

        String response = request(config);
        return deserialize(response, Boolean.class);
    }

    private Boolean revokeTokenWithClientSecretBasic(String token) {
        if (ProtocolEnum.OAUTH.getValue().equals(options.getProtocol())) {
            throw new InvalidParameterException("OAuth 2.0 暂不支持用 client_secret_basic 模式身份验证撤回 Token");
        }

        String basic64Str = "Basic " + Base64Encoder.encode((options.getAppId() + ":" + options.getAppSecret()).getBytes());

        AuthingRequestConfig config = new AuthingRequestConfig();
        config.setUrl("/oidc/token/revocation");
        config.setMethod("UrlencodedPOST");

        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");
        headers.put(Header.AUTHORIZATION.getValue(), basic64Str);
        config.setHeaders(headers);

        RevokeTokenDto reqDto = new RevokeTokenDto();
        reqDto.setToken(token);
        config.setBody(reqDto);

        String response = request(config);
        return deserialize(response, Boolean.class);
    }

    private Boolean revokeTokenWithNone(String token) {
        AuthingRequestConfig config = new AuthingRequestConfig();

        if (ProtocolEnum.OIDC.getValue().equals(options.getProtocol())) {
            config.setUrl("/oidc/token/revocation");
        } else {
            config.setUrl("/oauth/token/revocation");
        }

        config.setMethod("UrlencodedPOST");

        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded");
        config.setHeaders(headers);

        RevokeTokenDto reqDto = new RevokeTokenDto();
        reqDto.setToken(token);
        reqDto.setClientId(options.getAppId());
        config.setBody(reqDto);

        String response = request(config);
        return deserialize(response, Boolean.class);
    }
}
