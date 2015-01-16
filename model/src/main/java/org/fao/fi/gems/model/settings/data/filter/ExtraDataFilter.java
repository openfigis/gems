package org.fao.fi.gems.model.settings.data.filter;

/**
 * an Extra data filter that can be specified at
 * configuration level
 * 
 * @author eblondel
 *
 */
public class ExtraDataFilter extends DataFilter{

	private String value;
	
	public ExtraDataFilter(){
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
