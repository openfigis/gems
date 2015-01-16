package org.fao.fi.gems.publisher;

import java.io.File;
import java.util.EnumSet;
import java.util.Iterator;

import org.fao.fi.gems.GeographicEntityMetadata;
import org.fao.fi.gems.metaobject.GeographicMetaObject;
import org.fao.fi.gems.model.settings.metadata.MetadataCatalogueSettings;
import org.fao.fi.gems.model.settings.publication.PublicationSettings;
import org.geotoolkit.xml.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPriv;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNSearchResponse.GNMetadata;

/**
 * Metadata Publisher Allows to publish ISO 19115/19139 compliant metadata in a
 * Geonetwork catalogue.
 * 
 * @author eblondel (FAO)
 * 
 */
public class MetadataPublisher {
	
	private static Logger LOGGER = LoggerFactory.getLogger(MetadataPublisher.class);

	private String version;

	private String gnBaseURL;
	GNClient client;

	/**
	 * Metadata publisher
	 * 
	 * @param catalogueSettings
	 * @param publicationSettings
	 */
	public MetadataPublisher(MetadataCatalogueSettings catalogueSettings,
							 PublicationSettings publicationSettings) {
		this.version = publicationSettings.getVersion();

		// geonetwork connection
		this.gnBaseURL = catalogueSettings.getUrl();
		client = new GNClient(this.gnBaseURL);
		boolean logged = client.login(catalogueSettings.getUser(), catalogueSettings.getPassword());
		if (!logged) {
			throw new RuntimeException("Could not log in");
		}
	}
	
	
	/**
	 * check metadata existence
	 * 
	 * @param object
	 * @return a GNMetadata object, null otherwise
	 * @throws Exception 
	 */
	public GNMetadata checkMetadataExistence(GeographicMetaObject object) throws Exception{
		
		GNSearchRequest request = new GNSearchRequest();
		request.addParam("uuid", object.metaIdentifier());
		GNSearchResponse response;
		try {
			response = client.search(request);
		} catch (Exception e1) {
			throw new Exception("Fail to search metadata in Geonetwork", e1);
		}
		
		// delete
		Iterator<GNMetadata> it = response.iterator();
		GNMetadata metadata = null;
		while(it.hasNext()){
			GNMetadata md = it.next();
			if(md.getUUID().matches(object.metaIdentifier())){
				metadata = md;
				break;
			}
			
		}
		return metadata;
	}
	

	/**
	 * Method to publish a metadata
	 * 
	 * @param eobject
	 * @return the metadata identifier
	 * @throws Exception 
	 */
	public String publishMetadata(GeographicMetaObject object) throws Exception {

		String metadataID = null;
		try {

			final GeographicEntityMetadata metadata = new GeographicEntityMetadata(
					object, this.version);

			// metadata insert configuration
			GNInsertConfiguration icfg = new GNInsertConfiguration();
			icfg.setCategory("datasets");
			icfg.setGroup("1"); // group 1 is usually "all"
			icfg.setStyleSheet("_none_");
			icfg.setValidate(Boolean.FALSE);

			File tmp = File.createTempFile(metadata.getFileIdentifier(), ".xml");
			XML.marshal(metadata, tmp);

			long id = client.insertMetadata(icfg, tmp); // insert metadata
			tmp.delete(); // delete metadata file

			// public privileges configuration
			GNPrivConfiguration pcfg = new GNPrivConfiguration();
			pcfg.addPrivileges(1,
					EnumSet.of(GNPriv.VIEW, GNPriv.DYNAMIC, GNPriv.FEATURED));
			client.setPrivileges(id, pcfg); // set public view privilege

			// metadataURL
			metadataID = metadata.getFileIdentifier();

		} catch (Exception e) {
			throw new Exception("Failed to publish metadata", e);
		}

		return metadataID;

	}
	
	
	/**
	 * Delete a metadata from Geonetwork
	 * 
	 * @param object
	 * @throws Exception
	 */
	public void deleteMetadata(GeographicMetaObject object) throws Exception{
		
		GNMetadata metadata = this.checkMetadataExistence(object);
		if(metadata != null){
			try {
				client.deleteMetadata(metadata.getId());
			} catch (Exception e) {
				throw new Exception("Fail to delete metadata in Geonetwork", e);
			}
		}else{
			LOGGER.warn("No metadata for id = "+object.metaIdentifier());
		}
	}

}
