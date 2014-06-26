package org.fao.fi.gems.publisher;

import java.net.MalformedURLException;
import org.fao.fi.gems.association.GeographicMetaObject;
import org.fao.fi.gems.model.MetadataConfig;
import org.fao.fi.gems.model.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data & Metadata Publisher
 * 
 * @author eblondel (FAO)
 *
 */
public class Publisher {
	
	private static Logger LOGGER = LoggerFactory.getLogger(Publisher.class);
	
	private Settings settings;
	DataPublisher dataPublisher;
	MetadataPublisher metadataPublisher;
	

	/**
	 * Publisher
	 * 
	 * @param config
	 * @throws MalformedURLException
	 */
	public Publisher(MetadataConfig config) throws MalformedURLException{
	
		settings = config.getSettings();
		dataPublisher = new DataPublisher(config.getSettings().getGeographicServerSettings(),
										  config.getSettings().getMetadataCatalogueSettings());
		
		metadataPublisher = new MetadataPublisher(config.getSettings().getMetadataCatalogueSettings(),
												  config.getSettings().getPublicationSettings());
	}
	
	
	public DataPublisher getDataPublisher(){
		return this.dataPublisher;
	}
	
	public MetadataPublisher getMetadataPublisher(){
		return this.metadataPublisher;
	}
	
	/**
	 * Method to publish
	 * 
	 * @param object
	 * @param style
	 * @param exist
	 * @throws Exception
	 */
	public void publish(GeographicMetaObject object, String style, boolean exist) throws Exception {

		if (exist) {

			// force data publication
			if (this.settings.getPublicationSettings().isForceMetadata()) {
				try {
					this.getMetadataPublisher().deleteMetadata(object);
				} catch (Exception e) {
					LOGGER.warn("No metadata for id = "+object.getMetaIdentifier());
				}

				this.getMetadataPublisher().publishMetadata(object);

			}

			if (this.settings.getPublicationSettings().isForceData() == true) {
				this.getDataPublisher().deleteLayer(object);
				this.getDataPublisher().publishLayer(
						object,
						style,
						PublicationMethod.valueOf(this.settings.getGeographicServerSettings().getMethod()),
						this.settings.getGeographicServerSettings().getShapefileURL()
						);
			}

		} else {
			LOGGER.info("Publish new layer");
			this.getMetadataPublisher().publishMetadata(object);
			this.getDataPublisher().publishLayer(
					object,
					style,
					PublicationMethod.valueOf(this.settings.getGeographicServerSettings().getMethod()),
					this.settings.getGeographicServerSettings().getShapefileURL());

		}

		int sleep = 3;
		Thread.sleep(3*1000);
		LOGGER.info("Sleeping "+sleep+" seconds");
		
	}

	/**
	 * Method to unpublish
	 * 
	 * @param object
	 * @param exist
	 * @throws Exception
	 */
	public void unpublish(GeographicMetaObject object, boolean exist) throws Exception {

		if (this.settings.getPublicationSettings().isUnpublishData()) {
			if (!exist) {
				this.getDataPublisher().deleteOnlyFeatureType(object);

			} else {
				this.getDataPublisher().deleteLayer(object);
			}
		}

		if (this.settings.getPublicationSettings().isUnpublishMetadata()) {
			try {
				this.getMetadataPublisher().deleteMetadata(object);
			} catch (Exception e) {
				LOGGER.warn("metadata was yet deleted");
			}

		}

		int sleep = 3;
		Thread.sleep(3*1000);
		LOGGER.info("Sleeping "+sleep+" seconds");
	}

}
