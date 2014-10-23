package org.fao.fi.gems.model;

import java.io.File;
import java.io.IOException;

import org.fao.fi.gems.model.content.MetadataBiblioRef;
import org.fao.fi.gems.model.content.MetadataContact;
import org.fao.fi.gems.model.content.MetadataContent;
import org.fao.fi.gems.model.content.MetadataThesaurus;
import org.fao.fi.gems.model.settings.BaseLayer;
import org.fao.fi.gems.model.settings.GeoWorkerInstance;
import org.fao.fi.gems.model.settings.Settings;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;


/**
 * Main Metadata configuration
 * 
 * @author eblondel
 *
 */
public class MetadataConfig {
	
	private Settings settings;
	private MetadataContent content;
	
	/**
	 * Constructs a MetadataConfig
	 * 
	 */
	public MetadataConfig(){
		
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
	public static MetadataConfig fromXML(File file) {

		XStream xstream = new XStream(new StaxDriver());
		xstream.alias("configuration", MetadataConfig.class);
		
		//settings
		xstream.alias("worker", GeoWorkerInstance.class);
		xstream.alias("baseLayer", BaseLayer.class);
		
		//content
		xstream.alias("contact", MetadataContact.class);
		xstream.alias("thesaurus", MetadataThesaurus.class);
		xstream.alias("biblioRef", MetadataBiblioRef.class);

		MetadataConfig config = (MetadataConfig) xstream
				.fromXML(file);

		return config;
	}
	
}
