package org.fao.fi.gems.collection.rfb;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * FLOD RFB entity
 * 
 * @author eblondel (FAO)
 *
 */
public class FLODRfbEntity {

	private static String flodURL = "http://www.fao.org/figis/flod/askflod/json/ce4rfb.jsp?code=";
	private String code;
	private JsonObject flodJsonObject;

	/**
	 * Constructor
	 * 
	 * @param alphacode
	 */
	public FLODRfbEntity(String code) {
		this.code = code;
		this.flodJsonObject = retrieveFlodContent(code);
	};

	/**
	 * Get RFB acronym
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
	 * get FLOD Coded Entity RFB uri (reference for rfb in FLOD)
	 * 
	 * @return the flod entity RFB uri
	 */
	public String getRfbCodedEntity() {
		String codedEntity = null;
		JsonElement element = this.flodJsonObject.get("rfb");
		if (element != null) {
			codedEntity = element.getAsJsonObject().get("value").getAsString();
		}

		return codedEntity;
	}

	/**
	 * get RFB name
	 * 
	 * @return the RFB name
	 */
	public String getName() {
		String rfbName = null;
		JsonElement element = this.flodJsonObject.get("rfb_label");
		if (element != null) {
			String n = element.getAsJsonObject().get("value").getAsString()
					.split("\\(")[0];
			rfbName = n.substring(0, n.length() - 1);
		}

		return rfbName;
	}

	/**
	 * Get FLOD json object The output contains - the WoRMS scientific name -
	 * the FLOD species code entity URI
	 * 
	 * @param alphacode
	 * @return
	 */
	private JsonObject retrieveFlodContent(String alphacode) {

		String url = flodURL + alphacode;

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
