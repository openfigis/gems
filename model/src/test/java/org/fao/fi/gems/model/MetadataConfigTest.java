package org.fao.fi.gems.model;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MetadataConfigTest {

	MetadataConfig config;
	
	@Before
	public void setUp() throws URISyntaxException{
		File fileName = this.getResourceFile("metadataConfig.xml");
		config = (MetadataConfig) MetadataConfig.fromXML(fileName);
	}
	
	@Test
	public void testMetadataConfig(){
		Assert.assertNotNull(config);
		Assert.assertNotNull(config.getSettings());
		Assert.assertNotNull(config.getContent());
	}
	
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/model/"+resource).toURI());
	}
	
}
