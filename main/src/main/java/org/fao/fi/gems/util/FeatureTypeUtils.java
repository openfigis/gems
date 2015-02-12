package org.fao.fi.gems.util;

import java.net.URL;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.sis.internal.util.TemporalUtilities;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.xml.IdentifiedObject;
import org.apache.sis.xml.IdentifierSpace;
import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.feature.FeatureTypeProperty;
import org.fao.fi.gems.model.settings.data.GeographicServerSettings;
import org.fao.fi.gems.model.settings.data.filter.ExtraDataFilter;
import org.opengis.temporal.TemporalPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vividsolutions.jts.geom.Envelope;

public final class FeatureTypeUtils {

	private static Logger LOGGER = LoggerFactory.getLogger(FeatureTypeUtils.class);
	
	/**
	 * Compute FeatureType properties from a WFS request
	 * 
	 * @param settings
	 * @param code
	 * @param buffer
	 * @return
	 * @throws Exception 
	 */
	public static Map<FeatureTypeProperty, Object> computeFeatureTypeProperties(
			GeographicServerSettings settings,
			List<EntityCode> codeStack, double buffer) throws Exception {
		
		Map<FeatureTypeProperty, Object> map = null;

		// bbox coordinates
		double bboxMinX = 0;
		double bboxMinY = 0;
		double bboxMaxX = 0;
		double bboxMaxY = 0;
		
		double maxNegX = -180;
		double maxPosX = 180;

		// time
		boolean hasTime = false;
		if(settings.getTimeDimension() != null) hasTime = true;
		Date startTime = null;
		Date endTime = null;
		
		try{

			//dirty stuff (for future envisage some middleware relying on GeoAPI, why not VR plugin?)
			String wfsRequest= settings.getPublicUrl() + "/" + settings.getSourceWorkspace()
					+ "/ows?service=wfs&version=1.0.0&request=GetFeature"
					+ "&typeName=" + settings.getSourceLayer();
			
			String cqlFilter = null;
			if(codeStack.size() > 0){
				cqlFilter = "&cql_filter=";
				for(int i=0;i<codeStack.size();i++){
					EntityCode ec = codeStack.get(i);
					String filterCode = ec.getCode();
					if(ec.getFilter().getIsString()) filterCode = "'"+filterCode+"'";
					cqlFilter += ec.getFilter().getProperty() + "=" + filterCode;
					if(i<codeStack.size()-1) cqlFilter += " AND ";
				}
				cqlFilter = cqlFilter.replaceAll(" ", "%20");
			}
			
			List<ExtraDataFilter> extraFilters = settings.getFilters().getExtras();
			if(extraFilters.size() > 0){
				boolean hasFilter = cqlFilter != null;
				if(!hasFilter){
					cqlFilter = "&cql_filter=";
				}
				for(int i=0;i<extraFilters.size();i++){
					ExtraDataFilter ef = extraFilters.get(i);
					String efCode = ef.getValue();
					if(ef.getIsString()) efCode = "'"+efCode+"'";
					
					if(hasFilter || (!hasFilter && i > 0)) cqlFilter += " AND ";
					cqlFilter += ef.getProperty()+"="+efCode;
				}
				cqlFilter = cqlFilter.replaceAll(" ", "%20");
			}
			
			if(cqlFilter != null) wfsRequest += cqlFilter;
				
			LOGGER.info("== WFS GetFeature Request ==");
			LOGGER.info(wfsRequest);
			URL url = new URL(wfsRequest);
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(url.openStream());
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("gml:featureMember");
			if (nList != null) {
				map = new HashMap<FeatureTypeProperty, Object>();
				if (nList.getLength() > 0) {
					LOGGER.info(nList.getLength() + " features");
					
					// ADD COUNT
					map.put(FeatureTypeProperty.COUNT, nList.getLength());

					for (int temp = 0; temp < nList.getLength(); temp++) {
						Node nNode = nList.item(temp);
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {

							Element featureMember = (Element) nNode;
							
							// time information
							if(hasTime){
								//Start time
								String timeAttribute1 = settings.getSourceWorkspace() +":"+ settings.getTimeDimension().getStartTime();
								Node timeNode1 = (Node) featureMember.getElementsByTagName(timeAttribute1).item(0);
								Date time1 = Date.valueOf(timeNode1.getTextContent());
								if(startTime == null) startTime = time1;
								if(time1.before(startTime)) startTime = time1;
								
								//end time
								if(settings.getTimeDimension().getEndTime() != null){
									String timeAttribute2 = settings.getSourceWorkspace() +":"+ settings.getTimeDimension().getEndTime();
									Node timeNode2 = (Node) featureMember.getElementsByTagName(timeAttribute2).item(0);
									Date time2 = Date.valueOf(timeNode2.getTextContent());
									if(endTime == null) endTime = time2;
									if(time2.after(endTime)) endTime = time2;
								}else{
									if(endTime == null) endTime = time1;
									if(time1.after(endTime)) endTime = time1;
								}
							}

							// geographic bbox
							Node bbox = (Node) featureMember.getElementsByTagName("gml:Box").item(0);
							Element coords = (Element) bbox.getFirstChild();
							String[] gmlBounds = coords.getTextContent().split(" ");

							String[] min = gmlBounds[0].split(",");
							String[] max = gmlBounds[1].split(",");

							double minX = Double.parseDouble(min[0]);
							double minY = Double.parseDouble(min[1]);
							double maxX = Double.parseDouble(max[0]);
							double maxY = Double.parseDouble(max[1]);

							// first bbox recalculation
							if (temp == 0) {
								bboxMinX = minX;
								bboxMinY = minY;
								bboxMaxX = maxX;
								bboxMaxY = maxY;

							} else {
								// classic bbox expansion rule
								if (minX < bboxMinX)
									bboxMinX = minX;
								if (minY < bboxMinY)
									bboxMinY = minY;
								if (maxX > bboxMaxX)
									bboxMaxX = maxX;
								if (maxY > bboxMaxY)
									bboxMaxY = maxY;

							}

							if (maxX > maxNegX & maxX < 0)
								maxNegX = maxX;
							if (minX < maxPosX & minX > 0)
								maxPosX = minX;
						}

					}
					
					// time extent
					if(hasTime){
						//temporal primitive
						TemporalPrimitive temporalPrimitive = TemporalUtilities.createPeriod(startTime,endTime);
						map.put(FeatureTypeProperty.TIME, temporalPrimitive);
					}

					// final bbox adjustment
					// ***************
					// in case maxNegX & maxPosX unchanged
					if (maxNegX == -180)
						maxNegX = -90;
					if (maxPosX == 180)
						maxPosX = 90;

					// for date-limit geographic distributions
					if (maxNegX < -90 & maxPosX > 90) {
						bboxMinX = maxPosX;
						bboxMaxX = 360 - Math.abs(maxNegX);
					}

					// control for globally distributed layers
					if (bboxMinX < -175.0 && bboxMaxX > 175.0) {
						bboxMinX = -180.0;
						bboxMaxX = 180.0;
						bboxMinY = -90.0;
						bboxMaxY = 90.0;
					}

					// NEW: control for overlimit latitude
					if (bboxMinY < -90)
						bboxMinY = -90;
					if (bboxMaxY > 90)
						bboxMaxY = 90;

					// set null if the connection was lost (coords = 0)
					// the main app will have to recalculate the bbox until
					// while bbox == null
					Envelope bounds = null;
					if (!(bboxMinX == 0 & bboxMaxX == 0 & bboxMinY == 0 & bboxMaxY == 0)) {
						
						//actual bbox
						bounds = new Envelope(bboxMinX, bboxMaxX, bboxMinY, bboxMaxY);
						map.put(FeatureTypeProperty.BBOX_ACTUAL, bounds);

						LOGGER.info("Actual Bounding Box");
						LOGGER.info("min X = "+String.valueOf(bboxMinX));
						LOGGER.info("max X = "+String.valueOf(bboxMaxX));
						LOGGER.info("min Y = "+String.valueOf(bboxMinY));
						LOGGER.info("max Y = "+String.valueOf(bboxMaxY));
						
						// Preview bbox (apply buffer)
						if (!(bboxMinX < -180 + buffer)
								&& !(bboxMaxX > 180 - buffer)) {
							bboxMinX = bboxMinX - buffer;
							bboxMaxX = bboxMaxX + buffer;
						}

						if (!(bboxMinY < -90 + buffer)) {
							bboxMinY = bboxMinY - buffer;
						}
						if (!(bboxMaxY > 90 - buffer)) {
							bboxMaxY = bboxMaxY + buffer;

						}
						bounds = new Envelope(bboxMinX, bboxMaxX, bboxMinY, bboxMaxY);
						map.put(FeatureTypeProperty.BBOX_PREVIEW, bounds);
						
						LOGGER.info("Preview Bounding Box");
						LOGGER.info("min X = "+String.valueOf(bboxMinX));
						LOGGER.info("max X = "+String.valueOf(bboxMaxX));
						LOGGER.info("min Y = "+String.valueOf(bboxMinY));
						LOGGER.info("max Y = "+String.valueOf(bboxMaxY));

						map.put(FeatureTypeProperty.CRS, CommonCRS.WGS84.geographic());
					}
				}else{
					//no feature members
					map.put(FeatureTypeProperty.COUNT, 0);
					
				}
			}
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
			throw new Exception("Error trying to perform WFS GetFeature request", e);
		}

		return map;
	}

}
