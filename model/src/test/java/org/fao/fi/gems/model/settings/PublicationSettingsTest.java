/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings;

import java.io.File;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.fao.fi.gems.model.settings.publication.EntityList;
import org.fao.fi.gems.model.settings.publication.PublicationSettings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * PublicationSettingsTest
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
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
		Assert.assertTrue(settings.isActionData());
		Assert.assertTrue(settings.isActionMetadata());
		
		Assert.assertFalse(settings.isLinkedOpenData());
		
		Assert.assertEquals("http://www.organization.org/codelist", settings.getCodelistURL());
		Assert.assertEquals("org.fao.fi.gems.collection.anobject.parser", settings.getCodelistParser());
		
		Assert.assertEquals("mycollection", settings.getCollectionType());
		Assert.assertEquals("1.0", settings.getVersion());
		Assert.assertEquals(2, settings.getBuffer(), 0);
		Assert.assertEquals("somestyle", settings.getStyle());
		
		EntityList entities = settings.getEntities();
		Assert.assertNotNull(entities);
		
		LinkedList<String> include = entities.getInclude();
		Assert.assertNotNull(include);
		Assert.assertEquals(3, include.size());
		Assert.assertEquals("thecode1", include.get(0));
		Assert.assertEquals("thecode2", include.get(1));
		Assert.assertEquals("thecode3", include.get(2));
		
		LinkedList<String> exclude = entities.getExclude();
		Assert.assertNotNull(exclude);
		Assert.assertEquals(1, exclude.size());
		Assert.assertEquals("thecode3", exclude.get(0));
		
		//FIGIS specific
		Assert.assertEquals("http://www.fao.org/figis/geoserver/factsheets", settings.getFigisViewerResourceBaseUrl());
		Assert.assertEquals("http://www.fao.org/fishery", settings.getFigisWebResourceBaseUrl());
		Assert.assertEquals("Factsheet - Summary description", settings.getFigisWebResourceTitle());
		Assert.assertEquals(true, settings.figisHasFactsheet());

	}
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/model/"+resource).toURI());
	}
	
}
