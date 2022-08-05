package person.companion.ddns;

import com.aliyun.alidns20150109.Client;
import com.aliyun.alidns20150109.models.AddDomainRecordRequest;
import com.aliyun.alidns20150109.models.DescribeDomainRecordsRequest;
import com.aliyun.alidns20150109.models.UpdateDomainRecordRequest;
import com.aliyun.teaopenapi.models.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 动态域名解析
 */
public class DDNS {
    public static void main(String[] args) throws Exception {
        DomainParam param = new DomainParam();
        param.setAccessKeyId("xx");
        param.setSecret("xx");
        param.setType("A");
        param.setDomainName("xx.com");
        param.setParseList(Arrays.asList("note", "gitlab"));

        execute(param);
    }

    static void execute(DomainParam domainParam) throws Exception {
        Client client = createClient(domainParam);
        // 查询已经有的解析记录
        DescribeDomainRecordsRequest describe = new DescribeDomainRecordsRequest().setDomainName(domainParam.getDomainName());
        List<String> existsRecords = getExistRecords(client, describe).stream().map(DomainRecord::getRr).collect(Collectors.toList());

        // 不能添加已有记录，会报错
        ArrayList<String> addRecordList = new ArrayList<>(domainParam.getParseList());
        addRecordList.removeAll(existsRecords);
        String currentHostIP = getCurrentHostIP();
        System.out.println("-------------------------------当前主机公网IP为：" + currentHostIP + "-------------------------------");
        for (String rr : addRecordList) {
            AddDomainRecordRequest request = new AddDomainRecordRequest();
            request.setType(domainParam.getType());
            request.setDomainName(domainParam.getDomainName());
            request.setValue(currentHostIP);
            request.setRR(rr);
            client.addDomainRecord(request);
        }

        // 死循环获取最新的ip
        while (true) {
            TimeUnit.MINUTES.sleep(1);
            currentHostIP = getCurrentHostIP();
            System.out.println("-------------------------------当前主机公网IP为：" + currentHostIP + "-------------------------------");
            List<DomainRecord> domainRecords = getExistRecords(client, describe);
            for (DomainRecord record : domainRecords) {
                // 更新数据
                if (!currentHostIP.equals(record.getValue()) && domainParam.getParseList().contains(record.getRr())) {
                    UpdateDomainRecordRequest update = new UpdateDomainRecordRequest();
                    update.setType(domainParam.getType());
                    update.setValue(currentHostIP);
                    update.setRR(record.getRr());
                    update.setRecordId(record.getRecordId());
                    client.updateDomainRecord(update);
                }
            }
        }
    }

    /**
     * 获取已经存在的解析记录
     *
     * @param client   client
     * @param describe describe
     * @return 解析记录
     * @throws Exception 异常
     */
    private static List<DomainRecord> getExistRecords(Client client, DescribeDomainRecordsRequest describe) throws Exception {
        return client.describeDomainRecords(describe)
                .getBody().getDomainRecords().getRecord()
                .stream().map(c -> new DomainRecord(c.getRR(), c.getValue(), c.getRecordId()))
                .collect(Collectors.toList());
    }

    /**
     * 使用AK&SK初始化账号Client
     *
     * @return Client
     * @throws Exception exception
     */
    public static Client createClient(DomainParam domainParam) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(domainParam.getAccessKeyId())
                // 您的AccessKey Secret
                .setAccessKeySecret(domainParam.getSecret());
        // 访问的域名
        config.endpoint = "alidns.cn-hangzhou.aliyuncs.com";
        return new Client(config);
    }

    /**
     * 获取当前主机公网IP
     */
    private static String getCurrentHostIP() {
        // 这里使用jsonip.com第三方接口获取本地IP
        String jsonip = "https://jsonip.com/";
        // 接口返回结果
        String result = "";
        BufferedReader in = null;
        try {
            // 使用HttpURLConnection网络请求第三方接口
            URL url = new URL(jsonip);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            in = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }
        // 正则表达式，提取xxx.xxx.xxx.xxx，将IP地址从接口返回结果中提取出来
        String rexp = "(\\d{1,3}\\.){3}\\d{1,3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(result);
        String res = "";
        while (mat.find()) {
            res = mat.group();
            break;
        }
        return res;
    }
}