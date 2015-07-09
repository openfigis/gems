/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.fao.fi.gems.model.settings.data.BaseLayer;
import org.fao.fi.gems.model.settings.data.GeoInstanceList;
import org.fao.fi.gems.model.settings.data.GeoMasterInstance;
import org.fao.fi.gems.model.settings.data.GeoWorkerInstance;
import org.fao.fi.gems.model.settings.data.GeographicServerSettings;
import org.fao.fi.gems.model.settings.data.filter.DataObjectFilter;
import org.fao.fi.gems.model.settings.data.filter.ExtraDataFilter;
import org.fao.fi.gems.model.settings.data.filter.FilterList;
import org.junit.Before;
import org.junit.Test;

/**
 * GeographicServerSettingsTest
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class GeographicServerSettingsTest {

	GeographicServerSettings server;
	
	@Before
	public void setUp() throws URISyntaxException{
		File fileName = this.getResourceFile("geoserver.xml");
		server = (GeographicServerSettings) GeographicServerSettings.fromXML(fileName);
	}
	
	@Test
	public void testServer(){
		assertNotNull(server);
	}
	
	@Test
	public void testServices(){
		
		GeoInstanceList instances = server.getInstances();
		assertNotNull(instances);
		GeoMasterInstance master = instances.getMaster();
		assertNotNull(master);
		assertEquals(master.getUrl(), "http://www.organization.org/geoserver.dv.1");
		assertEquals(master.getUser(), "user");
		assertEquals(master.getPassword(), "pwd");
		assertEquals(master.getVersion(), "2.5");
		List<GeoWorkerInstance> workers = instances.getWorkers();
		assertNotNull(workers);
		assertEquals(workers.size(), 3);
		for(int i=0;i<workers.size();i++){
			int workerIdx = i+2;
			assertEquals(workers.get(i).getUrl(), "http://www.organization.org/geoserver.dv."+workerIdx);
		}
	}
	
	@Test
	public void testSourceConfig(){

		assertEquals("sourceWS", server.getSourceWorkspace());
		assertEquals("layer", server.getSourceLayer());
		
		FilterList filters = server.getFilters();
		assertNotNull(filters);
		
		List<DataObjectFilter> dataFilters = filters.getData();
		assertEquals(1,dataFilters.size());
		assertTrue(dataFilters.get(0).getIsString());
		assertEquals("att",dataFilters.get(0).getProperty());
		
		List<ExtraDataFilter> extraFilters = filters.getExtras();
		assertEquals(1,extraFilters.size());
		assertEquals(1,extraFilters.size());
		assertTrue(extraFilters.get(0).getIsString());
		assertEquals("att2", extraFilters.get(0).getProperty());
		assertEquals("val2", extraFilters.get(0).getValue());
		
		List<String> properties = filters.getProperties();
		assertNotNull(properties);
		assertEquals(2,properties.size());
		assertEquals("prop1", properties.get(0));
		assertEquals("prop2", properties.get(1));
	
	}
	
	@Test
	public void testTargetConfig(){

		assertEquals("http://www.organization.org/geoserver", server.getPublicUrl());
		assertEquals("targetWS", server.getTargetWorkspace());
		assertEquals("targetDS", server.getTargetDatastore());
		assertEquals("someprefix", server.getTargetLayerPrefix());
	
	}
	
	@Test
	public void testDimensions(){
		
		assertNotNull(server.getTimeDimension());
		assertEquals("START_YEAR", server.getTimeDimension().getStartTime());
		assertEquals("END_YEAR", server.getTimeDimension().getEndTime());
		
	}
	
	@Test
	public void testBaseLayers(){

		List<BaseLayer> baseLayers = server.getBaseLayerList();
		assertEquals("baselayerWS1", baseLayers.get(0).getWorkspace());
		assertEquals("baselayerName1", baseLayers.get(0).getName());
		assertEquals("baselayerWS2", baseLayers.get(1).getWorkspace());
		assertEquals("baselayerName2", baseLayers.get(1).getName());
		
	}
	
	@Test
	public void testMethod(){

		assertEquals("SQLVIEW", server.getMethod());
		//assertEquals("someURL",server.getShapefileURL());

	}
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/model/"+resource).toURI());
	}
	
}
