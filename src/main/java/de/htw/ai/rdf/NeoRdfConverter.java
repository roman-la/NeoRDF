package de.htw.ai.rdf;

import de.htw.ai.App;
import de.htw.ai.models.NeoElement;
import de.htw.ai.models.NeoIRI;
import de.htw.ai.models.NeoLiteral;
import de.htw.ai.models.NeoStatement;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;

public class NeoRdfConverter {

    public static Collection<NeoStatement> stringToNeoStatements(String input, String format) throws IOException {
        RDFFormat rdfFormat = resolveRdfFormat(format);

        if (rdfFormat == null)
            throw new IllegalArgumentException("Given format is unknown");

        RDFParser rdfParser = Rio.createParser(rdfFormat);

        StatementCollector statementCollector = new StatementCollector(new LinkedHashModel());

        rdfParser.setRDFHandler(statementCollector);

        rdfParser.parse(new StringReader(input), extractBaseIri(input));

        Collection<Statement> rdf4jStatements = statementCollector.getStatements();

        Collection<NeoStatement> neoStatements = new LinkedList<>();

        for (Statement rdf4jStatement : rdf4jStatements) {
            NeoElement subject = iriToNeoElement(rdf4jStatement.getSubject().stringValue());
            NeoIRI predicate = (NeoIRI) iriToNeoElement(rdf4jStatement.getPredicate().stringValue());
            NeoElement object = iriToNeoElement(rdf4jStatement.getObject().stringValue());

            neoStatements.add(new NeoStatement(subject, predicate, object));
        }

        return neoStatements;
    }

    public static String neoStatementsToString(Collection<NeoStatement> neoStatements, String format) {
        RDFFormat rdfFormat = resolveRdfFormat(format);

        if (rdfFormat == null)
            throw new IllegalArgumentException("Given format is unknown");

        Collection<Statement> rdf4jStatements = new LinkedList<>();

        for (NeoStatement neoStatement : neoStatements) {
            ValueFactory factory = SimpleValueFactory.getInstance();

            Resource subject = (Resource) neoElementToRdf4jValue(neoStatement.getSubject());
            IRI predicate = (IRI) neoElementToRdf4jValue(neoStatement.getPredicate());
            Value object = neoElementToRdf4jValue(neoStatement.getObject());

            rdf4jStatements.add(factory.createStatement(subject, predicate, object));
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        RDFWriter rdfWriter = Rio.createWriter(rdfFormat, outputStream);

        rdfWriter.startRDF();

        for (Statement statement : rdf4jStatements) {
            rdfWriter.handleStatement(statement);
        }

        rdfWriter.endRDF();

        return outputStream.toString();
    }

    private static Value neoElementToRdf4jValue(NeoElement neoElement) {
        ValueFactory factory = SimpleValueFactory.getInstance();

        if (neoElement instanceof NeoIRI) {
            return factory.createIRI((String) ((NeoIRI) neoElement).getProperties().get("iri"));
        } else if (neoElement instanceof NeoLiteral) {
            return factory.createLiteral((String) ((NeoLiteral) neoElement).getValue());
        }

        return null;
    }

    private static NeoElement iriToNeoElement(String iri) {
        if (iri.contains("/")) {

            String namespace;
            if (iri.lastIndexOf("/") > iri.lastIndexOf("#"))
                namespace = iri.substring(0, iri.lastIndexOf("/") + 1);
            else
                namespace = iri.substring(0, iri.lastIndexOf("#") + 1);

            String ns = resolveNamespace(namespace);

            return new NeoIRI(iri, ns, namespace);
        }

        if (iri.matches("-?\\d+"))
            return new NeoLiteral(Integer.parseInt(iri));
        else if (iri.matches("-?\\d+\\.\\d+"))
            return new NeoLiteral(Double.parseDouble(iri));
        else
            return new NeoLiteral(iri);
    }

    private static String extractBaseIri(String input) {
        for (String line : input.split("\\r?\\n"))
            if (line.startsWith("@base") || line.startsWith("BASE"))
                return line.split(" ")[1];

        return "";
    }

    private static String resolveNamespace(String namespace) {
        String namespaceAbbreviation = App.ontologyHandler.getOntologyKey(namespace);

        if (namespaceAbbreviation != null)
            return namespaceAbbreviation;

        return App.ontologyHandler.addOntology(namespace);
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
