package org.fao.fi.gems.collection.species;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * FLOD Species Entity
 * 
 * @author eblondel (FAO)
 *
 */
public class FLODSpeciesEntity {


    private static String flodURL = "http://www.fao.org/figis/flod/askflod/json/ce4a3c.jsp?code=";
	private String alphacode;
	private JsonObject flodJsonObject;
	
	/** Constructor
	 * 
	 * @param alphacode
	 */
	public FLODSpeciesEntity(String alphacode){
		this.alphacode = alphacode;
		this.flodJsonObject =retrieveFlodContent(alphacode);
	};
	
	
	
	/** Get alphacode
	 * 
	 * @return
	 */
	public String getAlphacode(){
		return this.alphacode;
	}
	
	
	/** get json as string
	 * 
	 * @return
	 */
	public String getJsonAsString(){
		return this.flodJsonObject.toString();
	}
	
	
	
	
	
	/** get FLOD Coded Entity ASFIS uri (reference for species in FLOD)
	 * 
	 * @return the flod entity ASFIS uri
	 */
	public String getASFISCodedEntity(){
		String codedEntity = null;
		JsonElement element = this.flodJsonObject.get("asfis_uri");
		if(element != null){
			codedEntity = element.getAsJsonObject().get("value").getAsString();
		}
										
		return codedEntity;
	}
	
	
	/** get ASFIS scientific name
	 * 
	 * @return the ASFIS scientific name
	 */
	public String getAsfisScientificName(){
		String asfisScientificName = null;
		JsonElement element = this.flodJsonObject.get("asfis_scname");
		if(element != null){
			asfisScientificName = element.getAsJsonObject().get("value").getAsString();
		}
										
		return asfisScientificName;
	}
	
	/** get ASFIS english name
	 * 
	 * @return the ASFIS english
	 */
	public String getAsfisEnglishName(){
		String asfisScientificName = null;
		JsonElement element = this.flodJsonObject.get("asfis_name");
		if(element != null){
			asfisScientificName = element.getAsJsonObject().get("value").getAsString();
		}
										
		return asfisScientificName;
	}
	
	
	/** get WoRMS scientific name
	 * 
	 * @return the WoRMS scientific name
	 */
	public String getWormsScientificName(){
		String wormsScientificName = null;
		JsonElement element = this.flodJsonObject.get("worms_scname");
		if(element != null){
			wormsScientificName = element.getAsJsonObject().get("value").getAsString();
		}
										
		return wormsScientificName;
	}
	
	
	
	/** get LSID
	 * 
	 * @return the LSID
	 */
	public String getLSID(){
		String LSID = null;
		JsonElement element = this.flodJsonObject.get("worms_lsid");
		if(element != null){
			LSID = element.getAsJsonObject().get("value").getAsString();
		}
										
		return LSID;
	}
	
	
	
	/** get AphiaID
	 * 
	 * @return the AphiaID
	 */
	public String getAphiaID(){
		String aphiaID = null;
		if(getLSID() != null){
			aphiaID = getLSID().split("taxname:")[1];
		}
		return aphiaID;
	}
	
	
	/** get FIGIS Coded entity
	 * 
	 * @return
	 */
	public String getFIGISCodedEntity(){
		String codedEntity = this.flodJsonObject
								.get("figis_uri").getAsJsonObject()
								.get("value").getAsString();
		return codedEntity;
	}
	
	
	
	/** get FIGIS Coded entity
	 * 
	 * @return
	 */
	public String getFigisID(){
		String uri = this.flodJsonObject
								.get("figis_id").getAsJsonObject()
								.get("value").getAsString();
		return uri.split("speciescode/")[1];
	}
	
	
	
	/** Get FLOD json object
	 *  The output contains
	 *  - the WoRMS scientific name
	 *  - the FLOD species code entity URI
	 *  
	 * @param alphacode
	 * @return
	 */
   private JsonObject retrieveFlodContent(String alphacode){
		
	   String url = flodURL + alphacode;
		
	   JsonObject flodJsonObject = null;
		JsonObject flodEntity = null;
		JsonReader reader = null;
		try {
			//read Json data
			URL dataURL = new URL(url);
			reader = new JsonReader(new InputStreamReader(dataURL.openStream()));
			JsonParser parser = new JsonParser();
			flodJsonObject = parser.parse(reader).getAsJsonObject();
			
			JsonArray bindings = flodJsonObject
					.get("results").getAsJsonObject()
					.get("bindings").getAsJsonArray();
			
			if( bindings.size() > 0){
				flodEntity = flodJsonObject
						.get("results").getAsJsonObject()
						.get("bindings").getAsJsonArray().get(0).getAsJsonObject();
			}
			
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return flodEntity;
		//return flodJsonObject;
	}
    
   
   
   public JsonObject getFlodContent(){
	   return this.flodJsonObject;
   }
   	
	
}
