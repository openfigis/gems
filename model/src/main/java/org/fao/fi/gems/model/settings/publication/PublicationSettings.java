/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings.publication;

import java.io.File;
import java.io.IOException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Configures the publication settings
 * 
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class PublicationSettings{

	private String action;
	private boolean actionData;
	private boolean actionMetadata;
	
	private String codelistURL;
	private String codelistParser;

	private String collectionType;
	private String version;
	private String style;
	private double buffer;
	
	private EntityList entities;
	
	private String figisViewerResourceBaseUrl;
	private String figisWebResourceBaseUrl;
	private String figisWebResourceTitle;
	private boolean figisHasFactsheet;

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
	 * @return the actionData
	 */
	public boolean isActionData() {
		return actionData;
	}


	/**
	 * @param actionData the actionData to set
	 */
	public void setActionData(boolean actionData) {
		this.actionData = actionData;
	}


	/**
	 * @return the actionMetadata
	 */
	public boolean isActionMetadata() {
		return actionMetadata;
	}


	/**
	 * @param actionMetadata the actionMetadata to set
	 */
	public void setActionMetadata(boolean actionMetadata) {
		this.actionMetadata = actionMetadata;
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
		this.codelistURL = codelistURL.replaceAll("&amp;","&");
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
	public EntityList getEntities() {
		return entities;
	}


	/**
	 * @param entities the entities to set
	 */
	public void setEntities(EntityList entities) {
		this.entities = entities;
	}


	/**
	 * @return the figisViewerResourceBaseUrl
	 */
	public String getFigisViewerResourceBaseUrl() {
		return figisViewerResourceBaseUrl;
	}


	/**
	 * @param figisViewerResourceBaseUrl the figisViewerResourceBaseUrl to set
	 */
	public void setFigisViewerResourceBaseUrl(String figisViewerUrl) {
		this.figisViewerResourceBaseUrl = figisViewerUrl;
	}


	/**
	 * @return the figisWebResourceBaseUrl
	 */
	public String getFigisWebResourceBaseUrl() {
		return figisWebResourceBaseUrl;
	}


	/**
	 * @param figisWebResourceBaseUrl the figisWebResourceBaseUrl to set
	 */
	public void setFigisWebResourceBaseUrl(String figisFactsheetUrl) {
		this.figisWebResourceBaseUrl = figisFactsheetUrl;
	}


	/**
	 * @return the figisWebResourceTitle
	 */
	public String getFigisWebResourceTitle() {
		return figisWebResourceTitle;
	}


	/**
	 * @param figisWebResourceTitle the figisWebResourceTitle to set
	 */
	public void setFigisWebResourceTitle(String figisWebResourceTitle) {
		this.figisWebResourceTitle = figisWebResourceTitle;
	}


	/**
	 * @return the figisHasFactsheet
	 */
	public boolean figisHasFactsheet() {
		return figisHasFactsheet;
	}


	/**
	 * @param figisHasFactsheet the figisHasFactsheet to set
	 */
	public void figisHasFactsheet(boolean figisHasFactsheet) {
		this.figisHasFactsheet = figisHasFactsheet;
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
