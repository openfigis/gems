package org.fao.fi.gems.codelist;

import java.util.Set;

import org.fao.fi.gems.entity.GeographicEntity;

/**
 * CodelistParser interface
 * 
 * @author eblondel
 *
 */
public interface CodelistParser {
	
	public Set<GeographicEntity> getCodelist(String owner, String collection, String url);

}
