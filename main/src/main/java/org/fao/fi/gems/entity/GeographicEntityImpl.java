package org.fao.fi.gems.entity;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;
import org.fao.fi.gems.util.Utils;

/**
 * GeographicEntityImpl
 * 
 * 
 * @author eblondel
 *
 */
public class GeographicEntityImpl implements GeographicEntity {

	private String owner;
	private String collection;
	private String code;
	private String refName;	
	private Map<GeographicMetaObjectProperty, List<String>> specificProperties;
	private String metaIdentifier;
 
	public GeographicEntityImpl(String owner, String collection,
								String code, String refName,
								Map<GeographicMetaObjectProperty, List<String>> properties) throws URISyntaxException {
		this.owner = owner;
		this.collection = collection;
		this.code = code;
		this.refName = refName;
		this.specificProperties = properties;
		this.setMetaIdentifier(Utils.buildMetadataIdentifier(this.owner, this.collection, this.code));
	}
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the refName
	 */
	public String getRefName() {
		return refName;
	}

	/**
	 * @param refName the refName to set
	 */
	public void setRefName(String refName) {
		this.refName = refName;
	}
	
	/**
	 * Get the owner id
	 */
	public String getOwner() {
		return this.owner;
	}
	
	/**
	 * Set the owner
	 * 
	 * @param owner
	 */
	public void setOwner(String owner){
		this.owner = owner;
	}

	/**
	 * Get the collection id
	 */
	public String getCollection() {
		return this.collection;
	}
	
	/**
	 * Set the collection id
	 * 
	 * @param collection
	 */
	public void setCollection(String collection){
		this.collection = collection;
	}

	/**
	 * @return the specificProperties
	 */
	public Map<GeographicMetaObjectProperty, List<String>> getSpecificProperties() {
		return specificProperties;
	}

	/**
	 * @param specificProperties the specificProperties to set
	 */
	public void setSpecificProperties(Map<GeographicMetaObjectProperty, List<String>> specificProperties) {
		this.specificProperties = specificProperties;
	}
	
	/**
	 * Get the meta identifier
	 * 
	 */
	public String getMetaIdentifier(){
		return this.metaIdentifier;
	}

	/**
	 * Set the meta Identifier
	 * @param metaIdentifier
	 */
	public void setMetaIdentifier(String metaIdentifier){
		this.metaIdentifier = metaIdentifier;
	}

}
