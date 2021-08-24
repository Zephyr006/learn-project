package learn.example.javase.http;

import learn.base.utils.JsonUtil;
import learn.example.javase.SleepUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Zephyr
 * @date 2021/8/13.
 */
public class ColorfulCloudTest {


    @Test
    public void testInviteUserToVip() throws IOException {
        for (int i = 0; i < 100; i++) {
            String token = getNewUserToken();
            System.out.println("token = " + token);

            // 模拟新用户注册后的兑换间隔
            SleepUtil.randomSleep(30, 50, TimeUnit.MINUTES);

            inviteNewUser(token);
            if (LocalTime.now().getHour() >= 20) {  // 晚上7点后不再执行
                SleepUtil.sleep(13, TimeUnit.HOURS);
            }
        }

    }


    private void inviteNewUser(String token) throws IOException {
        Map<String, String> inviteHeaders = new HashMap<>();
        inviteHeaders.put("User-Agent", "ColorfulClouds/6.1.11 (iPhone; iOS 13.5.1; Scale/2.00)");
        inviteHeaders.put("Content-Type", "application/json");
        inviteHeaders.put("Cy-User-Id", token);
        Map<String, Object> inviteParams = new HashMap<>();
        inviteParams.put("os_type", "ios");
        inviteParams.put("app_name", "wt");
        inviteParams.put("invitation_code", "38523485");

        String inviteRespStr = doPost("https://biz.caiyunapp.com/v3/user/invitation_code/redeem", inviteHeaders, inviteParams);
        Map<String, Object> inviteMap = (Map<String, Object>) JsonUtil.parseMap(inviteRespStr);
        if (Integer.valueOf(0) == inviteMap.get("rc")) {
            System.out.println();
            System.out.println(LocalTime.now().toString() + "  彩云天气新用户邀请成功，SVIP天数喜+ " + inviteMap.get("duration"));
            System.out.println();
        } else {
            System.err.println(LocalTime.now().toString() + "  彩云天气新用户邀请失败，接口返回的响应数据为 " + inviteRespStr);
        }
    }

    private String getNewUserToken() throws IOException {
        Map<String, String> registerHeaders = new HashMap<>(4);
        registerHeaders.put("User-Agent", "ColorfulClouds/6.0.0 (iPhone; iOS 13.5; Scale/2.00)");
        registerHeaders.put("Content-Type", "application/json");

        Map<String, Object> registerParams = new HashMap<>(4);
        registerParams.put("app_name", "weather");
        registerParams.put("zone", "86");


        Map<String, ?> registerRespMap;
        String respStr;
        boolean firstTime = true;
        do
        {
            if (!firstTime) {
                SleepUtil.randomSleep(1, 5, TimeUnit.MINUTES);
            }
            registerParams.put("code", "@time('" + nextRandom() + "')");  // 随机数5位-6位
            registerParams.put("device_id", "@time('" + nextRandom() + "')");
            registerParams.put("phone_num", "@time('" + nextRandom() + "')");
            respStr = doPost("https://biz.caiyunapp.com/v1/login_by_code", registerHeaders, registerParams);

            System.out.println(LocalTime.now().toString() + "  请求了一次注册彩云天气用户的接口。。。");
        } while ( !"ok".equals( (registerRespMap = JsonUtil.parseMap(respStr)).get("status") ) && !(firstTime = false));

        Map<String, Object> result = (Map<String, Object>) registerRespMap.get("result");
        return String.valueOf(result.get("token"));
    }

    private int nextRandom() {
        Random random = new Random();
        while (true) {
            int i = random.nextInt(99_9999);
            if (i > 10000 && i < 99_9999) {
                return i;
            }
            SleepUtil.sleep(35);
        }
    }


    private String doPost(String url, Map<String, String> headers, Map<String, Object> params) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost post = new HttpPost(url);
        if (headers != null) {
            headers.forEach(post::setHeader);
        }

        Optional.ofNullable(params).ifPresent(paramMap -> {
            String paramStr = JsonUtil.toJSONString(paramMap);
            post.setEntity(new StringEntity(Objects.requireNonNull(paramStr),
                    ContentType.create("application/json", "utf-8")));
        });

        CloseableHttpResponse response = httpClient.execute(post);
        if (response != null && response.getStatusLine().getStatusCode() == 200) {
            return EntityUtils.toString(response.getEntity());
        }
        return "{}";
    }
}
