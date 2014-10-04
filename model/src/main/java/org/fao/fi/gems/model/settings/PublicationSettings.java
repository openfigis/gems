package org.fao.fi.gems.model.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private String version;
	private String style;
	private double buffer;
	
	private List<String> entities = new ArrayList<String>();
	
	private String figisViewerUrl;
	private String figisFactsheetUrl;

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
	 * @return the entities
	 */
	public List<String> getEntities() {
		return entities;
	}


	/**
	 * @param entities the entities to set
	 */
	public void setEntities(List<String> entities) {
		this.entities = entities;
	}
	
	
	/**
	 * @param entity
	 */
	public void addEntity(String entity){
		this.entities.add(entity);
	}

	/**
	 * @return the figisViewerUrl
	 */
	public String getFigisViewerUrl() {
		return figisViewerUrl;
	}


	/**
	 * @param figisViewerUrl the figisViewerUrl to set
	 */
	public void setFigisViewerUrl(String figisViewerUrl) {
		this.figisViewerUrl = figisViewerUrl;
	}


	/**
	 * @return the figisFactsheetUrl
	 */
	public String getFigisFactsheetUrl() {
		return figisFactsheetUrl;
	}


	/**
	 * @param figisFactsheetUrl the figisFactsheetUrl to set
	 */
	public void setFigisFactsheetUrl(String figisFactsheetUrl) {
		this.figisFactsheetUrl = figisFactsheetUrl;
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
