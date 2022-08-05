package person.companion.ddns;

import java.util.Arrays;
import java.util.List;

/**
 * Title:
 * Author companion
 * Written by: 2022/2/19 14:18
 * Describe:
 */
public interface DDNSConstants {
    String ACCESS_KEY_ID = "xx";

    String SECRET = "xx";

    String TYPE = "A";
    // 二级域名
    String DOMAIN_NAME = "xx.com";
    // 主机记录
    List<String> RR = Arrays.asList("note", "gitlab");
}
