/**
 * (c) 2015 FAO / UN (project: gems-collection)
 */
package org.fao.fi.gems.collection.parsers;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.fao.fi.gems.codelist.CodelistParser;
import org.fao.fi.gems.collection.properties.SpeciesProperty;
import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.entity.FigisGeographicEntityImpl;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.lod.entity.common.FLODSpeciesEntity;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.settings.data.filter.DataObjectFilter;
import org.fao.fi.gems.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Species Codelist parser
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class SpeciesCodelistParser implements CodelistParser{
	
	public Set<GeographicEntity> getCodelist(GemsConfig config) {
		
		String owner = Utils.whoIsOwner(config);
		
		//available styles
		Random styleRandomizer = new Random();
		String[] styleColors = { "852D36", "A60314", "FF031C", "FA485B",
				"F58E98", "CC8914", "FAA616", "FAC002", "F0E92E", "FAFA5F"};
		List<String> styleColorList = Arrays.asList(styleColors);
		
		//parse codelist
		Set<GeographicEntity> codelist = new LinkedHashSet<GeographicEntity>();
		try {

			String specieslist = config.getSettings().getPublicationSettings().getCodelistURL();
			URL url = new URL(specieslist);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(url.openStream());
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("item");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				// for (int temp = 0; temp < 20; temp++) { //test with the first
				// 10 species
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Map<GeographicMetaObjectProperty, List<String>> properties = new HashMap<GeographicMetaObjectProperty, List<String>>();
					
					Element eElement = (Element) nNode;
					
					//codes
					String alphacode = eElement.getAttribute("a3c");
					DataObjectFilter speciesFilter = config.getSettings().getGeographicServerSettings().getFilters().getData().get(0);
					EntityCode speciesEntityCode = new EntityCode(speciesFilter, alphacode);
					List<EntityCode> speciesCodeStack = Arrays.asList(speciesEntityCode);
					
					String FigisId = eElement.getAttribute("FigisID");
					
					//wrapEntity by default is true
					//if there is a list of subset then wrap entity only for those ones
					boolean wrapEntity = Utils.wrapEntity(config, alphacode);
					
					if(wrapEntity){
						//other properties
						String englishName = eElement.getAttribute("en");
						String scName = eElement.getAttribute("lt");
						String family = eElement.getAttribute("family");
						String order = eElement.getAttribute("order");
						
						//habitat
						String mar = eElement.getAttribute("mar");
						String inl = eElement.getAttribute("inl");
						String hab = null;
						if (mar.equals("1")) {
							if (inl.equals("1")) {
								hab = "mi";
							} else {
								hab = "m";
							}
						} else if (mar.equals("0")) {
							if (inl.equals("1")) {
								hab = "i";
							}
	
						}
						
						//style
						String randomStyle = "species_style_"+ styleColorList.get(styleRandomizer.nextInt(styleColorList.size()));
						
						//properties
						//----------
						//FAO
						String collection = config.getSettings().getPublicationSettings().getCollectionType();
						properties.put(SpeciesProperty.FAO, Arrays.asList(Utils.buildMetadataIdentifier(owner, collection, alphacode)));
						
						//ASFIS
						properties.put(SpeciesProperty.ASFIS, Arrays.asList(alphacode, englishName, scName, family, order));
						
						//FIGIS
						properties.put(SpeciesProperty.FIGIS, Arrays.asList(FigisId));
						
						//FLOD & WoRMS (inherited from FLOD)
						FLODSpeciesEntity flodEntity = new FLODSpeciesEntity(alphacode);
						if(flodEntity.content() != null){
							properties.put(SpeciesProperty.FLOD, Arrays.asList(flodEntity.authorityUri()));
							
							if(flodEntity.wormsAphiaID() != null){//control because not all species in FLOD have worms info
								properties.put(SpeciesProperty.WORMS, Arrays.asList(flodEntity.wormsAphiaID(), flodEntity.wormsScientificName()));
							}	
						}
						
						//Others (habitat, style)
						properties.put(SpeciesProperty.HABITAT, Arrays.asList(hab));
						properties.put(SpeciesProperty.STYLE, Arrays.asList(randomStyle));
						
						//create Geographic entity
						FigisGeographicEntityImpl entity = new FigisGeographicEntityImpl(owner, collection, speciesCodeStack, scName, properties, null);
						
						//specific Figis stuff required
						entity.setFigisDomain("species");
						entity.setFigisId(FigisId);
						entity.setFigisViewerId(alphacode+"-"+hab);
						
						//add entity to codelist
						codelist.add(entity);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return codelist;
	}

}
