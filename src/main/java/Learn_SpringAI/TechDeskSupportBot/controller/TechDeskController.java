package Learn_SpringAI.TechDeskSupportBot.controller;

import Learn_SpringAI.TechDeskSupportBot.DTO.SupportRequest;
import Learn_SpringAI.TechDeskSupportBot.DTO.SupportResponse;
import Learn_SpringAI.TechDeskSupportBot.service.TechDeskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/techdesk")
public class TechDeskController {

    private final TechDeskService techDeskService;

    public TechDeskController(TechDeskService techDeskService) {
        this.techDeskService = techDeskService;
    }

    @PostMapping("/chat")
    public ResponseEntity<SupportResponse> chat(@RequestBody SupportRequest request) {
        String reply = techDeskService.chat(request.getMessage(), request.getSessionId());
        return ResponseEntity.ok(new SupportResponse(reply, request.getSessionId()));
    }

    @DeleteMapping("/chat/{sessionId}")
    public ResponseEntity<Void> clearHistory(@PathVariable String sessionId) {
        techDeskService.clearHistory(sessionId);
        return ResponseEntity.noContent().build();
    }

}
