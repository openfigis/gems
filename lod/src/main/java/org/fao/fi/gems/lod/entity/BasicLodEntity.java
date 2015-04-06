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
	
	/**
	 * BasicLodEntity constructor
	 * 
	 * @param endpoint
	 * @param code
	 * @throws Exception
	 */
	public BasicLodEntity(String endpoint, String code) throws Exception{
		client = new LodClientImpl(endpoint, code);
	}

	/**
	 * Get the LodEntity code
	 * 
	 * @return the code
	 * 
	 */
	public String code() {
		return client.code();
	}

	/**
	 * Get the LodEntity content of the upstream service response
	 * 
	 * @return a JsonObject
	 * 
	 */
	public JsonObject content() {
		return client.content();
	}
	
	/**
	 * Get the LodEntityt authority code
	 * 
	 * @return the authority code
	 * 
	 */
	public String authorityCode(){
		return client.code();
	}

}
