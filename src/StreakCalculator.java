import java.time.LocalDate;
import java.util.Set;

/**
 * Works out streaks from the saved check-in dates. Takes "today" as a
 * parameter instead of calling LocalDate.now() so tests can pick any date.
 */
public class StreakCalculator {

    /**
     * Returns how many days in a row the user has checked in, ending today
     * or yesterday.
     */
    public static int currentStreak(Set<LocalDate> checkIns, LocalDate today) {
        // If today has no check-in yet, start counting from yesterday: the
        // day isn't over, so the streak shouldn't drop to 0 until it is.
        LocalDate day = today;
        if (!checkIns.contains(today)) {
            day = today.minusDays(1);
        }

        int streak = 0;
        while (checkIns.contains(day)) {
            streak++;
            day = day.minusDays(1);
        }
        return streak;
    }
}
