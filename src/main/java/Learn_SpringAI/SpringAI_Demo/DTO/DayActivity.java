package Learn_SpringAI.SpringAI_Demo.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record DayActivity(
        @JsonPropertyDescription("Time of day: Morning, Afternoon, Evening, Night")
        String timeOfDay,

        @JsonPropertyDescription("Name of the activity or place to visit")
        String activityName,

        @JsonPropertyDescription("Brief description of what to do and why it is worth visiting")
        String description,

        @JsonPropertyDescription("Estimated duration in hours as a decimal e.g. 2.5")
        Double durationHours,

        @JsonPropertyDescription("Estimated cost in USD per person. 0 if free")
        Double estimatedCostUsd,

        @JsonPropertyDescription("Category of activity")
        ActivityType type

) {
}
