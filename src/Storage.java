import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

/**
 * Reads and writes the CSV files in the data/ folder.
 */
public class Storage {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path COMMITMENT_FILE = DATA_DIR.resolve("commitment.csv");

    /**
     * Saves this week's rule, replacing any earlier commitment.
     * Format: weekStart,minutesPerDay,allowedMisses,consequence
     */
    public static void saveCommitment(LocalDate weekStart, int minutesPerDay,
                                      int allowedMisses, String consequence) throws IOException {
        Files.createDirectories(DATA_DIR);

        String header = "weekStart,minutesPerDay,allowedMisses,consequence";
        String row = weekStart + "," + minutesPerDay + "," + allowedMisses + "," + consequence;
        Files.writeString(COMMITMENT_FILE, header + System.lineSeparator() + row + System.lineSeparator());
    }
}
