package org.fao.fi.gems.util;

import java.util.List;

import org.fao.fi.gems.entity.GeographicEntity;
import org.geotoolkit.xml.Namespaces;

public final class Utils {

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
	 * Builds a metadata identifier for an entity
	 * 
	 * @param entity
	 * @return the metadata identifier
	 */
	public static String buildMetadataIdentifier(GeographicEntity entity){
		String metaId = null;
		if(entity.getConfig() != null){
			metaId = entity.getConfig().getContent().getOrganizationContact().getAcronym().toLowerCase() +"-"+
					  entity.getConfig().getSettings().getPublicationSettings().getCollectionType().toLowerCase() + "-map-"+
					  entity.getCode().toLowerCase();
		}
		return metaId;
	}
	
	/**
	 * Builds a metadata identifier for a list of entities
	 * 
	 * @param entity
	 * @return the metadata identifier
	 */
	public static String buildMetadataIdentifier(List<GeographicEntity> entities, String org, String collectionType){
		String metaId = org.toLowerCase() +"-"+collectionType.toLowerCase() + "-map-";
		for(int i=0;i<entities.size();i++){
			GeographicEntity entity = entities.get(i);
			String ref = entity.getConfig().getSettings().getPublicationSettings().getCollectionType()+"-"+entity.getCode().toLowerCase();
			if(i==0){
				metaId += ref;
			}else{
				metaId += "_x_"+ref;
			}
		}
		return metaId;
	}

}
