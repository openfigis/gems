package org.fao.fi.gems.entity;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;

/**
 * FIGIS-specific implementation of GeographicEntity
 * 
 * @author eblondel
 *
 */
public class FigisGeographicEntityImpl extends GeographicEntityImpl implements FigisGeographicEntity{

	private String figisDomain;
	private String figisId;
	private String figisViewerId;
	
	/**
	 * Constructs a FIGIS-specific GeographicEntity
	 * 
	 * @param owner
	 * @param collection
	 * @param code
	 * @param refName
	 * @param properties
	 * @throws URISyntaxException
	 */
	public FigisGeographicEntityImpl(String owner, String collection,
			String code, String refName,
			Map<GeographicMetaObjectProperty, List<String>> properties)
			throws URISyntaxException {
		super(owner, collection, code, refName, properties);
		
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
