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

import org.fao.fi.gems.codelist.CodelistParser;
import org.fao.fi.gems.entity.EntityAuthority;
import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.entity.GeographicEntityImpl;
import org.fao.fi.gems.lod.entity.common.FLODEezEntity;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.settings.data.filter.DataObjectFilter;
import org.fao.fi.gems.model.settings.publication.EntityList;
import org.fao.fi.gems.util.Utils;

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
	
	public Set<GeographicEntity> getCodelist(GemsConfig config) {
		
		String owner = Utils.whoIsOwner(config);
		
		Set<GeographicEntity> eezCodelist = new HashSet<GeographicEntity>();
		
		JsonReader reader = null;
		try {
			// read Geoserver data
			String url = config.getSettings().getPublicationSettings().getCodelistURL();
			URL dataURL = new URL(url);
		
			reader = new JsonReader(new InputStreamReader(dataURL.openStream()));
			JsonParser parser = new JsonParser();
			JsonObject flodJsonObject = parser.parse(reader).getAsJsonObject();

			JsonArray bindings = flodJsonObject.get("features").getAsJsonArray();

			if (bindings.size() > 0) {
				for(int i = 0;i<bindings.size();i++){
					JsonObject obj = bindings.get(i).getAsJsonObject().get("properties").getAsJsonObject();
					String mrgid = obj.get("mrgid").getAsString();
					DataObjectFilter eezFilter = config.getSettings().getGeographicServerSettings().getFilters().getData().get(0);
					EntityCode eezCode = new EntityCode(eezFilter,mrgid);
					List<EntityCode> eezCodeStack = Arrays.asList(eezCode);
					
					String label = obj.get("eez").getAsString();
					String country = obj.get("country").getAsString();
					String iso_3digit = obj.get("iso_3digit").getAsString();

					GeographicEntity entity = null;
					
					//wrapEntity by default is true
					//if there is a list of subset then wrap entity only for those ones
					boolean wrapEntity = Utils.wrapEntity(config, mrgid);
					
					if(wrapEntity){
						try {
							String collection = config.getSettings().getPublicationSettings().getCollectionType();
							Map<GeographicMetaObjectProperty, List<String>> properties = new HashMap<GeographicMetaObjectProperty, List<String>>();
							properties.put(EezProperty.VLIZ, Arrays.asList(Utils.buildMetadataIdentifier(owner, collection, mrgid)));
							properties.put(EezProperty.MARINEREGIONS, Arrays.asList(mrgid, country, label));
							properties.put(EezProperty.ISO, Arrays.asList(iso_3digit));
							
							
							FLODEezEntity flodEntity = new FLODEezEntity(mrgid);
							if(flodEntity.content() != null){
								properties.put(EezProperty.FLOD, Arrays.asList(flodEntity.authorityUri()));
							}
							
							entity = new GeographicEntityImpl(owner, collection, eezCodeStack, label, properties);
							eezCodelist.add(entity);
						} catch (URISyntaxException e) {
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
