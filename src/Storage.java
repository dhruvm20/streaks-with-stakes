import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Reads and writes the CSV files in the data/ folder.
 */
public class Storage {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path COMMITMENT_FILE = DATA_DIR.resolve("commitment.csv");
    private static final Path CHECKINS_FILE = DATA_DIR.resolve("checkins.csv");
    private static final String CHECKINS_HEADER = "date";
    private static final String DAMAGED_COMMITMENT = "commitment.csv is damaged. Run commit again to replace it.";

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

    /**
     * Adds one check-in date to the end of checkins.csv, creating the file
     * with a header the first time.
     */
    public static void appendCheckIn(LocalDate date) throws IOException {
        Files.createDirectories(DATA_DIR);

        if (Files.notExists(CHECKINS_FILE) || Files.size(CHECKINS_FILE) == 0) {
            Files.writeString(CHECKINS_FILE, CHECKINS_HEADER + System.lineSeparator());
        }
        Files.writeString(CHECKINS_FILE, date + System.lineSeparator(), StandardOpenOption.APPEND);
    }

    /**
     * Returns the saved commitment, or null if none has been made yet.
     */
    public static Commitment loadCommitment() throws IOException {
        if (Files.notExists(COMMITMENT_FILE)) {
            return null;
        }
        List<String> lines = Files.readAllLines(COMMITMENT_FILE);
        if (lines.size() < 2) {
            return null;
        }

        String[] parts = lines.get(1).split(",", 4);
        if (parts.length != 4) {
            throw new IOException(DAMAGED_COMMITMENT);
        }
        try {
            return new Commitment(
                    LocalDate.parse(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    parts[3]);
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new IOException(DAMAGED_COMMITMENT);
        }
    }

    /**
     * Returns every saved check-in date, oldest first. Empty if there are none yet.
     */
    public static Set<LocalDate> loadCheckIns() throws IOException {
        Set<LocalDate> checkIns = new TreeSet<>();
        if (Files.notExists(CHECKINS_FILE)) {
            return checkIns;
        }
        List<String> lines = Files.readAllLines(CHECKINS_FILE);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.equals(CHECKINS_HEADER)) {
                continue;
            }
            try {
                checkIns.add(LocalDate.parse(line));
            } catch (DateTimeParseException e) {
                throw new IOException("checkins.csv line " + (i + 1)
                        + " isn't a date. Dates should look like 2026-11-27.");
            }
        }
        return checkIns;
    }
}
