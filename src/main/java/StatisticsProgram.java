import exceptions.InvalidAttributeException;
import exceptions.InvalidFolderException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import parsing.JsonFileStatistics;
import parsing.XmlParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * A program to collect statistics from JSON files within a
 * specified folder and generate XML reports based on attributes.
 * The supported attributes for statistics collection include
 * "developer", "yearReleased", and "genre".
 */
@Getter
@RequiredArgsConstructor
public class StatisticsProgram {

    private static final List<String> FIELDS = List.of("developer", "yearReleased", "genre");

    private final JsonFileStatistics jsonFileStatistics;
    private final XmlParser xmlParser;

    /**
     * Main method to execute the statistics program.
     *
     * @param args Command-line arguments: folderName and attribute
     * @throws IllegalArgumentException if the number of arguments is not exactly 2
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            StatisticsProgram program = new StatisticsProgram(new JsonFileStatistics(), new XmlParser());
            program.start(args[0], args[1]);
        } else {
            throw new IllegalArgumentException("Not enough arguments");
        }
    }

    /**
     * Starts the statistics collection and XML report generation process.
     *
     * @param folderName the name of the folder containing JSON files
     * @param attribute  the attribute for which statistics are to be collected
     * @throws InvalidFolderException    if the specified folder does not exist or is not a directory
     * @throws InvalidAttributeException if the specified attribute is not supported
     */
    public void start(String folderName, String attribute) {
        Path dir = Paths.get(folderName);

        folderValidation(dir);
        attributeValidation(attribute);

        System.out.println("Collecting stats...");
        Map<String, Integer> stats = getJsonFileStatistics().collectStats(dir, attribute);
        String fileName = getXmlParser().parseStatsToXmlFile(stats, attribute);
        System.out.println("Statistics collected in " + fileName);
    }

    /**
     * Validates if the specified directory path is a valid existing folder.
     *
     * @param dir the directory path to validate
     * @throws InvalidFolderException if the directory does not exist or is not a valid directory
     */
    public void folderValidation(Path dir) {
        if (Files.notExists(dir)) {
            throw new InvalidFolderException("Folder " + dir + " does not exist");
        }
        if (!Files.isDirectory(dir)) {
            throw new InvalidFolderException("Folder " + dir + " is not a directory");
        }
    }

    /**
     * Validates if the specified attribute is supported for statistics collection.
     *
     * @param attribute the attribute to validate
     * @throws InvalidAttributeException if the attribute is not one of the supported fields
     */
    public void attributeValidation(String attribute) {
        if (!FIELDS.contains(attribute)) {
            throw new InvalidAttributeException("Attribute " + attribute + " does not exist. \n" +
                    "Available fields are: developer, yearReleased, genre");
        }
    }
}
