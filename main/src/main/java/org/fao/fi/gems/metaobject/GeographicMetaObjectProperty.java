package org.fao.fi.gems.metaobject;

public interface GeographicMetaObjectProperty {

	Object getObject();
	
	boolean isAuthority();
	
	boolean isThesaurus();

	boolean containsURIs();
}
