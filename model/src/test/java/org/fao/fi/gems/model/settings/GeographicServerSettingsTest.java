package org.fao.fi.gems.model.settings;

import java.io.File;
import java.net.URISyntaxException;


import org.fao.fi.gems.model.settings.GeographicServerSettings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GeographicServerSettingsTest {

	GeographicServerSettings server;
	
	@Before
	public void setUp() throws URISyntaxException{
		File fileName = this.getResourceFile("geoserver.xml");
		server = (GeographicServerSettings) GeographicServerSettings.fromXML(fileName);
	}
	
	@Test
	public void testSettings(){
		Assert.assertNotNull(server);
		Assert.assertEquals("http://www.organization.org/geoserver", server.getUrl());
		Assert.assertEquals("user", server.getUser());
		Assert.assertEquals("pwd", server.getPassword());
		Assert.assertEquals("sourceWS", server.getSourceWorkspace());
		Assert.assertEquals("layer", server.getSourceLayer());
		Assert.assertEquals("att", server.getSourceAttribute());
		Assert.assertEquals("targetWS", server.getTargetWorkspace());
		Assert.assertEquals("targetDS", server.getTargetDatastore());
		Assert.assertEquals("someprefix", server.getTargetLayerPrefix());
		Assert.assertEquals("baselayerWS", server.getBaseLayerWorkspace());
		Assert.assertEquals("baselayerName", server.getBaseLayerName());
		Assert.assertEquals("SHAPEFILE", server.getMethod());
		Assert.assertEquals("someURL",server.getShapefileURL());

	}
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/model/"+resource).toURI());
	}
	
}
