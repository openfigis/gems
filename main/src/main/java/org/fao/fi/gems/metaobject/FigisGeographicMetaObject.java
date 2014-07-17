package org.fao.fi.gems.metaobject;

import java.net.URI;

/**
 * A sub interface for FIGIS-specific Geographic meta objects
 * 
 * @author eblondel
 *
 */
public interface FigisGeographicMetaObject extends GeographicMetaObject{
	
	URI getFigisViewerResource();
	
	String getFigisFactsheet();

}
