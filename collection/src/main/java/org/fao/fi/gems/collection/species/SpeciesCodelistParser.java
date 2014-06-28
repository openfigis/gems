package org.fao.fi.gems.collection.species;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.fao.fi.gems.association.GeographicMetaObjectProperty;
import org.fao.fi.gems.codelist.CodelistParser;
import org.fao.fi.gems.entity.EntityAddin;
import org.fao.fi.gems.entity.EntityAuthority;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.entity.GeographicEntityImpl;
import org.fao.fi.gems.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Species Codelist parser
 * 
 * @author eblondel
 *
 */
public class SpeciesCodelistParser implements CodelistParser{

	private static Logger LOGGER = LoggerFactory.getLogger(SpeciesCodelistParser.class);
	
	public enum SpeciesProperty implements GeographicMetaObjectProperty{
		
		FAO(EntityAuthority.FAO, true, true, true),
		FLOD (EntityAuthority.FLOD, true, true, true),
		FIGIS (EntityAuthority.FIGIS, true, true, false),
		ASFIS (EntityAuthority.ASFIS, true, true, false),
		WORMS (EntityAuthority.WORMS, true, true, false),
		
		STYLE(EntityAddin.STYLE, false, false, false),
		HABITAT(EntityAddin.HABITAT, false, false, false);
	
		private final Object object;
		private final boolean isAuthority;
		private final boolean isThesaurus;
		private final boolean containsURIs;
		
		SpeciesProperty(Object object, boolean isAuthority, boolean isThesaurus, boolean containsURIs){
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
	
	public Set<GeographicEntity> getCodelist(String owner, String collection, String specieslist) {
		
		//available styles
		Random styleRandomizer = new Random();
		String[] styleColors = { "852D36", "A60314", "FF031C", "FA485B",
				"F58E98", "CC8914", "FAA616", "FAC002", "F0E92E", "FAFA5F",
				"B5B7CF", "9B9EBF", "C3CBDF", "8B92DF", "D4D1EF", "B8B5CF",
				"B7AFFF", "BFBFBF", "9FB9FF", "8F98FF" };
		List<String> styleColorList = Arrays.asList(styleColors);
		
		//parse codelist
		Set<GeographicEntity> codelist = new HashSet<GeographicEntity>();
		try {

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
					String FigisId = eElement.getAttribute("FigisID");
					
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
					properties.put(SpeciesProperty.FAO, Arrays.asList(Utils.buildMetadataIdentifier(owner, collection, alphacode)));
					
					//ASFIS
					properties.put(SpeciesProperty.ASFIS, Arrays.asList(alphacode, englishName, scName, family, order));
					
					//FIGIS
					properties.put(SpeciesProperty.FIGIS, Arrays.asList(FigisId));
					
					//FLOD & WoRMS (inherited from FLOD)
					FLODSpeciesEntity flodEntity = new FLODSpeciesEntity(alphacode);
					if(flodEntity.getFlodContent() != null){
						properties.put(SpeciesProperty.FLOD, Arrays.asList(flodEntity.getASFISCodedEntity()));
						
						if(flodEntity.getAphiaID() != null){//control because not all species in FLOD have worms info
							properties.put(SpeciesProperty.WORMS, Arrays.asList(flodEntity.getAphiaID(), flodEntity.getWormsScientificName()));
						}	
					}
					
					//Others (habitat, style)
					properties.put(SpeciesProperty.HABITAT, Arrays.asList(hab));
					properties.put(SpeciesProperty.STYLE, Arrays.asList(randomStyle));
					
					//create Geographic entity
					GeographicEntityImpl entity = new GeographicEntityImpl(owner, collection, alphacode, scName, properties);
					
					//specific Figis stuff required
					entity.setFigisDomain("species");
					entity.setFigisId(FigisId);
					entity.setFigisViewerId(alphacode+"-"+hab);
					
					//add entity to codelist
					codelist.add(entity);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return codelist;
	}

}
