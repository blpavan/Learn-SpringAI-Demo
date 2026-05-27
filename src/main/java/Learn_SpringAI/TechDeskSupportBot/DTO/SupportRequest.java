package Learn_SpringAI.TechDeskSupportBot.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportRequest {
    private String message;

    @JsonProperty("sessionId")
    @JsonAlias("session_id")
    private String sessionId;
}
