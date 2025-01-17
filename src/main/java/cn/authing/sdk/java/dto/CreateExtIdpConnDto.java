package cn.authing.sdk.java.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;


public class CreateExtIdpConnDto {
    /**
     * 身份源连接 ID
     */
    @JsonProperty("extIdpId")
    private String extIdpId;
    /**
     * 身份源连接类型
     */
    @JsonProperty("type")
    private Type type;
    /**
     * 身份源连接标识
     */
    @JsonProperty("identifier")
    private String identifier;
    /**
     * 连接在登录页的显示名称
     */
    @JsonProperty("displayName")
    private String displayName;
    /**
     * 连接的自定义配置信息
     */
    @JsonProperty("fields")
    private Object fields;
    /**
     * 是否只支持登录
     */
    @JsonProperty("loginOnly")
    private Boolean loginOnly;
    /**
     * 身份源图标
     */
    @JsonProperty("logo")
    private String logo;
    /**
     * 租户 ID
     */
    @JsonProperty("tenantId")
    private String tenantId;

    public String getExtIdpId() {
        return extIdpId;
    }
    public void setExtIdpId(String extIdpId) {
        this.extIdpId = extIdpId;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Object getFields() {
        return fields;
    }
    public void setFields(Object fields) {
        this.fields = fields;
    }

    public Boolean getLoginOnly() {
        return loginOnly;
    }
    public void setLoginOnly(Boolean loginOnly) {
        this.loginOnly = loginOnly;
    }

    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }


    /**
     * 身份源连接类型
     */
    public static enum Type {

        @JsonProperty("oidc")
        OIDC("oidc"),

        @JsonProperty("oauth")
        OAUTH("oauth"),

        @JsonProperty("saml")
        SAML("saml"),

        @JsonProperty("ldap")
        LDAP("ldap"),

        @JsonProperty("ad")
        AD("ad"),

        @JsonProperty("cas")
        CAS("cas"),

        @JsonProperty("azure-ad")
        AZURE_AD("azure-ad"),

        @JsonProperty("alipay")
        ALIPAY("alipay"),

        @JsonProperty("facebook")
        FACEBOOK("facebook"),

        @JsonProperty("twitter")
        TWITTER("twitter"),

        @JsonProperty("google:mobile")
        GOOGLE_MOBILE("google:mobile"),

        @JsonProperty("google")
        GOOGLE("google"),

        @JsonProperty("wechat:pc")
        WECHAT_PC("wechat:pc"),

        @JsonProperty("wechat:mobile")
        WECHAT_MOBILE("wechat:mobile"),

        @JsonProperty("wechat:webpage-authorization")
        WECHAT_WEBPAGE_AUTHORIZATION("wechat:webpage-authorization"),

        @JsonProperty("wechatmp-qrcode")
        WECHATMP_QRCODE("wechatmp-qrcode"),

        @JsonProperty("wechat:miniprogram:default")
        WECHAT_MINIPROGRAM_DEFAULT("wechat:miniprogram:default"),

        @JsonProperty("wechat:miniprogram:qrconnect")
        WECHAT_MINIPROGRAM_QRCONNECT("wechat:miniprogram:qrconnect"),

        @JsonProperty("wechat:miniprogram:app-launch")
        WECHAT_MINIPROGRAM_APP_LAUNCH("wechat:miniprogram:app-launch"),

        @JsonProperty("github")
        GITHUB("github"),

        @JsonProperty("qq")
        QQ("qq"),

        @JsonProperty("wechatwork:corp:qrconnect")
        WECHATWORK_CORP_QRCONNECT("wechatwork:corp:qrconnect"),

        @JsonProperty("wechatwork:agency:qrconnect")
        WECHATWORK_AGENCY_QRCONNECT("wechatwork:agency:qrconnect"),

        @JsonProperty("wechatwork:service-provider:qrconnect")
        WECHATWORK_SERVICE_PROVIDER_QRCONNECT("wechatwork:service-provider:qrconnect"),

        @JsonProperty("wechatwork:mobile")
        WECHATWORK_MOBILE("wechatwork:mobile"),

        @JsonProperty("wechatwork:agency:mobile")
        WECHATWORK_AGENCY_MOBILE("wechatwork:agency:mobile"),

        @JsonProperty("dingtalk")
        DINGTALK("dingtalk"),

        @JsonProperty("dingtalk:provider")
        DINGTALK_PROVIDER("dingtalk:provider"),

        @JsonProperty("weibo")
        WEIBO("weibo"),

        @JsonProperty("apple")
        APPLE("apple"),

        @JsonProperty("apple:web")
        APPLE_WEB("apple:web"),

        @JsonProperty("baidu")
        BAIDU("baidu"),

        @JsonProperty("lark-internal")
        LARK_INTERNAL("lark-internal"),

        @JsonProperty("lark-public")
        LARK_PUBLIC("lark-public"),

        @JsonProperty("gitlab")
        GITLAB("gitlab"),

        @JsonProperty("linkedin")
        LINKEDIN("linkedin"),

        @JsonProperty("slack")
        SLACK("slack"),

        @JsonProperty("yidun")
        YIDUN("yidun"),

        @JsonProperty("qingcloud")
        QINGCLOUD("qingcloud"),

        @JsonProperty("gitee")
        GITEE("gitee"),

        @JsonProperty("instagram")
        INSTAGRAM("instagram"),

        @JsonProperty("welink")
        WELINK("welink"),

        @JsonProperty("ad-kerberos")
        AD_KERBEROS("ad-kerberos"),
        ;

        private String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


}