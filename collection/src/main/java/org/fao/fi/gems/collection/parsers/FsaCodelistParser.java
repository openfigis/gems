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
import org.fao.fi.gems.collection.properties.EezProperty;
import org.fao.fi.gems.collection.properties.FsaProperty;
import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.entity.GeographicEntityImpl;
import org.fao.fi.gems.lod.entity.common.FLODEezEntity;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.settings.data.filter.DataObjectFilter;
import org.fao.fi.gems.util.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * A codelist parser for FAO Areas
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class FsaCodelistParser implements CodelistParser {
	
	@Override
	public Set<GeographicEntity> getCodelist(GemsConfig config) {
		
		String owner = Utils.whoIsOwner(config);
		
		Set<GeographicEntity> fsaCodelist = new HashSet<GeographicEntity>();
		
		JsonReader reader = null;
		try {
			// read Geoserver data as json
			String url = config.getSettings().getPublicationSettings().getCodelistURL();
			URL dataURL = new URL(url);
		
			reader = new JsonReader(new InputStreamReader(dataURL.openStream()));
			JsonParser parser = new JsonParser();
			JsonObject flodJsonObject = parser.parse(reader).getAsJsonObject();

			JsonArray bindings = flodJsonObject.get("features").getAsJsonArray();

			if (bindings.size() > 0) {
				for(int i = 0;i<bindings.size();i++){
					JsonObject obj = bindings.get(i).getAsJsonObject().get("properties").getAsJsonObject();
					String codeName = "F_CODE";
					JsonElement fsaElem = obj.get(codeName);
					if(fsaElem == null){
						fsaElem = obj.get(codeName.toLowerCase());
					}
					String fsa = fsaElem.getAsString();
					DataObjectFilter fsaFilter = config.getSettings().getGeographicServerSettings().getFilters().getData().get(0);
					EntityCode fsaCode = new EntityCode(fsaFilter, fsa);
					List<EntityCode> fsaCodeStack = Arrays.asList(fsaCode);

					GeographicEntity entity = null;
					
					//wrapEntity by default is true
					//if there is a list of subset then wrap entity only for those ones
					boolean wrapEntity = Utils.wrapEntity(config, fsa);
					
					if(wrapEntity){
						try {
							String collection = config.getSettings().getPublicationSettings().getCollectionType();
							
							Map<GeographicMetaObjectProperty, List<String>> properties = new HashMap<GeographicMetaObjectProperty, List<String>>();
							properties.put(FsaProperty.FAO, Arrays.asList(Utils.buildMetadataIdentifier(owner, collection, fsa)));
							properties.put(FsaProperty.CWP, Arrays.asList(fsa));
							
							
							FLODEezEntity flodEntity = new FLODEezEntity(fsa);
							if(flodEntity.content() != null){
								properties.put(EezProperty.FLOD, Arrays.asList(flodEntity.authorityUri()));
							}
							
							entity = new GeographicEntityImpl(owner, collection, fsaCodeStack, fsa, properties);
							fsaCodelist.add(entity);
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

		return fsaCodelist;
	}

}
