package org.fao.fi.gems.lod.entity.common;

import org.fao.fi.gems.lod.entity.BasicLodEntity;
import org.fao.fi.gems.lod.entity.LodEntity;

import com.google.gson.JsonElement;

/**
 * A FLOD Rfb entity, referenced by acronym
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class FLODRfbEntity extends BasicLodEntity implements LodEntity {

	private static String endpoint = "http://www.fao.org/figis/grade/service/prod/query/ce4rfb/results.json";

	public FLODRfbEntity(String code) throws Exception {
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
		JsonElement element = this.content().get("rfb_uri");
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
		String rfbName = null;
		JsonElement element = this.content().get("rfb_label");
		if (element != null) {
			String n = element.getAsJsonObject().get("value").getAsString()
					.split("\\(")[0];
			rfbName = n.substring(0, n.length() - 1);
		}

		return rfbName;
	}

}
