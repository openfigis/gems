/**
 * (c) 2015 FAO / UN (project: gems-publisher)
 */
package org.fao.fi.gems.publisher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.apache.sis.internal.jaxb.LegacyNamespaces;
import org.apache.sis.xml.XML;
import org.fao.fi.gems.metaobject.GeographicMetadata;
import org.fao.fi.gems.metaobject.GeographicMetaObject;
import org.fao.fi.gems.model.settings.metadata.MetadataCatalogueSettings;
import org.fao.fi.gems.model.settings.validation.ValidationSettings;
import org.fao.fi.gems.validation.InspireValidationError;
import org.fao.fi.gems.validation.InspireValidationReport;
import org.fao.fi.gems.validation.InspireValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.GN26Client;
import it.geosolutions.geonetwork.GN28Client;
import it.geosolutions.geonetwork.GN3Client;
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
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 * 
 */
public class MetadataPublisher {
	
	private static Logger LOGGER = LoggerFactory.getLogger(MetadataPublisher.class);

	private String gnBaseURL;
	private String gnVersion;
	GNClient client;
	
	InspireValidator inspireValidator;
	boolean inspire;
	boolean strict;

	/**
	 * Metadata publisher
	 * 
	 * @param GemsConfig
	 * @throws MalformedURLException 
	 *
	 */
	public MetadataPublisher(MetadataCatalogueSettings catalogueSettings,
							 ValidationSettings validationSettings) throws MalformedURLException {

		// geonetwork connection
		this.gnBaseURL = catalogueSettings.getUrl();
		this.gnVersion = catalogueSettings.getVersion();
		if(this.gnVersion.startsWith("2.6")){
			client = new GN26Client(this.gnBaseURL, catalogueSettings.getUser(), catalogueSettings.getPassword());
		}else if(this.gnVersion.startsWith("2.8")){
			client = new GN28Client(this.gnBaseURL, catalogueSettings.getUser(), catalogueSettings.getPassword());
		}else if(this.gnVersion.startsWith("3")){
			client = new GN3Client(this.gnBaseURL, catalogueSettings.getUser(), catalogueSettings.getPassword());
		}else{
			throw new RuntimeException(String.format("Version '%s' unsupported with Geonetwork client", catalogueSettings.getVersion()));
		}
		
		//inspire validation
		inspireValidator = new InspireValidator();
		inspire = validationSettings.isInspire();
		strict = validationSettings.isStrict();
	}
	
	/**
	 * check metadata existence
	 * 
	 * @param metadata id
	 * @return a GNMetadata object, null otherwise
	 * @throws Exception 
	 */
	public static GNMetadata checkMetadataExistence(GNClient client, String metaId) throws Exception{
		GNSearchRequest request = new GNSearchRequest();
		request.addParam("uuid", metaId);
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
			if(md.getUUID().matches(metaId)){
				metadata = md;
				break;
			}
		}
		return metadata;
	}
	
	/**
	 * check metadata existence
	 * 
	 * @param object
	 * @return a GNMetadata object, null otherwise
	 * @throws Exception 
	 */
	public GNMetadata checkMetadataExistence(GeographicMetaObject object) throws Exception{	
		return MetadataPublisher.checkMetadataExistence(client, object.metaIdentifier());
	}
	

	/**
	 * Method to publish a metadata. If existing, metadata will be inserted, otherwise
	 * it will be updated using the GeoNetwork manager.
	 * 
	 * @param object
	 * @return the metadata identifier
	 * @throws Exception 
	 */
	public String insertOrUpdateMetadata(GeographicMetaObject object) throws Exception {

		String metadataID = null;

		//marshalling metadata
		final GeographicMetadata metadata = object.metadata();	
		File tmp = File.createTempFile(metadata.getMetadataIdentifier().getCode(), ".xml");
		Result out = new StreamResult(tmp);
		
		Map<String,Object> properties = new HashMap<>();
		properties.put(XML.STRING_SUBSTITUTES, new String[] {"filename","mimetype"});
		properties.put(XML.LOCALE, Locale.ENGLISH);
		properties.put(XML.GML_VERSION, LegacyNamespaces.VERSION_3_2);

		XML.marshal(metadata, out, properties);
		
		System.out.println(tmp.getAbsolutePath());
		
		//inspire validation
		if(inspire){
			Response resp = inspireValidator.validate(tmp);
			boolean isValid = inspireValidator.isValid(resp);
			if(isValid){
				LOGGER.info("[INSPIRE] - Metadata is valid");
			}else{
				InspireValidationReport inspireReport = inspireValidator.getReport(resp);
				LOGGER.info("[INSPIRE] Invalid metadata (completeness = "+inspireReport.getCompleteness()+"%)");
				for(InspireValidationError error : inspireReport.getValidationErrors()){
					LOGGER.info("[INSPIRE Issue] "+error.getElement()+": "+error.getMessage());
				}
				
				if(strict){
					LOGGER.info("[INSPIRE] Aborting metadata publication (Strict = true)");
					throw new Exception("Metadata is not INSPIRE compliant");
				}
				
			}
		}
		
		long id = 0;
		GNMetadata gnMetadata = this.checkMetadataExistence(object);
		if(gnMetadata == null){
			LOGGER.warn("No record for id = "+object.metaIdentifier());
			try {
				// metadata insert configuration
				LOGGER.info("Publishing new metadata");
				GNInsertConfiguration icfg = new GNInsertConfiguration();
				icfg.setCategory("datasets");
				icfg.setGroup("1"); // group 1 is usually "all"
				icfg.setStyleSheet("_none_");
				icfg.setValidate(Boolean.FALSE);
				id = client.insertMetadata(icfg, tmp);
				LOGGER.info("Successfull metadata insertion");
				
			} catch (Exception e) {
				throw new PublicationException("Fail to insert new metadata", e);
			}
		}else{
			id = gnMetadata.getId();
			LOGGER.warn("Existing record for id = "+object.metaIdentifier()+" ("+id+")");
			try {
				// metadata update configuration
				LOGGER.info("Updating existing metadata");
				client.updateMetadata(id, tmp);
				LOGGER.info("Successfull metadata update");
				
			} catch (Exception e) {
				throw new PublicationException("Fail to update metadata", e);
			}
		}

		// public privileges configuration
		GNPrivConfiguration pcfg = new GNPrivConfiguration();
		pcfg.addPrivileges(1, EnumSet.of(GNPriv.VIEW, GNPriv.DYNAMIC, GNPriv.FEATURED));
		client.setPrivileges(id, pcfg); // set public view privilege

		// delete metadata file
		//tmp.delete();
		
		// metadataURL
		metadataID = metadata.getMetadataIdentifier().getCode();
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
				throw new PublicationException("Fail to delete metadata", e);
			}
		}else{
			LOGGER.warn("No metadata for id = "+object.metaIdentifier());
		}
	}

}
