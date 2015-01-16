package org.fao.fi.gems.collection.vme;
import org.fao.fi.gems.entity.EntityAddin;
import org.fao.fi.gems.entity.EntityAuthority;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;

/**
 * Vme Property enumeration
 * (to use in Vme codelists)
 * 
 * @author eblondel
 *
 */
public enum VmeProperty implements GeographicMetaObjectProperty {

	FAO(EntityAuthority.FAO, true, true, true), FIGIS(EntityAuthority.FIGIS,
			true, true, false),

	VME("VME", false, true, false), GLOBALTYPE("GLOBALTYPE", false, false,
			false), BASETITLE(EntityAddin.BASETITLE, false, false, false), STYLE(
			EntityAddin.STYLE, false, false, false);

	private final Object object;
	private final boolean isAuthority;
	private final boolean isThesaurus;
	private final boolean containsURIs;

	VmeProperty(Object object, boolean isAuthority, boolean isThesaurus,
			boolean containsURIs) {
		this.object = object;
		this.isAuthority = isAuthority;
		this.isThesaurus = isThesaurus;
		this.containsURIs = containsURIs;
	}

	public Object getObject() {
		return this.object;
	}

	public boolean isAuthority() {
		return this.isAuthority;
	}

	public boolean isThesaurus() {
		return this.isThesaurus;
	}

	public boolean containsURIs() {
		return this.containsURIs;
	}

}	