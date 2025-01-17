package test.management.dataPermission.policy;

import cn.authing.sdk.java.client.ManagementClient;
import cn.authing.sdk.java.dto.ListDataPolicySubjectPaginatedRespDto;
import cn.authing.sdk.java.dto.ListDataPolicyTargetsDto;
import cn.authing.sdk.java.dto.SubjectDto;
import cn.authing.sdk.java.model.ManagementClientOptions;
import cn.authing.sdk.java.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class ListDataPolicyTargetsTest {
    // 需要替换成你的 Authing Access Key ID
    private static final String ACCESS_KEY_ID = "AUTHING_ACCESS_KEY_ID";
    // 需要替换成你的 Authing Access Key Secret
    private static final String ACCESS_KEY_SECRET = "AUTHING_ACCESS_KEY_SECRET";

    public static void main(String[] args) throws Throwable {
        ManagementClientOptions clientOptions = new ManagementClientOptions();
        clientOptions.setAccessKeyId(ACCESS_KEY_ID);
        clientOptions.setAccessKeySecret(ACCESS_KEY_SECRET);
        // 如果是私有化部署的客户，需要设置 Authing 服务域名
        // clientOptions.setHost("https://api.your-authing-service.com");
        ManagementClient client = new ManagementClient(clientOptions);

        ListDataPolicyTargetsDto reqDto = new ListDataPolicyTargetsDto();
        reqDto.setPolicyId("60b49xxxxxxxxxxxxxxx6e68");
        reqDto.setQuery("主体名称");
        reqDto.setPage(1);
        reqDto.setLimit(10);
        List<SubjectDto.Type> targetType = new ArrayList<>();
        targetType.add(SubjectDto.Type.USER);
        targetType.add(SubjectDto.Type.ROLE);
        reqDto.setTargetType(targetType);
        ListDataPolicySubjectPaginatedRespDto response = client.listDataPolicyTargets(reqDto);
        System.out.println(JsonUtils.serialize(response));
    }
}
