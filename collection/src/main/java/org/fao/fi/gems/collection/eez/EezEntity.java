package org.fao.fi.gems.collection.eez;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fao.fi.gems.association.GeographicMetaObjectProperty;
import org.fao.fi.gems.authority.AuthorityEntity;
import org.fao.fi.gems.entity.EntityAddin;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.entity.GeographicEntityImpl;
import org.fao.fi.gems.model.MetadataConfig;


/**
 * EEZ Entity.
 * Wrapper for EEZ publication by VLIZ.
 * 
 * @author eblondel (FAO)
 *
 */
public class EezEntity extends GeographicEntityImpl implements GeographicEntity{

	public enum EezProperty implements GeographicMetaObjectProperty{
		
		VLIZ (AuthorityEntity.VLIZ, true, true),
		FLOD (AuthorityEntity.FLOD, true, true);
		
		private final AuthorityEntity authority;
		private final boolean thesaurus;
		private final boolean containsURIs;
		
		EezProperty(AuthorityEntity authority, boolean thesaurus, boolean containsURIs){
			this.authority = authority;
			this.thesaurus = thesaurus;
			this.containsURIs = containsURIs;
		}
		
		public AuthorityEntity authority(){
			return this.authority;
		}

		public boolean isThesaurus() {
			return this.thesaurus;
		}

		public boolean containsURIs() {
			return this.containsURIs;
		}
		
	}
	
	//private FLODEezEntity FLODEezEntity;

	
	public EezEntity(String code, Map<EntityAddin,String> addins, MetadataConfig config) throws URISyntaxException {
		
		super(code, addins, config);
		
		//this.FLODEezEntity = new FLODEezEntity(code);
		//this.setRefName(this.FLODEezEntity.getName());
		this.setRefName(addins.get(EntityAddin.Label));
		
		Map<GeographicMetaObjectProperty, List<String>> properties = new HashMap<GeographicMetaObjectProperty, List<String>>();		
		//properties.put(EezProperty.FLOD, Arrays.asList(this.FLODEezEntity.getCodedEntity()));
		properties.put(EezProperty.VLIZ, Arrays.asList(this.getMetaIdentifier()));

		this.setSpecificProperties(properties);
		
	}


}
