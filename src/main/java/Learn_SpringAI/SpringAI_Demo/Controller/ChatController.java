package Learn_SpringAI.SpringAI_Demo.Controller;

import Learn_SpringAI.SpringAI_Demo.Service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat/OpenAI")
    public String chatWithOpenAIModel(String message) {
       return chatService.ChatWithOpenAIModel(message);
    }

    @GetMapping("/chat/LLama")
    public String chatWithLLamaModel(String message) {
        return chatService.ChatWithLLamaModel(message);
    }
}
