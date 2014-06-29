package org.fao.fi.gems.model.settings;

import java.io.File;
import java.io.IOException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Configures the publication settings
 * 
 * 
 * @author eblondel
 *
 */
public class PublicationSettings{

	private String action;
	private boolean forceData;
	private boolean forceMetadata;
	private boolean unpublishData;
	private boolean unpublishMetadata;
	
	private String codelistURL;
	private String codelistParser;

	private String collectionType;
	private String date;
	private String version;
	private String style;
	private double buffer;
	
	private boolean test;
	private String testCode;
	
	private boolean figis;

	/**
	 * Constructor
	 * 
	 */
	public PublicationSettings(){
		
	}


	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}


	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}


	/**
	 * @return the forceData
	 */
	public boolean isForceData() {
		return forceData;
	}


	/**
	 * @param forceData the forceData to set
	 */
	public void setForceData(boolean forceData) {
		this.forceData = forceData;
	}


	/**
	 * @return the forceMetadata
	 */
	public boolean isForceMetadata() {
		return forceMetadata;
	}


	/**
	 * @param forceMetadata the forceMetadata to set
	 */
	public void setForceMetadata(boolean forceMetadata) {
		this.forceMetadata = forceMetadata;
	}


	/**
	 * @return the unpublishData
	 */
	public boolean isUnpublishData() {
		return unpublishData;
	}


	/**
	 * @param unpublishData the unpublishData to set
	 */
	public void setUnpublishData(boolean unpublishData) {
		this.unpublishData = unpublishData;
	}


	/**
	 * @return the unpublishMetadata
	 */
	public boolean isUnpublishMetadata() {
		return unpublishMetadata;
	}


	/**
	 * @param unpublishMetadata the unpublishMetadata to set
	 */
	public void setUnpublishMetadata(boolean unpublishMetadata) {
		this.unpublishMetadata = unpublishMetadata;
	}


	/**
	 * @return the codelistURL
	 */
	public String getCodelistURL() {
		return codelistURL;
	}


	/**
	 * @param codelistURL the codelistURL to set
	 */
	public void setCodelistURL(String codelistURL) {
		this.codelistURL = codelistURL;
	}


	/**
	 * @return the codelistParser
	 */
	public String getCodelistParser() {
		return codelistParser;
	}


	/**
	 * @param codelistParser the codelistParser to set
	 */
	public void setCodelistParser(String codelistParser) {
		this.codelistParser = codelistParser;
	}


	/**
	 * @return the collectionType
	 */
	public String getCollectionType() {
		return collectionType;
	}


	/**
	 * @param collectionType the collectionType to set
	 */
	public void setCollectionType(String collectionType) {
		this.collectionType = collectionType;
	}


	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}


	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}


	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}


	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}


	/**
	 * @return the buffer
	 */
	public double getBuffer() {
		return buffer;
	}


	/**
	 * @param buffer the buffer to set
	 */
	public void setBuffer(double buffer) {
		this.buffer = buffer;
	}
	
	
	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}


	/**
	 * @param style the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}


	/**
	 * @return the test
	 */
	public boolean isTest() {
		return test;
	}


	/**
	 * @param test the test to set
	 */
	public void setTest(boolean test) {
		this.test = test;
	}


	/**
	 * @return the testCode
	 */
	public String getTestCode() {
		return testCode;
	}


	/**
	 * @param testCode the testCode to set
	 */
	public void setTestCode(String testCode) {
		this.testCode = testCode;
	}


	/**
	 * @return the figis
	 */
	public boolean isFigis() {
		return this.figis;
	}


	/**
	 * @param figis the fromFigis to set
	 */
	public void setFigis(boolean fromFigis) {
		this.figis = fromFigis;
	}


	/**
	 * Parsing from XML
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static PublicationSettings fromXML(File file) {

		XStream xstream = new XStream(new StaxDriver());
		xstream.aliasType("Publication",
				PublicationSettings.class);

		PublicationSettings settings = (PublicationSettings) xstream
				.fromXML(file);

		return settings;
	}
	

}
