package parsing;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import exceptions.InvalidFolderException;
import exceptions.JsonParsingException;
import exceptions.TaskExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Utility class to collect statistics from JSON files within a specified folder.
 */
public class JsonFileStatistics {

    /**
     * Collects statistics based on a specified attribute
     * from JSON files within a given folder path.
     *
     * @param folderPath the path to the folder containing JSON files
     * @param attribute  the attribute to collect statistics for
     *                   (e.g., "developer", "yearReleased", "genre")
     * @return a map containing attribute values as keys and their occurrence counts as values
     * @throws JsonParsingException   if there is an error parsing a JSON file
     * @throws InvalidFolderException if the specified folder is empty
     *                                or does not contain any JSON files
     */
    public Map<String, Integer> collectStats(Path folderPath, String attribute) {
        List<File> files = getJsonFiles(folderPath);
        Map<String, Integer> stats = new ConcurrentHashMap<>();

        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<Future<?>> futures = new ArrayList<>();

        for (File file : files) {
            futures.add(executor.submit(() -> {
                try (JsonParser parser = new JsonFactory().createParser(file)) {
                    processFields(attribute, parser, stats);
                } catch (IOException e) {
                    throw new JsonParsingException("Failed to parse json file", e);
                }
            }));
        }

        waitForCompletion(futures);
        executor.shutdown();

        return stats;
    }

    /**
     * Processes JSON fields within a JSON file's content to extract attribute values.
     *
     * @param attribute the attribute to extract values for
     * @param parser    the JSON parser for the file being processed
     * @param stats     the map to store the extracted attribute values and their counts
     * @throws IOException if there is an error reading the JSON content
     */
    private void processFields(String attribute, JsonParser parser, Map<String, Integer> stats) throws IOException {
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            if (parser.currentToken() == JsonToken.FIELD_NAME && attribute.equals(parser.currentName())) {
                handleAttribute(attribute, parser, stats);
            }
        }
    }

    /**
     * Handles processing of a specific attribute found within the JSON content.
     *
     * @param attribute the attribute to process
     * @param parser    the JSON parser for the file being processed
     * @param stats     the map to store the extracted attribute values and their counts
     * @throws IOException if there is an error reading the JSON content
     */
    private void handleAttribute(String attribute, JsonParser parser, Map<String, Integer> stats) throws IOException {
        if (attribute.equals("developer")) {
            processDeveloperField(parser, stats);
        } else {
            processField(parser, stats);
        }
    }

    /**
     * Processes the "developer" field within the JSON content to extract developer names.
     *
     * @param parser the JSON parser for the file being processed
     * @param stats  the map to store the extracted developer names and their counts
     * @throws IOException if there is an error reading the JSON content
     */
    private void processDeveloperField(JsonParser parser, Map<String, Integer> stats) throws IOException {
        parser.nextToken();
        parser.nextToken();
        processField(parser, stats);
    }

    /**
     * Processes JSON field value and updates the statistics map with its occurrences.
     *
     * @param parser the JSON parser for the file being processed
     * @param stats  the map to store the extracted field values and their counts
     * @throws IOException if there is an error reading the JSON content
     */
    private void processField(JsonParser parser, Map<String, Integer> stats) throws IOException {
        parser.nextToken();
        countAttributes(parser.getText(), stats);
    }

    /**
     * Splits the field value by comma and updates the statistics map with each value's occurrence.
     *
     * @param value the field value to split and count
     * @param stats the map to store the extracted values and their counts
     */
    private void countAttributes(String value, Map<String, Integer> stats) {
        String[] values = value.split(", ");
        for (String val : values) {
            stats.put(val, stats.getOrDefault(val, 0) + 1);
        }
    }

    /**
     * Retrieves all JSON files within a specified folder path.
     *
     * @param folderPath the path to the folder containing JSON files
     * @return a list of File objects representing the JSON files within the folder
     * @throws InvalidFolderException if the specified folder is empty or does not contain any JSON files
     */
    private List<File> getJsonFiles(Path folderPath) {
        File folder = new File(String.valueOf(folderPath));
        File[] files = folder.listFiles();

        if (files.length == 0) {
            throw new InvalidFolderException("Folder " + folder + " does not contain any files");
        }

        List<File> jsonFiles = new ArrayList<>();
        for (File file : files) {
            if (file.getName().endsWith(".json")) {
                jsonFiles.add(file);
            }
        }
        return jsonFiles;
    }

    /**
     * Waits for all submitted tasks to complete execution.
     *
     * @param futures a list of Future objects representing submitted tasks
     * @throws TaskExecutionException if any task execution encounters an exception
     */
    private void waitForCompletion(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                throw new TaskExecutionException("Failed to collect statistics", e);
            }
        }
    }
}
