import exceptions.InvalidAttributeException;
import exceptions.InvalidFolderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import parsing.JsonFileStatistics;
import parsing.XmlParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StatisticsProgramTest {

    static final List<String> FIELDS = List.of("developer", "yearReleased", "genre");
    static final String JSON = """
            [
              {
                "title": "The Legend of Zelda: Breath of the Wild",
                "developer": { "name": "Nintendo EPD" },
                "yearReleased": 2017,
                "genre": "Action, Adventure"
              }
            ]""";

    private StatisticsProgram statisticsProgram;
    private Path dir;
    @Mock
    private JsonFileStatistics jsonFileStatistics;
    @Mock
    private XmlParser xmlParser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        statisticsProgram = new StatisticsProgram(jsonFileStatistics, xmlParser);
    }

    @Test
    public void testCorrectFolder() throws IOException {
        String folder = "test2";
        dir = Files.createDirectory(Path.of(folder));

        assertDoesNotThrow(() -> statisticsProgram.folderValidation(dir));

        Files.delete(dir);
    }

    @Test
    public void testFolderDoesNOtExist() {
        String folder = "test2";
        Path dir = Paths.get(folder);

        assertThatThrownBy(() -> statisticsProgram.folderValidation(dir))
                .isInstanceOf(InvalidFolderException.class)
                .hasMessageContaining("Folder " + dir + " does not exist");
    }

    @Test
    public void testNonDirectoryFolder() throws IOException {
        String folder = "test2.txt";
        dir = Files.createFile(Path.of(folder));

        assertThatThrownBy(() -> statisticsProgram.folderValidation(dir))
                .isInstanceOf(InvalidFolderException.class)
                .hasMessageContaining("Folder " + dir + " is not a directory");

        Files.delete(dir);
    }

    @Test
    public void testCorrectAttribute() throws IOException {
        String folder = "test2";
        String attribute = "developer";
        dir = Files.createDirectory(Path.of(folder));

        assertThat(FIELDS.contains(attribute)).isTrue();
        assertDoesNotThrow(() -> statisticsProgram.attributeValidation(attribute));

        Files.delete(dir);
    }

    @Test
    public void testIncorrectAttribute() throws IOException {
        String folder = "test2";
        String attribute = "dev";
        dir = Files.createDirectory(Path.of(folder));

        assertThat(FIELDS.contains(attribute)).isFalse();
        assertThatThrownBy(() -> statisticsProgram.attributeValidation(attribute))
                .isInstanceOf(InvalidAttributeException.class)
                .hasMessageContaining("Attribute " + attribute + " does not exist. \n" +
                        "Available fields are: developer, yearReleased, genre");

        Files.delete(dir);
    }

    @Test
    public void testStart() throws IOException {
        String folder = "test2";
        dir = Files.createDirectory(Path.of(folder));

        Map<String, Integer> mockStats = new HashMap<>();
        mockStats.put("Developer1", 10);
        mockStats.put("Developer2", 5);

        when(jsonFileStatistics.collectStats(eq(dir), eq("developer")))
                .thenReturn(mockStats);

        when(xmlParser.parseStatsToXmlFile(eq(mockStats), eq("developer")))
                .thenReturn("stats.xml");

        statisticsProgram.start(folder, "developer");

        verify(jsonFileStatistics).collectStats(dir, "developer");
        verify(xmlParser).parseStatsToXmlFile(mockStats, "developer");

        Files.delete(dir);
    }

    @Test
    void testMain() throws IOException {
        Path file;
        try {
            dir = Files.createDirectory(Path.of("testDir"));
            file = Files.createFile(Path.of("testDir\\testFile.json"));
            Files.write(file, JSON.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8));

        String[] args = {"testDir", "genre"};
        StatisticsProgram.main(args);

        System.setOut(originalOut);

        String consoleOutput = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(consoleOutput.contains("Collecting stats..."));
        assertTrue(consoleOutput.contains("Statistics collected"));

        Files.delete(file);
        Files.delete(Path.of("src/main/resources/statistics_by_genre.xml"));
        Files.delete(dir);
    }

    @Test
    void testMainWithInsufficientArguments() {
        String[] insufficientArgs = {"developer"};

        assertThatThrownBy(() -> StatisticsProgram.main(insufficientArgs))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Not enough arguments");
    }
}