package learn.springcloud.config.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Zephyr
 * @date 2020/11/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConfigClientApp.class)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class SpringAppTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Transactional
    @Rollback   // 事务回滚,默认是true
    public void testMockMvc() throws Exception {
        //StandaloneMockMvcBuilder standaloneMockMvcBuilder = MockMvcBuilders.standaloneSetup(new TestConfigController());

        String json = "{}";
        ResultActions resultActions = mockMvc
                // 执行一个请求
                .perform(MockMvcRequestBuilders
                        // 模拟请求构建
                        .get("/testConfig")
                        .param("param", "fromTest")
                        //代表发送端发送的数据格式是application/json;charset=UTF-8
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        //代表客户端希望接受的数据类型
                        .accept(MediaType.ALL)
                        .header("Authorization", "243543645")
                        .content(json.getBytes()));
        resultActions.andReturn().getResponse().setCharacterEncoding("UTF-8");
        resultActions
                // 添加执行完成后的断言: 看请求的状态响应码是否为200如果不是则抛异常，测试不通过
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 添加一个结果处理器，表示要对结果做点什么事情，比如此处使用print()：输出整个响应结果信息
                .andDo(MockMvcResultHandlers.print(System.err));
    }

}
