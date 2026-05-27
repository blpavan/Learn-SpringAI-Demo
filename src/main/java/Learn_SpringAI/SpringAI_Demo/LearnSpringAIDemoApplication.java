package Learn_SpringAI.SpringAI_Demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "Learn_SpringAI.SpringAI_Demo",
        "Learn_SpringAI.TechDeskSupportBot"})
public class LearnSpringAIDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnSpringAIDemoApplication.class, args);
    }

}
