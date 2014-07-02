package org.fao.fi.gems.model.settings;

import java.io.File;
import java.io.IOException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Configures the settings
 * 
 * @author eblondel
 *
 */
public class Settings{
	
	private GeographicServerSettings geographicServerSettings;
	private MetadataCatalogueSettings metadataCatalogueSettings;
	private PublicationSettings publicationSettings;
	
	/**
	 * Constructor
	 * 
	 */
	public Settings(){
		
	}

	/**
	 * @return the geographicServerSettings
	 */
	public GeographicServerSettings getGeographicServerSettings() {
		return geographicServerSettings;
	}

	/**
	 * @param geographicServerSettings the geographicServerSettings to set
	 */
	public void setGeographicServerSettings(GeographicServerSettings geographicServerSettings) {
		this.geographicServerSettings = geographicServerSettings;
	}

	/**
	 * @return the metadataCatalogueSettings
	 */
	public MetadataCatalogueSettings getMetadataCatalogueSettings() {
		return metadataCatalogueSettings;
	}

	/**
	 * @param metadataCatalogueSettings the metadataCatalogueSettings to set
	 */
	public void setMetadataCatalogueSettings(MetadataCatalogueSettings metadataCatalogueSettings) {
		this.metadataCatalogueSettings = metadataCatalogueSettings;
	}

	/**
	 * @return the publicationSettings
	 */
	public PublicationSettings getPublicationSettings() {
		return publicationSettings;
	}

	/**
	 * @param publicationSettings the publicationSettings to set
	 */
	public void setPublicationSettings(PublicationSettings publicationSettings) {
		this.publicationSettings = publicationSettings;
	}
	
	
	/**
	 * Parsing from XML
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Settings fromXML(File file) {

		XStream xstream = new XStream(new StaxDriver());
		xstream.aliasType("settings", Settings.class);
		xstream.aliasType("baseLayer", BaseLayer.class);

		Settings settings = (Settings) xstream
				.fromXML(file);

		return settings;
	}
	

}
