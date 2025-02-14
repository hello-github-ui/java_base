import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created By 19921227 on 2025/2/14 10:16
 */
public class KLine1MinTest {

    public static void main(String[] args) {
        // 定义请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf8");
        // 请求获取数据
        String requestUrl = "http://168.63.17.218:6400/reqxml?action=10002&code=600519&market=1&klinetype=501&cqtype" +
                "=0&props=0|1|2&klineunit=1&501.count=-1500";
        Map<String, Object> map = null;
        // 所有请求的url列表
        List<String> urls = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();

        // 开始时间
        long start = System.currentTimeMillis();

        while (true) {
            long count = urls.stream().distinct().count();
            if (urls.size() != count) {
                break;
            }

            HttpEntity<Object> entity = new HttpEntity<>(headers);
            System.out.println("requestUrl：" + requestUrl);
            map = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, Map.class).getBody();

            // 获取前一个分隔符节点
            if (map != null) {
                Set<String> keyed = map.keySet();
                if (keyed.contains("501.locator")) {
                    List locators = (List) map.get("501.locator");
                    if (locators != null && !locators.isEmpty()) {
                        List inner = (List) locators.get(0);
                        if (inner != null && !inner.isEmpty()) {
                            String preLocator = inner.get(0).toString();
                            requestUrl = "http://168.63.17.218:6400/reqxml?action=10002&code=600519&market=1&klinetype=501&cqtype" +
                                    "=0&props=0|1|2&klineunit=1&501.count=-1500&501.locator=" + preLocator;
                            urls.add(requestUrl);
                        }
                    }
                }

                // 结束
                // 获取 HQDATE 节点
                if (keyed.contains("Min1KLine")) {
                    List min1KLine = (List) map.get("Min1KLine");
                    if (min1KLine != null && !min1KLine.isEmpty()) {
                        List subKLine = (List) min1KLine.get(0);
                        if (subKLine != null && !subKLine.isEmpty()) {
                            String str = subKLine.get(0).toString();
                            if (str != null) {
                                str = str.replaceAll("=", ":");
                                Map<String, Object> tmpMap = JSONObject.parseObject(str, new TypeReference<Map<String, Object>>() {

                                });
                                if (tmpMap != null && !tmpMap.isEmpty()) {
                                    Object hqDate = tmpMap.get("HQDATE");
                                    if (hqDate != null) {
                                        // 找到近两年的数据为止
                                        if (hqDate.toString().contains("202302")) {
                                            System.out.println("找到 HQDATE=202302 的 requestUrl 了：" + requestUrl);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("requestUrl已经不变了：" + requestUrl);

        // 结束时间
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + "ms");

        // 打印map
        System.out.println("map = " + map);

        // 打印数据量
        System.out.println("总共请求的数据量：" + urls.size() * 1500 + "条~~~");
    }
}
