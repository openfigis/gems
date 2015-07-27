/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.content;

/**
 * Metadata Resource
 * Class to handle a metadata online resource common to all metadata
 * generated with a GEMS configuration template.
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class MetadataResource {

	private String name;
	private String url;
	
	/**
	 * Metadata Resource constructor
	 */
	public MetadataResource(){};
	
	
	/**
	 * Gets the metadata online resource name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * Set the metadata online resource name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * Gets the metadata online resource url
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}
	
	
	/**
	 * Set the metadata online resource url
	 * 
	 * @param name
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
}
