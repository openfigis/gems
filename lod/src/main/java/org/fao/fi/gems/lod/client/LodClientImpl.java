package org.fao.fi.gems.lod.client;

import static javax.ws.rs.client.ClientBuilder.newClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.filter.EncodingFilter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * A basic client for Linked Open Data services
 * compliant with SPARQL 1.0
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class LodClientImpl implements LodClient{
	
	
	private String endpoint;
	private String code;
	
	private Client client;
	private Builder builder;
	private JsonObject content;
	
	/**
	 * LodClient constructor
	 * 
	 * @param endpoint
	 * @param code
	 * @throws Exception
	 */
	public LodClientImpl(String endpoint, String code) throws Exception{
		
		this.endpoint = endpoint;
		this.code = code;
		
		this.client = configureClient();
		this.builder = configureBuilder();
		this.content = requestData();
		
	};
	
	/**
	 * Get the LodClient endpoint
	 * 
	 * @return the endpoint
	 */
	public String endpoint(){
		return this.endpoint;
	}
	
	/**
	 * Get the LodClient code
	 * 
	 * @return the code
	 */
	public String code(){
		return this.code;
	}
	
	
	/**
	 * Get the Json service response
	 * 
	 * @the the service response in JsonObject
	 * 
	 */
	public JsonObject content(){		
		return this.content;
	}
	

	/**
	 * Constructs a basic client
	 * 
	 * @return
	 */
	private Client configureClient(){
		Client client = newClient()
				.register(EncodingFilter.class);
		return client;
	}
	
	/**
	 * Configures the service request builder
	 * 
	 * @return
	 */
	private Builder configureBuilder(){
		WebTarget target = client.target(endpoint).queryParam("code", code);
		return target.request();
	}
	
	/**
	 * Request the data from service
	 * 
	 * @return a JsonObject
	 * @throws Exception 
	 */
	private JsonObject requestData() throws Exception{
		
		
		JsonObject result = null;
		JsonObject content = null;
		
		try {
			InputStream is = this.builder.get(InputStream.class);
			JsonReader reader = new JsonReader(new InputStreamReader(is));
			JsonParser parser = new JsonParser();
			result = parser.parse(reader).getAsJsonObject();
			
			JsonArray bindings = result
					.get("results").getAsJsonObject()
					.get("bindings").getAsJsonArray();
			
			if( bindings.size() > 0){
				content = bindings.get(0).getAsJsonObject();
			}
			is.close();
			reader.close();
			
		} catch (Exception e) {
			throw new Exception("Failed to get LOD content", e);
		}		
		
		return content;
	}

}
