package de.htw.ai.rdf;

import java.util.Collection;
import java.util.LinkedList;

public class SparqlConverter {

    public static String sparqlToCypher(String sparqlQuery) {
        Collection<String> prefixLines = new LinkedList<>();
        LinkedList<String> queryLines = new LinkedList<>();

        for (String line : sparqlQuery.split("(\\r?\\n)+"))
            if (line.startsWith("PREFIX"))
                prefixLines.add(line);
            else if (line.length() > 3)
                queryLines.add(line);

        return generateCypher(resolveQueryPrefixes(prefixLines, queryLines));
    }

    private static LinkedList<String> resolveQueryPrefixes(Collection<String> prefixes, LinkedList<String> query) {

        for (String prefixLine : prefixes) {
            String prefixAbbreviation = prefixLine.split(" ")[1].replace(":", "");
            String prefixNamespace = prefixLine.split(" ")[2].replace("<", "").replace(">", "");

            for (int i = 0; i < query.size(); i++) {
                query.set(i, query.get(i).replace(prefixAbbreviation + ":", prefixNamespace));
            }
        }

        return query;
    }

    private static String generateCypher(Collection<String> sparqlQuery) {
        Collection<String> selectClauses = new LinkedList<>();
        Collection<String> whereClauses = new LinkedList<>();
        Collection<String> filterClauses = new LinkedList<>();

        for (String queryLine : sparqlQuery) {
            if (queryLine.startsWith("SELECT"))
                for (int i = 1; i < queryLine.split(" ").length; i++)
                    selectClauses.add(queryLine.split(" ")[i].replace("?", ""));
            else if (queryLine.startsWith("FILTER")) {
                queryLine = queryLine.replace("?", "");
                queryLine = queryLine.replaceAll("^FILTER \\(", "");
                queryLine = queryLine.replaceAll("\\)$", "");

                filterClauses.add(queryLine);
            } else {
                queryLine = queryLine.replace("?", "");
                queryLine = queryLine.replaceAll("^WHERE \\{ ", "");
                queryLine = queryLine.replaceAll(" \\.( })?$", "");

                whereClauses.add(queryLine);
            }
        }

        Collection<String> cypherMatchClauses = new LinkedList<>();

        for (String whereClause : whereClauses) {
            String[] whereClauseParts = whereClause.split(" ");
            String cypherMatchClause = "(" + whereClauseParts[0] + ")";
            cypherMatchClause += "-[:predicate {iri: \"" + whereClauseParts[1] + "\"}]->";
            cypherMatchClause += "(" + whereClauseParts[2] + ")";

            cypherMatchClauses.add(cypherMatchClause);
        }

        String cypherQuery = "MATCH " + String.join("," + System.lineSeparator(), cypherMatchClauses) + System.lineSeparator();

        if (filterClauses.size() > 0)
            cypherQuery += "WHERE ";

        //TODO: check for number or string
        for (String filterClause : filterClauses) {
            String[] filterClauseParts = filterClause.split(" ");
            cypherQuery += filterClauseParts[0] + ".value " + filterClauseParts[1] + " " + filterClauseParts[2];
            cypherQuery += System.lineSeparator();
        }

        cypherQuery += "RETURN " + String.join(", ", selectClauses);

        return cypherQuery;
    }
}
