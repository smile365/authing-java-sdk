package test;

import cn.authing.sdk.java.client.AuthenticationClient;
import cn.authing.sdk.java.dto.authentication.OIDCTokenResponse;
import cn.authing.sdk.java.enums.AuthMethodEnum;
import cn.authing.sdk.java.model.AuthenticationClientOptions;
import cn.authing.sdk.java.util.JsonUtils;


public class RevokeTokenTest {
    private static final String APP_ID = "YOUR_APP_ID";
    private static final String APP_SECRET = "YOUR_SECRET";
    private static final String REDIRECT_URI = "YOUR_REDIRECT_URI";

    public static void main(String[] args) throws Exception {
        AuthenticationClientOptions clientOptions = new AuthenticationClientOptions(APP_ID, APP_SECRET, REDIRECT_URI);
        clientOptions.setIntrospectionEndPointAuthMethod(AuthMethodEnum.NONE.getValue());
        AuthenticationClient client = new AuthenticationClient(clientOptions);

        OIDCTokenResponse oidcTokenResponse = client.getAccessTokenByCode("");
        Boolean response = client.revokeToken(oidcTokenResponse.getAccessToken());
        System.out.println(JsonUtils.serialize(response));
    }
}
