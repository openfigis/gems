package org.fao.fi.gems.collection.rfb;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

public class RfbCodelistParser implements CodelistParser{

	private static Logger LOGGER = LoggerFactory.getLogger(RfbCodelistParser.class);
	
	public enum RfbProperty implements GeographicMetaObjectProperty{
		
		FAO (EntityAuthority.FAO, true, true, true),
		FLOD (EntityAuthority.FLOD, true, true, true),		
		FIGIS(EntityAuthority.FIGIS, true, true, false),
		
		STYLE(EntityAddin.STYLE, false, false, false),
		ABSTRACT(EntityAddin.ABSTRACT, false, false, false);
		
		private final Object object;
		private final boolean isAuthority;
		private final boolean isThesaurus;
		private final boolean containsURIs;
		
		RfbProperty(Object object, boolean isAuthority, boolean isThesaurus, boolean containsURIs){
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
		
		Set<GeographicEntity> rfbCodelist = new HashSet<GeographicEntity>();
		
		InputStream is = null;
		try {

			URL dataURL = new URL(url);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			is = dataURL.openStream();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("rfb");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					String rfb = eElement.getAttribute("name");
					String style = eElement.getAttribute("style");
					String fid = eElement.getAttribute("fid");
					String label = ((Element) eElement.getElementsByTagName("descriptor").item(0)).getAttribute("title");
					label += " ("+rfb+")"; //add RFB acronym in parentesis
					
					Map<GeographicMetaObjectProperty, List<String>> properties = new HashMap<GeographicMetaObjectProperty, List<String>>();
					//FAO
					properties.put(RfbProperty.FAO, Arrays.asList(Utils.buildMetadataIdentifier(owner, collection, rfb)));
					
					//FIGIS
					properties.put(RfbProperty.FIGIS, Arrays.asList(fid));
					
					//FLOD
					FLODRfbEntity flodEntity = new FLODRfbEntity(rfb);
					if(flodEntity.getFlodContent() != null){
						properties.put(RfbProperty.FLOD, Arrays.asList(flodEntity.getRfbCodedEntity()));
					}
					
					//Style
					properties.put(RfbProperty.STYLE, Arrays.asList(style));
					
					//Rfb abstract
		
					URL fsURL = new URL("http://www.fao.org/fishery/xml/organization/"+fid);
					Document doc2 = dBuilder.parse(fsURL.openStream());	

					Element geoCoverage = (Element) doc2.getDocumentElement().getElementsByTagName("fi:GeoCoverage").item(0);
					NodeList nodeList = geoCoverage.getElementsByTagName("fi:Text");
					if(nodeList.getLength() > 0){
						String abstractText = nodeList.item(0).getTextContent().replaceAll("<p>", "").replaceAll("</p>", "");
						properties.put(RfbProperty.ABSTRACT, Arrays.asList(abstractText));
					}
					
					GeographicEntityImpl entity = new GeographicEntityImpl(owner, collection, rfb, label, properties);
					
					//Figis stuff
					entity.setFigisDomain("rfbs");
					entity.setFigisId(rfb.toLowerCase());
					entity.setFigisViewerId(rfb);
					
					//add geographic entity
					rfbCodelist.add(entity);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return rfbCodelist;
	}

}
