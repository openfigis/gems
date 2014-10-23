package org.fao.fi.gems.model.settings;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;


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
		Assert.assertEquals("http://www.organization.org/geoserver", server.getPublicUrl());
		
		GeoInstanceList instances = server.getInstances();
		Assert.assertNotNull(instances);
		GeoMasterInstance master = instances.getMaster();
		Assert.assertNotNull(master);
		Assert.assertEquals(master.getUrl(), "http://www.organization.org/geoserver.dv.1");
		Assert.assertEquals(master.getUser(), "user");
		Assert.assertEquals(master.getPassword(), "pwd");
		Assert.assertEquals(master.getVersion(), "2.5");
		List<GeoWorkerInstance> workers = instances.getWorkers();
		Assert.assertNotNull(workers);
		Assert.assertEquals(workers.size(), 3);
		for(int i=0;i<workers.size();i++){
			int workerIdx = i+2;
			Assert.assertEquals(workers.get(i).getUrl(), "http://www.organization.org/geoserver.dv."+workerIdx);
		}
		
		Assert.assertEquals("sourceWS", server.getSourceWorkspace());
		Assert.assertEquals("layer", server.getSourceLayer());
		Assert.assertEquals("att", server.getSourceAttribute());
		Assert.assertEquals("targetWS", server.getTargetWorkspace());
		Assert.assertEquals("targetDS", server.getTargetDatastore());
		Assert.assertEquals("someprefix", server.getTargetLayerPrefix());
		
		Assert.assertNotNull(server.getTimeDimension());
		Assert.assertEquals("START_YEAR", server.getTimeDimension().getStartTime());
		Assert.assertEquals("END_YEAR", server.getTimeDimension().getEndTime());
		
		List<BaseLayer> baseLayers = server.getBaseLayerList();
		Assert.assertEquals("baselayerWS1", baseLayers.get(0).getWorkspace());
		Assert.assertEquals("baselayerName1", baseLayers.get(0).getName());
		Assert.assertEquals("baselayerWS2", baseLayers.get(1).getWorkspace());
		Assert.assertEquals("baselayerName2", baseLayers.get(1).getName());
		
		Assert.assertEquals("SHAPEFILE", server.getMethod());
		Assert.assertEquals("someURL",server.getShapefileURL());

	}
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/model/"+resource).toURI());
	}
	
}
