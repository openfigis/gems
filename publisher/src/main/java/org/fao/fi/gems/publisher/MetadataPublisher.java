package org.fao.fi.gems.publisher;

import java.io.File;
import java.util.EnumSet;

import org.fao.fi.gems.GeographicEntityMetadata;
import org.fao.fi.gems.association.GeographicMetaObject;
import org.fao.fi.gems.model.settings.MetadataCatalogueSettings;
import org.fao.fi.gems.model.settings.PublicationSettings;
import org.geotoolkit.xml.XML;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPriv;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNSearchRequest.Config;
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

	private String revisionDate;
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
		this.revisionDate = publicationSettings.getDate();
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
	 * Method to publish a metadata
	 * 
	 * @param eobject
	 * @return the metadata identifier
	 */
	public String publishMetadata(GeographicMetaObject object) {

		String metadataID = null;
		try {

			final GeographicEntityMetadata metadata = new GeographicEntityMetadata(
					object, this.revisionDate, this.version);

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
			e.printStackTrace();
		}

		return metadataID;

	}

	/**
	 * Delete a metadata from Geonetwork
	 * 
	 * @param object
	 * @throws Exception
	 */
	public void deleteMetadata(GeographicMetaObject object) throws Exception {
		
		//configure metadata search 
		GNSearchRequest request = new GNSearchRequest();
		request.addConfig(Config.similarity, "1");
		request.addParam("uuid", object.getMetaIdentifier());
		GNSearchResponse response = client.search(request);
		GNMetadata metadata = response.getMetadata(0);
		
		// delete
		client.deleteMetadata(metadata.getId());
	}

}
