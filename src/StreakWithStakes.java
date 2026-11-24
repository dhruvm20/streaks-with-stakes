import java.io.IOException;
import java.time.LocalDate;

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

        switch (command) {
            case "commit":
                handleCommit(args);
                break;
            case "done":
                handleDone();
                break;
            case "status":
                handleStatus();
                break;
            case "help":
                printUsage();
                break;
            default:
                System.out.println("Unknown command: " + args[0]);
                printUsage();
        }
    }

    private static void handleCommit(String[] args) {
        // Exactly four tokens: the command plus three values. The consequence
        // must be quoted on the command line so spaces stay in one argument.
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

    private static void handleDone() {
        // Chunk 4 replaces this with appending today's date to checkins.csv.
        System.out.println("Check-in received.");
    }

    private static void handleStatus() {
        // Chunks 6-8 replace this with the streak, miss count, and warnings.
        System.out.println("Status: no data yet.");
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  commit <minutesPerDay> <allowedMisses> \"<consequence>\"   set this week's rule");
        System.out.println("  done                                                  check in for today");
        System.out.println("  status                                                show streak and misses");
        System.out.println("  help                                                  show this message");
    }
}
