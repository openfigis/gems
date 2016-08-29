/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings.publication;

import java.util.LinkedList;

/**
 * EntityList
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class EntityList {
	
	private LinkedList<String> include;
	private LinkedList<String> exclude;
	
	
	public EntityList(){}


	public LinkedList<String> getInclude() {
		return include;
	}


	public void setInclude(LinkedList<String> include) {
		this.include = include;
	}


	public LinkedList<String> getExclude() {
		return exclude;
	}


	public void setExclude(LinkedList<String> exclude) {
		this.exclude = exclude;
	};
	

}
