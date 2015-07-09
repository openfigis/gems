package org.fao.fi.gems.collection.properties;

import org.fao.fi.gems.entity.EntityAddin;
import org.fao.fi.gems.entity.EntityAuthority;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;

/**
 * Species Property enumeration
 * (to use in Species codelists)
 * 
 * @author eblondel
 *
 */
public enum SpeciesProperty implements GeographicMetaObjectProperty{
	
	FAO(EntityAuthority.FAO, true, true, true),
	FLOD (EntityAuthority.FLOD, true, true, true),
	FIGIS (EntityAuthority.FIGIS, true, true, false),
	ASFIS (EntityAuthority.ASFIS, true, true, false),
	WORMS (EntityAuthority.WORMS, true, true, false),
	
	STYLE(EntityAddin.STYLE, false, false, false),
	HABITAT(EntityAddin.HABITAT, false, false, false);

	private final Object object;
	private final boolean isAuthority;
	private final boolean isThesaurus;
	private final boolean containsURIs;
	
	SpeciesProperty(Object object, boolean isAuthority, boolean isThesaurus, boolean containsURIs){
		this.object = object;
		this.isAuthority = isAuthority;
		this.isThesaurus = isThesaurus;
		this.containsURIs = containsURIs;
	}
	
	public Object getObject(){
		return this.object;
	}
	
	public boolean isAuthority(){
		return this.isAuthority;
	}
	
	public boolean isThesaurus(){
		return this.isThesaurus;
	}

	public boolean containsURIs() {
		return this.containsURIs;
	}
	
}	