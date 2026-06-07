package Learn_SpringAI.KnowledgeDesk.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;


@RestController
@RequestMapping("/knowledge")
public class ChatContoller {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:prompts/systemDataPrompt.st")
    private Resource template;

    public ChatContoller(@Qualifier("knowledgeDeskChatClient") ChatClient chatClient,
                         VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/connect")
    public String connectToRagAI(@RequestParam String prompt,
                                 @RequestHeader String username) {

        // Manual RAG example:
        // 1. Build the vector search request.
        // 2. Call vectorStore.similaritySearch(...) ourselves.
        // 3. Extract document text ourselves.
        // 4. Inject those documents into the system prompt ourselves.
        //
        // To avoid these manual steps, use QuestionAnswerAdvisor from
        // spring-ai-advisors-vector-store. See AdvisorRagController, where the
        // advisor handles retrieval and prompt augmentation before the LLM call.

        // R - Retrieval query
        SearchRequest searchRequest =
                SearchRequest.builder()
                        .query(prompt)
                        .topK(3) // retrieve top 3 relevant documents from vector store
                        .similarityThreshold(0.5) // search for documents with similarity match above 0.5
                        .build();

        List<Document> similarDocuments = vectorStore
                .similaritySearch(searchRequest);

        // extract the text content from the retrieved documents
        List<String> similarResults = similarDocuments.stream()
                .map(Document::getText)
                .toList();

        // G - Generation

        String results = chatClient.prompt()
                .system(promptSystemSpec ->
                        promptSystemSpec
                                .text(template)
                                .param("documents", similarResults))
                .advisors(adviceSpec ->
                        adviceSpec.param(CONVERSATION_ID, username)
                )
                .user(prompt)
                .call()
                .content();


        return results;

    }
}
