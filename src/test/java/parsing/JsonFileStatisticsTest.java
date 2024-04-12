package parsing;

import com.fasterxml.jackson.core.JsonParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class JsonFileStatisticsTest {
    static JsonFileStatistics jfs;
    static Path dir;
    static Path file;
    static final String json = """
            [
              {
                "title": "The Legend of Zelda: Breath of the Wild",
                "developer": { "name": "Nintendo EPD" },
                "yearReleased": 2017,
                "genre": "Action, Adventure"
              }
            ]""";


    @BeforeAll
    static void setUpBeforeClass() {
        try {
            dir = Files.createDirectory(Path.of("testDir"));
            file = Files.createFile(Path.of("testDir\\testFile.json"));
            Files.write(file, json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void tearDownAfterClass() {
        try {
            Files.delete(file);
            Files.delete(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        jfs = new JsonFileStatistics();
    }


    @Test
    void testCollectStatsSuccess() {
        Map<String, Integer> stats = jfs.collectStats(dir, "genre");
        Map<String, Integer> expectedStats = Map.of("Action", 1, "Adventure", 1);

        assertThat(stats).isEqualTo(expectedStats);
    }

    @Test
    void testDeveloperFieldCountSuccess() {
        Map<String, Integer> stats = jfs.collectStats(dir, "developer");
        Map<String, Integer> expectedStats = Map.of("Nintendo EPD", 1);

        assertThat(stats).isEqualTo(expectedStats);
    }

    @Test
    void testEmptyFolder() throws IOException {
        Path emptyDir = Files.createDirectory(Path.of("empty"));

        assertThatThrownBy(() -> jfs.collectStats(emptyDir, "genre"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Folder " + emptyDir + " does not contain any files");

        Files.delete(emptyDir);
    }

    @Test
    void testNotJsonFileIgnores() throws IOException {
        Path notJson = Files.createFile(Path.of("testDir\\notJson.xml"));
        Map<String, Integer> stats = jfs.collectStats(dir, "genre");
        Map<String, Integer> expectedStats = Map.of("Action", 1, "Adventure", 1);

        assertThat(stats).isEqualTo(expectedStats);
        Files.delete(notJson);
    }

    @Test
    void testJsonFileFailedToCollectStatistics() throws IOException {
        Path incorrectJson = Files.createFile(Path.of("testDir\\incorrect.json"));
        try {
            Files.write(incorrectJson, "[{]}".getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertThatThrownBy(() -> jfs.collectStats(dir, "genre"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to collect statistics");
        Files.delete(incorrectJson);
    }

    @Test
    void testJsonFileFailedToParse() throws IOException {
        JsonParser jsonParser = mock(JsonParser.class);
        doThrow(new IOException("Simulated IOException")).when(jsonParser).nextToken();
        assertThrows(IOException.class, jsonParser::nextToken);

    }
}