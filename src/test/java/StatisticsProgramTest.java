import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import parsing.JsonFileStatistics;
import parsing.XmlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StatisticsProgramTest {

    @Mock
    private JsonFileStatistics jsonFileStatistics;

    @Mock
    private XmlParser xmlParser;

    private static final List<String> FIELDS = List.of("developer", "yearReleased", "genre");
    private static StatisticsProgram statisticsProgram;
    private static Path dir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCorrectFolder() throws IOException {
        String folder = "test2";
        dir = Files.createDirectory(Path.of(folder));
        statisticsProgram = new StatisticsProgram(folder, "developer", jsonFileStatistics, xmlParser);

        assertDoesNotThrow(() -> statisticsProgram.checkForCorrectFolder());

        Files.delete(dir);
    }

    @Test
    public void testFolderDoesNOtExist() throws IOException {
        String folder = "test2";
        Path dir = Paths.get(folder);
        statisticsProgram = new StatisticsProgram(folder, "developer", jsonFileStatistics, xmlParser);

        assertThatThrownBy(() -> statisticsProgram.checkForCorrectFolder())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Folder " + dir + " does not exist");
    }

    @Test
    public void testNonDirectoryFolder() throws IOException {
        String folder = "test2.txt";
        dir = Files.createFile(Path.of(folder));
        statisticsProgram = new StatisticsProgram(folder, "developer", jsonFileStatistics, xmlParser);

        assertThatThrownBy(() -> statisticsProgram.checkForCorrectFolder())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Folder " + dir + " is not a directory");

        Files.delete(dir);
    }

    @Test
    public void testCorrectAttribute() throws IOException {
        String folder = "test2";
        String attribute = "developer";
        dir = Files.createDirectory(Path.of(folder));

        statisticsProgram = new StatisticsProgram(folder, attribute, jsonFileStatistics, xmlParser);

        boolean correct = false;
        for (String field : FIELDS) {
            if (field.equals(attribute)) {
                correct = true;
                break;
            }
        }

        assertThat(correct).isTrue();
        assertDoesNotThrow(() -> statisticsProgram.checkForCorrectAttribute());

        Files.delete(dir);
    }

    @Test
    public void testIncorrectAttribute() throws IOException {
        String folder = "test2";
        String attribute = "dev";
        dir = Files.createDirectory(Path.of(folder));

        statisticsProgram = new StatisticsProgram(folder, attribute, jsonFileStatistics, xmlParser);

        boolean correct = false;
        for (String field : FIELDS) {
            if (field.equals(attribute)) {
                correct = true;
                break;
            }
        }

        assertThat(correct).isFalse();
        assertThatThrownBy(() -> statisticsProgram.checkForCorrectAttribute())
                .isInstanceOf(IllegalArgumentException.class)
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

        statisticsProgram = new StatisticsProgram(folder, "developer", jsonFileStatistics, xmlParser);

        statisticsProgram.start();

        verify(jsonFileStatistics).collectStats(dir, "developer");
        verify(xmlParser).parseStatsToXmlFile(mockStats, "developer");

        Files.delete(dir);
    }
}