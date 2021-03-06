/**
 * (c) 2015 FAO / UN (project: gems-feature)
 */
package org.fao.fi.gems.feature;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.sis.internal.util.TemporalUtilities;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.xml.IdentifiedObject;
import org.apache.sis.xml.IdentifierSpace;
import org.fao.fi.gems.feature.FeatureTypeProperty;
import org.fao.fi.gems.model.settings.data.dimension.TimeDimension;
import org.geotoolkit.feature.DefaultFeature;
import org.geotoolkit.feature.type.GeometryType;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.opengis.feature.Feature;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * A set of Feature utilities
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
@SuppressWarnings("deprecation")
public final class FeatureUtils {

	private static Logger LOGGER = LoggerFactory.getLogger(FeatureUtils.class);

	/**
	 * Computes the actual of the feature collection to be published. The method
	 * proceeds to an optimization of the bounding box to manage the date line
	 * issues (at global scale).
	 * 
	 * @param features a list of {@link org.opengis.feature.Feature}
	 * @return an object of class {@link com.vividsolutions.jts.geom.Envelope}
	 */
	public static Envelope envelopeActual(List<Feature> features){
		
		// bbox coordinates
		double bboxMinX = 0;
		double bboxMinY = 0;
		double bboxMaxX = 0;
		double bboxMaxY = 0;
		double maxNegX = -180;
		double maxPosX = 180;
		
		boolean first = true;
		Iterator<Feature> it = features.iterator();
		while(it.hasNext()){
			
			DefaultFeature feature = (DefaultFeature) it.next();
			
			//get base envelope
			String geometryName = null;
			for(PropertyDescriptor prop : feature.getType().getDescriptors()){
				if(prop.getType() instanceof GeometryType){
					geometryName = prop.getName().tip().toString();
					break;
				}
			}

			Geometry geometry = (Geometry) feature.getPropertyValue(geometryName);
			Envelope baseEnvelope = geometry.getEnvelopeInternal();
			
			double minX = baseEnvelope.getMinX();
			double minY = baseEnvelope.getMinY();
			double maxX = baseEnvelope.getMaxX();
			double maxY = baseEnvelope.getMaxY();
			
			// first bbox recalculation
			if (first) {
				bboxMinX = minX;
				bboxMinY = minY;
				bboxMaxX = maxX;
				bboxMaxY = maxY;
				first = false;
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

		// control for overlimit latitude
		if (bboxMinY < -90)
			bboxMinY = -90;
		if (bboxMaxY > 90)
			bboxMaxY = 90;

		// set null if the connection was lost (coords = 0)
		// the main app will have to recalculate the bbox until
		// while bbox == null
		Envelope envelope = null;
		if (!(bboxMinX == 0 & bboxMaxX == 0 & bboxMinY == 0 & bboxMaxY == 0)) {
			envelope = new Envelope(bboxMinX, bboxMaxX, bboxMinY, bboxMaxY);
		}
		return envelope;
	}
	
	/**
	 * Computes a preview envelope using the actual data envelope (computed with
	 * {@link #envelopeActual(List)}) and applying a buffer distance.
	 * 
	 * @param envelope object of class {@link com.vividsolutions.jts.geom.Envelope}
	 * @param buffer
	 * @return an object of class {@link com.vividsolutions.jts.geom.Envelope}
	 */
	public static Envelope envelopePreview(Envelope envelope, double buffer){
		
		if(envelope == null) return null;
		
		double bboxMinX = envelope.getMinX();
		double bboxMinY = envelope.getMinY();
		double bboxMaxX = envelope.getMaxX();
		double bboxMaxY = envelope.getMaxY();
		
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
		Envelope preview = new Envelope(bboxMinX, bboxMaxX, bboxMinY, bboxMaxY);
		return preview;
	}
	
	/**
	 * Computes the temporal extent given a list of
	 * {@link org.opengis.feature.Feature} retrieved through a dedicated
	 * {@link FeatureClient}, and a {@link TimeDimension} object provided
	 * (Optionally) by a instance of {@link GemsConfig} configuration.
	 * 
	 * @param features
	 *            the list of {@link org.opengis.feature.Feature}
	 * @param time
	 *            a {@link TimeDimension} object
	 * @return an object implementing the
	 *         {@link org.opengis.temporal.TemporalPrimitive}
	 * @throws Exception
	 */
	public static TemporalPrimitive time(List<Feature> features, TimeDimension time) throws Exception{
		
		TemporalPrimitive temporalPrimitive = null;
		String periodId = null;
		java.util.Date startTime = null;
		java.util.Date endTime = null;
		
		String timeStartAttr = time.getStartTime();
		String timeEndAttr = time.getEndTime();
		
		try{
			Iterator<Feature> it = features.iterator();
			while(it.hasNext()){
				
				Feature feature = it.next();
				
				//Start time
				DateTime jtime1 = new DateTime((java.util.Date) feature.getPropertyValue(timeStartAttr), DateTimeZone.UTC);
				if(startTime == null) startTime = jtime1.toDate();
				if(jtime1.isBefore(startTime.getTime())) startTime = jtime1.toDate();
				
				//end time
				if(timeEndAttr != null){
					DateTime jtime2 = new DateTime((java.util.Date) feature.getPropertyValue(timeEndAttr), DateTimeZone.UTC);
					if(endTime == null) endTime = jtime2.toDate();
					if(jtime2.isAfter(endTime.getTime())) endTime = jtime2.toDate();
				}else{
					if(endTime == null) endTime = jtime1.toDate();
					if(jtime1.isAfter(endTime.getTime())) endTime = jtime1.toDate();
				}
				
			}
			
			if(startTime == null && endTime == null) return null;
		
			if(endTime == null || startTime.equals(endTime)){
				temporalPrimitive = TemporalUtilities.createInstant(startTime);
				DateTime instant = new DateTime(startTime.getTime(), DateTimeZone.UTC);
				Interval interval = new Interval(instant.getMillis(), instant.getMillis(), DateTimeZone.UTC);
				periodId = interval.toPeriod().toString();
			}else{
				temporalPrimitive = TemporalUtilities.createPeriod(startTime, endTime);
				DateTime start = new DateTime(startTime.getTime(), DateTimeZone.UTC);
				DateTime end = new DateTime(endTime.getTime(), DateTimeZone.UTC);
				Interval interval = new Interval(start.getMillis(), end.getMillis(), DateTimeZone.UTC);
				periodId = interval.toPeriod().toString();
			}
			
			//assign an ISO-8601 compliant string id
			((IdentifiedObject) temporalPrimitive).getIdentifierMap()
												  .putSpecialized(IdentifierSpace.ID, periodId);
			
		}catch(Exception e){
			LOGGER.info(e.getMessage());
			throw new Exception("Impossible to created temporal primitive ",e);
		}
		
		return temporalPrimitive;
	}
	
	
	/**
	 * Computes the geo-properties required by the GEMS application. This method
	 * is isolated from the way data is retrieved by GEMS. Data is retrieved
	 * using a {@link FeatureClient} implementation.
	 * 
	 * @param features
	 * @param buffer
	 * @param timeDimension
	 * @return the set of geoproperties
	 * @throws Exception
	 */
	public static Map<FeatureTypeProperty, Object> computeFeatureTypeProperties(
			List<Feature> features, double buffer, TimeDimension timeDimension) throws Exception {

		Map<FeatureTypeProperty, Object> map = null;
		
		if(features.size() > 0){
			map = new HashMap<FeatureTypeProperty,Object>();
		
			//adding CRS
			map.put(FeatureTypeProperty.CRS, CommonCRS.WGS84.geographic());
			
			//adding number of features
			LOGGER.info("* Nb of Features: "+features.size());
			map.put(FeatureTypeProperty.COUNT, features.size());
			
			//adding envelopes (actual & preview)
			Envelope actual = envelopeActual(features);
			LOGGER.info("* Actual envelope: "+actual.toString());
			map.put(FeatureTypeProperty.BBOX_ACTUAL, actual);
			
			Envelope preview = envelopePreview(actual, buffer);
			LOGGER.info("* Preview envelope: "+preview.toString());
			map.put(FeatureTypeProperty.BBOX_PREVIEW, preview);
			
			//adding time dimension
			if(timeDimension != null){
				if(timeDimension.getStartTime() != null){

					TemporalPrimitive time = null;
					try {
						time = time(features, timeDimension);
					} catch (Exception e) {
						throw new Exception("Failed to add time dimension", e);
					}
					
					String timeString = (time instanceof Period) ?
							"from=" + ((Period) time).getBeginning().getDate().toString() +" - "+
							"to=" + ((Period) time).getEnding().getDate().toString()
							: 
							"instant=" + ((Instant) time).getDate().toString();
					LOGGER.info("* Temporal extent: " + timeString);
					
					map.put(FeatureTypeProperty.TIME, time);
				}
			}
			
		}
		return map;
	}
	

}
