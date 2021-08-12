package learn.springcloud.config.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;



/**
 * @author Zephyr
 * @date 2021/8/12.
 */
@Rollback
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("local")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConfigClientApp.class)
public class MockMvcExample {

    @Autowired
    private MockMvc mockMvc;
    // @Autowired
    // WebApplicationContext applicationContext;

    @Before
    public void setup() {
        System.out.println("Before " + this.getClass().getSimpleName() + " test ...");
        // mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
    }


    private ResultActions useGetRequestTemplate(String urlTemplate, MediaType contentType,
                                                Map<String, String> headers, Map<String, String> params) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(urlTemplate);
        Optional.ofNullable(contentType).ifPresent(requestBuilder::contentType);
        requestBuilder.characterEncoding(StandardCharsets.UTF_8.displayName());

        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach((requestBuilder::header));
        }
        if (!CollectionUtils.isEmpty(params)) {
            params.forEach(requestBuilder::queryParam);
        }

        return mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print(System.out))
                // .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private ResultActions usePostRequestTemplate(String urlTemplate, MediaType contentType,
                                                      Map<String, String> headers, Object params) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(urlTemplate);
        // 将转换的json数据放到request的body中
        requestBuilder.contentType(Optional.ofNullable(contentType).orElse(MediaType.APPLICATION_JSON));
        requestBuilder.characterEncoding(StandardCharsets.UTF_8.displayName());

        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach((requestBuilder::header));
        }
        if (params != null) {
            requestBuilder.content(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(params));
        }

        return mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print(System.out))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
