package parsing;

import exceptions.XmlFileCreationException;
import exceptions.XmlParsingException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Utility class for parsing statistics into an XML file
 * with fileName based on a specified attribute.
 */
public class XmlParser {

    private static final String XML_FILE_TEMPLATE = "src/main/resources/statistics_by_%s.xml";
    private static final String ITEM_TEMPLATE = """
                <item>
                    <value>%s</value>
                    <count>%d</count>
                </item>
            """;

    /**
     * Parses statistics into an XML file based on the provided attribute.
     *
     * @param statistic a map containing attribute values
     *                  as keys and their occurrence counts as values
     * @param attribute the attribute for which statistics are being parsed
     *                  (e.g., "developer", "yearReleased", "genre")
     * @return the path to the created XML file
     * @throws XmlParsingException if an error occurs while writing to the XML file
     */
    public String parseStatsToXmlFile(Map<String, Integer> statistic, String attribute) {
        Path xmlPath = createXmlFile(attribute);

        try (BufferedWriter writer = Files.newBufferedWriter(xmlPath)) {
            writer.append("<statistics>\n");
            for (Map.Entry<String, Integer> entry : statistic.entrySet()) {
                writer.append(ITEM_TEMPLATE.formatted(entry.getKey(), entry.getValue()));
            }
            writer.append("</statistics>");

            return xmlPath.toString();
        } catch (IOException e) {
            throw new XmlParsingException("Could not write to xml file", e);
        }
    }

    /**
     * Creates an XML file for storing statistics based on the provided attribute.
     *
     * @param attribute the attribute for which the XML file is being created
     *                  (e.g., "developer", "yearReleased", "genre")
     * @return the Path object representing the created XML file
     * @throws XmlFileCreationException if an error occurs while creating the XML file
     */
    private Path createXmlFile(String attribute) {
        Path xmlPath = Path.of(String.format(XML_FILE_TEMPLATE, attribute));

        try {
            Files.deleteIfExists(xmlPath);
            Files.createFile(xmlPath);
            return xmlPath;
        } catch (IOException e) {
            throw new XmlFileCreationException("Could not create xml file", e);
        }
    }
}
