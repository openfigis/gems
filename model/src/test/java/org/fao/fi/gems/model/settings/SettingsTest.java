/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;

import org.fao.fi.gems.model.settings.data.GeographicServerSettings;
import org.fao.fi.gems.model.settings.metadata.MetadataCatalogueSettings;
import org.fao.fi.gems.model.settings.publication.PublicationSettings;
import org.fao.fi.gems.model.settings.validation.ValidationSettings;
import org.junit.Before;
import org.junit.Test;

/**
 * Settings Test
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
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
		assertNotNull(settings);
	}
	
	@Test
	public void testGeographicServerSettings(){
		GeographicServerSettings server = settings.getGeographicServerSettings();
		assertNotNull(server);
	}
	
	@Test
	public void testMetadataCatalogueSettings(){
		MetadataCatalogueSettings catalog = settings.getMetadataCatalogueSettings();
		assertNotNull(catalog);
	}
	
	@Test
	public void testPublicationSettings(){
		PublicationSettings publication = settings.getPublicationSettings();
		assertNotNull(publication);
	}
	
	@Test
	public void testValidationSettings(){
		ValidationSettings validation = settings.getValidationSettings();
		assertNotNull(validation);
	}
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/model/"+resource).toURI());
	}
	
}
