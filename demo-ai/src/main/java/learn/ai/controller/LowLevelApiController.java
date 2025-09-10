package learn.ai.controller;

import dev.langchain4j.agent.tool.ToolParameters;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统 prompts and models of ai tools: https://github.com/x1xhlol/system-prompts-and-models-of-ai-tools
 */
@RestController
public class LowLevelApiController {

    final ChatLanguageModel chatLanguageModel = OpenAiChatModel.builder()
            .baseUrl(System.getProperty("openai_url"))
            .apiKey("apikey")
            .modelName("gpt-4o")
            // Qwen3: 在 enable_thinking=True 时，对于思考模式（/think），请使用 Temperature=0.6、TopP=0.95、TopK=20 和 MinP=0
            // Qwen3: 在 enable_thinking=True 时，对于非思考模式（/no_think），建议使用 Temperature=0.7、TopP=0.8、TopK=20 和 MinP=0
            .temperature(0.7d).topP(0.8d).maxTokens(2048).build();


    /**
     * 用于测试请求的 url { @link http://localhost:8080/api/low/chat?msg=%E8%BF%9E%E9%80%9A%E6%80%A7%E6%B5%8B%E8%AF%95%EF%BC%8C%E4%BB%A5%E6%9C%80%E5%BF%AB%E7%9A%84%E9%80%9F%E5%BA%A6%E5%9B%9E%E7%AD%94%E6%88%91}
     */
    @GetMapping("/api/low/chat")
    public String chat(@RequestParam("msg") String msg) {
        String think = "/think", noThink = "/no_think";
        // SystemMessage：定义扮演什么角色、它应该如何表现、以什么方式回答等的说明，比其他类型的消息优先级更高。
        ChatRequest chatRequest = new ChatRequest.Builder()
                .responseFormat(ResponseFormat.TEXT)
                .messages(
                        UserMessage.from(msg + noThink),
                        SystemMessage.from("你是一个知识渊博的小助手，能快速回答各种问题"))

                .toolSpecifications(ToolSpecification.builder().parameters(ToolParameters.builder()
                        .build()).build())
                .build();
        ChatResponse chatResponse = chatLanguageModel.chat(chatRequest);
        return chatResponse.aiMessage().text().replaceAll("think", "pre");
    }

}
