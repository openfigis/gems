/**
 * (c) 2015 FAO / UN (project: gems-main)
 */
package org.fao.fi.gems.metaobject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fao.fi.gems.entity.EntityAddin;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.feature.FeatureTypeProperty;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.content.MetadataContact;
import org.fao.fi.gems.model.content.MetadataContent;
import org.fao.fi.gems.model.settings.data.BaseLayer;
import org.fao.fi.gems.util.Utils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.temporal.TemporalPrimitive;

import com.vividsolutions.jts.geom.Envelope;

/**
 * GeographicAssociationImpl
 * 
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class GeographicMetaObjectImpl implements GeographicMetaObject {

	final static String SEPARATOR_PREFIX = "_";
	final static String SEPARATOR_INTERSECT_CODE = "_x_";
	final static String SEPARATOR_INTERSECT_NAME = " | ";
	final static String SEPARATOR_DOT_REPLACEMENT = "-";
	
	protected GemsConfig config;

	protected String collection;
	protected String owner;
	protected List<GeographicEntity> entities;
	private MetadataContent template;
	
	private String code;
	
	private String refName;
	private String metaId;
	private String metaTitle;
	private String targetLayername;
	
	private Map<EntityAddin,String> addins;
	protected Map<GeographicMetaObjectProperty,List<String>> specificProperties;
	
	private Map<FeatureTypeProperty, Object> geoproperties;
	private URI graphicOverview;
	
 
	/**
	 * Constructs a Geographic Meta object association from one or more GeographicEntity
	 * 
	 * @param entities
	 * @param objectProperties
	 * @param geoproperties
	 * @param config
	 * @throws URISyntaxException
	 */
	public GeographicMetaObjectImpl(List<GeographicEntity> entities,
			Map<GeographicMetaObjectProperty, List<String>> objectProperties,
			Map<FeatureTypeProperty, Object> geoproperties,
			GemsConfig config) throws URISyntaxException {

		//settings
		this.config = config;
			
		//identification
		this.template = config.getContent();
		this.collection = this.config.getSettings().getPublicationSettings().getCollectionType();
		for(MetadataContact org : config.getContent().getOrganizationContacts()){
			if(org.getRole().matches("OWNER")){
				this.owner = org.getAcronym();
				break;
			}
		}
		
		this.setEntities(entities);
		this.setCode(entities);
		this.setRefname(entities);
		this.setMetaIdentifier();
		this.setMetaTitle();
		
		this.setSpecificProperties(entities, objectProperties);
		this.setTargetLayername(this.config.getSettings().getGeographicServerSettings().getTargetLayerPrefix());
		
		//gis
		this.geoproperties = geoproperties;
		this.setGraphicOverview();

	}
	
	/** ==============================================
	 *  		    IDENTIFICATION METHODS
	 *  ==============================================
	 */ 
	
	/**
	 * @return the entities
	 */
	public List<GeographicEntity> entities() {
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(List<GeographicEntity> entities) {
		this.entities = entities;
	}

	/**
	 * Get Code
	 * 
	 */
	public String code(){
		return this.code;
	}
	
	/**
	 * Set Code
	 * @param entities
	 */
	public void setCode(List<GeographicEntity> entities){
		
		String codes ="";
		for(int i = 0; i <entities.size();i++){
			if(i==0){
				codes = entities.get(i).code();
			}else{
				codes += SEPARATOR_INTERSECT_CODE + entities.get(i).code();
			}
		}
		this.code = codes;
		
	}
	
	/**
	 * Alternative method
	 * 
	 * @param codes
	 */
	public void setCode(String[] codes){
		String refCodes = "";
		for(int i = 0; i <codes.length;i++){
			if(i==0){
				refCodes = codes[i];
			}else{
				refCodes += SEPARATOR_INTERSECT_CODE + codes[i];
			}
		}
		this.code = refCodes;
	}

	/**
	 * Get Ref Name
	 * 
	 */
	public String refName() {
		return this.refName;
	}
	
	/**
	 * Set Ref Name
	 * 
	 * @param entities
	 */
	public void setRefname(List<GeographicEntity> entities){
		
		String refName = "";
		if(entities.size() == 1){
			refName += entities.get(0).refName();
		}else if(entities.size() > 1){
		
			for(int i=0;i<entities.size();i++){
				if(i==0){
					refName += entities.get(i).refName();
				}else{
					refName += " | "+entities.get(i).refName();
				}
			}
		}
		this.refName = refName;
		
	}
	
	/**
	 * Alternative method to set names
	 * 
	 * @param names
	 */
	public void setRefName(String[] names){
		String refNames = "";
		for(int i = 0; i <names.length;i++){
			if(i==0){
				refNames = names[i];
			}else{
				refNames += " | "+names[i];
			}
		}
		this.refName = refNames;
		
	}
	
	
	/**
	 * Get Target layer name
	 * 
	 */
	public String targetLayerName() {
		return this.targetLayername;
	}
	
	/**
	 * Set Target layer name
	 * 
	 * @param trgLayerPrefix
	 */
	public void setTargetLayername(String trgLayerPrefix) {
		
		String code = this.code();
		
		//control and prevent the presence of "." in targetLayerName
		if(code.contains(".")){
			code = code.replaceAll("[.]", SEPARATOR_DOT_REPLACEMENT);
		}
		
		if (trgLayerPrefix == null | trgLayerPrefix == "") {
			this.targetLayername = code;
		} else {
			this.targetLayername = trgLayerPrefix + SEPARATOR_PREFIX + code;
		}
	}
	
	
	/**
	 * Get Collection prefix
	 * 
	 */
	public String collection() {
		return this.collection;
	}
	
	/**
	 * Get Meta Identifier
	 * 
	 */
	public String metaIdentifier(){
		return this.metaId;
	}
	
	/**
	 * Set Meta Identifier
	 * 
	 * @param entities
	 */
	public void setMetaIdentifier() {
		MetadataContact owner = null;
		for(MetadataContact contact : this.template().getOrganizationContacts()){
			if(contact.getRole().matches("OWNER")){
				owner = contact;
				break;
			}
		}
		this.metaId = Utils.buildMetadataIdentifier(owner.getAcronym(), this.collection, entities);
	}
	
	/**
	 * Get Ref Name
	 * 
	 */
	public String metaTitle() {
		return this.metaTitle;
	}
	
	/**
	 * Set Ref Name
	 * 
	 * @param entities
	 */
	public void setMetaTitle(){
		
		this.metaTitle = "";
		if(this.template().getHasBaseTitle()){
			this.metaTitle += this.template().getBaseTitle();
		}
		this.metaTitle += this.refName;
		
	}
	
	
	/**
	 * Get Object Addins
	 * 
	 * @return
	 */
	public Map<EntityAddin,String> addins(){
		return this.addins;
	}
	
	public void setSpecificProperties(List<GeographicEntity> entities, Map<GeographicMetaObjectProperty, List<String>> objectProperties){
		this.specificProperties = new HashMap<GeographicMetaObjectProperty, List<String>>();
		if(objectProperties != null){
			this.specificProperties.putAll(objectProperties);
		}
		
		for(GeographicEntity entity : entities){
			this.specificProperties.putAll(entity.properties());
		}
		
	}
	
	/** ==============================================
	 *  		    SETTINGS METHODS
	 *  ==============================================
	 */
	
	
	public GemsConfig config() {
		return this.config;
	}

	public MetadataContent template(){
		return this.template;
	}

	public Map<GeographicMetaObjectProperty, List<String>> properties() {
		return this.specificProperties;
	}
	
	/** ==============================================
	 *  		    GIS PROPERTIES METHODS
	 *  ==============================================
	 */
	
	/**
	 * Get Geo properties
	 * 
	 * @return
	 */
	public Map<FeatureTypeProperty, Object> getGeoProperties() {
		return this.geoproperties;
	}

	
	public Envelope geographicExtentActual() {
		Envelope bbox = null;
		if(this.geoproperties != null){
			if (this.geoproperties.containsKey(FeatureTypeProperty.BBOX_ACTUAL)) {
				bbox = (Envelope) this.geoproperties.get(FeatureTypeProperty.BBOX_ACTUAL);
			}
		}
		return bbox;
	}
	
	public Envelope geographicExtentPreview() {
		Envelope bbox = null;
		if(this.geoproperties != null){
			if (this.geoproperties.containsKey(FeatureTypeProperty.BBOX_PREVIEW)) {
				bbox = (Envelope) this.geoproperties.get(FeatureTypeProperty.BBOX_PREVIEW);
			}
		}
		return bbox;
	}

	public int featuresCount() {
		Integer count = 0;
		if(this.geoproperties != null){
			if (this.geoproperties.containsKey(FeatureTypeProperty.COUNT)) {
				count = (Integer) this.geoproperties.get(FeatureTypeProperty.COUNT);
			}
		}
		return count;
	}
	

	public CoordinateReferenceSystem crs() {
		CoordinateReferenceSystem crs = null;
		if(this.geoproperties != null){
			if (this.geoproperties.containsKey(FeatureTypeProperty.CRS)) {
				crs = (CoordinateReferenceSystem) this.geoproperties.get(FeatureTypeProperty.CRS);
			}
		}
		return crs;
	}
	
	public TemporalPrimitive temporalExtent(){
		TemporalPrimitive time = null;
		if(this.geoproperties != null){
			if(this.geoproperties.containsKey(FeatureTypeProperty.TIME)){
				time = (TemporalPrimitive) this.geoproperties.get(FeatureTypeProperty.TIME);
			}
		}
		return time;
	}
	

	/**
	 * Get Layer Graphic Overview
	 * 
	 */
	public URI graphicOverview() {
		return this.graphicOverview;
	}
	
	/**
	 * Method that will handle the layer graphic overview to be appended to the
	 * layer metadata as layer preview
	 * 
	 * @return URI
	 * @throws URISyntaxException
	 */
	public void setGraphicOverview() throws URISyntaxException {

		// compute the image size
		double minX = -180.0;
		double maxX = 180.0;
		double minY = -90.0;
		double maxY = 90.0;
		int width = 600;
		int height = 300;

		Envelope bbox = this.geographicExtentPreview();
		if (bbox != null) {
			minX = bbox.getMinX();
			maxX = bbox.getMaxX();
			minY = bbox.getMinY();
			maxY = bbox.getMaxY();

			// adjust width &height
			double rangeX = maxX - minX;
			double rangeY = maxY - minY;
			double newheight = width * rangeY / rangeX;
			height = (int) Math.round(newheight);

			// check height size and adjust width size if necessary
			if (height > width) {
				width = (int) Math.round(Math.pow(width, 2) / height);
			}

		}

		// build the layer preview URI
		String baselayers = null;
		int i = 0;
		for(BaseLayer baselayer : this.config.getSettings().getGeographicServerSettings().getBaseLayerList()){
			if(i == 0){
				baselayers = baselayer.getWorkspace() +":"+ baselayer.getName();
			}else{
				baselayers += "," + baselayer.getWorkspace() +":"+ baselayer.getName(); 
			}
			i++;
		}

		String completeLayerName = this.config.getSettings().getGeographicServerSettings().getTargetWorkspace() + ":" + this.targetLayername;
		String graphicLink = this.config.getSettings().getGeographicServerSettings().getPublicUrl()
				+ "/wms?service=WMS&version=1.1.0&request=GetMap" + "&layers="
				+ baselayers+"," + completeLayerName + "&bbox=" + minX
				+ "," + minY + "," + maxX + "," + maxY + "&width=" + width
				+ "&height=" + height + "&srs=EPSG:4326" + // for now only
															// EPSG:4326 as
															// projection
				"&format=image%2Fpng";

		this.graphicOverview = new URI(graphicLink);
	}

}
