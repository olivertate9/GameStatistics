package parsing;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class XmlParser {

    public String parseStatsToXmlFile(Map<String, Integer> statistic, String attribute) {
        String fileName = createXmlFile(attribute);
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(fileName))) {
            writer.append("<statistics>\n");
            for (Map.Entry<String, Integer> entry : statistic.entrySet()) {
                writer.append("""
                        <item>
                            <value>%s</value>
                            <count>%d</count>
                        </item>
                    """.formatted(entry.getKey(), entry.getValue()));
            }
            writer.append("</statistics>");

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not write xml file", e);
        }
    }

    private String createXmlFile(String attribute) {
        String fileName = String.format("statistics_by_%s.xml", attribute);
        try {
            Files.createFile(Path.of(fileName));
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not create xml file", e);
        }
    }
}
