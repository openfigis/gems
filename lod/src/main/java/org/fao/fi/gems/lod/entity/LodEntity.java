package org.fao.fi.gems.lod.entity;

import com.google.gson.JsonObject;

/**
 * Basic interface to wrap Linked-Open-Data entities
 * 
 * @author Emmanuel
 *
 */
public interface LodEntity {

	public String code();
	
	public JsonObject content();
	
	public String authorityUri();
	
	public String authorityCode();
	
	public String authorityName();
	
}
