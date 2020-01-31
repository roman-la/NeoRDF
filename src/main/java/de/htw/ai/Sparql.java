package de.htw.ai;

import java.util.*;

public class Sparql {

    public void sparqlToCypher(String sparqlQuery) {

        Map<String, String> prefixes = new HashMap<>();
        Collection<String> selectVariables = new LinkedList<>();
        Collection<String> wherePattern = new LinkedList<>();
        Collection<String> filterPattern = new LinkedList<>();

        for (String line : sparqlQuery.split("\\r?\\n")) {

            line = line.trim();
            line = line.replace("  ", " ");

            if (line.startsWith("PREFIX")) {
                prefixes.putAll(handlePrefix(line));
            } else if (line.startsWith("SELECT")) {
                selectVariables = handleSelect(line);
            } else if (line.startsWith("WHERE")) {
                wherePattern = handleWhere(line, prefixes);
            } else if (line.startsWith("FILTER")) {
                filterPattern = handleFilter(line);
            } else if (line.startsWith("LIMIT")) {
                int limit = Integer.parseInt(line.split(" ")[1]);
            }
        }
    }

    private Map<String, String> handlePrefix(String line) {
        String[] lineParts = line.split(" ");
        String key = lineParts[1];
        String value = lineParts[2];
        return new HashMap<String, String>() {{
            put(key, value);
        }};
    }

    private Collection<String> handleSelect(String line) {
        String[] lineParts = line.split(" ");

        return Arrays.asList(lineParts).subList(1, lineParts.length);
    }

    private Collection<String> handleWhere(String line, Map<String, String> prefixes) {
        String rawWhereClauses = line.substring(line.indexOf("{") + 1, line.indexOf("}"));

        return Arrays.asList(rawWhereClauses.split("."));
    }

    private Collection<String> handleFilter(String line) {
        return null;
    }
}
