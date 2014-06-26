package org.fao.fi.gems.collection.eez;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * FLOD TEST entity
 * Here the SPARQL endpoint is queried directly (no service available)
 * 
 * @author eblondel (FAO)
 *
 */
public class FLODEezEntity {

	private String code;
	private JsonObject flodJsonObject;

	/**
	 * Constructor
	 * 
	 * @param alphacode
	 */
	public FLODEezEntity(String code) {
		this.code = code;
		this.flodJsonObject = retrieveFlodContent(code);
	};

	/**
	 * Get EEZ mrgid
	 * 
	 * @return
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * get json as string
	 * 
	 * @return
	 */
	public String getJsonAsString() {
		return this.flodJsonObject.toString();
	}

	/**
	 * get FLOD Coded Entity EEZ uri
	 * 
	 * @return the flod entity EEZ uri
	 */
	public String getCodedEntity() {
		String codedEntity = null;
		JsonElement element = this.flodJsonObject.get("eez");
		if (element != null) {
			codedEntity = element.getAsJsonObject().get("value").getAsString();
		}

		return codedEntity;
	}

	/**
	 * get EEZ name
	 * 
	 * @return the EEZ name
	 */
	public String getName() {
		String eezName = null;
		JsonElement element = this.flodJsonObject.get("eez_label");
		if (element != null) {
			eezName = element.getAsJsonObject().get("value").getAsString()
					.split("\\(")[0];		
		}
		return eezName;
	}

	/**
	 * Get FLOD json object The output contains:
	 * - the EEZ code (mrgid)
	 * - the EEZ FLOD uri
	 * - the EEZ name
	 * 
	 * @param code
	 * @return
	 */
	private JsonObject retrieveFlodContent(String code) {

		String url = "http://www.fao.org/figis/flod/askflod/json/ce4eez.jsp?code=" + code;

		JsonObject flodJsonObject = null;
		JsonObject flodEntity = null;
		JsonReader reader = null;
		try {
			// read Json data
			URL dataURL = new URL(url);
			reader = new JsonReader(new InputStreamReader(dataURL.openStream()));
			JsonParser parser = new JsonParser();
			flodJsonObject = parser.parse(reader).getAsJsonObject();

			JsonArray bindings = flodJsonObject.get("results")
					.getAsJsonObject().get("bindings").getAsJsonArray();

			if (bindings.size() > 0) {
				flodEntity = flodJsonObject.get("results").getAsJsonObject()
						.get("bindings").getAsJsonArray().get(0)
						.getAsJsonObject();
			}

			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return flodEntity;
		// return flodJsonObject;
	}

	public JsonObject getFlodContent() {
		return this.flodJsonObject;
	}

}
