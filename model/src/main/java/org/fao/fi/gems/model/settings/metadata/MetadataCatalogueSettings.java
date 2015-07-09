/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings.metadata;

import java.io.File;
import java.io.IOException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * 
 * Handles the configuration of the geographic server
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class MetadataCatalogueSettings{
	
	private String url;
	private String user;
	private String password;
	
	public MetadataCatalogueSettings(){}
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}


	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}


	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}


	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}


	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}


	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}


	/**
	 * Parsing from XML
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static MetadataCatalogueSettings fromXML(File file){
			
		XStream xstream = new XStream(new StaxDriver());
		xstream.aliasType("MetadataCatalogue", MetadataCatalogueSettings.class);
		
		MetadataCatalogueSettings settings = (MetadataCatalogueSettings) xstream.fromXML(file);
			
        return settings;
	}
	
}
