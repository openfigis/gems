/**
 * (c) 2015 FAO / UN (project: gems-publisher)
 */
package org.fao.fi.gems.publisher;

import java.io.File;
import java.net.MalformedURLException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

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
		client = new GNClient(this.gnBaseURL);
		boolean logged = client.login(catalogueSettings.getUser(), catalogueSettings.getPassword());
		if (!logged) {
			throw new RuntimeException("Could not log in");
		}
		
		//inspire validation
		inspireValidator = new InspireValidator();
		inspire = validationSettings.isInspire();
		strict = validationSettings.isStrict();
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

			//marshalling metadata
			final GeographicMetadata metadata = object.metadata();	
			File tmp = File.createTempFile(metadata.getMetadataIdentifier().getCode(), ".xml");
			Result out = new StreamResult(tmp);
			
			Map<String,Object> properties = new HashMap<>();
			properties.put(XML.STRING_SUBSTITUTES, new String[] {"filename","mimetype"});
			properties.put(XML.LOCALE, Locale.ENGLISH);

			XML.marshal(metadata, out, properties);
			
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
			
			// metadata insert configuration
			GNInsertConfiguration icfg = new GNInsertConfiguration();
			icfg.setCategory("datasets");
			icfg.setGroup("1"); // group 1 is usually "all"
			icfg.setStyleSheet("_none_");
			icfg.setValidate(Boolean.FALSE);
			long id = client.insertMetadata(icfg, tmp); // insert metadata
			
			tmp.delete(); // delete metadata file

			// public privileges configuration
			GNPrivConfiguration pcfg = new GNPrivConfiguration();
			pcfg.addPrivileges(1,
					EnumSet.of(GNPriv.VIEW, GNPriv.DYNAMIC, GNPriv.FEATURED));
			client.setPrivileges(id, pcfg); // set public view privilege

			// metadataURL
			metadataID = metadata.getMetadataIdentifier().getCode();

		} catch (Exception e) {
			e.printStackTrace();
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
