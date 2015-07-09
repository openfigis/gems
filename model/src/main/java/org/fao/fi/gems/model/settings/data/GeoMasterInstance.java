/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings.data;

/**
 * GeoMasterInstance
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class GeoMasterInstance extends GeoInstance{
	
	private String version;
	
	public GeoMasterInstance(){
		super();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
