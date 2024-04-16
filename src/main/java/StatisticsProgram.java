import lombok.Getter;
import lombok.RequiredArgsConstructor;
import parsing.JsonFileStatistics;
import parsing.XmlParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class StatisticsProgram {

    private static final List<String> FIELDS = List.of("developer", "yearReleased", "genre");

    private final JsonFileStatistics jsonFileStatistics;
    private final XmlParser xmlParser;

    public static void main(String[] args) {
        if (args.length == 2) {
            StatisticsProgram program = new StatisticsProgram(new JsonFileStatistics(), new XmlParser());
            program.start(args[0], args[1]);
        } else {
            throw new IllegalArgumentException("Not enough arguments");
        }
    }

    public void start(String folderName, String attribute) {
        Path dir = Paths.get(folderName);

        folderValidation(dir);
        attributeValidation(attribute);

        System.out.println("Collecting stats...");
        Map<String, Integer> stats = getJsonFileStatistics().collectStats(dir, attribute);
        String fileName = getXmlParser().parseStatsToXmlFile(stats, attribute);
        System.out.println("Statistics collected in " + fileName);
    }

    public void folderValidation(Path dir) {
        if (Files.notExists(dir)) {
            throw new IllegalArgumentException("Folder " + dir + " does not exist");
        }
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Folder " + dir + " is not a directory");
        }
    }

    public void attributeValidation(String attribute) {
        if (!FIELDS.contains(attribute)) {
            throw new IllegalArgumentException("Attribute " + attribute + " does not exist. \n" +
                    "Available fields are: developer, yearReleased, genre");
        }
    }
}
