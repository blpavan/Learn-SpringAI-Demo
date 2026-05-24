package Learn_SpringAI.SpringAI_Demo.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record BudgetBreakdown(
        @JsonPropertyDescription("Total accommodation cost in USD for the entire trip")
        Double accommodationTotal,

        @JsonPropertyDescription("Total food and dining cost in USD for the entire trip")
        Double foodTotal,

        @JsonPropertyDescription("Total activities and entrance fees in USD")
        Double activitiesTotal,

        @JsonPropertyDescription("Total transport cost in USD including local transport")
        Double transportTotal,

        @JsonPropertyDescription("Recommended buffer for miscellaneous expenses in USD")
        Double miscBuffer,

        @JsonPropertyDescription("Grand total in USD for the entire trip per person")
        Double grandTotal

) {
}
