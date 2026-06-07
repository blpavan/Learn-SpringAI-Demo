package Learn_SpringAI.KnowledgeDesk.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Configuration("knowledgeDeskChatClientConfig")
public class ChatClientConfig {

    @Bean("knowledgeDeskChatMemory")
    public ChatMemory knowledgeDeskChatMemory(JdbcChatMemoryRepository  jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .maxMessages(20) // Store the last 20 messages in memory for context
                .chatMemoryRepository(jdbcChatMemoryRepository) // Use JDBC repository to persist chat history
                .build();
    }

    @Bean
    public ChatClient knowledgeDeskChatClient(OpenAiChatModel openAiChatModel,
                                              @Qualifier("knowledgeDeskChatMemory") ChatMemory chatMemory) {

        Advisor chatMemoryAdvisor = MessageChatMemoryAdvisor
                .builder(chatMemory)
                .build();

        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(), chatMemoryAdvisor)
                .build();
    }

    // This Question And Answer Advisor is Old, new version Of Spring Boot have a RetrievalAugmentationAdvisor
    @Bean
    public QuestionAnswerAdvisor knowledgeDeskQuestionAnswerAdvisor(VectorStore vectorStore) {
        return QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .topK(3)
                        .similarityThreshold(0.5)
                        .build())
                .build();
    }

    @Bean
    public RetrievalAugmentationAdvisor  knowledgeDeskRetrievalAugmentationAdvisor(VectorStore vectorStore) {
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .similarityThreshold(0.5)
                        .topK(3)
                        .build())
                .build();

    }

    @Bean
    public RetrievalAugmentationAdvisor advancedKnowledgeDeskRetrievalAugmentationAdvisor(
            VectorStore vectorStore,
            OpenAiChatModel openAiChatModel) {

        ChatClient.Builder chatClientBuilder = ChatClient.builder(openAiChatModel);

        // Advanced RAG use case:
        // User asks short or vague support questions like:
        // "vpn broken", "can't login", "need laptop", "software license issue".
        //
        // Pre-retrieval strategy:
        // RewriteQueryTransformer rewrites vague user text into a cleaner search
        // query for the KnowledgeDesk support knowledge base.
        //
        // MultiQueryExpander creates a few related search queries, which improves
        // recall when one query wording misses the best vector chunks.
        //
        // Retrieval:
        // VectorStoreDocumentRetriever searches Qdrant with topK and similarity
        // threshold settings.
        //
        // Post-retrieval strategy:
        // knowledgeDeskDocumentPostProcessor removes duplicate chunks, prefers
        // higher scoring chunks, and limits context so the LLM receives less noise.
        //
        // Hallucination reduction:
        // ContextualQueryAugmenter with allowEmptyContext(false) prevents the
        // model from answering normally when no relevant context is found. The
        // document formatter also includes source metadata, so answers can stay
        // grounded in Mongo/PDF/Tika/static data.
        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(RewriteQueryTransformer.builder()
                        .chatClientBuilder(chatClientBuilder)
                        .targetSearchSystem("KnowledgeDesk IT support knowledge base")
                        .build())
                .queryExpander(MultiQueryExpander.builder()
                        .chatClientBuilder(chatClientBuilder)
                        .includeOriginal(true)
                        .numberOfQueries(3)
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .similarityThreshold(0.55)
                        .topK(8)
                        .build())
                .documentPostProcessors(knowledgeDeskDocumentPostProcessor())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(false)
                        .documentFormatter(this::formatDocumentsWithSourceMetadata)
                        .build())
                .build();
    }

    private DocumentPostProcessor knowledgeDeskDocumentPostProcessor() {
        return (query, documents) -> documents.stream()
                .sorted(Comparator.comparing(
                        document -> document.getScore() == null ? 0.0 : document.getScore(),
                        Comparator.reverseOrder()))
                .collect(
                        LinkedHashMap<String, Document>::new,
                        (uniqueDocuments, document) -> uniqueDocuments.putIfAbsent(documentIdentity(document), document),
                        Map::putAll)
                .values()
                .stream()
                .limit(4)
                .toList();
    }

    private String documentIdentity(Document document) {
        Object source = document.getMetadata().getOrDefault("source", "unknown");
        Object title = document.getMetadata().getOrDefault("title", "");
        return source + ":" + title + ":" + document.getText();
    }

    private String formatDocumentsWithSourceMetadata(List<Document> documents) {
        StringBuilder context = new StringBuilder();

        for (int index = 0; index < documents.size(); index++) {
            Document document = documents.get(index);
            context.append("Document ").append(index + 1).append("\n");
            context.append("Source: ").append(document.getMetadata().getOrDefault("source", "unknown")).append("\n");
            context.append("Title: ").append(document.getMetadata().getOrDefault("title", "untitled")).append("\n");
            context.append("Category: ").append(document.getMetadata().getOrDefault("category", "general")).append("\n");
            context.append("Content: ").append(document.getText()).append("\n\n");
        }

        return context.toString();
    }


    // Notes

    // @Primary --> When we have multiple beans of the same type, we can use @Primary to specify which one should be injected by default.
    // In this case, if there are multiple ChatClient beans, the one annotated with @Primary will be injected when a ChatClient is required without specifying a qualifier.


    // @Qualifier --> When we have multiple beans of the same type, we can use @Qualifier to specify which one should be injected.
    // This is useful when we want to inject a specific bean that is not marked as @Primary.



    // Spring Bean Lifecycle Notes
    //
    // 1. Constructor
    //    Spring creates the object first.
    //    Example: new MongoDataLoader(knowledgeArticleService, vectorStore)
    //
    // 2. Dependency Injection
    //    Spring injects required dependencies through constructor, field, or setter.
    //    Example: KnowledgeArticleService and VectorStore are given to MongoDataLoader.
    //
    // 3. Initialization Hooks
    //    These run after dependencies are ready.
    //
    //    @PostConstruct:
    //    Annotation-based init method. Good for simple startup logic.
    //    Example: load Mongo data into Qdrant after all dependencies are injected.
    //
    //    InitializingBean.afterPropertiesSet():
    //    Interface-based init method. Similar to @PostConstruct, but it couples
    //    the class directly to Spring's InitializingBean interface.
    //
    //    @Bean(initMethod = "methodName"):
    //    Used in config classes when creating third-party beans manually.
    //
    // 4. Bean Is Used
    //    Controllers, services, and other beans call methods on this bean during
    //    normal application execution.
    //
    // 5. Startup Runner Hooks
    //    ApplicationRunner and CommandLineRunner run after the full Spring
    //    application context is ready. These are often better than @PostConstruct
    //    for application startup tasks that need the whole app initialized.
    //
    // 6. Destroy Hooks
    //    These run when the app is shutting down.
    //
    //    @PreDestroy:
    //    Annotation-based cleanup method. Good for closing resources or stopping
    //    background tasks.
    //
    //    DisposableBean.destroy():
    //    Interface-based cleanup method. Similar to @PreDestroy, but couples the
    //    class directly to Spring's DisposableBean interface.
    //
    //    @Bean(destroyMethod = "methodName"):
    //    Used in config classes when a manually created bean needs cleanup.
    //
    // Simple order:
    // Constructor -> Dependency Injection -> @PostConstruct/afterPropertiesSet
    // -> Bean is used -> @PreDestroy/destroy


    // Advanced RAG Notes
    //
    // Pre-retrieval strategies happen before Qdrant search.
    // Goal: improve the search query before retrieval.
    // Examples:
    // - Rewrite "vpn broken" into a clear support search query.
    // - Expand "can't login" into related queries about password reset, MFA,
    //   account lock, and access portal.
    //
    // Retrieval happens when VectorStoreDocumentRetriever searches Qdrant.
    // Goal: find candidate chunks using embedding similarity.
    //
    // Post-retrieval strategies happen after Qdrant returns documents.
    // Goal: clean up retrieved chunks before sending context to the model.
    // Examples:
    // - Remove duplicate chunks.
    // - Keep only the top few chunks.
    // - Prefer chunks with better similarity score.
    //
    // Hallucination reduction happens during prompt augmentation.
    // Goal: force the model to answer from retrieved context.
    // Practical controls:
    // - Use allowEmptyContext(false), so empty retrieval does not produce a
    //   confident unsupported answer.
    // - Include source/title/category metadata in context.
    // - Keep fewer, cleaner documents instead of many noisy chunks.


}
