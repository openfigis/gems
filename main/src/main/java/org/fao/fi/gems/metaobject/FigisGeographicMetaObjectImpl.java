/**
 * (c) 2015 FAO / UN (project: gems-main)
 */
package org.fao.fi.gems.metaobject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.sis.metadata.iso.extent.DefaultTemporalExtent;
import org.fao.fi.gems.entity.FigisGeographicEntity;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.entity.FigisGeographicEntityImpl;
import org.fao.fi.gems.feature.FeatureTypeProperty;
import org.fao.fi.gems.model.GemsConfig;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.vividsolutions.jts.geom.Envelope;

/**
 * A FIGIS-specific implementation of a GeographicMetaObject
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class FigisGeographicMetaObjectImpl extends GeographicMetaObjectImpl implements FigisGeographicMetaObject{
	
	static final String DEFAULT_FACTSHEET_TITLE = "Factsheet - Summary description";
	
	private String figisViewerResourceBaseUrl;
	private String figisWebResourceBaseUrl;
	private URI viewerResourceUrl;
	private String webResourceUrl;
	private String webResourceTitle;
	private boolean hasFactsheet;
	
	/**
	 * Constructs a FIGIS-specific GeographicMetaObject
	 * 
	 * @param entities
	 * @param objectProperties
	 * @param geoproperties
	 * @param config
	 * @throws URISyntaxException
	 */
	public FigisGeographicMetaObjectImpl(List<GeographicEntity> entities,
			Map<GeographicMetaObjectProperty, List<String>> objectProperties,
			Map<FeatureTypeProperty, Object> geoproperties,
			GemsConfig config) throws URISyntaxException {
		super(entities, objectProperties, geoproperties, config);
		
		this.figisViewerResourceBaseUrl = config.getSettings().getPublicationSettings().getFigisViewerResourceBaseUrl();
		this.figisWebResourceBaseUrl = config.getSettings().getPublicationSettings().getFigisWebResourceBaseUrl();
		this.webResourceTitle = config.getSettings().getPublicationSettings().getFigisWebResourceTitle();
		this.hasFactsheet = config.getSettings().getPublicationSettings().figisHasFactsheet();
		this.setFigisViewerResource();
		this.setFigisWebResource();
		
	}

	
	/**
	 * Get FIGIS Viewer Resource (specific to FAO)
	 * 
	 * @return the URI for the FIGIS map viewer
	 * @throws URISyntaxException
	 */
	private void setFigisViewerResource() throws URISyntaxException {
		
		this.viewerResourceUrl = null;
		
		String resource = null;
		if(this.collection != null && this.figisViewerResourceBaseUrl != null){
			
			Envelope bbox = this.geographicExtentPreview();
			String figisDomain = ((FigisGeographicEntityImpl) this.entities.get(0)).getFigisDomain();
			
			if (figisDomain != null && bbox != null) {
				
				if (this.collection().matches("vme")){
					
					//get last year
					DefaultTemporalExtent temporalExtent = new DefaultTemporalExtent();
					temporalExtent.setExtent(this.temporalExtent());
					
					DateTime jtime = new DateTime(temporalExtent.getEndTime().getTime(), DateTimeZone.UTC);
					String lastYear = jtime.year().getAsString();
					
					//build xy center
					double centerX = (bbox.getMaxX() + bbox.getMinX()) / 2;
					double centerY = (bbox.getMaxY() + bbox.getMinY()) / 2;
					
					// build the link
					String globalType = null;
					for(GeographicMetaObjectProperty prop : this.specificProperties.keySet()){
						if(prop.toString().matches("GLOBALTYPE")){
							globalType = this.specificProperties.get(prop).get(0);
							break;
						}
					}
					String layerOfInterest = null;
					if(globalType.matches("VME")) layerOfInterest = "VME closed areas";
					if(globalType.matches("BTM_FISH")) layerOfInterest = "Bottom fishing areas";
					if(globalType.matches("OTHER")) layerOfInterest = "Other access regulated areas";
					
					resource = this.figisViewerResourceBaseUrl + "/vme-db/?"
							+ "embed=true"
							+ "&extent=" + bbox.getMinX() + "," + bbox.getMinY() + ","
										 + bbox.getMaxX() + "," + bbox.getMaxY()
							+ "&center=" + centerX + "," + centerY
							+ "&prj=4326"
							+ "&year=" + lastYear
							+ "&rfb=" + this.owner
							+ "&layers=Oceans imagery;"+layerOfInterest+";UN_CONTINENT2;Gebco Undersea Features";
					resource = resource.replaceAll(" ", "%20");
				} else {
					if(((FigisGeographicEntityImpl) this.entities.get(0)).getFigisViewerId() != null){
						resource = this.figisViewerResourceBaseUrl+ "/" + figisDomain
								+ ".html?" + this.collection + "=" + ((FigisGeographicEntityImpl) this.entities.get(0)).getFigisViewerId()
								+ "&extent=" + bbox.getMinX() + "," + bbox.getMinY() + ","
								+ bbox.getMaxX() + "," + bbox.getMaxY() + "&prj=4326";		
					}		
				}
	
				if (resource != null) {
					this.viewerResourceUrl = new URI(resource);
				}
			}
		}
	}

	public URI getFigisViewerResourceUrl() {
		return this.viewerResourceUrl;
	}

	/**
	 * Set Figis Factsheet URL
	 * 
	 */
	public void setFigisWebResource() {

		this.webResourceUrl = null;
		if (this.collection != null && this.figisWebResourceBaseUrl != null){
			
			FigisGeographicEntity entity = (FigisGeographicEntity) this.entities.get(0);

			if(entity != null) {
				
				String figisViewerDomain = this.collection;
				
				if(this.hasFactsheet && entity.getFigisId() != null){
					
					if(this.webResourceTitle == null) this.webResourceTitle = DEFAULT_FACTSHEET_TITLE;
				
					this.webResourceUrl = this.figisWebResourceBaseUrl
							+ "/" + figisViewerDomain
							+ "/" + entity.getFigisId();
					
				}else{
					if(figisViewerDomain.equalsIgnoreCase("fsa") || figisViewerDomain.equalsIgnoreCase("fsa-nested")){
						
						this.webResourceUrl = this.figisWebResourceBaseUrl + "/Area" + entity.code().substring(0,2);
						if(entity.code().length() > 2){					
							this.webResourceUrl += "#codearea-" + entity.code();
						}
					}
				}
			}
		}
	}
	
	/**
	 * Get Figis Web Resource URL
	 * 
	 */
	public String getFigisWebResourceUrl(){
		return this.webResourceUrl;
	}
	
	/**
	 * Get Figis Web Resource Title
	 */
	public String getFigisWebResourceTitle(){
		return this.webResourceTitle;
	}
	

}
