package shop.shportfolio.marketdata.insight.infrastructure.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.net.URISyntaxException;

@Configuration
@EnableConfigurationProperties(ChatClientConfigData.class)
public class ChatClientConfig {

    private final ChatClientConfigData chatClientConfigData;

    @Autowired
    public ChatClientConfig(ChatClientConfigData chatClientConfigData) {
        this.chatClientConfigData = chatClientConfigData;
    }

    @Bean
    public ChatClient openAiChatClient() {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(chatClientConfigData.getApiKey())
                .baseUrl(chatClientConfigData.getBaseUrl())
                .build();
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions
                .builder()
                .model(chatClientConfigData.getModel())
                .temperature(Double.parseDouble(chatClientConfigData.getTemperature()))
                .maxCompletionTokens(chatClientConfigData.getMaxCompletionTokens())
                .build();

        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(chatClientConfigData.getMaxTries().intValue());
        retryTemplate.setRetryPolicy(retryPolicy);
        ChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .retryTemplate(retryTemplate)
                .defaultOptions(openAiChatOptions)
                .build();
        return ChatClient.create(chatModel);
    }


}
