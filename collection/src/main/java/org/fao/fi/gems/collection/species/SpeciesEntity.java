package org.fao.fi.gems.collection.species;

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
 * Species entity
 * 
 * @author eblondel (FAO)
 *
 */
public class SpeciesEntity extends GeographicEntityImpl implements GeographicEntity{

	
	public enum SpeciesProperty implements GeographicMetaObjectProperty{
		
		FAO(AuthorityEntity.FAO, true, true),
		FLOD (AuthorityEntity.FLOD, true, true),
		
		HABITAT (AuthorityEntity.HABITAT, false, false),
		FIGIS (AuthorityEntity.FIGIS, true, false),
		ASFIS (AuthorityEntity.ASFIS, true, false),
		WORMS (AuthorityEntity.WORMS, true, false);		
	
		private final AuthorityEntity authority;
		private final boolean thesaurus;
		private final boolean containsURIs;
		
		SpeciesProperty(AuthorityEntity authority, boolean thesaurus, boolean containsURIs){
			this.authority = authority;
			this.thesaurus = thesaurus;
			this.containsURIs = containsURIs;
		}
		
		public AuthorityEntity authority(){
			return this.authority;
		}
		
		public boolean isThesaurus(){
			return this.thesaurus;
		}

		public boolean containsURIs() {
			return this.containsURIs;
		}
		
	}	
	
	private FLODSpeciesEntity FLODSpeciesEntity;

	public SpeciesEntity(String code, Map<EntityAddin,String> addins,MetadataConfig config) throws URISyntaxException{
		
		super(code,addins,config);
	
		this.FLODSpeciesEntity = new FLODSpeciesEntity(code);
		this.setRefName(this.FLODSpeciesEntity.getAsfisScientificName());
		
		
		Map<GeographicMetaObjectProperty, List<String>> properties = new HashMap<GeographicMetaObjectProperty, List<String>>();
		String habitat = null;
		if(addins != null){
			habitat = this.getAddins().get(EntityAddin.Habitat);
			properties.put(SpeciesProperty.HABITAT, Arrays.asList(habitat));
		}
		properties.put(SpeciesProperty.FAO, Arrays.asList(this.getMetaIdentifier()));
		properties.put(SpeciesProperty.FIGIS, Arrays.asList(this.FLODSpeciesEntity.getFigisID()));
		properties.put(SpeciesProperty.ASFIS, Arrays.asList(this.FLODSpeciesEntity.getAlphacode(),
															this.FLODSpeciesEntity.getAsfisScientificName(),
															this.FLODSpeciesEntity.getAsfisEnglishName()));
		
		if(this.FLODSpeciesEntity.getAphiaID() != null){//control because not all species in FLOD have worms info
			properties.put(SpeciesProperty.WORMS, Arrays.asList(
													this.FLODSpeciesEntity.getAphiaID(),
													this.FLODSpeciesEntity.getWormsScientificName()));
		}
		properties.put(SpeciesProperty.FLOD, Arrays.asList(this.FLODSpeciesEntity.getASFISCodedEntity()));
		this.setSpecificProperties(properties);
		
		if(habitat != null){
			this.setFigisDomain("species");
			this.setFigisId(this.FLODSpeciesEntity.getFigisID());
			this.setFigisViewerId(code+"-"+this.getAddins().get(EntityAddin.Habitat));
		}
		
	}

}
