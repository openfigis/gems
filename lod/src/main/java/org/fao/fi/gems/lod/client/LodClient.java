package org.fao.fi.gems.lod.client;

import com.google.gson.JsonObject;

/**
 * A basic interface for Linked Open Data clients
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public interface LodClient {
	
	public String endpoint();
	
	public String code();
	
	public JsonObject content();

}
