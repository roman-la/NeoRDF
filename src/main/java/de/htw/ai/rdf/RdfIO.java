package de.htw.ai.rdf;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

public class RdfIO {

    public static Collection<Statement> stringToRdf(String input, RDFFormat format) throws IOException {
        RDFParser rdfParser = Rio.createParser(format);

        StatementCollector statementCollector = new StatementCollector(new LinkedHashModel());

        rdfParser.setRDFHandler(statementCollector);

        rdfParser.parse(new StringReader(input), extractBaseIri(input));

        return statementCollector.getStatements();
    }

    public static String rdfToString(Collection<Statement> statements, RDFFormat format) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        RDFWriter rdfWriter = Rio.createWriter(format, outputStream);

        rdfWriter.startRDF();

        for (Statement statement : statements) {
            rdfWriter.handleStatement(statement);
        }

        rdfWriter.endRDF();

        return outputStream.toString();
    }

    private static String extractBaseIri(String input) {
        for (String line : input.split("\\r?\\n"))
            if (line.startsWith("@base") || line.startsWith("BASE"))
                return line.split(" ")[1];

        return "";
    }
}
