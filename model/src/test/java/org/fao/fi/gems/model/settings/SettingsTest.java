package org.fao.fi.gems.model.settings;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Settings Test
 * 
 * @author eblondel
 *
 */
public class SettingsTest {

	Settings settings;
	
	@Before
	public void setUp() throws URISyntaxException{
		File fileName = this.getResourceFile("settings.xml");
		settings = (Settings) Settings.fromXML(fileName);
	}
	
	@Test
	public void testSettings(){
		Assert.assertNotNull(settings);
	}
	
	@Test
	public void testGeographicServerSettings(){
		GeographicServerSettings server = settings.getGeographicServerSettings();
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
	
	@Test
	public void testMetadataCatalogueSettings(){
		MetadataCatalogueSettings catalog = settings.getMetadataCatalogueSettings();
		Assert.assertNotNull(catalog);
		Assert.assertEquals("http://www.organization.org/catalogue", catalog.getUrl());
		Assert.assertEquals("user", catalog.getUser());
		Assert.assertEquals("pwd", catalog.getPassword());
	}
	
	
	@Test
	public void testPublicationSettings(){
		PublicationSettings publication = 	settings.getPublicationSettings();
		Assert.assertNotNull(publication);
		Assert.assertEquals("PUBLISH", publication.getAction());
		Assert.assertTrue(publication.isForceData());
		Assert.assertTrue(publication.isForceMetadata());
		Assert.assertTrue(publication.isUnpublishData());
		Assert.assertTrue(publication.isUnpublishMetadata());
		
		Assert.assertEquals("http://www.organization.org/codelist", publication.getCodelistURL());
		
		Assert.assertEquals("mycollection", publication.getCollectionType());
		Assert.assertEquals("2013-10-31", publication.getDate());
		Assert.assertEquals("1.0", publication.getVersion());
		Assert.assertEquals(2, publication.getBuffer(), 0);
		Assert.assertTrue(publication.isTest());
		Assert.assertEquals("thecode", publication.getTestCode());
		Assert.assertTrue(publication.isFigis());

	}
	
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/model/"+resource).toURI());
	}
	
}
