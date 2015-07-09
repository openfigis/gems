/**
 * (c) 2015 FAO / UN (project: gems-main)
 */
package org.fao.fi.gems.metaobject;

import java.net.URI;

/**
 * A sub interface for FIGIS-specific Geographic meta objects
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public interface FigisGeographicMetaObject extends GeographicMetaObject{
	
	URI getFigisViewerResource();
	
	String getFigisFactsheet();

}
