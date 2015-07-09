/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * GemsConfigTest
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class GemsConfigTest {

	GemsConfig config;
	
	@Before
	public void setUp() throws URISyntaxException{
		File fileName = this.getResourceFile("metadataConfig.xml");
		config = (GemsConfig) GemsConfig.fromXML(fileName);
	}
	
	@Test
	public void testMetadataConfig(){
		Assert.assertNotNull(config);
		Assert.assertEquals("DATASET",config.getScope());
		Assert.assertNotNull(config.getSettings());
		Assert.assertNotNull(config.getContent());
	}
	
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/model/"+resource).toURI());
	}
	
}
