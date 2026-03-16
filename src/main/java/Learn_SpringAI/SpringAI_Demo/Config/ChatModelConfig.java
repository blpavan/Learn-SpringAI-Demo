package Learn_SpringAI.SpringAI_Demo.Config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatModelConfig {

    // Here ChatClient.Builder is automatically injected by Spring, by looking into the dependencies in out project, ex: OpenAI, Gemini or Anthropic Claude
    // the corresponding model object was passed to the builder, so Spring will know which one to inject here

//    @Bean
//    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
//        return chatClientBuilder
//                .build();
//    }


    // If we are running multiple AI models, we can define multiple ChatClient beans, each with a different model
    // see previous block of code ChatClient.Builder is automatically injected by Spring, by looking into the dependencies in out project, ex: OpenAI, Gemini or Anthropic Claude
    // But if we have multiple AI model dependencies in our project spring will not know which one to inject, so we need to specify the model in the builder, so Spring will know which one to inject here
    // we are OpenAiChatModel and OllamaChatModel in our project, so we will define two ChatClient beans, one for each model, and specify the model in the builder, so Spring will know which one to inject here
    @Bean
    public ChatClient openAIChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel)
                .build();
    }

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel  ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel)
                .build();
    }





}
