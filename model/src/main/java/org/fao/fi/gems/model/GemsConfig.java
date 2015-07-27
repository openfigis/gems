/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model;

import java.io.File;
import java.io.IOException;

import org.fao.fi.gems.model.content.MetadataBiblioRef;
import org.fao.fi.gems.model.content.MetadataContact;
import org.fao.fi.gems.model.content.MetadataContent;
import org.fao.fi.gems.model.content.MetadataResource;
import org.fao.fi.gems.model.content.MetadataThesaurus;
import org.fao.fi.gems.model.settings.Settings;
import org.fao.fi.gems.model.settings.data.BaseLayer;
import org.fao.fi.gems.model.settings.data.GeoWorkerInstance;
import org.fao.fi.gems.model.settings.data.filter.DataObjectFilter;
import org.fao.fi.gems.model.settings.data.filter.ExtraDataFilter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;


/**
 * Main GEMS configuration
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class GemsConfig {
	
	private String scope;
	private Settings settings;
	private MetadataContent content;
	
	/**
	 * Constructs a MetadataConfig
	 * 
	 */
	public GemsConfig(){
		
	}
	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	
	/**
	 * @return the settings
	 */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * @param settings the settings to set
	 */
	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	/**
	 * @return the content
	 */
	public MetadataContent getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(MetadataContent content) {
		this.content = content;
	}

	/**
	 * Parsing from XML
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static GemsConfig fromXML(File file) {

		XStream xstream = new XStream(new StaxDriver());
		xstream.alias("configuration", GemsConfig.class);
		
		//settings
		xstream.alias("worker", GeoWorkerInstance.class);
		xstream.aliasType("filter", DataObjectFilter.class);
		xstream.aliasType("extra", ExtraDataFilter.class);
		xstream.alias("baseLayer", BaseLayer.class);
		
		//content
		xstream.alias("contact", MetadataContact.class);
		xstream.alias("thesaurus", MetadataThesaurus.class);
		xstream.alias("onlineResource", MetadataResource.class);
		xstream.alias("biblioRef", MetadataBiblioRef.class);

		GemsConfig config = (GemsConfig) xstream
				.fromXML(file);

		return config;
	}
	
}
