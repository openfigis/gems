package org.fao.fi.gems.association;

import org.fao.fi.gems.authority.AuthorityEntity;

public interface GeographicMetaObjectProperty {

	AuthorityEntity authority();

	boolean isThesaurus();

	boolean containsURIs();
}
