package person.companion.ddns;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 功能描述: ddns需要的参数
 *
 * @author companion
 * Written by : 2022/8/5 16:25
 */
@Data
@NoArgsConstructor
public class DomainParam {
    private String accessKeyId;

    private String secret;

    private String type;

    // 二级域名
    private String domainName;

    // 主机记录
    private List<String> parseList;
}
