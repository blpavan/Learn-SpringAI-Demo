package Learn_SpringAI.SpringAI_Demo.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;

    // Here the ChatClient is passed to the constructor, and Spring will automatically inject the ChatClient bean that we defined in the ChatModelConfig class

    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String chatWithLLMModel(String message) {
        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }
}
