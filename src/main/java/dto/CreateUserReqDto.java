package dto;

import java.util.List;

import dto.CreateIdentityDto;
import dto.CreateUserOptionsDto;

public class CreateUserReqDto {
    /**
     * 账户当前状态
     */
    private CreateUserReqDto.status status;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 手机区号
     */
    private String phoneCountryCode;
    /**
     * 用户名，用户池内唯一
     */
    private String username;
    /**
     * 用户真实名称，不具备唯一性
     */
    private String name;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像链接
     */
    private String photo;
    /**
     * 性别
     */
    private CreateUserReqDto.gender gender;
    /**
     * 邮箱是否验证
     */
    private Boolean emailVerified;
    /**
     * 手机号是否验证
     */
    private Boolean phoneVerified;
    /**
     * 第三方外部 ID
     */
    private String externalId;
    /**
     * 用户所属部门 ID 列表
     */
    private List<string> departmentIds;
    /**
     * 自定义数据，传入的对象中的 key 必须先在用户池定义相关自定义字段
     */
    private any customData;
    /**
     * 密码。必须通过加密方式进行加密。
     */
    private String password;
    /**
     * 租户 ID
     */
    private List<string> tenantIds;
    /**
     * 第三方身份源（建议调用绑定接口进行绑定）
     */
    private List<CreateIdentityDto> identities;
    /**
     * 附加选项
     */
    private CreateUserOptionsDto options;

    public CreateUserReqDto.status getStatus() {
        return status;
    }
    public void setStatus(CreateUserReqDto.status status) {
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

    public CreateUserReqDto.gender getGender() {
        return gender;
    }
    public void setGender(CreateUserReqDto.gender gender) {
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

    public String getExternalId() {
        return externalId;
    }
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<string> getDepartmentIds() {
        return departmentIds;
    }
    public void setDepartmentIds(List<string> departmentIds) {
        this.departmentIds = departmentIds;
    }

    public any getCustomData() {
        return customData;
    }
    public void setCustomData(any customData) {
        this.customData = customData;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public List<string> getTenantIds() {
        return tenantIds;
    }
    public void setTenantIds(List<string> tenantIds) {
        this.tenantIds = tenantIds;
    }

    public List<CreateIdentityDto> getIdentities() {
        return identities;
    }
    public void setIdentities(List<CreateIdentityDto> identities) {
        this.identities = identities;
    }

    public CreateUserOptionsDto getOptions() {
        return options;
    }
    public void setOptions(CreateUserOptionsDto options) {
        this.options = options;
    }


    /**
     * 账户当前状态
     */
    public static enum Status {
        DELETED('Deleted'),
        SUSPENDED('Suspended'),
        RESIGNED('Resigned'),
        ACTIVATED('Activated'),
        ARCHIVED('Archived'),
        ;

        private String value;
        status(String value) {
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
        M('M'),
        W('W'),
        U('U'),
        ;

        private String value;
        gender(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


};