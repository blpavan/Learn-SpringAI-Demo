package Learn_SpringAI.KnowledgeDesk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "knowledge_articles")
public class KnowledgeArticle {

    @Id
    private String id;
    private String title;
    private String category;
    private String content;
}
