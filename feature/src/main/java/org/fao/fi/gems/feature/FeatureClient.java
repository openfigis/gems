package org.fao.fi.gems.feature;

import java.util.List;

import org.opengis.feature.Feature;

/**
 * A basic feature client interface relying on the GeoAPI
 * 
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public interface FeatureClient {
	
	public List<Feature> features();

}
