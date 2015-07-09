/**
 * (c) 2015 FAO / UN (project: gems-main)
 */
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
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class GeographicEntityImpl implements GeographicEntity {

	private String owner;
	private String collection;
	private List<EntityCode> codeStack;
	private String code;
	private String refName;	
	private Map<GeographicMetaObjectProperty, List<String>> specificProperties;
	private String metaIdentifier;
 
	public GeographicEntityImpl(String owner, String collection,
								List<EntityCode> codeStack, String refName,
								Map<GeographicMetaObjectProperty, List<String>> properties) throws URISyntaxException {
		this.owner = owner;
		this.collection = collection;
		this.codeStack = codeStack;
		this.code = Utils.buildCode(this.codeStack);
		this.refName = refName;
		this.specificProperties = properties;
		this.setMetaIdentifier(Utils.buildMetadataIdentifier(this.owner, this.collection, this.code));
	}
	
	public List<EntityCode> codeStack() {
		return codeStack;
	}

	public void setCodeStack(List<EntityCode> codeStack) {
		this.codeStack = codeStack;
	}
	
	public String code() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the refName
	 */
	public String refName() {
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
	public String owner() {
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
	public String collection() {
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
	public Map<GeographicMetaObjectProperty, List<String>> properties() {
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
	public String metaIdentifier(){
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
