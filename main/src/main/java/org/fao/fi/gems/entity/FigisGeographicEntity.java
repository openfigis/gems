package org.fao.fi.gems.entity;

/**
 * FIGIS-specific subinterface of GeographicEntity
 * 
 * @author eblondel
 *
 */
public interface FigisGeographicEntity extends GeographicEntity{

	String getFigisDomain();
	
	String getFigisId();

	String getFigisViewerId();
}
