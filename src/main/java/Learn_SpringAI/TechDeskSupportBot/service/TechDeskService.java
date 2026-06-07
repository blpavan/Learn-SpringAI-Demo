package Learn_SpringAI.TechDeskSupportBot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
public class TechDeskService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final Advisor advisor;

    public TechDeskService(@Qualifier("chatClient") ChatClient chatClient,
                           @Qualifier("chatMemory") ChatMemory chatMemory,
                           @Qualifier("chatMemoryAdvisor") Advisor advisor) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.advisor = advisor;
    }

    @Cacheable(value = "responses", key = "#sessionId + ':' + #userMessage")
    public String chat(String userMessage, String sessionId) {
        return chatClient.prompt()
                .user(userMessage)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, sessionId))
                .call()
                .content();
    }

    @CacheEvict(value = "responses", allEntries = true)
    public void clearHistory(String sessionId) {
        chatMemory.clear(sessionId);
    }


    // Note: Here redis has no use case just to learn how to use it, we can use in-memory cache for this example. In real world application we can use redis to store chat history and retrieve it when needed.

}
