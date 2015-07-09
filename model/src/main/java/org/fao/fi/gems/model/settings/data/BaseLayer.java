/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings.data;

/**
 * BaseLayer model
 * 
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class BaseLayer {

	private String workspace;
	private String name;

	public BaseLayer() {
	}
	
	/**
	 * @return the workspace
	 */
	public String getWorkspace() {
		return workspace;
	}



	/**
	 * @param workspace the workspace to set
	 */
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}



	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	
}
