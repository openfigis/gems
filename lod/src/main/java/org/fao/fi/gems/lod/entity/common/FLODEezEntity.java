package org.fao.fi.gems.lod.entity.common;

import org.fao.fi.gems.lod.entity.BasicLodEntity;
import org.fao.fi.gems.lod.entity.LodEntity;

import com.google.gson.JsonElement;

/**
 * A FLOD Eez entity, referenced by mrgid
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class FLODEezEntity extends BasicLodEntity implements LodEntity {

	private static String endpoint = "http://www.fao.org/figis/grade/service/prod/query/ce4species/results.json";

	public FLODEezEntity(String code) throws Exception {
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
		JsonElement element = this.content().get("eez_uri");
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
		return this.englishName();
	}
	
	/**
	 * Get english name
	 * 
	 * @return the english name
	 */
	public String englishName(){
		String eezName = null;
		JsonElement element = this.content().get("eez_label");
		if (element != null) {
			eezName = element.getAsJsonObject().get("value").getAsString()
					.split("\\(")[0];		
		}
		return eezName;
	}

}
