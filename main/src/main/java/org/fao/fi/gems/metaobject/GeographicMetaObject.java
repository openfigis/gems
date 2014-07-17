package org.fao.fi.gems.metaobject;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.model.content.MetadataContent;
import org.fao.fi.gems.model.settings.GeographicServerSettings;
import org.fao.fi.gems.model.settings.MetadataCatalogueSettings;
import org.fao.fi.gems.model.settings.PublicationSettings;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.temporal.TemporalPrimitive;

import com.vividsolutions.jts.geom.Envelope;

public interface GeographicMetaObject {

	/*
	 * Identification methods
	 */
	
	List<GeographicEntity> getEntities();
	
	String getCode();
	
	String getRefName();
	
	String getMetaIdentifier();
	
	String getMetaTitle();
	
	MetadataContent getTemplate();
	
	Map<GeographicMetaObjectProperty, List<String>> getSpecificProperties();
	
	/*
	 * Settings methods
	 */

	GeographicServerSettings getGeographicServerSettings();
	
	MetadataCatalogueSettings getMetadataCatalogueSettings();
	
	PublicationSettings getPublicationSettings();
	
	String getTargetLayerName();
	
	/*
	 * GIS methods
	 */

	int getFeaturesCount();
	
	CoordinateReferenceSystem getCRS();
	
	Envelope getActualBBOX();
	
	Envelope getPreviewBBOX();
	
	TemporalPrimitive getTIME();

	URI getLayerGraphicOverview();

}