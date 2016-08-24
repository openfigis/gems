/**
 * (c) 2015 FAO / UN (project: gems-collection)
 */
package org.fao.fi.gems.collection.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.fao.fi.gems.codelist.CodelistParser;
import org.fao.fi.gems.collection.properties.RfbProperty;
import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.entity.FigisGeographicEntityImpl;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.lod.entity.common.FLODRfbEntity;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.settings.data.filter.DataObjectFilter;
import org.fao.fi.gems.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * RFB codelist parser
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class RfbCodelistParser implements CodelistParser{
	
	public LinkedHashSet<GeographicEntity> getCodelist(GemsConfig config) {
		
		String owner = Utils.whoIsOwner(config);
		
		LinkedHashSet<GeographicEntity> rfbCodelist = new LinkedHashSet<GeographicEntity>();
		
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
			
			LinkedList<Node> nodes = new LinkedList<Node>();
			NodeList nList = doc.getElementsByTagName("rfb");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				nodes.add(nNode);
			}
			
			Collections.sort(nodes, new Comparator<Node>(){
			     public int compare(Node o1, Node o2){
			    	 Element e1 = (Element) o1;
			    	 Element e2 = (Element) o2;
			         return e1.getAttribute("name").compareTo(e2.getAttribute("name"));
			     }
			});
			
			for (int temp = 0; temp < nodes.size(); temp++) {

				Node nNode = nodes.get(temp);
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
						
						FigisGeographicEntityImpl entity = new FigisGeographicEntityImpl(owner, collection, rfbCodeStack, label, properties, null);
						
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
