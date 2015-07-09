/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings.data.filter;

/**
 * an Extra data filter that can be specified at
 * configuration level
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
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
