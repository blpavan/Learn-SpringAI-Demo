package Learn_SpringAI.SpringAI_Demo.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TripPlan(
        @NotBlank
        @JsonPropertyDescription("The destination city and country")
        String destination,

        @NotNull
        @JsonPropertyDescription("Number of days for the trip")
        @Min(1) @Max(30)
        Integer durationDays,

        @NotNull
        @JsonPropertyDescription("Best season to visit this destination")
        Season bestSeason,

        @NotNull
        @JsonPropertyDescription("Travel style this plan is optimised for")
        TravelStyle travelStyle,

        @JsonPropertyDescription("Brief overview of the destination and why it is worth visiting")
        String destinationOverview,

        @NotNull
        @JsonPropertyDescription("Recommended accommodation option for this travel style")
        Accommodation accommodation,

        @NotNull
        @JsonPropertyDescription("Day-by-day itinerary. One DayPlan per day of the trip")
        List<DayPlan> itinerary,

        @JsonPropertyDescription("Top 5 practical travel tips specific to this destination")
        List<String> travelTips,

        @JsonPropertyDescription("Top 3 foods the traveller must try")
        List<String> mustTryFoods,

        @NotNull
        @JsonPropertyDescription("Complete budget breakdown for the trip")
        BudgetBreakdown budgetBreakdown

) {}