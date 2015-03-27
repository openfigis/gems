package org.fao.fi.gems.lod.entity.common;

import org.fao.fi.gems.lod.entity.BasicLodEntity;
import org.fao.fi.gems.lod.entity.LodEntity;

import com.google.gson.JsonElement;

/**
 * A FLOD Species entity, referenced by ASFIS 3-alpha code
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class FLODSpeciesEntity extends BasicLodEntity implements LodEntity {

	private static String endpoint = "http://figisapps.fao.org/grade/service/prod/query/ce4species/results.json";

	public FLODSpeciesEntity(String code) throws Exception {
		super(endpoint, code);
	}

	/**
	 * Authority Uri
	 * 
	 * @return the authority Uri for this FLOD entity
	 * 
	 */
	@Override
	public String authorityUri() {
		String authorityUri = null;
		JsonElement element = this.content().get("asfis_uri");
		if(element != null){
			authorityUri = element.getAsJsonObject().get("value").getAsString();
		}
		return authorityUri;	
	}

	/**
	 * Authority name
	 * 
	 * @return the authority name for this FLOD entity
	 * 
	 */
	@Override
	public String authorityName() {
		return this.asfisScientificName();
	}
	
	/**
	 * Get ASFIS scientific name
	 * 
	 * @return the ASFIS scientific name
	 */
	public String asfisScientificName(){
		String asfisScientificName = null;
		JsonElement element = this.content().get("asfis_scname");
		if(element != null){
			asfisScientificName = element.getAsJsonObject().get("value").getAsString();
		}
										
		return asfisScientificName;
	}

	/**
	 * Get ASFIS english name
	 * 
	 * @return the ASFIS english name
	 */
	public String asfisEnglishName(){
		String asfisScientificName = null;
		JsonElement element = this.content().get("asfis_name");
		if(element != null){
			asfisScientificName = element.getAsJsonObject().get("value").getAsString();
		}
										
		return asfisScientificName;
	}
	
	/** get WoRMS scientific name
	 * 
	 * @return the WoRMS scientific name
	 */
	public String wormsScientificName(){
		String wormsScientificName = null;
		JsonElement element = this.content().get("worms_scname");
		if(element != null){
			wormsScientificName = element.getAsJsonObject().get("value").getAsString();
		}
										
		return wormsScientificName;
	}
	
	
	/** get WoRMS LSID
	 * 
	 * @return the WoRMS LSID
	 */
	public String wormsLsid(){
		String LSID = null;
		JsonElement element = this.content().get("worms_lsid");
		if(element != null){
			LSID = element.getAsJsonObject().get("value").getAsString();
		}							
		return LSID;
	}
	
	
	
	/** get AphiaID
	 * 
	 * @return the AphiaID
	 */
	public String wormsAphiaID(){
		String aphiaID = null;
		if(this.wormsLsid() != null){
			aphiaID = this.wormsLsid().split("taxname:")[1];
		}
		return aphiaID;
	}
	

}
