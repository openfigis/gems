package org.fao.fi.gems.entity;

import java.util.List;
import java.util.Map;

import org.fao.fi.gems.association.GeographicMetaObjectProperty;
import org.fao.fi.gems.model.MetadataConfig;

/**
 * GeographicEntity interface
 * 
 * @author eblondel
 *
 */
public interface GeographicEntity {

	//identification
	
	String getCode();

	String getRefName();
	
	String getOwner();
	
	String getCollection();
	
	String getMetaIdentifier();

	Map<GeographicMetaObjectProperty, List<String>> getSpecificProperties();
	
	//specific to FIGIS
	
	String getFigisDomain();
	
	String getFigisId();

	String getFigisViewerId();

}
