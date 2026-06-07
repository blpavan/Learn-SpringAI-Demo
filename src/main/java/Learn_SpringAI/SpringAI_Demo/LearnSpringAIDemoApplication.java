package Learn_SpringAI.SpringAI_Demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {
        "Learn_SpringAI.SpringAI_Demo",
        "Learn_SpringAI.TechDeskSupportBot",
        "Learn_SpringAI.KnowledgeDesk"})
@EnableMongoRepositories(basePackages = "Learn_SpringAI.KnowledgeDesk.repository")
public class LearnSpringAIDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnSpringAIDemoApplication.class, args);
    }

}
