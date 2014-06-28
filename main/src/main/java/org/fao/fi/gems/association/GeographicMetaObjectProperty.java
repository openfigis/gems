package org.fao.fi.gems.association;

public interface GeographicMetaObjectProperty {

	Object getObject();
	
	boolean isAuthority();
	
	boolean isThesaurus();

	boolean containsURIs();
}
