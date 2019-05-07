package csv;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;

import utils.HelperFunctions;
import utils.XSLT_Util;


public class CrawledLinkedInSkillPagesZipToCSV {
	
    public static void main(String[] args) {
    			
    	try {
			new XSLT_Util(); //load Industry Codes and Redirects
			new File(HelperFunctions.SKILLS_INDUSTRIES_CSV).delete();
			//from: http://www.javamex.com/tutorials/compression/zip_individual_entries.shtml#.UsVyPdLuJlw:			
			ZipFile zipFile = new ZipFile(HelperFunctions.HTML_SKILL_PAGES_ZIP);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while(entries.hasMoreElements()){
		       	ZipEntry entry = entries.nextElement();
		       	InputStream htmlPage = zipFile.getInputStream(entry);
		       	if(entry.getName().endsWith(".html")) {		       		
		       		transformHTML5fileToCSV(htmlPage);
		       	}
		    }
			zipFile.close();
		} catch (Exception  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    
    private static void transformHTML5fileToCSV(InputStream htmlPage) throws IOException, ParserConfigurationException, TransformerException {
    	
    	HtmlCleaner cleaner = new HtmlCleaner();
    	CleanerProperties props = cleaner.getProperties();    	
    	TagNode node = cleaner.clean(htmlPage);

    	Document myDom = new DomSerializer(props, true).createDOM(node);
    	DOMSource domSource = new DOMSource(myDom);    	
    	    	
    	TransformerFactory tFactory = TransformerFactory.newInstance();
    	Transformer transformer = tFactory.newTransformer(new StreamSource(HelperFunctions.XSL_FILE));
    	FileOutputStream output = new FileOutputStream(new File(HelperFunctions.SKILLS_INDUSTRIES_CSV), true);
    	transformer.transform(domSource, new StreamResult(output));
    	
    	output.close();
    }
}