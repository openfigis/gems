/**
 * (c) 2015 FAO / UN (project: gems-main)
 */
package org.fao.fi.gems.entity;

import java.util.List;
import java.util.Map;

import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;

/**
 * GeographicEntity interface
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public interface GeographicEntity {

	List<EntityCode> codeStack();
	
	String code();

	String refName();
	
	String owner();
	
	String collection();
	
	String metaIdentifier();

	Map<GeographicMetaObjectProperty, List<String>> properties();

}
