package Learn_SpringAI.SpringAI_Demo.Controller;

import Learn_SpringAI.SpringAI_Demo.DTO.TravelStyle;
import Learn_SpringAI.SpringAI_Demo.DTO.TripPlan;
import Learn_SpringAI.SpringAI_Demo.Service.TripPlannerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/trip")
public class TripPlannerController {

    private final TripPlannerService tripPlannerService;

    public TripPlannerController(TripPlannerService tripPlannerService) {
        this.tripPlannerService = tripPlannerService;
    }

    // ── Returns fully typed TripPlan object ─────────────────────────────────
    // Spring serializes TripPlan → JSON automatically
    // Try: GET /trip/plan?destination=Tokyo&days=5&style=COMFORT
    @GetMapping("/plan")
    public TripPlan planTrip(
            @RequestParam String destination,
            @RequestParam(defaultValue = "3") int days,
            @RequestParam(defaultValue = "COMFORT") TravelStyle style) {
        return tripPlannerService.planTrip(destination, days, style);
    }

    // ── Returns typed TripPlan with error handling ───────────────────────────
    // Try: GET /trip/plan/safe?destination=Paris&days=4&style=LUXURY
    @GetMapping("/plan/safe")
    public TripPlan planTripSafely(
            @RequestParam String destination,
            @RequestParam(defaultValue = "3") int days,
            @RequestParam(defaultValue = "COMFORT") TravelStyle style) {
        return tripPlannerService.planTripSafely(destination, days, style);
    }

    // ── Returns List<String> — ParameterizedTypeReference demo ─────────────
    // Try: GET /trip/suggest?interest=food&count=5
    @GetMapping("/suggest")
    public List<String> suggestDestinations(
            @RequestParam String interest,
            @RequestParam(defaultValue = "5") int count) {
        return tripPlannerService.suggestDestinations(interest, count);
    }

    // ── Returns List<TripPlan> — complex generic ─────────────────────────────
    // Try: GET /trip/multi?destinations=Rome,Barcelona&days=3&style=BUDGET
    @GetMapping("/multi")
    public List<TripPlan> planMultiple(
            @RequestParam List<String> destinations,
            @RequestParam(defaultValue = "3") int days,
            @RequestParam(defaultValue = "COMFORT") TravelStyle style) {
        return tripPlannerService.planMultipleTrips(destinations, days, style);
    }

    // ── Returns raw JSON string — responseFormat demo ────────────────────────
    // Try: GET /trip/facts?destination=Bangkok
    @GetMapping("/facts")
    public Map<String, Object> destinationFacts(@RequestParam String destination) {
        return tripPlannerService.getDestinationFacts(destination);
    }
}
