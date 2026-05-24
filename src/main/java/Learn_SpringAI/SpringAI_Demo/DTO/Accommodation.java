package Learn_SpringAI.SpringAI_Demo.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record Accommodation(
        @JsonPropertyDescription("Hotel or accommodation name")
        String name,

        @JsonPropertyDescription("Type: hotel, hostel, airbnb, resort, guesthouse")
        String type,

        @JsonPropertyDescription("Estimated price per night in USD")
        Double pricePerNight,

        @JsonPropertyDescription("Star rating from 1 to 5")
        Integer starRating,

        @JsonPropertyDescription("Key amenities as a list e.g. WiFi, pool, breakfast")
        List<String> amenities

) {
}


// Notes

//  DTO Class
//   ↓
// Jackson Reflection inspects it
//   ↓
// Discovers fields + annotations + types
//   ↓
// Used for:
//    - JSON parsing
//    - JSON serialization
//    - Spring AI schema generation
