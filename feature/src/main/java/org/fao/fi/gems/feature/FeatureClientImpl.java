/**
 * (c) 2015 FAO / UN (project: gems-feature)
 */
package org.fao.fi.gems.feature;

import java.util.List;

import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.model.GemsConfig;
import org.opengis.feature.Feature;

/**
 * An abstract Feature client implementation. To be extended by dedicated
 * Feature client implementations.
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public abstract class FeatureClientImpl implements FeatureClient{

	GemsConfig config;
	
	List<EntityCode> codeStack;
	
	public FeatureClientImpl(GemsConfig config, List<EntityCode> codeStack){
		
		this.config = config;
		this.codeStack = codeStack;
		
	}
	
	public abstract List<Feature> features();
	
}
