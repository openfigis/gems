package org.fao.fi.gems.entity;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.fao.fi.gems.association.GeographicMetaObjectProperty;
import org.fao.fi.gems.model.MetadataConfig;
import org.fao.fi.gems.model.content.MetadataContent;
import org.fao.fi.gems.util.Utils;

/**
 * GeographicEntityImpl
 * 
 * 
 * @author eblondel
 *
 */
public abstract class GeographicEntityImpl implements GeographicEntity {

	private String code;
	private String refName;
	private String metaIdentifier;
	private Map<EntityAddin,String> addins;
	private MetadataConfig config;
	
	private Map<GeographicMetaObjectProperty, List<String>> specificProperties;
	
	private String figisDomain;
	private String figisId;
	private String figisViewerId;
 
	public GeographicEntityImpl(String code, Map<EntityAddin,String> addins, MetadataConfig config) throws URISyntaxException {
		this.code = code;
		this.setAddins(addins);
		this.config = config;
		this.setMetaIdentifier();
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
	 * @return the metaIdentifier
	 */
	public String getMetaIdentifier() {
		return metaIdentifier;
	}

	/**
	 */
	public void setMetaIdentifier() {
		this.metaIdentifier = Utils.buildMetadataIdentifier(this);
	}

	/**
	 * @return the addins
	 */
	public Map<EntityAddin,String> getAddins() {
		return addins;
	}

	/**
	 * @param addins the addins to set
	 */
	public void setAddins(Map<EntityAddin,String> addins) {
		this.addins = addins;
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
	 * @return the config
	 */
	public MetadataConfig getConfig() {
		return config;
	}

	/**
	 * @param template the template to set
	 */
	public void setConfig(MetadataConfig config) {
		this.config = config;
	}

	/**
	 * @return the figisDomain
	 */
	public String getFigisDomain() {
		return figisDomain;
	}

	/**
	 * @param figisDomain the figisDomain to set
	 */
	public void setFigisDomain(String figisDomain) {
		this.figisDomain = figisDomain;
	}

	/**
	 * @return the figisId
	 */
	public String getFigisId() {
		return figisId;
	}

	/**
	 * @param figisId the figisId to set
	 */
	public void setFigisId(String figisId) {
		this.figisId = figisId;
	}

	/**
	 * @return the figisViewerId
	 */
	public String getFigisViewerId() {
		return figisViewerId;
	}

	/**
	 * @param figisViewerId the figisViewerId to set
	 */
	public void setFigisViewerId(String figisViewerId) {
		this.figisViewerId = figisViewerId;
	}


}
