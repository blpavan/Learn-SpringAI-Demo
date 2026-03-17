package Learn_SpringAI.SpringAI_Demo.Config;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class PromptTemplateConfig {

    // ── System Prompt Templates ──────────────────────────────────────────────

    @Bean
    public PromptTemplate ariaSystemPrompt() {
        return new PromptTemplate(
                new ClassPathResource("prompts/system/aria-system.st")
        );
    }

    @Bean
    public PromptTemplate maxSystemPrompt() {
        return new PromptTemplate(
                new ClassPathResource("prompts/system/max-system.st")
        );
    }

    @Bean
    public PromptTemplate seniorAgentSystemPrompt() {
        return new PromptTemplate(
                new ClassPathResource("prompts/system/senior-agent-system.st")
        );
    }

    // ── User Prompt Templates ────────────────────────────────────────────────

    @Bean
    public PromptTemplate supportUserPrompt() {
        return new PromptTemplate(
                new ClassPathResource("prompts/user/support-user.st")
        );
    }

    @Bean
    public PromptTemplate escalationUserPrompt() {
        return new PromptTemplate(
                new ClassPathResource("prompts/user/escalation-user.st")
        );
    }
}