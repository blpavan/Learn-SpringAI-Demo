package Learn_SpringAI.KnowledgeDesk.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/knowledge/advisor")
public class AdvisorRagController {

    private final ChatClient chatClient;
    private final QuestionAnswerAdvisor questionAnswerAdvisor;
    private final RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;
    private final RetrievalAugmentationAdvisor advancedRetrievalAugmentationAdvisor;

    public AdvisorRagController(@Qualifier("knowledgeDeskChatClient") ChatClient chatClient,
                                @Qualifier("knowledgeDeskQuestionAnswerAdvisor") QuestionAnswerAdvisor questionAnswerAdvisor,
                                @Qualifier("knowledgeDeskRetrievalAugmentationAdvisor") RetrievalAugmentationAdvisor retrievalAugmentationAdvisor,
                                @Qualifier("advancedKnowledgeDeskRetrievalAugmentationAdvisor") RetrievalAugmentationAdvisor advancedRetrievalAugmentationAdvisor) {
        this.chatClient = chatClient;
        this.questionAnswerAdvisor = questionAnswerAdvisor;
        this.retrievalAugmentationAdvisor = retrievalAugmentationAdvisor;
        this.advancedRetrievalAugmentationAdvisor = advancedRetrievalAugmentationAdvisor;
    }

    @GetMapping("/connect")
    public String connectWithAdvisorRag(@RequestParam String prompt,
                                        @RequestHeader String username) {
        // Advisor-based RAG example:
        // QuestionAnswerAdvisor uses the user prompt as the vector search query.
        // It searches Qdrant through VectorStore, applies the configured topK and
        // similarityThreshold settings, adds the retrieved documents to the prompt
        // context, and then lets ChatClient call the LLM.
        //
        // This avoids the manual steps shown in ChatContoller:
        // vectorStore.similaritySearch(...), extracting document text, and manually
        // injecting those documents into the system prompt.
        return chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec
                        .param(CONVERSATION_ID, username)
                        .advisors(questionAnswerAdvisor))
                .user(prompt)
                .call()
                .content();
    }


    @GetMapping("/retrieval-augmentation")
    public String connectWithRetrievalAugmentationAdvisor(@RequestParam String prompt,
                                                          @RequestHeader String username) {
        // RetrievalAugmentationAdvisor is the newer, more modular RAG advisor.
        // It represents the full RAG pipeline instead of only wrapping a vector
        // store search.
        //
        // In ChatClientConfig, this advisor is connected to a
        // VectorStoreDocumentRetriever. That retriever is the part that searches
        // Qdrant using the configured topK and similarityThreshold values.
        //
        // Request flow:
        // 1. The user prompt becomes the RAG query.
        // 2. RetrievalAugmentationAdvisor runs the retrieval pipeline.
        // 3. VectorStoreDocumentRetriever searches Qdrant through VectorStore.
        // 4. Retrieved documents are added to the prompt context.
        // 5. ChatClient sends the augmented prompt to the LLM.
        //
        // Compared with QuestionAnswerAdvisor, this advisor is better when you
        // want to customize more RAG stages later, such as query transformation,
        // query expansion, document joining, document post-processing, or custom
        // query augmentation.
        return chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec
                        .param(CONVERSATION_ID, username)
                        .advisors(retrievalAugmentationAdvisor))
                .user(prompt)
                .call()
                .content();

    }

    @GetMapping("/advanced")
    public String connectWithAdvancedRag(@RequestParam String prompt,
                                         @RequestHeader String username) {
        // Advanced RAG endpoint:
        //
        // Pre-retrieval:
        // The advisor rewrites and expands the user question before Qdrant search.
        // Good for short real-world prompts like "vpn broken" or "can't login".
        //
        // Retrieval:
        // Qdrant is searched using VectorStoreDocumentRetriever with a stronger
        // similarity threshold and a larger candidate set.
        //
        // Post-retrieval:
        // Retrieved chunks are sorted, deduplicated, and reduced to the best few
        // documents before prompt augmentation.
        //
        // Hallucination reduction:
        // The final prompt is built from retrieved context with source metadata.
        // If no context is found, the advisor is configured to avoid normal
        // unsupported answering.
        return chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec
                        .param(CONVERSATION_ID, username)
                        .advisors(advancedRetrievalAugmentationAdvisor))
                .user(prompt)
                .call()
                .content();
    }

}
