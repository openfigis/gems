/**
 * (c) 2015 FAO / UN (project: gems-main)
 */
package org.fao.fi.gems.metaobject;

/**
 * GeographicMetaObjectProperty
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public interface GeographicMetaObjectProperty {

	Object getObject();
	
	boolean isAuthority();
	
	boolean isThesaurus();

	boolean containsURIs();
}
