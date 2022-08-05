package person.companion.ddns;

import java.util.Arrays;

/**
 * 功能描述: docker运行ddns
 *
 * @author companion
 * Written by : 2022/8/5 16:38
 */
public class DDNSDocker {
    public static void main(String[] args) throws Exception {
        DomainParam param = new DomainParam();
        param.setAccessKeyId(args[0]);
        param.setSecret(args[1]);
        param.setType(args[2]);
        param.setDomainName(args[3]);
        param.setParseList(Arrays.asList(args[4].split(",")));

        DDNS.execute(param);
    }
}
