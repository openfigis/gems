package org.fao.fi.gems.collection.eez;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fao.fi.gems.association.GeographicMetaObjectProperty;
import org.fao.fi.gems.codelist.CodelistParser;
import org.fao.fi.gems.entity.EntityAuthority;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.entity.GeographicEntityImpl;
import org.fao.fi.gems.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * EEZ Codelist parser
 * 
 * @author eblondel
 *
 */
public class EezCodelistParser implements CodelistParser{
	
	private static Logger LOGGER = LoggerFactory.getLogger(EezCodelistParser.class);

	public enum EezProperty implements GeographicMetaObjectProperty{
		
		VLIZ (EntityAuthority.VLIZ, true, true, true),
		FLOD (EntityAuthority.FLOD, true, true, true),
		ISO (EntityAuthority.ISO, true, true, false),
		MARINEREGIONS(EntityAuthority.MARINEREGIONS, true, true, false);
		
		private final Object object;
		private final boolean isAuthority;
		private final boolean isThesaurus;
		private final boolean containsURIs;
		
		EezProperty(Object object, boolean isAuthority, boolean isThesaurus, boolean containsURIs){
			this.object = object;
			this.isAuthority = isAuthority;
			this.isThesaurus = isThesaurus;
			this.containsURIs = containsURIs;
		}
		
		public Object getObject(){
			return this.object;
		}

		public boolean isAuthority(){
			return this.isAuthority;
		}
		
		public boolean isThesaurus() {
			return this.isThesaurus;
		}

		public boolean containsURIs() {
			return this.containsURIs;
		}
		
	}
	
	public Set<GeographicEntity> getCodelist(String owner, String collection,
			String url) {
		
		Set<GeographicEntity> eezCodelist = new HashSet<GeographicEntity>();
		
		JsonReader reader = null;
		try {
			// read Geoserver data
			URL dataURL = new URL(url);
		
			reader = new JsonReader(new InputStreamReader(dataURL.openStream()));
			JsonParser parser = new JsonParser();
			JsonObject flodJsonObject = parser.parse(reader).getAsJsonObject();

			JsonArray bindings = flodJsonObject.get("features").getAsJsonArray();

			if (bindings.size() > 0) {
				for(int i = 0;i<bindings.size();i++){
					JsonObject obj = bindings.get(i).getAsJsonObject().get("properties").getAsJsonObject();
					String mrgid = obj.get("mrgid").getAsString();
					String label = obj.get("eez").getAsString();
					String country = obj.get("country").getAsString();
					String iso_3digit = obj.get("iso_3digit").getAsString();

					GeographicEntity entity =null;
					try {
						Map<GeographicMetaObjectProperty, List<String>> properties = new HashMap<GeographicMetaObjectProperty, List<String>>();
						properties.put(EezProperty.VLIZ, Arrays.asList(Utils.buildMetadataIdentifier(owner, collection, mrgid)));
						properties.put(EezProperty.MARINEREGIONS, Arrays.asList(mrgid, country, label));
						properties.put(EezProperty.ISO, Arrays.asList(iso_3digit));
						
						
						FLODEezEntity flodEntity = new FLODEezEntity(mrgid);
						if(flodEntity.getFlodContent() != null){
							properties.put(EezProperty.FLOD, Arrays.asList(flodEntity.getCodedEntity()));
						}
						
						entity = new GeographicEntityImpl(owner, collection, mrgid, label, properties);
						eezCodelist.add(entity);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
					
				}
			}

			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}


		
		return eezCodelist;
	}

}
