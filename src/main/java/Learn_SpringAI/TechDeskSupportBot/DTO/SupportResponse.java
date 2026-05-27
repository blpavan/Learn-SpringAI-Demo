package Learn_SpringAI.TechDeskSupportBot.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportResponse {
    private String reply;

    @JsonProperty("sessionId")
    private String sessionId;
}
