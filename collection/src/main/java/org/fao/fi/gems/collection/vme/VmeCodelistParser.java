package org.fao.fi.gems.collection.vme;

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
import org.fao.fi.gems.entity.EntityAddin;
import org.fao.fi.gems.entity.EntityAuthority;
import org.fao.fi.gems.entity.FigisGeographicEntityImpl;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * VME codelist parser
 * 
 * @author eblondel
 *
 */
public class VmeCodelistParser implements CodelistParser{

	private static final String VME_WS_SERVICE = "http://figisapps.fao.org/figis/ws/vme/webservice/get?inventoryIdentifier=";
	
	private static Logger LOGGER = LoggerFactory.getLogger(VmeCodelistParser.class);
	
	public enum VmeProperty implements GeographicMetaObjectProperty{
		
		FAO (EntityAuthority.FAO, true, true, true),
		FIGIS (EntityAuthority.FIGIS, true, true, false),
		
		VME ("VME", false, true, false),
		GLOBALTYPE("GLOBALTYPE", false, false, false),
		STYLE(EntityAddin.STYLE, false, false, false);
	
		private final Object object;
		private final boolean isAuthority;
		private final boolean isThesaurus;
		private final boolean containsURIs;
		
		VmeProperty(Object object, boolean isAuthority, boolean isThesaurus, boolean containsURIs){
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
		
		public boolean isThesaurus(){
			return this.isThesaurus;
		}

		public boolean containsURIs() {
			return this.containsURIs;
		}
		
	}	
	
	public Set<GeographicEntity> getCodelist(String owner, String collection,
			String url) {
		
		Set<GeographicEntity> vmeCodelist = new HashSet<GeographicEntity>();
		
		JsonReader reader = null;
		try {
			// read Geoserver data
			URL dataURL = new URL(url);
		
			reader = new JsonReader(new InputStreamReader(dataURL.openStream()));
			JsonParser parser = new JsonParser();
			JsonObject wfsObject = parser.parse(reader).getAsJsonObject();

			JsonArray bindings = wfsObject.get("features").getAsJsonArray();

			if (bindings.size() > 0) {
				for(int i = 0;i<bindings.size();i++){
					JsonObject obj = bindings.get(i).getAsJsonObject().get("properties").getAsJsonObject();
					String vmeId = obj.get("VME_ID").getAsString();
					String dataOwner = obj.get("OWNER").getAsString();
					String localName = obj.get("LOCAL_NAME").getAsString();
					String globalName = obj.get("GLOB_NAME").getAsString();
					String globalType = obj.get("GLOB_TYPE").getAsString();
					
					String title = localName + " ("+globalName+" - "+dataOwner+")";
					
					//retrieving FIGIS stuff
					String figisId = null;
					String wsRequestUrl = VME_WS_SERVICE + vmeId;
					URL wsUrl = new URL(wsRequestUrl);
					JsonReader wsReader = new JsonReader(new InputStreamReader(wsUrl.openStream()));
					JsonParser wsParser = new JsonParser();
					JsonObject wsObject = wsParser.parse(wsReader).getAsJsonObject();
					
					JsonArray wsRequest = wsObject.get("resultList").getAsJsonArray();
					if(wsRequest != null){
						if(wsRequest.size() > 0){	
							figisId = wsRequest.get(0).getAsJsonObject().get("vmeId").getAsString();
						}
					}
					wsReader.close();
					
					//configure geographic entity	
					FigisGeographicEntityImpl entity = null;
					try {
						boolean addEntity = true;
						for(GeographicEntity vmeEntity : vmeCodelist){
							if(vmeEntity.getCode().matches(vmeId)){
								addEntity = false;
								break;
							}
						}
						
						if(addEntity){
							Map<GeographicMetaObjectProperty, List<String>> properties = new HashMap<GeographicMetaObjectProperty, List<String>>();
							//properties.put(VmeProperty.FAO, Arrays.asList(Utils.buildMetadataIdentifier(owner, collection, vmeId)));
							properties.put(VmeProperty.VME, Arrays.asList(vmeId, localName, globalName));
							if(figisId != null) properties.put(VmeProperty.FIGIS, Arrays.asList(figisId));
							
							//add global type (used for the mapviewer link)
							properties.put(VmeProperty.GLOBALTYPE, Arrays.asList(globalType));
							
							//add style
							String style = "MEASURES_" + globalType + "_for_" + owner;
							properties.put(VmeProperty.STYLE, Arrays.asList(style));
								
							entity = new FigisGeographicEntityImpl(owner, collection, vmeId, title, properties);
							if(figisId != null) entity.setFigisId(figisId);
							entity.setFigisDomain("vme");
							
							vmeCodelist.add(entity);
						}
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
					
				}
			}

			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return vmeCodelist;
		
	}

}
