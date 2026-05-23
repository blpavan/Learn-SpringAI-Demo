package Learn_SpringAI.SpringAI_Demo.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

@Service
public class ChatService {

//    private final ChatClient chatClient;
//
//    // Here the ChatClient is passed to the constructor, and Spring will automatically inject the ChatClient bean that we defined in the ChatModelConfig class
//
//    public ChatService(ChatClient chatClient) {
//        this.chatClient = chatClient;
//    }
//
//    public String chatWithLLMModel(String message) {
//        return chatClient
//                .prompt()
//                .user(message)
//                .call()
//                .content();
//    }


    private ChatClient openAIChatClient;
    private ChatClient ollamaChatClient;

    public ChatService(ChatClient openAIChatClient, ChatClient ollamaChatClient) {
        this.openAIChatClient = openAIChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    public String ChatWithOpenAIModel(String message) {
        return openAIChatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }


    public String ChatWithLLamaModel(String message) {
        return ollamaChatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }

    // OpenAI: Precise SQL / Code generation
    // Purpose: temperature=0.0 makes output deterministic and exact.
    //          maxTokens=300 keeps it short (SQL is never 2000 tokens).
    //          frequencyPenalty=0 because we WANT exact SQL keywords repeated.
    //          stop=";" stops the model right after the statement — no extra text.
    public String generateSqlWithOpenAI(String question) {

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("gpt-4o-mini")
                .temperature(0.0)          // deterministic output
                .maxTokens(300)            // limit response size
                .frequencyPenalty(0.0)     // allow repeated SQL keywords
                .stop(List.of(";"))        // stop after SQL statement
                .build();

        return openAIChatClient
                .prompt()
                .system("You are a SQL expert. Return ONLY valid SQL. No explanation. No markdown.")
                .user(question)
                .options(options)
                .call()
                .content();
    }

    // NEW METHOD 2 — OpenAI: Creative story generation
    // Purpose: temperature=1.2 makes output imaginative and varied.
    //          maxTokens=2000 allows a full story to develop.
    //          frequencyPenalty=0.6 prevents repetitive phrases in long text.
    //          presencePenalty=0.4 pushes the story to introduce new ideas/characters.
    public String generateStoryWithOpenAI(String prompt) {

        ChatOptions options = OpenAiChatOptions.builder()
                .model("gpt-4o-mini")
                .temperature(1.2)           // high creativity — varied, imaginative output
                .maxTokens(2000)             // long enough for a full story
                .frequencyPenalty(0.6)       // prevents "the hero... the hero... the hero..."
                .presencePenalty(0.4)        // pushes story to introduce new plot points and characters
                .build();

        return openAIChatClient
                .prompt()
                .system("You are a creative storyteller. Write engaging, imaginative stories.")
                .user(prompt)
                .options(options)
                .call()
                .content();
    }

    // NEW METHOD 3 — OpenAI: Cheap one-word classification
    // Purpose: gpt-3.5-turbo is the cheapest model — perfect for simple tasks.
    //          temperature=0.0 because classification must be consistent.
    //          maxTokens=5 — literally only need one word (POSITIVE/NEGATIVE/NEUTRAL).
    //          No penalties needed — output is one word, no repetition possible.
    public String classifySentimentWithOpenAI(String text) {

        ChatOptions options = OpenAiChatOptions.builder()
                .model("gpt-3.5-turbo")    // cheapest model — more than enough for one-word output
                .temperature(0.0)           // must be consistent — same text = same label every time
                .maxTokens(5)               // one word is 1-2 tokens — 5 is more than enough
                .build();

        return openAIChatClient
                .prompt()
                .system("Classify the sentiment. Reply with exactly ONE word: POSITIVE, NEGATIVE, or NEUTRAL.")
                .user(text)
                .options(options)
                .call()
                .content();
    }

    // NEW METHOD 4 — Ollama: Local balanced chat
    // Purpose: numCtx=4096 gives enough context for multi-turn conversation.
    //          repeatPenalty=1.1 gently reduces repetitive phrases (neutral is 1.0 in Ollama).
    //          numPredict=800 caps output — never leave this uncapped locally.
    //          temperature=0.7 — balanced, natural chat responses.
    public String chatLocallyWithOllama(String message) {

        ChatOptions options = OllamaChatOptions.builder()
                .model("llama3.2")         // local model — no latency, great for multi-turn chat
                .temperature(0.7)           // balanced — natural sounding chat
                .numCtx(4096)               // context window — enough for multi-turn conversation
                .repeatPenalty(1.1)         // gently reduces repetition (1.0 = neutral in Ollama, NOT 0.0)
                .numPredict(800)             // cap output — equivalent of maxTokens in OpenAI
                .build();

        return ollamaChatClient
                .prompt()
                .system("You are a helpful assistant.")
                .user(message)
                .options(options)
                .call()
                .content();
    }


    // NEW METHOD 5 — Ollama: Local precise code review (privacy-safe)
    // Purpose: codellama model is trained specifically for code tasks.
    //          temperature=0.0 — code review must be precise and repeatable.
    //          numCtx=8192 — code files are long, need big context window.
    //          repeatPenalty=1.0 — no penalty, technical terms repeat by design.
    //          numPredict=1000 — detailed review needs more tokens.
    //          Data never leaves your machine — safe for proprietary code.
    public String reviewCodeWithOllama(String code) {

        ChatOptions options = OllamaChatOptions.builder()
                .model("codellama")         // code-specific model — better than llama3.2 for code tasks
                .temperature(0.0)           // precise and deterministic — code review must be consistent
                .numCtx(8192)               // large context — code files can be long
                .repeatPenalty(1.0)         // no penalty — technical terms (null, return, void) repeat by design
                .numPredict(1000)            // detailed review needs more tokens than a chat reply
                .build();

        return ollamaChatClient
                .prompt()
                .system("You are a senior software engineer. Review the code for bugs, performance issues, and best practices.")
                .user("Review this code:\n\n" + code)
                .options(options)
                .call()
                .content();
    }

    public Flux<String> streamData(String message) {
        return openAIChatClient
                .prompt()
                .user(message)
                .stream()
                .content();
    }

    public Flux<String> streamDataWithOpenAI(String question) {

        ChatOptions options = OpenAiChatOptions.builder()
                .model("gpt-4o-mini")
                .temperature(1.0)           // high creativity — varied, imaginative output
                .maxTokens(1500)            // long enough for a full story
                .build();

        return openAIChatClient
                .prompt()
                .user(question)
                .options(options)
                .stream()
                .content();
    }


}
