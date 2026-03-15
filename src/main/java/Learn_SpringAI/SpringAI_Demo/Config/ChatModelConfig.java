package Learn_SpringAI.SpringAI_Demo.Config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatModelConfig {

    // Here ChatClient.Builder is automatically injected by Spring, by looking into the dependencies in out project, ex: OpenAI, Gemini or Anthropic Claude
    // the corresponding model object was passed to the builder, so Spring will know which one to inject here

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .build();
    }


}
