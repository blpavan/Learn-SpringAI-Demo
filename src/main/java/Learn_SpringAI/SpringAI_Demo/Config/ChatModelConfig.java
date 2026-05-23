package Learn_SpringAI.SpringAI_Demo.Config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatModelConfig {

    // Here ChatClient.Builder is automatically injected by Spring, by looking into the dependencies in our project, ex: OpenAI, Gemini or Anthropic Claude
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
    public ChatClient openAIChatClient(OpenAiChatModel openAiChatModel, PromptTemplate ariaSystemPrompt) {
        return ChatClient.builder(openAiChatModel)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4o-mini")
                        .temperature(0.7)
                        .maxTokens(800)
                        .frequencyPenalty(0.3)
                        .build())
                .defaultSystem(ariaSystemPrompt.render())
                .build();
    }

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel  ollamaChatModel,  PromptTemplate maxSystemPrompt) {
        return ChatClient.builder(ollamaChatModel)
                .defaultOptions(OllamaChatOptions.builder()
                .model("llama3.2")
                .temperature(0.7)
                .numCtx(4096)
                .repeatPenalty(1.1)
                .build())
                .defaultSystem(maxSystemPrompt.render())
                .build();
    }


    // Notes

//    In Spring AI ChatOptions,
//    --> temperature controls creativity (
//            0.1 → predictable coding answers,
//            1.0 → creative story-like answers
//    --> topK limits the AI to only the top K possible next words
//                    (example: topK=3 means AI can choose only from the top 3 likely words like Pizza/Burger/Biryani)
//    --> topP allows words until cumulative probability reaches P
//            (example: topP=0.8 may keep only Pizza + Burger if together they make 80% probability)
//    --> seed fixes randomness so the same prompt can generate nearly the same output every time
//            (example: seed=42 helps reproduce results for testing/debugging).



}
