package Learn_SpringAI.KnowledgeDesk.rag;

import Learn_SpringAI.KnowledgeDesk.model.KnowledgeArticle;
import Learn_SpringAI.KnowledgeDesk.service.KnowledgeArticleService;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class MongoDataLoader {

    private final KnowledgeArticleService knowledgeArticleService;
    private final VectorStore vectorStore;

    public MongoDataLoader(KnowledgeArticleService knowledgeArticleService, VectorStore vectorStore) {
        this.knowledgeArticleService = knowledgeArticleService;
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    private void loadMongoDataIntoVectorStore() {
        // First Loading Data Into MongoDB
        knowledgeArticleService.seedArticlesIfEmpty();

        List<Document> documents = knowledgeArticleService.findAllArticles()
                .stream()
                .map(this::toVectorDocument)
                .toList();

        vectorStore.add(documents);
    }

    private Document toVectorDocument(KnowledgeArticle article) {
        String text = """
                Title: %s
                Category: %s
                Content: %s
                """.formatted(article.getTitle(), article.getCategory(), article.getContent());

        String vectorDocumentId = UUID.nameUUIDFromBytes(article.getId().getBytes(StandardCharsets.UTF_8)).toString();

        return new Document(
                vectorDocumentId,
                text,
                Map.of(
                        "source", "mongodb",
                        "mongoId", article.getId(),
                        "title", article.getTitle(),
                        "category", article.getCategory()
                )
        );
    }
}
