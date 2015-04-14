package org.fao.fi.gems.model.settings.publication;

import java.util.List;

public class EntityList {
	
	private List<String> include;
	private List<String> exclude;
	
	
	public EntityList(){}


	public List<String> getInclude() {
		return include;
	}


	public void setInclude(List<String> include) {
		this.include = include;
	}


	public List<String> getExclude() {
		return exclude;
	}


	public void setExclude(List<String> exclude) {
		this.exclude = exclude;
	};
	

}
