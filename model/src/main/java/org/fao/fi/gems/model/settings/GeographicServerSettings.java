package org.fao.fi.gems.model.settings;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
	
	private String publicUrl;

	private GeoInstanceList instances;
	
	private String sourceWorkspace;
	private String sourceLayer;
	private String sourceAttribute;
	private String targetWorkspace;
	private String targetDatastore;
	private String targetLayerPrefix;
	private TimeDimension timeDimension;
	
	private List<BaseLayer> baseLayerList;
	
	private String method;
	private String shapefileURL;

	public GeographicServerSettings() {
	}

	/**
	 * @return the public url
	 */
	public String getPublicUrl() {
		return publicUrl;
	}

	/**
	 * @param publicUrl
	 *            the publicUrl to set
	 */
	public void setPublicUrl(String publicUrl) {
		this.publicUrl = publicUrl;
	}

	/**
	 * 
	 * @return the GeoInstanceList
	 */
	public GeoInstanceList getInstances() {
		return instances;
	}

	/**
	 * 
	 * @param instances
	 */
	public void setInstanceList(GeoInstanceList instances) {
		this.instances = instances;
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
	 * @return the timeDimension
	 */
	public TimeDimension getTimeDimension() {
		return timeDimension;
	}

	/**
	 * @param timeDimension the timeDimension to set
	 */
	public void setTimeDimension(TimeDimension timeDimension) {
		this.timeDimension = timeDimension;
	}

	/**
	 * @return the baseLayerList
	 */
	public List<BaseLayer> getBaseLayerList() {
		return baseLayerList;
	}

	/**
	 * @param baseLayers the baseLayerList to set
	 */
	public void setBaseLayerList(List<BaseLayer> baseLayerList) {
		this.baseLayerList = baseLayerList;
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
		xstream.aliasType("GeographicServer", GeographicServerSettings.class);
		
		xstream.aliasType("instances", GeoInstanceList.class);
		xstream.aliasType("master", GeoMasterInstance.class);
		xstream.aliasType("worker", GeoWorkerInstance.class);
		
		xstream.aliasType("baseLayer", BaseLayer.class);
		xstream.aliasType("timeDimension", TimeDimension.class);
		
		GeographicServerSettings settings = (GeographicServerSettings) xstream
				.fromXML(file);

		return settings;
	}

}
