package org.fao.fi.gems.model.settings;

/**
 * BaseLayer model
 * 
 * 
 * @author eblondel
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
