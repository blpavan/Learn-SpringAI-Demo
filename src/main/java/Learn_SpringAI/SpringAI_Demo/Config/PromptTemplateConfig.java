package Learn_SpringAI.SpringAI_Demo.Config;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class PromptTemplateConfig {

    @Value("${app.prompts.stuffedPrompt}")
    private Resource stuffedPromptResource;

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

    @Bean
    public PromptTemplate stuffedPrompt() {
        return new PromptTemplate(stuffedPromptResource);
    }
}