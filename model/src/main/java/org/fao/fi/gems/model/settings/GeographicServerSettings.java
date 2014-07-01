package org.fao.fi.gems.model.settings;

import java.io.File;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * 
 * Handles the configuration of the geographic server
 * 
 * @author eblondel
 * 
 */
public class GeographicServerSettings{
	
	private String url;
	private String user;
	private String password;
	private String version;
	private String sourceWorkspace;
	private String sourceLayer;
	private String sourceAttribute;
	private String targetWorkspace;
	private String targetDatastore;
	private String targetLayerPrefix;
	private String baseLayerWorkspace;
	private String baseLayerName;
	private String method;
	private String shapefileURL;

	public GeographicServerSettings() {
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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
	 * @return the sourceWorkspace
	 */
	public String getSourceWorkspace() {
		return sourceWorkspace;
	}

	/**
	 * @param sourceWorkspace
	 *            the sourceWorkspace to set
	 */
	public void setSourceWorkspace(String sourceWorkspace) {
		this.sourceWorkspace = sourceWorkspace;
	}

	/**
	 * @return the sourceLayer
	 */
	public String getSourceLayer() {
		return sourceLayer;
	}

	/**
	 * @param sourceLayer
	 *            the sourceLayer to set
	 */
	public void setSourceLayer(String sourceLayer) {
		this.sourceLayer = sourceLayer;
	}

	/**
	 * @return the sourceAttribute
	 */
	public String getSourceAttribute() {
		return sourceAttribute;
	}

	/**
	 * @param sourceAttribute
	 *            the sourceAttribute to set
	 */
	public void setSourceAttribute(String sourceAttribute) {
		this.sourceAttribute = sourceAttribute;
	}

	/**
	 * @return the targetWorkspace
	 */
	public String getTargetWorkspace() {
		return targetWorkspace;
	}

	/**
	 * @param targetWorkspace
	 *            the targetWorkspace to set
	 */
	public void setTargetWorkspace(String targetWorkspace) {
		this.targetWorkspace = targetWorkspace;
	}

	/**
	 * @return the targetDatastore
	 */
	public String getTargetDatastore() {
		return targetDatastore;
	}

	/**
	 * @param targetDatastore
	 *            the targetDatastore to set
	 */
	public void setTargetDatastore(String targetDatastore) {
		this.targetDatastore = targetDatastore;
	}

	/**
	 * @return the targetLayerPrefix
	 */
	public String getTargetLayerPrefix() {
		return targetLayerPrefix;
	}

	/**
	 * @param targetLayerPrefix
	 *            the targetLayerPrefix to set
	 */
	public void setTargetLayerPrefix(String targetLayerPrefix) {
		this.targetLayerPrefix = targetLayerPrefix;
	}

	/**
	 * @return the baseLayerWorkspace
	 */
	public String getBaseLayerWorkspace() {
		return baseLayerWorkspace;
	}

	/**
	 * @param baseLayerWorkspace the baseLayerWorkspace to set
	 */
	public void setBaseLayerWorkspace(String baseLayerWorkspace) {
		this.baseLayerWorkspace = baseLayerWorkspace;
	}

	/**
	 * @return the baseLayerName
	 */
	public String getBaseLayerName() {
		return baseLayerName;
	}

	/**
	 * @param baseLayerName the baseLayerName to set
	 */
	public void setBaseLayerName(String baseLayerName) {
		this.baseLayerName = baseLayerName;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the shapefileURL
	 */
	public String getShapefileURL() {
		return shapefileURL;
	}

	/**
	 * @param shapefileURL the shapefileURL to set
	 */
	public void setShapefileURL(String shapefileURL) {
		this.shapefileURL = shapefileURL;
	}

	/**
	 * Parsing from XML
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static GeographicServerSettings fromXML(File file) {

		XStream xstream = new XStream(new StaxDriver());
		xstream.aliasType("GeographicServer",
				GeographicServerSettings.class);

		GeographicServerSettings settings = (GeographicServerSettings) xstream
				.fromXML(file);

		return settings;
	}

}
