package Learn_SpringAI.SpringAI_Demo.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record DayPlan(
        @JsonPropertyDescription("Day number starting from 1")
        Integer dayNumber,

        @JsonPropertyDescription("Theme or focus of the day e.g. Historical Exploration")
        String dayTheme,

        @JsonPropertyDescription("List of activities planned for this day")
        List<DayActivity> activities,

        @JsonPropertyDescription("Recommended restaurant for dinner with brief description")
        String dinnerRecommendation,

        @JsonPropertyDescription("Total estimated cost for this day in USD per person")
        Double totalDayCostUsd

) {
}
