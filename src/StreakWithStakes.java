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

        // Lowercase so "Done" and "DONE" work too; a typo in case shouldn't
        // cost the user a check-in.
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
        // Chunk 3 replaces this with validation and saving to commitment.csv.
        System.out.println("Commitment received: " + args[1] + " min/day, "
                + args[2] + " misses allowed, consequence: " + args[3]);
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
