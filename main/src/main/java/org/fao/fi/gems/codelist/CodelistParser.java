/**
 * (c) 2015 FAO / UN (project: gems-main)
 */
package org.fao.fi.gems.codelist;

import java.util.Set;

import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.model.GemsConfig;

/**
 * CodelistParser interface
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public interface CodelistParser {
	
	public Set<GeographicEntity> getCodelist(GemsConfig config);

}
