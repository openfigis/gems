package org.fao.fi.gems.model.settings;

import java.io.File;
import java.net.URISyntaxException;


import org.fao.fi.gems.model.settings.MetadataCatalogueSettings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MetadataCatalogueSettingsTest {

	MetadataCatalogueSettings catalog;
	
	@Before
	public void setUp() throws URISyntaxException{
		File fileName = this.getResourceFile("catalogue.xml");
		catalog= (MetadataCatalogueSettings) MetadataCatalogueSettings.fromXML(fileName);
	}
	
	@Test
	public void testSettings(){
		Assert.assertNotNull(catalog);
		Assert.assertEquals("http://www.organization.org/catalogue", catalog.getUrl());
		Assert.assertEquals("user", catalog.getUser());
		Assert.assertEquals("pwd", catalog.getPassword());
	}
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/model/"+resource).toURI());
	}
	
}
