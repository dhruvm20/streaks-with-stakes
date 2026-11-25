import java.time.LocalDate;

/**
 * One week's study rule, as saved in commitment.csv.
 */
public record Commitment(LocalDate weekStart, int minutesPerDay, int allowedMisses, String consequence) {
}
