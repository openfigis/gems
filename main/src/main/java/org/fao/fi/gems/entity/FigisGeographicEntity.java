/**
 * (c) 2015 FAO / UN (project: gems-main)
 */
package org.fao.fi.gems.entity;

/**
 * FIGIS-specific subinterface of GeographicEntity
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public interface FigisGeographicEntity extends GeographicEntity{

	String getFigisDomain();
	
	String getFigisId();

	String getFigisViewerId();
}
