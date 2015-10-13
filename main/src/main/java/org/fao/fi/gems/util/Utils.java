/**
 * (c) 2015 FAO / UN (project: gems-main)
 */
package org.fao.fi.gems.util;

import java.util.Iterator;
import java.util.List;

import org.apache.sis.xml.Namespaces;
import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.content.MetadataContact;
import org.fao.fi.gems.model.settings.publication.EntityList;

/**
 * Utils
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
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
	 * Util to indicate if an entity should be wrapped by the codelist parser
	 * 
	 * @param config
	 * @param code
	 * @return true if the entity should be wrapped by Codelist parser
	 */
	public static boolean wrapEntity(GemsConfig config, String code){
		
		boolean wrapEntity = true;
		EntityList entities = config.getSettings().getPublicationSettings().getEntities();
		if(entities != null){
			List<String> included = entities.getInclude();
			if(included != null){
				if(included.size() > 0){
					if(!included.contains(code)){
						wrapEntity = false;
					}
				}
			}
			
			List<String> excluded = entities.getExclude();
			if(excluded != null){
				if(excluded.size() > 0){
					if(excluded.contains(code)){
						wrapEntity = false;
					}
				}
			}
		}
	
		return wrapEntity;
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
				+ "&elementSetName=full" + "&outputSchema=" + Namespaces.GMD.replaceAll(":", "%3A")
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
		metaId = metaId.replaceAll(",", "");
		metaId = metaId.replaceAll(" ", "");
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
		metaId = metaId.replaceAll(",", "");
		metaId = metaId.replaceAll(" ", "");
		return metaId;
	}

}
