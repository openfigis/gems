/**
 * (c) 2015 FAO / UN (project: gems-validation)
 */
package org.fao.fi.gems.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Inspire validation report
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class InspireValidationReport {

	private String onlineReportUrl;
	private List<InspireValidationError> errors;
	double completeness;
	
	/**
	 * Basic constructor
	 */
	public InspireValidationReport(){
	}
	
	public void setValidationErrors(List<InspireValidationError> errors){
		this.errors = errors;
	}
	
	public List<InspireValidationError> getValidationErrors(){
		return this .errors;
	}

	public void setOnlineReportUrl(String onlineReportUrl) {
		this.onlineReportUrl = onlineReportUrl;
	}
	
	public String getOnlineReportUrl() {
		return onlineReportUrl;
	}
	
	public void setCompleteness(double value){
		completeness = value;
	}
	
	public double getCompleteness(){
		return completeness;
	}
	
	public static InspireValidationReport fromXML(InputStream is) throws Exception {
		
		InspireValidationReport report = new InspireValidationReport();
		
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
	
			//online report
			NodeList nList1 = doc.getElementsByTagName("ns2:GeoportalMetadataLocator");
			Node node1 = nList1.item(0);
			
			if (node1.getNodeType() == Node.ELEMENT_NODE) {
				Element elem1 = (Element) node1;
				Element urlElem = (Element) elem1.getElementsByTagName("URL").item(0);
				String url = urlElem.getTextContent();
				report.setOnlineReportUrl(url);
			}
			
			//errors
			List<InspireValidationError> errors = new ArrayList<InspireValidationError>();
			NodeList nList2 = doc.getElementsByTagName("ns2:GeoportalExceptionMessage");
			for(int i=0;i<nList2.getLength();i++){
				
				Node node = nList2.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE){
					Element elem = (Element) node;
					
					InspireValidationError error = new InspireValidationError();
					
					NodeList argList = elem.getElementsByTagName("ns2:Argument");
					for(int j=0;j<argList.getLength();j++){
						Node argNode = argList.item(j);
						if(argNode.getNodeType() == Node.ELEMENT_NODE){
							Element argElem = (Element) argNode;
							
							String argName = argElem.getElementsByTagName("ns2:ArgumentName").item(0).getTextContent();
							String argValue = argElem.getElementsByTagName("ns2:ArgumentValue").item(0).getTextContent();
							
							if(argName.contentEquals("metadataElementName")){
								error.setElement(argValue);
							}
							
							if(argName.contentEquals("hint")){
								error.setMessage(argValue);
							}
							
						}
					}
					
					errors.add(error);
				}
			}
			
			report.setValidationErrors(errors);
			
			//completeness indicator
			NodeList nList3 = doc.getElementsByTagName("ns2:CompletenessIndicator");
			Node node3 = nList3.item(0);
			if(node3.getNodeType() == Node.ELEMENT_NODE){
				Element elem3 = (Element) node3;
				double value = Double.parseDouble(elem3.getTextContent());
				value = Math.round( value * 100.0 ) / 100.0;
				report.setCompleteness(value);
			}
			
		}catch(Exception e){
			throw new Exception("Failed to marshall INSPIRE validation report", e);
		}
		
		return report;
	}
	
	public static InspireValidationReport fromXML(File file) throws Exception {
		FileInputStream is = new FileInputStream(file);
		return fromXML(is);
	}
	
	
}
