import parsing.JsonFileStatistics;
import parsing.XmlParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


public class StatisticsProgram {

    private final Path dir;
    private final String attribute;
    private final JsonFileStatistics jsonFileStatistics;
    private final XmlParser xmlParser;
    private static final List<String> FIELDS = List.of("developer", "yearReleased", "genre");

    public StatisticsProgram(String folder, String attribute, JsonFileStatistics jsonFileStatistics, XmlParser xmlParser) {
        this.dir = Paths.get(folder);
        this.attribute = attribute;
        this.jsonFileStatistics = jsonFileStatistics;
        this.xmlParser = xmlParser;
    }

    public void start() {
        checkForCorrectFolder();
        checkForCorrectAttribute();

        System.out.println("Collecting stats...");
        Map<String, Integer> stats = jsonFileStatistics.collectStats(dir, attribute);
        String file = xmlParser.parseStatsToXmlFile(stats, attribute);
        System.out.println("Statistics collected in " + file);
    }

    public void checkForCorrectFolder() {
        if (Files.notExists(dir)) {
            throw new IllegalArgumentException("Folder " + dir + " does not exist");
        }
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Folder " + dir + " is not a directory");
        }
    }

    public void checkForCorrectAttribute() {
        boolean correct = false;
        for (String field : FIELDS) {
            if (field.equals(attribute)) {
                correct = true;
                break;
            }
        }
        if (!correct) {
            throw new IllegalArgumentException("Attribute " + attribute + " does not exist. \n" +
                    "Available fields are: developer, yearReleased, genre");
        }
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            JsonFileStatistics jfs = new JsonFileStatistics();
            XmlParser xmlParser = new XmlParser();
            StatisticsProgram program = new StatisticsProgram(args[0], args[1], jfs, xmlParser);
            program.start();
        } else {
            System.out.println("Not enough arguments");
        }
    }
}
