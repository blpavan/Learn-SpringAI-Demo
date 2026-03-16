package Learn_SpringAI.SpringAI_Demo.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

//    private final ChatClient chatClient;
//
//    // Here the ChatClient is passed to the constructor, and Spring will automatically inject the ChatClient bean that we defined in the ChatModelConfig class
//
//    public ChatService(ChatClient chatClient) {
//        this.chatClient = chatClient;
//    }
//
//    public String chatWithLLMModel(String message) {
//        return chatClient
//                .prompt()
//                .user(message)
//                .call()
//                .content();
//    }


    private ChatClient openAIChatClient;
    private ChatClient ollamaChatClient;

    public ChatService(ChatClient openAIChatClient, ChatClient ollamaChatClient) {
        this.openAIChatClient = openAIChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    public String ChatWithOpenAIModel(String message) {
        return openAIChatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }


    public String ChatWithLLamaModel(String message) {
        return ollamaChatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }





}
