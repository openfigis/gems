package org.fao.fi.gems.metaobject;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.content.MetadataContent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.temporal.TemporalPrimitive;

import com.vividsolutions.jts.geom.Envelope;

public interface GeographicMetaObject {

	/*
	 * Identification methods
	 */
	
	List<GeographicEntity> entities();
	
	String code();
	
	String refName();
	
	String targetLayerName();
	
	String metaIdentifier();
	
	String metaTitle();
	
	MetadataContent template();
	
	Map<GeographicMetaObjectProperty, List<String>> properties();
	
	/*
	 * Settings methods
	 */

	GemsConfig config();

	
	/*
	 * GIS methods
	 */

	int featuresCount();
	
	CoordinateReferenceSystem crs();
	
	Envelope geographicExtentActual();
	
	Envelope geographicExtentPreview();
	
	TemporalPrimitive temporalExtent();

	URI graphicOverview();

}