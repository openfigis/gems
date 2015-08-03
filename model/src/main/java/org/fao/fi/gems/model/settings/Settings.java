/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings;

import java.io.File;
import java.io.IOException;

import org.fao.fi.gems.model.settings.data.BaseLayer;
import org.fao.fi.gems.model.settings.data.GeoWorkerInstance;
import org.fao.fi.gems.model.settings.data.GeographicServerSettings;
import org.fao.fi.gems.model.settings.data.filter.DataObjectFilter;
import org.fao.fi.gems.model.settings.data.filter.ExtraDataFilter;
import org.fao.fi.gems.model.settings.metadata.MetadataCatalogueSettings;
import org.fao.fi.gems.model.settings.publication.PublicationSettings;
import org.fao.fi.gems.model.settings.validation.ValidationSettings;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Configures the settings
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class Settings{
	
	private GeographicServerSettings geographicServerSettings;
	private MetadataCatalogueSettings metadataCatalogueSettings;
	private PublicationSettings publicationSettings;
	private ValidationSettings validationSettings;
	
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
	 * @return the validationSettings
	 */
	public ValidationSettings getValidationSettings() {
		return validationSettings;
	}

	/**
	 * @param validationSettings the validationSettings to set
	 */
	public void setValidationSettings(ValidationSettings validationSettings) {
		this.validationSettings = validationSettings;
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
		xstream.aliasType("worker", GeoWorkerInstance.class);
		xstream.aliasType("filter", DataObjectFilter.class);
		xstream.aliasType("extra", ExtraDataFilter.class);
		xstream.aliasType("baseLayer", BaseLayer.class);

		Settings settings = (Settings) xstream
				.fromXML(file);

		return settings;
	}
	

}
