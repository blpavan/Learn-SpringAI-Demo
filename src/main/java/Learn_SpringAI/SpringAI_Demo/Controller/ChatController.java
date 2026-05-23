package Learn_SpringAI.SpringAI_Demo.Controller;

import Learn_SpringAI.SpringAI_Demo.Service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String message) {
        return chatService.streamData(message);
    }

    // Streaming + ChatOptions combined
    @GetMapping(value = "/stream/creative", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamCreative(@RequestParam String prompt) {
        return chatService.streamDataWithOpenAI(prompt);
    }

}
