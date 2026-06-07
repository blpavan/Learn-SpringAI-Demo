package Learn_SpringAI.KnowledgeDesk.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TikaDocumentDataLoader {

    @Value("${app.rag.tika-location-patterns}")
    private List<String> documentLocationPatterns;

    private final VectorStore vectorStore;
    private final ResourcePatternResolver resourcePatternResolver;


    public TikaDocumentDataLoader(VectorStore vectorStore,
                                  ResourcePatternResolver resourcePatternResolver) {
        this.vectorStore = vectorStore;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @PostConstruct
    private void loadMixedDocumentsIntoVectorStore() throws IOException {
        List<Resource> documentResources = findDocumentResources();

        if (documentResources.isEmpty()) {
            return;
        }

        TokenTextSplitter textSplitter = TokenTextSplitter.builder()
                .withChunkSize(15)
                .withMaxNumChunks(100)
                .build();

        for (Resource documentResource : documentResources) {
            // Generic real-world document ingestion flow:
            // 1. TikaDocumentReader uses Apache Tika to extract text from many
            //    formats, such as PDF, DOCX, TXT, and HTML.
            // 2. TokenTextSplitter splits large extracted text into smaller chunks
            //    so the embedding model and vector search work better.
            // 3. vectorStore.add(...) embeds every chunk and stores it in Qdrant.
            //
            // Use this for a mixed knowledge base, for example:
            // - product-manual.pdf
            // - refund-policy.docx
            // - support-faq.txt
            // - release-notes.html
            //
            // Put those files under src/main/resources/rag/documents/.
            TikaDocumentReader documentReader = new TikaDocumentReader(documentResource);
            List<Document> documents = documentReader.read();
            List<Document> chunkedDocuments = textSplitter.apply(documents);

            vectorStore.add(chunkedDocuments);
        }
    }

    private List<Resource> findDocumentResources() throws IOException {
        List<Resource> resources = new ArrayList<>();

        for (String pattern : documentLocationPatterns) {
            resources.addAll(List.of(resourcePatternResolver.getResources(pattern)));
        }

        return resources;
    }
}
