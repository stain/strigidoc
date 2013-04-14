package uk.ac.abdn.erg.iain.strigidoc;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OWLData {

	private OWLOntologyManager m;
	private OWLOntology o;

	public OWLData(String iri) throws OWLOntologyCreationException, IOException {

		m = OWLManager.createOWLOntologyManager();

		o = m.loadOntologyFromOntologyDocument(new URL(iri).openStream());

	}

	public String getIntroductionValues() {
		Set<OWLAnnotation> values = o.getAnnotations();
		String introduction = "\\begin{description}\n";

		// Ontology IRI
		IRI ontologyIRI = o.getOntologyID().getOntologyIRI();

		if (o != null) {
			introduction += "\\item[Ontology IRI] \\hfill \\\\\n";
			introduction += "\\url{" + ontologyIRI.toString() + "}\n";
		}

		// Ontology Authors
		Set<String> authors = new HashSet<String>();

		for (OWLAnnotation a : values) {
			if (a.getProperty().getIRI().toString()
					.equals("http://purl.org/dc/elements/1.1/creator")) {
				authors.add(a.getValue().toString().replaceAll("\"", ""));
			}
		}

		if (authors.size() > 0) {
			introduction += "\\item[Authors] \\hfill \\\\\n";

			for (String author : authors) {
				if (author.contains("<")) {
					author = author.substring(0, author.indexOf('<') - 1);
				}

				introduction += author + "\\\n";
			}

		}

		// Ontology Imports
		Set<OWLOntology> imports = o.getDirectImports();
		
		if ( imports.size() > 0 ) {
			introduction += "\\item[Imported Ontologies] \\hfill \\\\\n";
			
			for ( OWLOntology im : imports ) {
				introduction += "\\url{" + im.getOntologyID().getOntologyIRI() + "}\\\n";
			}

		}
		
		if (introduction.equals("\\begin{description}\n"))
			return "% -- No introduction values available\n";
		else
			return introduction + "\\end{description}\n";

	}

	public String getAbstract() {
		
		Set<OWLAnnotation> anns = o.getAnnotations();
		
		for ( OWLAnnotation a : anns ) {
			
			if ( a.getProperty().getIRI().toString().equals("http://www.w3.org/2000/01/rdf-schema#comment")) {
				return a.getValue().toString().replaceAll("\"", "") + "\n\n";
			}
			
		}
		
		return null;
	}
	
	public static void main(String args[]) {

		try {
			OWLData data = new OWLData(
					"http://homepages.abdn.ac.uk/i.learmonth/pages/qunits/qunits.owl");

			System.out.println(data.getIntroductionValues());

			System.out.println(data.getAbstract());
			
			System.out.println(data.getIntroduction());
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getIntroduction() {
		
		Set<OWLAnnotation> anns = o.getAnnotations();
		
		for ( OWLAnnotation a : anns ) {
			
			if ( a.getProperty().getIRI().toString().equals("http://purl.org/dc/elements/1.1/description")
					&& (!a.getValue().toString().startsWith("http://")) ) {
				return "\\section{Introduction}\n\n" + a.getValue().toString().replaceAll("\"", "") + "\n\n";
			}
			
		}
		
		return null;
	}

}
