/**
 * (c) 2015 FAO / UN (project: gems-publisher)
 */
package org.fao.fi.gems.publisher;

import java.net.MalformedURLException;

import org.fao.fi.gems.metaobject.GeographicMetaObject;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.settings.Settings;
import org.fao.fi.gems.model.settings.data.GemsMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data & Metadata Publisher
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class Publisher {
	
	private static Logger LOGGER = LoggerFactory.getLogger(Publisher.class);
	
	private Settings settings;
	private int sleep = 1;
	
	DataPublisher dataPublisher;
	MetadataPublisher metadataPublisher;
	

	/**
	 * Publisher
	 * 
	 * @param config
	 * @throws MalformedURLException
	 */
	public Publisher(GemsConfig config) throws MalformedURLException{
	
		settings = config.getSettings();
		dataPublisher = new DataPublisher(config.getSettings().getGeographicServerSettings(),
										  config.getSettings().getMetadataCatalogueSettings());
		
		metadataPublisher = new MetadataPublisher(config.getSettings().getMetadataCatalogueSettings(),
												  config.getSettings().getValidationSettings());
		
	}
	
	
	/**
	 * Convenience method to get the data publisher
	 * 
	 * @return the data publisher
	 */
	public DataPublisher getDataPublisher(){
		return this.dataPublisher;
	}
	
	
	/**
	 * Convenience method to get the metadata publisher
	 * 
	 * @return the metadata published
	 */
	public MetadataPublisher getMetadataPublisher(){
		return this.metadataPublisher;
	}
	
	
	/**
	 * Method to publish
	 * 
	 * @param object
	 * @param style
	 * @return 
	 * @throws Exception
	 */
	public boolean publish(GeographicMetaObject object, String style) throws Exception {

		boolean published = false;		
		
		//metadata publication
		boolean metadataPublished = false;
		if (this.settings.getPublicationSettings().isActionMetadata()) {
			try{
				this.getMetadataPublisher().insertOrUpdateMetadata(object);
				metadataPublished = true;
			
			} catch(Exception e) {
				LOGGER.info(e.getMessage());
				throw e;
			}
		}
		
		//data publication
		boolean layerExists = this.getDataPublisher().checkLayerExistence(object);
		boolean layerPublished = false;
		if(metadataPublished){
			if (this.settings.getPublicationSettings().isActionData()) {
				try{
					if (layerExists) {
						// force data publication
						LOGGER.info("Updating existing layer");
						this.getDataPublisher().deleteLayer(object);
						this.getDataPublisher().publishLayer(
								object,
								style,
								GemsMethod.valueOf(this.settings.getGeographicServerSettings().getMethod()),
								this.settings.getGeographicServerSettings().getShapefileURL()
								);
						LOGGER.info("Successfull layer update");				
			
					} else {
						LOGGER.info("Publishing new layer");
						this.getDataPublisher().publishLayer(
								object,
								style,
								GemsMethod.valueOf(this.settings.getGeographicServerSettings().getMethod()),
								this.settings.getGeographicServerSettings().getShapefileURL());
						LOGGER.info("Successfull layer publication");
			
					}
				
					layerPublished = true;
				
				} catch(Exception e){
					LOGGER.info(e.getMessage());
					throw e;	
				}
			}
		}
		
		//check data and metadata publication & eventually roll-back
		if(this.settings.getPublicationSettings().isActionData()){
			if(this.settings.getPublicationSettings().isActionMetadata()){
				if(layerPublished && metadataPublished) published = true;
			}else{
				if(layerPublished) published = true;
			}
		}else{
			if(this.settings.getPublicationSettings().isActionMetadata()){
				if(metadataPublished) published = true;
			}
		}
			
		if(!published){
			//rolling-back publication
			if(layerPublished){
				try {
					LOGGER.info("Rolling-back layer publication");
					this.getDataPublisher().deleteLayer(object);
					LOGGER.info("Successfull roll-back");
				} catch(Exception e){
					throw new Exception("Fail to roll-back layer publication", e);
				}
			}else if(metadataPublished){
				try {
					LOGGER.info("Rolling-back metadata publication");
					this.getMetadataPublisher().deleteMetadata(object);
					LOGGER.info("Successfull roll-back");
				} catch(Exception e){
					throw new Exception("Fail to roll-back metadata publication", e);
				}	
			}
		}
		
		return published;
	}

	
	/**
	 * Method to unpublish
	 * 
	 * @param object
	 * @param style
	 * @return 
	 * @throws Exception
	 */
	public boolean unpublish(GeographicMetaObject object, String style) throws Exception {
		
		boolean unpublished = false;

		//unpublish data
		boolean layerUnpublished = false;
		try {
			if (this.settings.getPublicationSettings().isActionData()) {
				LOGGER.info("Unpublishing layer");
				this.getDataPublisher().deleteLayer(object);
				LOGGER.info("Successfull layer unpublication");
			}
			layerUnpublished = true;
		} catch (Exception e){
			throw new Exception("Fail to unpublish layer", e);
		} finally {
			Thread.sleep(sleep*1000);
			LOGGER.info("Sleeping "+sleep+" seconds");
		}
	
		//unpublish metadata
		boolean metadataUnpublished = false;
		try{
			if (this.settings.getPublicationSettings().isActionMetadata()) {
				LOGGER.info("Unpublishing metadata");
				this.getMetadataPublisher().deleteMetadata(object);
				LOGGER.info("Successfull metadata unpublication");
			}
			metadataUnpublished = true;
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
			throw e;
		} finally {
			Thread.sleep(sleep*1000);
			LOGGER.info("Sleeping "+sleep+" seconds");
		}
		
		//check data and metadata publication & eventually roll-back
		if(this.settings.getPublicationSettings().isActionData()){
			if(this.settings.getPublicationSettings().isActionMetadata()){
				if(layerUnpublished && metadataUnpublished) unpublished = true;
			}else{
				if(layerUnpublished) unpublished = true;
			}
		}else{
			if(this.settings.getPublicationSettings().isActionMetadata()){
				if(metadataUnpublished) unpublished = true;
			}
		}	
		
		//in case try to roll-back
		if(!unpublished){
			//rolling-back publication
			if(layerUnpublished){
				try {
					LOGGER.info("Rolling-back layer unpublication");
					this.getDataPublisher().publishLayer(
							object,
							style,
							GemsMethod.valueOf(this.settings.getGeographicServerSettings().getMethod()),
							this.settings.getGeographicServerSettings().getShapefileURL());
					LOGGER.info("Successfull layer unpublication roll-back");
					
				} catch(Exception e){
					throw new Exception("Fail to roll-back layer unpublication", e);
				}
			}else if(metadataUnpublished){
				try {
					LOGGER.info("Rolling-back metadata unpublication");
					this.getMetadataPublisher().insertOrUpdateMetadata(object);
					LOGGER.info("Successfull metadata unpublication roll-back");
					
				} catch(Exception e){
					throw new Exception("Fail to roll-back metadata unpublication", e);
				}
			}
		}
	
		return unpublished;
	}

}
