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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.fao.fi.gems.codelist.CodelistParser;
import org.fao.fi.gems.collection.properties.FsaProperty;
import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.entity.GeographicEntityImpl;
import org.fao.fi.gems.lod.entity.common.FLODFsaEntity;
import org.fao.fi.gems.metaobject.GeographicMetaObject;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.settings.data.filter.DataObjectFilter;
import org.fao.fi.gems.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
	
	static final String FIELD_CODE = "F_CODE";
	static final String FIELD_LEVEL = "F_LEVEL";
	
	@Override
	public Set<GeographicEntity> getCodelist(GemsConfig config) {
		
		String owner = Utils.whoIsOwner(config);
		String collection = config.getSettings().getPublicationSettings().getCollectionType();
		
		Set<GeographicEntity> fsaCodelist = new HashSet<GeographicEntity>();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		
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
					JsonElement fsaElem = obj.get(FIELD_CODE);
					if(fsaElem == null){
						fsaElem = obj.get(FIELD_CODE.toLowerCase());
					}
					String fsa = fsaElem.getAsString();
					String majorFsa = fsa.substring(0,2);
					
					JsonElement levelElem = obj.get(FIELD_LEVEL);
					if(levelElem == null){
						levelElem = obj.get(FIELD_LEVEL.toLowerCase());
					}
					String fsaLevel = levelElem.getAsString();
					
					DataObjectFilter fsaFilter = config.getSettings().getGeographicServerSettings().getFilters().getData().get(0);
					EntityCode fsaCode = new EntityCode(fsaFilter, fsa);
					List<EntityCode> fsaCodeStack = Arrays.asList(fsaCode);

					GeographicEntity entity = null;
					
					//wrapEntity by default is true
					//if there is a list of subset then wrap entity only for those ones
					boolean yetIncluded = false;
					for(GeographicEntity included : fsaCodelist){
						if(included.code().equals(fsa)){
							yetIncluded = true;
							break;
						}
					}
					
					boolean wrapEntity = Utils.wrapEntity(config, fsa) && !yetIncluded;
					if(wrapEntity){
						try {
							
							Map<GeographicMetaObjectProperty, List<String>> properties = new HashMap<GeographicMetaObjectProperty, List<String>>();
							properties.put(FsaProperty.FAO, Arrays.asList(Utils.buildMetadataIdentifier(owner, collection, fsa)));
							properties.put(FsaProperty.CWP, Arrays.asList(fsa));
							
							FLODFsaEntity flodEntity = new FLODFsaEntity(fsa);
							if(flodEntity.content() != null){
								properties.put(FsaProperty.FLOD, Arrays.asList(flodEntity.authorityUri()));
							}
							
							//Fsa name & abstract
							String fsaName = null;
							URL fsURL = new URL("http://www.fao.org/fishery/xml/area/Area" + majorFsa);
							Document doc = dBuilder.parse(fsURL.openStream());	

							NodeList waterAreaRefs = doc.getDocumentElement().getElementsByTagName("fi:WaterAreaRef");
							for(int j=0;j<waterAreaRefs.getLength();j++){
								Element waterAreaRef = (Element) waterAreaRefs.item(j);
								if(waterAreaRef.getAttribute("Code").equalsIgnoreCase(fsa)){
								
									//get name
									Element areaIdent = (Element) waterAreaRef.getParentNode();
									Element nameEl = (Element) areaIdent.getElementsByTagName("fi:Name").item(0);
									fsaName = nameEl.getTextContent();
									if(fsaLevel != "MAJOR"){
										fsaName = fsaName.replace(")", " ");
										fsaName += "of FAO Major Area " + majorFsa + ")";
									}
									
									break;
								}
							}
							
							//parent
							GeographicEntity parentEntity = null;
							
							String parentFsaLevel = null;
							switch(fsaLevel){
								case "SUBAREA":
									parentFsaLevel = "F_AREA";
									break;
								case "DIVISION":
									parentFsaLevel = "F_SUBAREA";
									break;
								case "SUBDIVISION":
									parentFsaLevel = "F_DIVISION";
									break;
								case "SUBUNIT":
									parentFsaLevel = "F_SUBDIVIS";
									break;
							}
							if(parentFsaLevel != null){
								JsonElement parentElem = obj.get(parentFsaLevel);
								if(parentElem == null){
									parentElem = obj.get(parentFsaLevel.toLowerCase());
								}
								String parentFsa = parentElem.getAsString();
								EntityCode parentCode = new EntityCode(fsaFilter, parentFsa);
								List<EntityCode> parentCodeStack = Arrays.asList(parentCode);
								parentEntity = new GeographicEntityImpl(owner, collection, parentCodeStack, null, null, null);
							}
							
							entity = new GeographicEntityImpl(owner, collection, fsaCodeStack, fsaName, properties, parentEntity);
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
