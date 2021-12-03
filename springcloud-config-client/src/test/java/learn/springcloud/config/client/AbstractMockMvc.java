package learn.springcloud.config.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 针对基于 MockMvc 的单元测试辅助类，用法：
 * 1. 继承本类
 * 2. 必须 调用本类的初始化方法
 * 3. 必须 在子类的每个@Test方法开头都判断'skipTests'的变量值，如果为true，则直接return，不再继续向下执行
 * 4. ** 为了提高打包速度，只要不是在spring环境中执行，就跳过测试 **（否则正常运行测试方法）
 *
 * @author Zephyr
 * @date 2021/11/12.
 */
// @Rollback
// @Transactional
// @AutoConfigureMockMvc
// @ActiveProfiles("local")
// @RunWith(SpringRunner.class)
// @SpringBootTest(classes = ConfigClientApp.class)
public abstract class AbstractMockMvc {
    // @Autowired
    protected MockMvc mockMvc;
    protected boolean skipTests = true;

    // @Before
    public void init(WebApplicationContext webApplicationContext) {
        if (webApplicationContext == null) {
            System.err.println("Skip tests in [" + getClass().getSimpleName() + "] , because applicationContext is null .......");
            return;
        }
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // MockitoAnnotations.initMocks(this);
        // Mockito.when(lessonClient.getLessonBaseInfo(Collections.emptyList()))
        //         .thenReturn(Results.success(Collections.emptyList()));
        skipTests = false;
    }


    protected ResultActions getRequestTemplate(String urlTemplate, MediaType contentType,
                                               Map<String, String> headers, Map<String, String> params) throws Exception {
        checkStatus();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(urlTemplate);
        Optional.ofNullable(contentType).ifPresent(requestBuilder::contentType);
        requestBuilder.characterEncoding(StandardCharsets.UTF_8.name());

        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach((requestBuilder::header));
        }
        if (!CollectionUtils.isEmpty(params)) {
            params.forEach(requestBuilder::queryParam);
        }

        return performMockMvc(requestBuilder);
    }

    protected ResultActions postRequestTemplate(String urlTemplate, MediaType contentType,
                                                Map<String, String> headers, Object params) throws Exception {
        checkStatus();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(urlTemplate);
        // 将转换的json数据放到request的body中
        requestBuilder.contentType(Optional.ofNullable(contentType).orElse(MediaType.APPLICATION_JSON));
        requestBuilder.characterEncoding(StandardCharsets.UTF_8.name());

        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach((requestBuilder::header));
        }
        if (params != null) {
            requestBuilder.content(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(params));
        }

        return performMockMvc(requestBuilder);
    }


    /**
     * 用给定的 UriComponents.UriTemplateVariables 的值替换所有 URI 模板变量
     *
     * @see UriComponents#expandUriComponent(java.lang.String, org.springframework.web.util.UriComponents.UriTemplateVariables, java.util.function.UnaryOperator)
     */
    @Nullable
    protected String expandUriComponent(@Nullable String urlTemplate, Map<String, Object> uriVariables,
                                        @Nullable UnaryOperator<String> encoder) {
        if (urlTemplate == null) {
            return null;
        }
        if (urlTemplate.indexOf('{') == -1) {
            return urlTemplate;
        }
        // if (urlTemplate.indexOf(':') != -1) {
        //     urlTemplate = sanitizeSource(urlTemplate);
        // }
        Matcher matcher = Pattern.compile("\\{([^/]+?)}").matcher(urlTemplate);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String match = matcher.group(1);
            // String varName = getVariableName(match);
            Object varValue = uriVariables.get(match);
            // if (UriTemplateVariables.SKIP_VALUE.equals(varValue)) {
            //     continue;
            // }
            String formatted = (varValue != null ? varValue.toString() : "");
            formatted = encoder != null ? encoder.apply(formatted) : Matcher.quoteReplacement(formatted);
            matcher.appendReplacement(sb, formatted);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private ResultActions performMockMvc(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        // 为返回值设置字符编码，默认值可能会出现中文乱码
        resultActions.andReturn().getResponse().setCharacterEncoding(StandardCharsets.UTF_8.name());
        return resultActions
                .andDo(MockMvcResultHandlers.print(System.out))
                // .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void checkStatus() {
        if (!skipTests && mockMvc == null) {
            throw new IllegalStateException("MockMvc参数未初始化，请先调用初始化方法初始化参数！");
        }
    }
}
