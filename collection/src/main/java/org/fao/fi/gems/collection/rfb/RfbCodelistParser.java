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

import org.fao.fi.gems.codelist.CodelistParser;
import org.fao.fi.gems.entity.EntityAddin;
import org.fao.fi.gems.entity.EntityAuthority;
import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.entity.FigisGeographicEntityImpl;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.lod.entity.common.FLODRfbEntity;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.settings.data.filter.DataObjectFilter;
import org.fao.fi.gems.model.settings.publication.EntityList;
import org.fao.fi.gems.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RfbCodelistParser implements CodelistParser{
	
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
	
	
	public Set<GeographicEntity> getCodelist(GemsConfig config) {
		
		String owner = Utils.whoIsOwner(config);
		
		Set<GeographicEntity> rfbCodelist = new HashSet<GeographicEntity>();
		
		InputStream is = null;
		try {

			String url = config.getSettings().getPublicationSettings().getCodelistURL();
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
					DataObjectFilter rfbFilter = config.getSettings().getGeographicServerSettings().getFilters().getData().get(0);
					EntityCode rfbCode = new EntityCode(rfbFilter, rfb);
					List<EntityCode> rfbCodeStack = Arrays.asList(rfbCode);
					
					String style = eElement.getAttribute("style");
					String fid = eElement.getAttribute("fid");
					String label = ((Element) eElement.getElementsByTagName("descriptor").item(0)).getAttribute("title");
					
					//wrapEntity by default is true
					//if there is a list of subset then wrap entity only for those ones
					boolean wrapEntity = Utils.wrapEntity(config, rfb);		
					
					if(wrapEntity){
						Map<GeographicMetaObjectProperty, List<String>> properties = new HashMap<GeographicMetaObjectProperty, List<String>>();
						//FAO
						String collection = config.getSettings().getPublicationSettings().getCollectionType();
						properties.put(RfbProperty.FAO, Arrays.asList(Utils.buildMetadataIdentifier(owner, collection, rfb)));
						
						//FIGIS
						properties.put(RfbProperty.FIGIS, Arrays.asList(fid));
						
						//FLOD
						FLODRfbEntity flodEntity = new FLODRfbEntity(rfb);
						if(flodEntity.content() != null){
							properties.put(RfbProperty.FLOD, Arrays.asList(flodEntity.authorityUri()));
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
						
						FigisGeographicEntityImpl entity = new FigisGeographicEntityImpl(owner, collection, rfbCodeStack, label, properties);
						
						//Figis stuff
						entity.setFigisDomain("rfbs");
						entity.setFigisId(rfb.toLowerCase());
						entity.setFigisViewerId(rfb);
						
						//add geographic entity
						rfbCodelist.add(entity);
					}
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
