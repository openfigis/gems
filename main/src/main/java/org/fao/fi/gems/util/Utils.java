package org.fao.fi.gems.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.content.MetadataContact;
import org.geotoolkit.xml.Namespaces;

public final class Utils {

	
	/**
	 * Builds a code from a code stack
	 * 
	 * @param codeStack
	 * @return a string code
	 */
	public static String buildCode(List<EntityCode> codeStack){
		String code = null;
		if(codeStack.size() > 0){
			Iterator<EntityCode> it = codeStack.iterator();
			EntityCode ec = it.next();
			code = ec.getCode();
			while(it.hasNext()){
				EntityCode ecn = it.next();
				code += "-"+ecn.getCode();
			}
		}
		
		return code;
	}
	
	
	/**
	 * Util to get who is the owner for the GEMS products
	 * 
	 * @param config
	 * @return the owner
	 */
	public static String whoIsOwner(GemsConfig config){
		String owner = null;
		for(MetadataContact organization : config.getContent().getOrganizationContacts()){
			if(organization.getRole().matches("OWNER")){
				owner = organization.getAcronym();
				break;
			}
		}
		return owner;
	}
	
	
	/**
	 * Get the metadataURL
	 * 
	 * @param gnBaseURL
	 * @param mdIdentifier
	 * @return the metadata URL
	 */
	public static String getXMLMetadataURL(String gnBaseURL, String mdIdentifier) {

		String metadataURL = gnBaseURL + "/srv/en/" + "csw?service=CSW"
				+ "&request=GetRecordById" + "&Version=2.0.2"
				+ "&elementSetName=full" + "&outputSchema=" + Namespaces.GMD
				+ "&id=" + mdIdentifier;

		return metadataURL;
	}

	/**
	 * Get the metadataURL
	 * 
	 * @param gnBaseURL
	 * @param mdIdentifier
	 * @return the metadata URL
	 */
	public static String getHTMLMetadataURL(String gnBaseURL,
			String mdIdentifier) {

		String metadataURL = gnBaseURL + "/srv/en/main.home?uuid="
				+ mdIdentifier;
		return metadataURL;
	}

	/**
	 * Get Feature Catalogue
	 * 
	 * @param gnBaseURL
	 * @param mdIdentifier
	 * @return the metadata URL
	 */
	public static String getXMLFeatureCatalogueURL(String gnBaseURL,
			String mdIdentifier) {

		String metadataURL = gnBaseURL + "/srv/en/" + "csw?service=CSW"
				+ "&request=GetRecordById" + "&Version=2.0.2"
				+ "&elementSetName=full" + "&outputSchema=" + Namespaces.GFC
				+ "&id=" + mdIdentifier;

		return metadataURL;
	}
	
	/**
	 * Builds a simple meta Identifier
	 * 
	 * @param owner
	 * @param collection
	 * @param code
	 * @return
	 */
	public static String buildMetadataIdentifier(String owner, String collection, String code){
		String metaId = owner.toLowerCase() + "-" + collection.toLowerCase() + "-map-" + code.toLowerCase();
		return metaId;
	}
	
	/**
	 * Builds a metadata identifier for a list of entities
	 * 
	 * @param owner
	 * @param collection
	 * @param entities
	 * @return the metadata identifier
	 */
	public static String buildMetadataIdentifier(String owner, String collection, List<GeographicEntity> entities){
		String metaId = null;
		for(int i=0;i<entities.size();i++){
			GeographicEntity entity = entities.get(i);
			String ref = entity.metaIdentifier();
			if(i==0){
				metaId = ref;
			}else{
				metaId += "_x_"+ref;
			}
		}
		return metaId;
	}

}
