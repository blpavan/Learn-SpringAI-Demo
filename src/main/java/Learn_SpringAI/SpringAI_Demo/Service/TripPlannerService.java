package Learn_SpringAI.SpringAI_Demo.Service;

import Learn_SpringAI.SpringAI_Demo.DTO.TravelStyle;
import Learn_SpringAI.SpringAI_Demo.DTO.TripPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TripPlannerService {

    private static final Logger log = LoggerFactory.getLogger(TripPlannerService.class);

    private final ChatClient openAIChatClient;

    public TripPlannerService(ChatClient openAIChatClient) {
        this.openAIChatClient = openAIChatClient;
    }

    // Full trip plan (BeanOutputConverter via .entity(Class)) ──
    // Demonstrates: single complex nested object, enums, validation annotations,
    // temperature=0.3 (needs to be accurate but slightly varied per destination)
    // maxTokens=3000 (nested structure with a full itinerary is large)
    public TripPlan planTrip(String destination, int days, TravelStyle style) {

        log.info("Planning {} day {} trip to {}", days, style, destination);

        String userPrompt = String.format(
                "Plan a %d day %s trip to %s. " +
                        "Include a full day-by-day itinerary, accommodation, budget breakdown, " +
                        "travel tips, and must-try foods.",
                days, style.name().toLowerCase(), destination
        );

        TripPlan plan = openAIChatClient.prompt()
                .system("""
                You are an expert travel planner with deep knowledge of global destinations.
                Create detailed, realistic, and practical trip plans.
                All costs must be in USD and realistic for the travel style requested.
                Ensure the itinerary has exactly the number of days requested.
                """)
                .user(userPrompt)
                .options(OpenAiChatOptions.builder()
                        .model("gpt-4o-mini")
                        .temperature(0.3)     // low — needs accurate costs and realistic info
                        .maxTokens(3000)      // high — nested structure with full itinerary is large
                        .build())
                .call()
                .entity(TripPlan.class);  // ← BeanOutputConverter — Spring AI maps JSON → TripPlan

        log.info("Trip plan generated for {} — {} days — total cost: ${}",
                plan.destination(), plan.durationDays(),
                plan.budgetBreakdown().grandTotal());

        return plan;
    }

    // List of destinations (ParameterizedTypeReference)
    // Demonstrates: List<T> with generics — cannot use List.class (type erasure)
    // Must use ParameterizedTypeReference to preserve List<String> at runtime
    public List<String> suggestDestinations(String interest, int count) {
        log.info("Suggesting {} destinations for interest: {}", count, interest);

        return openAIChatClient.prompt()
                .system("You are a travel expert. Suggest specific city destinations only. No explanations.")
                .user(String.format(
                        "Suggest %d travel destinations for someone interested in %s. " +
                                "Return as a JSON array of strings. Each item: 'City, Country'",
                        count, interest))
                .options(OpenAiChatOptions.builder()
                        .temperature(0.7)     // moderate — variety in suggestions is good
                        .maxTokens(200)       // list of city names is short
                        .build())
                .call()
                .entity(new ParameterizedTypeReference<List<String>>() {});
        // ↑ ParameterizedTypeReference preserves <String> generic type at runtime
        // Cannot use List.class — Java erases generics, Jackson cannot map correctly
    }

    // List of TripPlans (ParameterizedTypeReference<List<T>>) ──
    // Demonstrates: List of complex objects — same pattern, more complex type
    public List<TripPlan> planMultipleTrips(List<String> destinations, int days, TravelStyle style) {
        log.info("Planning {} trips for destinations: {}", destinations.size(), destinations);

        String userPrompt = String.format(
                "Create %s travel trip plans for %d days each in %s style for these destinations: %s. " +
                        "Return as a JSON array of trip plan objects.",
                destinations.size(), days, style.name().toLowerCase(),
                String.join(", ", destinations)
        );

        return openAIChatClient.prompt()
                .system("You are an expert travel planner. Create concise but complete trip plans.")
                .user(userPrompt)
                .options(OpenAiChatOptions.builder()
                        .temperature(0.3)
                        .maxTokens(4000)      // multiple plans need more tokens
                        .build())
                .call()
                .entity(new ParameterizedTypeReference<List<TripPlan>>() {});
        // ↑ List<TripPlan> — complex generic. ParameterizedTypeReference is mandatory here
    }

    // Quick destination summary (MapOutputConverter pattern) ───
    // Demonstrates: flexible key-value output without defining a Java class
    // Use when schema is dynamic or you are prototyping
    public Map<String, Object> getDestinationFacts(String destination) {
        log.info("Getting quick facts for: {}", destination);

        return openAIChatClient.prompt()
                .system("""
                You are a travel expert. Return ONLY JSON with keys:
                bestMonth, currency, language, visaRequired,
                avgTempCelsius, safetyRating, topAttraction
            """)
                .user("Give me quick facts about: " + destination)
                .options(OpenAiChatOptions.builder()
                        .model("gpt-4o-mini")
                        .temperature(0.0)
                        .maxTokens(300)
                        .build())
                .call()
                .entity(new MapOutputConverter());
    }

    // Error handling pattern ───────────────────────────────────
    // Demonstrates: what to do when .entity() fails (malformed JSON from LLM)
    public TripPlan planTripSafely(String destination, int days, TravelStyle style) {
        try {
            return planTrip(destination, days, style);

        } catch (RuntimeException e) {
            // Spring AI wraps JSON parse failures in RuntimeException
            log.warn("First attempt failed for {}: {} — retrying with stricter prompt",
                    destination, e.getMessage());

            // Retry with a stricter system prompt
            return openAIChatClient.prompt()
                    .system("""
                    You are a travel planner. You MUST return ONLY valid JSON.
                    No markdown. No explanation. No backticks. ONLY the JSON object.
                    """)
                    .user(String.format("Plan a %d day %s trip to %s",
                            days, style.name().toLowerCase(), destination))
                    .options(OpenAiChatOptions.builder()
                            .temperature(0.0)  // zero on retry — be as deterministic as possible
                            .maxTokens(3000)
                            .build())
                    .call()
                    .entity(TripPlan.class);
        }
    }
}
