package de.htw.ai;

import java.util.Collection;
import java.util.LinkedList;

public class Sparql {

    public void sparqlToCypher(String sparqlQuery) {

        Collection<String> prefixes = new LinkedList<>();
        Collection<String> selectVariables = new LinkedList<>();
        Collection<String> wherePattern = new LinkedList<>();
        Collection<String> filterPattern = new LinkedList<>();

        for (String line : sparqlQuery.split("\\r?\\n")) {

            line = line.trim();
            line = line.replace("  ", " ");

            if (line.startsWith("@prefix")) {
                prefixes.addAll(handlePrefix());
            } else if (line.startsWith("SELECT")) {
                selectVariables = handleSelect();
            } else if (line.startsWith("WHERE")) {
                wherePattern = handleWhere();
            } else if (line.startsWith("FILTER")) {
                filterPattern = handleFilter();
            } else if (line.startsWith("LIMIT")) {
                int limit = Integer.parseInt(line.split(" ")[1]);
            }
        }
    }

    private Collection<String> handlePrefix() {
        return null;
    }

    private Collection<String> handleSelect() {
        return null;
    }

    private Collection<String> handleWhere() {
        return null;
    }

    private Collection<String> handleFilter() {
        return null;
    }
}
