package de.htw.ai;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

public class RDFImport {

    public Collection<Statement> readRDF(String input, String baseIri, RDFFormat format) throws IOException {
        Reader reader = new StringReader(input);

        RDFParser rdfParser = Rio.createParser(format);

        Model model = new LinkedHashModel();

        StatementCollector coll = new StatementCollector(model);

        rdfParser.setRDFHandler(coll);

        rdfParser.parse(reader, baseIri);

        return coll.getStatements();
    }
}
