/**
 * (c) 2015 FAO / UN (project: gems-lod)
 */
package org.fao.fi.gems.lod.entity.common;

import org.fao.fi.gems.lod.entity.BasicLodEntity;
import org.fao.fi.gems.lod.entity.LodEntity;

import com.google.gson.JsonElement;

/**
 * A FLOD Fishery statistical area (Fsa) entity, referenced by area code
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class FLODFsaEntity extends BasicLodEntity implements LodEntity {

	private static String endpoint = "http://www.fao.org/figis/grade/service/prod/query/ce4fsa/results.json";

	public FLODFsaEntity(String code) throws Exception {
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
		JsonElement element = this.content().get("fsa_uri");
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
		return this.code();
	}


}
