package cn.authing.sdk.java.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

import cn.authing.sdk.java.dto.IdentityDto;

public class UserDto {
    /**
     * 用户 ID
     */
    @JsonProperty("userId")
    private String userId;
    /**
     * 账号创建时间
     */
    @JsonProperty("createdAt")
    private String createdAt;
    /**
     * 账号更新时间
     */
    @JsonProperty("updatedAt")
    private String updatedAt;
    /**
     * 账户当前状态
     */
    @JsonProperty("status")
    private Status status;
    /**
     * 邮箱
     */
    @JsonProperty("email")
    private String email;
    /**
     * 手机号
     */
    @JsonProperty("phone")
    private String phone;
    /**
     * 手机区号
     */
    @JsonProperty("phoneCountryCode")
    private String phoneCountryCode;
    /**
     * 用户名，用户池内唯一
     */
    @JsonProperty("username")
    private String username;
    /**
     * 用户真实名称，不具备唯一性
     */
    @JsonProperty("name")
    private String name;
    /**
     * 昵称
     */
    @JsonProperty("nickname")
    private String nickname;
    /**
     * 头像链接
     */
    @JsonProperty("photo")
    private String photo;
    /**
     * 历史总登录次数
     */
    @JsonProperty("loginsCount")
    private Integer loginsCount;
    /**
     * 上次登录时间
     */
    @JsonProperty("lastLogin")
    private String lastLogin;
    /**
     * 上次登录 IP
     */
    @JsonProperty("lastIp")
    private String lastIp;
    /**
     * 性别
     */
    @JsonProperty("gender")
    private Gender gender;
    /**
     * 邮箱是否验证
     */
    @JsonProperty("emailVerified")
    private Boolean emailVerified;
    /**
     * 手机号是否验证
     */
    @JsonProperty("phoneVerified")
    private Boolean phoneVerified;
    /**
     * 用户上次密码修改时间
     */
    @JsonProperty("passwordLastSetAt")
    private String passwordLastSetAt;
    /**
     * 出生日期
     */
    @JsonProperty("birthdate")
    private String birthdate;
    /**
     * 所在国家
     */
    @JsonProperty("country")
    private String country;
    /**
     * 所在省份
     */
    @JsonProperty("province")
    private String province;
    /**
     * 所在城市
     */
    @JsonProperty("city")
    private String city;
    /**
     * 所处地址
     */
    @JsonProperty("address")
    private String address;
    /**
     * 所处街道地址
     */
    @JsonProperty("streetAddress")
    private String streetAddress;
    /**
     * 邮政编码号
     */
    @JsonProperty("postalCode")
    private String postalCode;
    /**
     * 第三方外部 ID
     */
    @JsonProperty("externalId")
    private String externalId;
    /**
     * 下次登录要求重置密码
     */
    @JsonProperty("resetPasswordOnNextLogin")
    private Boolean resetPasswordOnNextLogin;
    /**
     * 用户所属部门 ID 列表
     */
    @JsonProperty("departmentIds")
    private List<String> departmentIds;
    /**
     * 外部身份源
     */
    @JsonProperty("identities")
    private List<IdentityDto> identities;
    /**
     * 用户的扩展字段数据
     */
    @JsonProperty("customData")
    private Object customData;
    /**
     * 用户状态上次修改时间
     */
    @JsonProperty("statusChangedAt")
    private String statusChangedAt;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }
    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getLoginsCount() {
        return loginsCount;
    }
    public void setLoginsCount(Integer loginsCount) {
        this.loginsCount = loginsCount;
    }

    public String getLastLogin() {
        return lastLogin;
    }
    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastIp() {
        return lastIp;
    }
    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }
    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getPasswordLastSetAt() {
        return passwordLastSetAt;
    }
    public void setPasswordLastSetAt(String passwordLastSetAt) {
        this.passwordLastSetAt = passwordLastSetAt;
    }

    public String getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getStreetAddress() {
        return streetAddress;
    }
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getExternalId() {
        return externalId;
    }
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Boolean getResetPasswordOnNextLogin() {
        return resetPasswordOnNextLogin;
    }
    public void setResetPasswordOnNextLogin(Boolean resetPasswordOnNextLogin) {
        this.resetPasswordOnNextLogin = resetPasswordOnNextLogin;
    }

    public List<String> getDepartmentIds() {
        return departmentIds;
    }
    public void setDepartmentIds(List<String> departmentIds) {
        this.departmentIds = departmentIds;
    }

    public List<IdentityDto> getIdentities() {
        return identities;
    }
    public void setIdentities(List<IdentityDto> identities) {
        this.identities = identities;
    }

    public Object getCustomData() {
        return customData;
    }
    public void setCustomData(Object customData) {
        this.customData = customData;
    }

    public String getStatusChangedAt() {
        return statusChangedAt;
    }
    public void setStatusChangedAt(String statusChangedAt) {
        this.statusChangedAt = statusChangedAt;
    }


    /**
     * 账户当前状态
     */
    public static enum Status {

        @JsonProperty("Suspended")
        SUSPENDED("Suspended"),

        @JsonProperty("Resigned")
        RESIGNED("Resigned"),

        @JsonProperty("Activated")
        ACTIVATED("Activated"),

        @JsonProperty("Archived")
        ARCHIVED("Archived"),
        ;

        private String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 性别
     */
    public static enum Gender {

        @JsonProperty("M")
        M("M"),

        @JsonProperty("W")
        W("W"),

        @JsonProperty("U")
        U("U"),
        ;

        private String value;

        Gender(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


}