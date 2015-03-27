package org.fao.fi.gems.lod.entity;

import org.fao.fi.gems.lod.client.LodClient;
import org.fao.fi.gems.lod.client.LodClientImpl;

import com.google.gson.JsonObject;

/**
 * A basic wrapper for Linked-Open-Data entity
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public abstract class BasicLodEntity implements LodEntity{
	
	private LodClient client;
	
	public BasicLodEntity(String endpoint, String code) throws Exception{
		client = new LodClientImpl(endpoint, code);
	}

	public String code() {
		return client.code();
	}

	public JsonObject content() {
		return client.content();
	}
	
	public String authorityCode(){
		return client.code();
	}

}
