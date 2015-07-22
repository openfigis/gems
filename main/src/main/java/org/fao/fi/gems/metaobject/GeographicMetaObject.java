/**
 * (c) 2015 FAO / UN (project: gems-main)
 */
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

/**
 * Geographic Meta Object
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public interface GeographicMetaObject {

	/*
	 * Identification methods
	 */
	
	List<GeographicEntity> entities();
	
	String code();
	
	String refName();
	
	String targetLayerName();
	
	String metaIdentifier();
	
	String metaParentIdentifier();
	
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