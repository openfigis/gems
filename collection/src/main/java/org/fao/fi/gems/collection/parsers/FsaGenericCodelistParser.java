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
import org.fao.fi.gems.entity.FigisGeographicEntityImpl;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.lod.entity.common.FLODFsaEntity;
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
 * A generic abstract codelist parser for FAO Areas
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public abstract class FsaGenericCodelistParser implements CodelistParser {
	
	boolean nested;
	
	static final String FIELD_CODE = "F_CODE";
	static final String FIELD_LEVEL = "F_LEVEL";
	
	public FsaGenericCodelistParser(boolean nested){
		this.nested = nested;
	}
	
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
					
					DataObjectFilter fsaFilter = null;
					EntityCode fsaCode = null;
					if (nested) {
						String nestingFieldName = null;
						switch(fsaLevel){
						case "MAJOR":
							nestingFieldName = "F_AREA";
							break;
						case "SUBAREA":
							nestingFieldName = "F_SUBAREA";
							break;
						case "DIVISION":
							nestingFieldName = "F_DIVISION";
							break;
						case "SUBDIVISION":
							nestingFieldName = "F_SUBDIVIS";
							break;
						case "SUBUNIT":
							nestingFieldName = "F_SUBUNIT";
							break;	
						}
						fsaFilter = new DataObjectFilter();
						fsaFilter.setIsString(true);
						fsaFilter.setProperty(nestingFieldName);
						
					} else {
						fsaFilter = config.getSettings().getGeographicServerSettings().getFilters().getData().get(0);
					}
					
					fsaCode = new EntityCode(fsaFilter, fsa);
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
								if(waterAreaRef.getAttribute("Code").equalsIgnoreCase(fsa.replaceAll(" ", ""))){
								
									//get name
									Element areaIdent = (Element) waterAreaRef.getParentNode();
									Element nameEl = (Element) areaIdent.getElementsByTagName("fi:Name").item(0);
									fsaName = nameEl.getTextContent();
									
									if(!fsaLevel.equalsIgnoreCase("MAJOR")){
										fsaName = fsaName.replace(")", " ");
										fsaName += "of FAO Major Area " + majorFsa + ")";
									}
									
									if(nested && !fsaFilter.getProperty().equalsIgnoreCase("F_SUBUNIT")){
										fsaName += " and its breakdown";
									}
									
									break;
								}
								
								if(fsaName == null){
									switch(fsaLevel){
									case "SUBAREA":
										fsaName = "Subarea " + fsa;
										break;
									case "DIVISION":
										fsaName = "Division " + fsa;
										break;
									case "SUBDIVISION":
										fsaName = "Subdivision " + fsa;
										break;
									case "SUBUNIT":
										fsaName = "Subunit " + fsa;
										break;
									}
								}
							}
							properties.put(FsaProperty.CWP, Arrays.asList(fsa, fsaName, fsaLevel));
							
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
								parentFsa = parentFsa.replaceAll(" ", "");
								EntityCode parentCode = new EntityCode(null, parentFsa);
								List<EntityCode> parentCodeStack = Arrays.asList(parentCode);
								parentEntity = new FigisGeographicEntityImpl(owner, collection, parentCodeStack, null, null, null);
							}
							
							entity = new FigisGeographicEntityImpl(owner, collection, fsaCodeStack, fsaName, properties, parentEntity);
							
							if(!nested || (nested && fsaLevel != "SUBUNIT")){
								fsaCodelist.add(entity);
							}
						} catch (URISyntaxException e) {
							e.printStackTrace();
						} catch (Exception e) {
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
