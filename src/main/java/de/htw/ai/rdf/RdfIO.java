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

    public static Collection<Statement> stringToStatements(String input, String format) throws IOException {
        RDFFormat rdfFormat = resolveRdfFormat(format);

        if (rdfFormat == null)
            return null;

        RDFParser rdfParser = Rio.createParser(rdfFormat);

        StatementCollector statementCollector = new StatementCollector(new LinkedHashModel());

        rdfParser.setRDFHandler(statementCollector);

        rdfParser.parse(new StringReader(input), extractBaseIri(input));

        return statementCollector.getStatements();
    }

    public static String statementsToString(Collection<Statement> statements, String format) {
        RDFFormat rdfFormat = resolveRdfFormat(format);

        if (rdfFormat == null)
            return null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        RDFWriter rdfWriter = Rio.createWriter(rdfFormat, outputStream);

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

    private static RDFFormat resolveRdfFormat(String format) {
        switch (format) {
            case "BINARY":
                return RDFFormat.BINARY;
            case "JSONLD":
                return RDFFormat.JSONLD;
            case "N3":
                return RDFFormat.N3;
            case "NQUADS":
                return RDFFormat.NQUADS;
            case "NTRIPLES":
                return RDFFormat.NTRIPLES;
            case "RDFJSON":
                return RDFFormat.RDFJSON;
            case "RDFXML":
                return RDFFormat.RDFXML;
            case "TRIG":
                return RDFFormat.TRIG;
            case "TRIX":
                return RDFFormat.TRIX;
            case "TURTLE":
                return RDFFormat.TURTLE;
        }

        return null;
    }
}
