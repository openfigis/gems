package org.fao.fi.gems.metaobject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.entity.FigisGeographicEntityImpl;
import org.fao.fi.gems.feature.FeatureTypeProperty;
import org.fao.fi.gems.model.MetadataConfig;
import org.geotoolkit.metadata.iso.extent.DefaultTemporalExtent;

import com.vividsolutions.jts.geom.Envelope;

/**
 * A FIGIS-specific implementation of a GeographicMetaObject
 * 
 * @author eblondel
 *
 */
public class FigisGeographicMetaObjectImpl extends GeographicMetaObjectImpl implements FigisGeographicMetaObject{

	private String figisFactsheetUrl;
	private URI viewerResource;
	private String factsheet;
	
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
			MetadataConfig config) throws URISyntaxException {
		super(entities, objectProperties, geoproperties, config);
		
		this.figisFactsheetUrl = config.getSettings().getPublicationSettings().getFigisFactsheetUrl();
		this.setFigisViewerResource();
		this.setFigisFactsheet();
		
	}

	
	/**
	 * Get FIGIS Viewer Resource (specific to FAO)
	 * 
	 * @return the URI for the FIGIS map viewer
	 * @throws URISyntaxException
	 */
	private void setFigisViewerResource() throws URISyntaxException {
		
		this.viewerResource = null;		
		
		Envelope bbox = this.getPreviewBBOX();
		String figisDomain = ((FigisGeographicEntityImpl) this.entities.get(0)).getFigisDomain();
		
		String resource = null;
		if (figisDomain != null && this.collection != null && bbox != null) {
			
			if (this.getCollection().matches("vme")){
				
				//get last year
				DefaultTemporalExtent temporalExtent = new DefaultTemporalExtent();
				temporalExtent.setExtent(this.getTIME());
				String lastYear = temporalExtent.getEndTime().toString().substring(0, 4);
				
				//build xy center
				double centerX = (bbox.getMaxX() + bbox.getMinX()) / 2;
				double centerY = (bbox.getMaxY() + bbox.getMinY()) / 2;
				
				// build the link
				resource = this.figisFactsheetUrl + "/vme-db/?"
						+ "embed=true"
						+ "&extent=" + bbox.getMinX() + "," + bbox.getMinY() + ","
									 + bbox.getMaxX() + "," + bbox.getMaxY()
						+ "&center=" + centerX + "," + centerY
						+ "&prj=4326"
						+ "&year=" + lastYear;
						
			} else {
				if(((FigisGeographicEntityImpl) this.entities.get(0)).getFigisViewerId() != null){
					resource = this.gsSettings.getUrl()+"/factsheets/" + figisDomain
							+ ".html?" + this.collection + "=" + ((FigisGeographicEntityImpl) this.entities.get(0)).getFigisViewerId()
							+ "&extent=" + bbox.getMinX() + "," + bbox.getMinY() + ","
							+ bbox.getMaxX() + "," + bbox.getMaxY() + "&prj=4326";		
				}		
			}

			if (resource != null) {
				this.viewerResource = new URI(resource);
			}
		}
	}

	public URI getFigisViewerResource() {
		return this.viewerResource;
	}

	/**
	 * Set Figis Factsheet URL
	 * 
	 */
	public void setFigisFactsheet() {

		this.factsheet = null;
		if (this.collection != null && this.figisFactsheetUrl != null
				&& this.entities != null) {
			String figisViewerDomain = this.collection;
			this.factsheet = this.figisFactsheetUrl + "/" + figisViewerDomain
					+ "/" + ((FigisGeographicEntityImpl) this.entities.get(0)).getFigisId();
		}
	}
	
	/**
	 * Get Figis Factsheet URL
	 * 
	 */
	public String getFigisFactsheet(){
		return this.factsheet;
	}
	

}
