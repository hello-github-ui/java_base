import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
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
}
