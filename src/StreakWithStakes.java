import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

/**
 * Entry point for Streak with Stakes: reads one command from the terminal
 * and hands it to the matching handler.
 *
 * Usage:
 *   java -cp out StreakWithStakes commit <minutesPerDay> <allowedMisses> "<consequence>"
 *   java -cp out StreakWithStakes done
 *   java -cp out StreakWithStakes status
 */
public class StreakWithStakes {

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0].toLowerCase();

        // commit and help don't need saved data, so they run before loading.
        // That way a damaged file never blocks the command that replaces it.
        if (command.equals("commit")) {
            handleCommit(args);
            return;
        }
        if (command.equals("help")) {
            printUsage();
            return;
        }

        Commitment commitment;
        Set<LocalDate> checkIns;
        try {
            commitment = Storage.loadCommitment();
            checkIns = Storage.loadCheckIns();
        } catch (IOException e) {
            System.out.println("Couldn't read your saved data. " + e.getMessage());
            return;
        }

        switch (command) {
            case "done":
                handleDone(commitment, checkIns);
                break;
            case "status":
                handleStatus(commitment, checkIns);
                break;
            default:
                System.out.println("Unknown command: " + args[0]);
                printUsage();
        }
    }

    private static void handleCommit(String[] args) {
        if (args.length != 4) {
            System.out.println("commit needs a daily goal, allowed misses, and a consequence.");
            printUsage();
            return;
        }
        int minutesPerDay;
        int allowedMisses;
        try {
            minutesPerDay = Integer.parseInt(args[1]);
            allowedMisses = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("Daily goal and allowed misses must be whole numbers, like: commit 30 2 \"No Netflix\"");
            return;
        }

        // A 0-minute goal isn't a commitment, and 7+ misses in a 7-day week
        // means the consequence could never trigger, so both are rejected.
        if (minutesPerDay <= 0) {
            System.out.println("Daily goal must be at least 1 minute.");
            return;
        }
        if (allowedMisses < 0 || allowedMisses > 6) {
            System.out.println("Allowed misses must be between 0 and 6.");
            return;
        }

        String consequence = args[3].trim();
        if (consequence.isEmpty()) {
            System.out.println("Consequence can't be empty. The stakes are the point.");
            return;
        }

        LocalDate weekStart = LocalDate.now();
        try {
            Storage.saveCommitment(weekStart, minutesPerDay, allowedMisses, consequence);
        } catch (IOException e) {
            System.out.println("Couldn't save your commitment: " + e.getMessage());
            return;
        }

        System.out.println("Commitment saved for the week starting " + weekStart + ":");
        System.out.println("  Study " + minutesPerDay + " min/day, up to " + allowedMisses + " missed day(s).");
        System.out.println("  If you break it: " + consequence);
    }

    private static void handleDone(Commitment commitment, Set<LocalDate> checkIns) {
        if (commitment == null) {
            System.out.println("Set a commitment first, like: commit 30 2 \"No Netflix\"");
            return;
        }

        LocalDate today = LocalDate.now();
        // One check-in per day: a second one would inflate the streak and
        // hide misses, which defeats the point of the stakes.
        if (checkIns.contains(today)) {
            System.out.println("You already checked in today (" + today + ").");
            return;
        }

        try {
            Storage.appendCheckIn(today);
        } catch (IOException e) {
            System.out.println("Couldn't save your check-in: " + e.getMessage());
            return;
        }
        System.out.println("Checked in for " + today + ". Nice work.");
    }

    private static void handleStatus(Commitment commitment, Set<LocalDate> checkIns) {
        if (commitment == null) {
            System.out.println("No commitment yet. Start one with: commit 30 2 \"No Netflix\"");
            return;
        }
        System.out.println("Week starting " + commitment.weekStart() + ": "
                + commitment.minutesPerDay() + " min/day, " + commitment.allowedMisses()
                + " miss(es) allowed. Consequence: " + commitment.consequence());

        LocalDate today = LocalDate.now();
        int streak = StreakCalculator.currentStreak(checkIns, today);
        String unit = " days";
        if (streak == 1) {
            unit = " day";
        }
        System.out.println("Current streak: " + streak + unit);
        if (!checkIns.contains(today)) {
            System.out.println("You haven't checked in today yet.");
        }

        int misses = StreakCalculator.countMisses(checkIns, commitment.weekStart(), today);
        System.out.println("Misses this week: " + misses + " of " + commitment.allowedMisses() + " allowed");
        printStakes(commitment, misses, today);
    }

    private static void printStakes(Commitment commitment, int misses, LocalDate today) {
        int allowed = commitment.allowedMisses();
        boolean weekOver = today.isAfter(commitment.weekStart().plusDays(6));

        if (misses > allowed) {
            System.out.println("COMMITMENT BROKEN. You missed " + misses + " days, but only " + allowed + " were allowed.");
            System.out.println("Consequence triggered: " + commitment.consequence());
        } else if (weekOver) {
            System.out.println("Week complete. You kept your commitment!");
            System.out.println("Start next week with a new commit.");
        } else if (misses == allowed) {
            System.out.println("WARNING: You're at your limit. One more miss and: " + commitment.consequence());
        } else {
            System.out.println("You can miss " + (allowed - misses) + " more day(s) this week.");
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  commit <minutesPerDay> <allowedMisses> \"<consequence>\"   set this week's rule");
        System.out.println("  done                                                  check in for today");
        System.out.println("  status                                                show streak and misses");
        System.out.println("  help                                                  show this message");
    }
}
