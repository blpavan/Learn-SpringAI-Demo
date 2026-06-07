package Learn_SpringAI.KnowledgeDesk.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class PdfDataLoader {

    @Value("${app.rag.pdf-location-pattern}")
    private String pdfLocationPattern;

    private final VectorStore vectorStore;
    private final ResourcePatternResolver resourcePatternResolver;

    public PdfDataLoader(VectorStore vectorStore,
                         ResourcePatternResolver resourcePatternResolver) {
        this.vectorStore = vectorStore;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @PostConstruct
    private void loadPdfDataIntoVectorStore() throws IOException {
        Resource[] pdfResources = resourcePatternResolver.getResources(pdfLocationPattern);

        if (pdfResources.length == 0) {
            return;
        }

        TokenTextSplitter textSplitter = TokenTextSplitter.builder()
                .withChunkSize(20)
                .withMaxNumChunks(200)
                .build();

        for (Resource pdfResource : pdfResources) {
            // PDF RAG indexing flow:
            // 1. PagePdfDocumentReader reads the PDF into Spring AI Document objects.
            // 2. TokenTextSplitter splits large PDF page text into smaller chunks.
            // 3. vectorStore.add(...) embeds those chunks and stores them in Qdrant.
            //
            // Put PDF files under src/main/resources/rag/pdfs/.
            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfResource);
            List<Document> pdfDocuments = pdfReader.read();
            List<Document> chunkedDocuments = textSplitter.apply(pdfDocuments);

            vectorStore.add(chunkedDocuments);
        }
    }
}
