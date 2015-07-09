/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings.data.filter;

/**
 * A basic data filter
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public abstract class DataFilter {
	
	private String property;
	private boolean isString = false;
	
	public DataFilter(){
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public boolean getIsString() {
		return isString;
	}
	

	public void setIsString(boolean isString) {
		this.isString = isString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isString ? 1231 : 1237);
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
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
		DataFilter other = (DataFilter) obj;
		if (isString != other.isString)
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		return true;
	}



}
