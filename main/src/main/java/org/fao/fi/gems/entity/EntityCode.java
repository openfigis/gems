/**
 * (c) 2015 FAO / UN (project: gems-main)
 */
package org.fao.fi.gems.entity;

import org.fao.fi.gems.model.settings.data.filter.DataObjectFilter;

/**
 * EntityCode
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class EntityCode {

	private DataObjectFilter filter;
	private String code;
	
	public EntityCode(){
	}
	
	public EntityCode(DataObjectFilter filter, String code){
		this.filter = filter;
		this.code = code;
	}

	public DataObjectFilter getFilter() {
		return filter;
	}

	public void setFilter(DataObjectFilter filter) {
		this.filter = filter;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityCode other = (EntityCode) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (filter == null) {
			if (other.filter != null)
				return false;
		} else if (!filter.equals(other.filter))
			return false;
		return true;
	}

	
}
