package Learn_SpringAI.KnowledgeDesk.repository;

import Learn_SpringAI.KnowledgeDesk.model.KnowledgeArticle;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface KnowledgeArticleRepository extends MongoRepository<KnowledgeArticle, String> {
}
