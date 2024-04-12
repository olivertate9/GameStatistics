package parsing;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class JsonFileStatistics {

    private final Map<String, Integer> stats;
    private final ExecutorService executor;

    public JsonFileStatistics() {
        this.stats = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(4);
    }

    public Map<String, Integer> collectStats(Path folderPath, String attribute) {
        List<File> files = getJsonFiles(folderPath);

        List<Future<?>> futures = new ArrayList<>();

        for (File file : files) {
            futures.add(executor.submit(() -> {
                try (JsonParser parser = new JsonFactory().createParser(file)) {
                    processFields(attribute, parser);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to parse json file", e);
                }
            }));
        }

        waitForCompletion(futures);
        executor.shutdown();

        return stats;
    }

    private void processFields(String attribute, JsonParser parser) throws IOException {
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            if (parser.currentToken() == JsonToken.FIELD_NAME && attribute.equals(parser.currentName())) {
                if (attribute.equals("developer")) {
                    processDeveloperField(parser);
                } else {
                    processField(parser);
                }
            }
        }
    }

    private void processField(JsonParser parser) throws IOException {
        parser.nextToken();
        countAttributes(parser.getText(), stats);
    }

    private void processDeveloperField(JsonParser parser) throws IOException {
        parser.nextToken();
        parser.nextToken();
        processField(parser);
    }

    private void countAttributes(String value, Map<String, Integer> stats) {
        String[] values = value.split(", ");
        for (String val : values) {
            stats.put(val, stats.getOrDefault(val, 0) + 1);
        }
    }

    private List<File> getJsonFiles(Path folderPath) {
        File folder = new File(String.valueOf(folderPath));
        File[] files = folder.listFiles();

        if (files.length == 0) {
            throw new IllegalArgumentException("Folder " + folder + " does not contain any files");
        }

        List<File> jsonFiles = new ArrayList<>();
        for (File file : files) {
            if (file.getName().endsWith(".json")) {
                jsonFiles.add(file);
            }
        }
        return jsonFiles;
    }

    private void waitForCompletion(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException("Failed to collect statistics", e);
            }
        }
    }

}
