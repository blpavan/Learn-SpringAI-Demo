package Learn_SpringAI.SpringAI_Demo.advisors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class TokenCountAdvisor implements CallAdvisor, StreamAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(TokenCountAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        int estimatedInputTokens = estimateCounts(chatClientRequest);
        logger.info("Estimated input token count: " + estimatedInputTokens);

        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

        var usage = chatClientResponse
                        .chatResponse()
                        .getMetadata()
                        .getUsage();

        logger.info("[TokenCountAdvisor] ACTUAL token usage:");
        logger.info("Prompt tokens: {}", usage.getPromptTokens());
        logger.info("Completion tokens: {}", usage.getCompletionTokens());
        logger.info("Total tokens: {}", usage.getTotalTokens());

        if (usage.getTotalTokens() > 1000) {
            logger.warn("[TokenCountAdvisor] Token usage exceeded 2000 threshold!");
        }

        return chatClientResponse;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {

        int estimatedInputTokens = estimateCounts(chatClientRequest);
        logger.info("[TokenCountAdvisor] Stream - Estimated INPUT tokens: {}", estimatedInputTokens);

        return streamAdvisorChain.nextStream(chatClientRequest)
                .doOnNext(chatClientResponse -> {
                    var usage = chatClientResponse
                            .chatResponse()
                            .getMetadata()
                            .getUsage();

                    logger.info("[TokenCountAdvisor] Stream - ACTUAL token usage:");
                    logger.info("Prompt tokens: {}", usage.getPromptTokens());
                    logger.info("Completion tokens: {}", usage.getCompletionTokens());
                    logger.info("Total tokens: {}", usage.getTotalTokens());

                    if (usage.getTotalTokens() > 1000) {
                        logger.warn("[TokenCountAdvisor] Stream - Token usage exceeded 1000 threshold!");
                    }
                });

    }

    @Override
    public String getName() {

        return "TokenCountAdvisor";
    }

    @Override
    public int getOrder() {

        return 20;
    }


    private int estimateCounts(ChatClientRequest chatClientRequest) {

        return chatClientRequest
                .prompt()
                .getInstructions()
                .stream()
                .mapToInt(msg -> msg.getText().length() / 4)
                .sum();

    }

}
