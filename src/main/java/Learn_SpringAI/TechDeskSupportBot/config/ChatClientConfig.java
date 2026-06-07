package Learn_SpringAI.TechDeskSupportBot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .maxMessages(5)
                .chatMemoryRepository(jdbcChatMemoryRepository).build();
    }

    @Bean
    public Advisor chatMemoryAdvisor(@Qualifier("chatMemory") ChatMemory chatMemory) {
        return MessageChatMemoryAdvisor
                .builder(chatMemory)
                .build();
    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel, Advisor chatMemoryAdvisor) {
        return ChatClient.builder(openAiChatModel)
                .defaultSystem("""
                        You are TechDesk, an IT support assistant.
                        Help users troubleshoot hardware and software issues.
                        Be concise and step-by-step in your responses.
                        """)
                .defaultAdvisors(List.of(chatMemoryAdvisor, new SimpleLoggerAdvisor()))
                .build();
    }


}
