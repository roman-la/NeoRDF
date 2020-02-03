package de.htw.ai.rdf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SparqlConverterTest {

    @Test
    public void sparqlToCypherFullTest() {
        String sparqlQuery = "PREFIX dc: <http://purl.org/dc/elements/1.1/>" + System.lineSeparator() +
                "PREFIX ns: <http://example.org/>" + System.lineSeparator() +
                "SELECT ?title ?price" + System.lineSeparator() +
                "WHERE { ?x ns:price ?price ." + System.lineSeparator() +
                "FILTER (?price < 30.5)" + System.lineSeparator() +
                "?x dc:title ?title . }";

        String expectedCypherQuery = "MATCH (x)-[:predicate {iri: \"http://example.org/price\"}]->(price)," + System.lineSeparator() +
                "(x)-[:predicate {iri: \"http://purl.org/dc/elements/1.1/title\"}]->(title)" + System.lineSeparator() +
                "WHERE price.value < 30.5" + System.lineSeparator() +
                "RETURN title, price";

        Assertions.assertEquals(expectedCypherQuery, SparqlConverter.sparqlToCypher(sparqlQuery));
    }

    @Test
    public void sparqlToCypherNoFilterTest() {
        String sparqlQuery = "PREFIX dc: <http://purl.org/dc/elements/1.1/>" + System.lineSeparator() +
                "PREFIX ns: <http://example.org/>" + System.lineSeparator() +
                "SELECT ?title ?price" + System.lineSeparator() +
                "WHERE { ?x ns:price ?price ." + System.lineSeparator() +
                "?x dc:title ?title . }";

        String expectedCypherQuery = "MATCH (x)-[:predicate {iri: \"http://example.org/price\"}]->(price)," + System.lineSeparator() +
                "(x)-[:predicate {iri: \"http://purl.org/dc/elements/1.1/title\"}]->(title)" + System.lineSeparator() +
                "RETURN title, price";

        Assertions.assertEquals(expectedCypherQuery, SparqlConverter.sparqlToCypher(sparqlQuery));
    }

    @Test
    public void sparqlToCypherMixedTest() {
        String sparqlQuery = "PREFIX ns: <http://example.org/>" + System.lineSeparator() +
                "SELECT ?price" + System.lineSeparator() +
                "WHERE { ?x ns:price ?price ." + System.lineSeparator() +
                "FILTER (?price < 30.5)" + System.lineSeparator() +
                "}";

        String expectedCypherQuery = "MATCH (x)-[:predicate {iri: \"http://example.org/price\"}]->(price)" + System.lineSeparator() +
                "WHERE price.value < 30.5" + System.lineSeparator() +
                "RETURN price";

        Assertions.assertEquals(expectedCypherQuery, SparqlConverter.sparqlToCypher(sparqlQuery));
    }
}
