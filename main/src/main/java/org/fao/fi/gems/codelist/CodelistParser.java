package org.fao.fi.gems.codelist;

import java.util.Set;

import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.model.GemsConfig;

/**
 * CodelistParser interface
 * 
 * @author eblondel
 *
 */
public interface CodelistParser {
	
	public Set<GeographicEntity> getCodelist(GemsConfig config);

}
