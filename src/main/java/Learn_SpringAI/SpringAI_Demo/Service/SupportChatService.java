package Learn_SpringAI.SpringAI_Demo.Service;

import Learn_SpringAI.SpringAI_Demo.advisors.TokenCountAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class SupportChatService {

    private final ChatClient openAIChatClient;
    private final ChatClient ollamaChatClient;

    // Inject prompt templates — loaded from .st files via PromptTemplateConfig
    private final PromptTemplate supportUserPrompt;
    private final PromptTemplate escalationUserPrompt;
    private final PromptTemplate stuffedPrompt;

    private final TokenCountAdvisor tokenCountAdvisor;

    @Value("${app.prompts.seniorAgentSystemPrompt}")
    private Resource seniorAgentSystemPrompt;

    List<String> blockedWords = List.of(
            "confidential", "internal", "secret", "password", "ssn"
    );


    public SupportChatService(ChatClient openAIChatClient,
                              ChatClient ollamaChatClient,
                              PromptTemplate supportUserPrompt,
                              PromptTemplate escalationUserPrompt,
                              PromptTemplate stuffedPrompt,
                              TokenCountAdvisor tokenCountAdvisor) {

        this.openAIChatClient = openAIChatClient;
        this.ollamaChatClient = ollamaChatClient;
        this.supportUserPrompt = supportUserPrompt;
        this.escalationUserPrompt = escalationUserPrompt;
        this.stuffedPrompt = stuffedPrompt;
        this.tokenCountAdvisor = tokenCountAdvisor;

    }

    // ── Aria (OpenAI) ────────────────────────────────────────────────────────
    public String askAria(String message,
                          String customerName,
                          String orderId) {

        // Fill in the user prompt template variables
        String filledUserPrompt = supportUserPrompt.render(Map.of(
                "message",      message,
                "customerName", customerName,
                "orderId",      orderId
        ));

        return openAIChatClient
                .prompt()
                .user(filledUserPrompt)                 // USER role — filled template
                .call()
                .content();
    }


    // ── Max (Ollama) ─────────────────────────────────────────────────────────
    public String askMax(String message,
                         String customerName,
                         String orderId) {

        String filledUserPrompt = supportUserPrompt.render(Map.of(
                "message",      message,
                "customerName", customerName,
                "orderId",      orderId
        ));

        return ollamaChatClient
                .prompt()
                .user(filledUserPrompt)                 // USER role — filled template
                .call()
                .content();
    }

    // ── Escalation (SYSTEM role override + escalation user template) ─────────
    public String escalateToSeniorAgent(String message,
                                        String customerName,
                                        String orderId,
                                        String priority) {

        // Fill escalation user template
        String filledUserPrompt = escalationUserPrompt.render(Map.of(
                "message",      message,
                "customerName", customerName,
                "orderId",      orderId,
                "priority",     priority
        ));

        String systemPrompt = new PromptTemplate(seniorAgentSystemPrompt)
                .render();

        return openAIChatClient
                .prompt()
                .system(systemPrompt)                     // SYSTEM role override from .st file
                .user(filledUserPrompt)                   // USER role — filled escalation template
                .call()
                .content();
    }


    // ── Update stuffedPromptDemo() ──
    public String stuffedPromptDemo(String message) {

        // Fill {message} placeholder — everything else is already in the .st file
        String filledStuffedPrompt = stuffedPrompt.render(Map.of(
                "message", message
        ));

        return openAIChatClient
                .prompt()
                .advisors(new SafeGuardAdvisor(blockedWords),
                          tokenCountAdvisor,
                          SimpleLoggerAdvisor
                                  .builder()
                                  .order(Integer.MAX_VALUE)
                                  .build()
                        )
                .user(filledStuffedPrompt)
                .call()
                .content();
    }
}


// Message Roles
//1. User Message - > the prompt which users sends to LLMs
//2. System Message -> the prompt that defines the behavior of the LLM, ex: you are a senior customer support specialist at TechShop with full authority to approve refunds and replacements. Review the customer's issue and provide a definitive resolution. Be empathetic, direct, and solutions-focused.
//3. Assistant Message -> the response from the LLM, ex: I understand your issue and I will do my best to help you. I can offer you a refund or a replacement for your product. Please let me know which option you prefer.

// Prompt Template is just a reusable prompt with placeholders that you fill in at runtime — exactly like a mail merge or an SQL prepared statement.