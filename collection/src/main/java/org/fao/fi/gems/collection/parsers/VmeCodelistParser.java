/**
 * (c) 2015 FAO / UN (project: gems-collection)
 */
package org.fao.fi.gems.collection.parsers;

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
import org.fao.fi.gems.collection.properties.VmeProperty;
import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.entity.FigisGeographicEntityImpl;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.settings.data.filter.DataObjectFilter;
import org.fao.fi.gems.util.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * VME measure codelist parser
 * (reference for publishing Metadata at VME measure resolution)
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class VmeCodelistParser implements CodelistParser{

	private static final String VME_WS_SERVICE = "http://www.fao.org/figis/ws/vme/webservice/get?inventoryIdentifier=";
	
	public Set<GeographicEntity> getCodelist(GemsConfig config) {
		
		Set<GeographicEntity> vmeCodelist = new HashSet<GeographicEntity>();
		
		String owner = Utils.whoIsOwner(config);
		
		JsonReader reader = null;
		try {
			// read Geoserver data
			String url = config.getSettings().getPublicationSettings().getCodelistURL();
			URL dataURL = new URL(url);
		
			reader = new JsonReader(new InputStreamReader(dataURL.openStream()));
			JsonParser parser = new JsonParser();
			JsonObject wfsObject = parser.parse(reader).getAsJsonObject();

			JsonArray bindings = wfsObject.get("features").getAsJsonArray();

			if (bindings.size() > 0) {
				for(int i = 0;i<bindings.size();i++){
					JsonObject obj = bindings.get(i).getAsJsonObject().get("properties").getAsJsonObject();
					String vmeId = obj.get("VME_ID").getAsString();
					DataObjectFilter vmeFilter = config.getSettings().getGeographicServerSettings().getFilters().getData().get(0);
					EntityCode vmeEntityCode = new EntityCode(vmeFilter, vmeId);
					
					List<EntityCode> vmeCodeStack = Arrays.asList(vmeEntityCode);
					
					//wrapEntity by default is true
					//if there is a list of subset then wrap entity only for those ones
					boolean wrapEntity = Utils.wrapEntity(config, vmeId);
					
					if(wrapEntity){
						String dataOwner = obj.get("OWNER").getAsString();
						String localName = obj.get("LOCAL_NAME").getAsString();
						String globalName = obj.get("GLOB_NAME").getAsString();
						String globalType = obj.get("GLOB_TYPE").getAsString();
						
						//basetitle
						String basetitle = null;
						if(globalType.matches("VME")){
								basetitle = "VME closed areas";
								
						}else if(globalType.matches("BTM_FISH")){
								basetitle = "Bottom fishing areas";
								
						}else if(globalType.matches("OTHER")){
								basetitle = "Other areas";
						}
						basetitle += " related to UNGA Res. 61-105 – ";
						
						//title
						String title = localName + " ("+dataOwner+")";
						
						//style
						String style = "MEASURES_" + globalType + "_for_" + owner;
						
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
								if(vmeEntity.code().matches(vmeId)){
									addEntity = false;
									break;
								}
							}
							
							if(addEntity){
								Map<GeographicMetaObjectProperty, List<String>> properties = new HashMap<GeographicMetaObjectProperty, List<String>>();
								//properties.put(VmeProperty.FAO, Arrays.asList(Utils.buildMetadataIdentifier(owner, collection, vmeId)));
								properties.put(VmeProperty.VME, Arrays.asList(vmeId, localName, globalName));
								if(figisId != null) properties.put(VmeProperty.FIGIS, Arrays.asList(figisId));
								
								//add basetitle
								properties.put(VmeProperty.BASETITLE, Arrays.asList(basetitle));
								
								//add global type (used for the mapviewer link)
								properties.put(VmeProperty.GLOBALTYPE, Arrays.asList(globalType));
								
								//add style
								properties.put(VmeProperty.STYLE, Arrays.asList(style));
								
								String collection = config.getSettings().getPublicationSettings().getCollectionType();
								entity = new FigisGeographicEntityImpl(owner, collection, vmeCodeStack, title, properties);
								if(figisId != null) entity.setFigisId(figisId);
								entity.setFigisDomain("vme");
								
								vmeCodelist.add(entity);
							}
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}
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
