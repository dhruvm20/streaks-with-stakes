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

    /**
     * Returns how many days of the commitment week have no check-in,
     * from the week's start up to yesterday.
     */
    public static int countMisses(Set<LocalDate> checkIns, LocalDate weekStart, LocalDate today) {
        // Today isn't counted because there's still time to check in, and
        // days before weekStart don't count because the rule wasn't set yet.
        LocalDate lastDayToCount = today.minusDays(1);
        LocalDate weekEnd = weekStart.plusDays(6);
        if (lastDayToCount.isAfter(weekEnd)) {
            lastDayToCount = weekEnd;
        }

        int misses = 0;
        for (LocalDate day = weekStart; !day.isAfter(lastDayToCount); day = day.plusDays(1)) {
            if (!checkIns.contains(day)) {
                misses++;
            }
        }
        return misses;
    }
}
