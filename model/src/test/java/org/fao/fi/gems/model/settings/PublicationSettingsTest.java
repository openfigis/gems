package org.fao.fi.gems.model.settings;

import java.io.File;
import java.net.URISyntaxException;


import org.fao.fi.gems.model.settings.PublicationSettings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PublicationSettingsTest {

	PublicationSettings settings;
	
	@Before
	public void setUp() throws URISyntaxException{
		File fileName = this.getResourceFile("publication.xml");
		settings = (PublicationSettings) PublicationSettings.fromXML(fileName);
	}
	
	@Test
	public void testSettings(){
		Assert.assertNotNull(settings);
		Assert.assertEquals("PUBLISH", settings.getAction());
		Assert.assertTrue(settings.isForceData());
		Assert.assertTrue(settings.isForceMetadata());
		Assert.assertTrue(settings.isUnpublishData());
		Assert.assertTrue(settings.isUnpublishMetadata());
		
		Assert.assertEquals("http://www.organization.org/codelist", settings.getCodelistURL());
		Assert.assertEquals("org.fao.fi.gems.collection.anobject.parser", settings.getCodelistParser());
		
		Assert.assertEquals("mycollection", settings.getCollectionType());
		Assert.assertEquals("1.0", settings.getVersion());
		Assert.assertEquals(2, settings.getBuffer(), 0);
		Assert.assertEquals("somestyle", settings.getStyle());
		Assert.assertTrue(settings.isTest());
		Assert.assertEquals("thecode", settings.getTestCode());
		
		//FIGIS specific
		Assert.assertEquals("http://www.fao.org/figis/geoserver/factsheets", settings.getFigisViewerUrl());
		Assert.assertEquals("http://www.fao.org/fishery", settings.getFigisFactsheetUrl());

	}
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/model/"+resource).toURI());
	}
	
}
