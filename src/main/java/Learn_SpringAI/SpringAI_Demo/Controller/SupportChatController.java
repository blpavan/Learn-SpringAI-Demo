package Learn_SpringAI.SpringAI_Demo.Controller;

import Learn_SpringAI.SpringAI_Demo.Service.SupportChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/support")
@Tag(name = "TechShop Customer Support", description = "Multi-model customer support chatbot")
public class SupportChatController {

    private final SupportChatService supportChatService;

    public SupportChatController(SupportChatService supportChatService) {
        this.supportChatService = supportChatService;
    }

    @Operation(summary = "Chat with Aria — OpenAI (GPT-4o)")
    @GetMapping("/aria")
    public String chatWithAria(
            @RequestParam String message,
            @RequestParam(defaultValue = "Guest")  String customerName,
            @RequestParam(defaultValue = "N/A")    String orderId) {
        return supportChatService.askAria(message, customerName, orderId);
    }

    @Operation(summary = "Chat with Max — Ollama (LLaMA 3.2)")
    @GetMapping("/max")
    public String chatWithMax(
            @RequestParam String message,
            @RequestParam(defaultValue = "Guest")  String customerName,
            @RequestParam(defaultValue = "N/A")    String orderId) {
        return supportChatService.askMax(message, customerName, orderId);
    }

    @Operation(summary = "Escalate to senior agent — SYSTEM role override")
    @GetMapping("/escalate")
    public String escalate(
            @RequestParam String message,
            @RequestParam(defaultValue = "Guest")  String customerName,
            @RequestParam(defaultValue = "N/A")    String orderId,
            @RequestParam(defaultValue = "HIGH")   String priority) {
        return supportChatService.escalateToSeniorAgent(
                message, customerName, orderId, priority);
    }

    @Operation(
            summary = "Prompt Stuffing Demo — dumps entire catalog into prompt",
            description = "Compare response quality and speed against /aria with the same message"
    )
    @GetMapping("/stuffed")
    public String stuffedDemo(
            @RequestParam String message) {
        return supportChatService.stuffedPromptDemo(message);
    }

}