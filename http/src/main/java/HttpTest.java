import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

/**
 * @author 030
 * @date 14:24 2021/11/8
 * @description
 */
public class HttpTest {
    /**
     * 获取某个GET请求的响应
     */
    public String getResp(String urlStr) {

        HttpClient client = new DefaultHttpClient();
        BasicHttpContext context = new BasicHttpContext();
        HttpGet httpGet = new HttpGet(urlStr);
        try {
            HttpResponse response = client.execute(httpGet, context);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String httpResponseBody = EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset());
                if (httpResponseBody != null) {
                    return httpResponseBody;
                }
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            return e.getLocalizedMessage();
        } finally {
            client.getConnectionManager().shutdown();
        }
        return null;
    }

    @Test
    public void test1() {
        String resp = getResp("https://www.baidu.com");
        System.out.println(resp);
    }


    /**
     * GET 请求获取响应结果(json格式的返回格式)
     */
    public String httpGet(String urlStr) {
        String str = "";
        try {
//            String urlStr = "https://www.dmoe.cc/random.php?return=json";
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(urlStr);
            //获取 GET 请求响应值
            CloseableHttpResponse response = httpClient.execute(httpGet);
            //将请求响应值转换为String类型
            // InputStream content = response.getEntity().getContent(); // 如果你只想获取响应内容，通过此方式
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            //将String类型转换为json对象
            JSONObject responseJson = JSON.parseObject(responseString);
            //获取对应的value值
            System.out.println(responseJson);
            str = responseJson.get("imgurl").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    @Test
    public void httpGetTest() {
        httpGet("https://www.dmoe.cc/random.php?return=json");
    }

    /**
     * POST 请求获取响应结果（JSON返回格式）
     */
    public void httpPost(String urlStr) {
        try {
//            String urlStr = "https://www.dmoe.cc/random.php?return=json";
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(urlStr);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseString);
            System.out.println(responseJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void httpPostTest() {
        httpPost("https://www.dmoe.cc/random.php?return=json");
    }
}
