package cn.authing.sdk.java.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ListChildrenDepartmentsDto {
    /**
     * 需要获取的部门 ID
     */
    @JsonProperty("departmentId")
    private String departmentId;
    /**
     * 组织 code
     */
    @JsonProperty("organizationCode")
    private String organizationCode;
    /**
     * 此次调用中使用的部门 ID 的类型
     */
    @JsonProperty("departmentIdType")
    private String departmentIdType;
    /**
     * 是否要排除虚拟组织
     */
    @JsonProperty("excludeVirtualNode")
    private Boolean excludeVirtualNode;

    public String getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getDepartmentIdType() {
        return departmentIdType;
    }
    public void setDepartmentIdType(String departmentIdType) {
        this.departmentIdType = departmentIdType;
    }

    public Boolean getExcludeVirtualNode() {
        return excludeVirtualNode;
    }
    public void setExcludeVirtualNode(Boolean excludeVirtualNode) {
        this.excludeVirtualNode = excludeVirtualNode;
    }



}