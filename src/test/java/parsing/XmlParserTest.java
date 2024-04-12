package parsing;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class XmlParserTest {

    @Test
    void testParseStatsToFileSuccess() throws IOException {
        Map<String, Integer> stats = Map.of("Developer 1", 2, "Developer 2", 5);
        String attribute = "developer";

        XmlParser parser = new XmlParser();
        String fileName = parser.parseStatsToXmlFile(stats, attribute);
        Path filePath = Path.of(fileName);

        boolean exists = Files.exists(filePath);

        assertThat(fileName).isEqualTo("statistics_by_developer.xml");
        assertThat(exists).isTrue();

        Files.delete(filePath);
    }
}