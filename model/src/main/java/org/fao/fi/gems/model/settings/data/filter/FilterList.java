package org.fao.fi.gems.model.settings.data.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * A class giving a list of filters including an instance of PropertyFilter
 * and a list of DataFilter.
 * 
 * @author eblondel
 *
 */
public class FilterList {
	

	private List<DataObjectFilter> data = new ArrayList<DataObjectFilter>();
	private List<ExtraDataFilter> extras = new ArrayList<ExtraDataFilter>();
	private List<String> properties = new ArrayList<String>();
	
	public FilterList(){
	}

	public List<DataObjectFilter> getData() {
		return data;
	}

	public void setData(List<DataObjectFilter> data) {
		this.data = data;
	}

	public List<ExtraDataFilter> getExtras() {
		return extras;
	}

	public void setExtras(List<ExtraDataFilter> extra) {
		this.extras = extra;
	}

	public List<String> getProperties() {
		return properties;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}
	
}
