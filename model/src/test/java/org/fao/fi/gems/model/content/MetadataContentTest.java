/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.content;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.fao.fi.gems.model.content.MetadataContact;
import org.fao.fi.gems.model.content.MetadataContent;
import org.fao.fi.gems.model.content.MetadataThesaurus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Test MetadataContent decoder
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class MetadataContentTest {

	MetadataContent content;
	
	
	@Before
	public void setUp() throws URISyntaxException{
		File fileName = this.getResourceFile("content.xml");
		content = (MetadataContent) MetadataContent.fromXML(fileName);
	}
	
	@Test
	public void testTextualContent(){
		
		Assert.assertNotNull(content);
		Assert.assertEquals("collection1", content.getCollection());
		Assert.assertEquals("url1", content.getCollectionURL());
		Assert.assertEquals("prefix | ", content.getTitlePrefix());
		Assert.assertEquals(" | suffix", content.getTitleSuffix());
		Assert.assertEquals("abstract", content.getAbstract());
		Assert.assertEquals("purpose", content.getPurpose());
		Assert.assertEquals("methodology", content.getMethodology());
		Assert.assertEquals("suppInfo", content.getSupplementaryInformation());
		Assert.assertEquals("license", content.getLicense());
		Assert.assertEquals("disclaimer", content.getDisclaimer());
	}
	
	@Test
	public void testThesaurusList(){
		List<MetadataThesaurus> thesaurusList = content.getThesaurusList();
		Assert.assertNotNull(thesaurusList);
		Assert.assertEquals(2, thesaurusList.size());
		
		Assert.assertEquals("General", thesaurusList.get(0).getName());
		List<String> keywords1 = thesaurusList.get(0).getKeywords();
		Assert.assertNotNull(keywords1);
		Assert.assertEquals(2,keywords1.size());
		Assert.assertEquals("keyword1", keywords1.get(0));
		Assert.assertEquals("keyword2", keywords1.get(1));
		
		Assert.assertEquals("INSPIRE", thesaurusList.get(1).getName());
		List<String> keywords2 = thesaurusList.get(1).getKeywords();
		Assert.assertNotNull(keywords2);
		Assert.assertEquals(2,keywords2.size());
		Assert.assertEquals("theme1", keywords2.get(0));
		Assert.assertEquals("theme2", keywords2.get(1));
		
	}
	
	@Test
	public void testOnlineResources(){
		List<MetadataResource> onlineResources = content.getOnlineResources();
		Assert.assertNotNull(onlineResources);
		Assert.assertEquals(2, onlineResources.size());
		
		MetadataResource res1 = onlineResources.get(0);
		Assert.assertNotNull(res1);
		Assert.assertEquals("link1", res1.getName());
		Assert.assertEquals("http://www.organization.org/link1", res1.getUrl());
		
		MetadataResource res2 = onlineResources.get(1);
		Assert.assertNotNull(res2);
		Assert.assertEquals("link2", res2.getName());
		Assert.assertEquals("http://www.organization.org/link2", res2.getUrl());
		
	}

	@Test
	public void testOrganizationContacts(){
		
		List<MetadataContact> orgs = content.getOrganizationContacts();
		Assert.assertNotNull(orgs);
		
		Assert.assertEquals("ORG", orgs.get(0).getAcronym());
		Assert.assertEquals("ORGANIZATION", orgs.get(0).getName());
		Assert.assertEquals("ORGANIZATION", orgs.get(0).getOrgName());
		Assert.assertEquals("http://www.organization.org", orgs.get(0).getUrl());
		Assert.assertEquals("address", orgs.get(0).getAddress());
		Assert.assertEquals("TheCity", orgs.get(0).getCity());
		Assert.assertEquals("TheCountry", orgs.get(0).getCountry());
		Assert.assertEquals("78", orgs.get(0).getPostalCode());
		Assert.assertEquals("OWNER", orgs.get(0).getRole());
		Assert.assertFalse(orgs.get(0).isBiblioAuthor());
		
		Assert.assertEquals("ORG2", orgs.get(1).getAcronym());
		Assert.assertEquals("ORGANIZATION2", orgs.get(1).getName());
		Assert.assertEquals("ORGANIZATION2", orgs.get(1).getOrgName());
		Assert.assertEquals("http://www.organization2.org", orgs.get(1).getUrl());
		Assert.assertEquals("address2", orgs.get(1).getAddress());
		Assert.assertEquals("TheCity2", orgs.get(1).getCity());
		Assert.assertEquals("TheCountry2", orgs.get(1).getCountry());
		Assert.assertEquals("782", orgs.get(1).getPostalCode());
		Assert.assertEquals("POINT_OF_CONTACT", orgs.get(1).getRole());
		Assert.assertTrue(orgs.get(1).isBiblioAuthor());
		
	}
	
	@Test
	public void testIndividualContacts(){
		
		List<MetadataContact> contacts = content.getIndividualContacts();
		Assert.assertNotNull(contacts);
		
		Assert.assertEquals("ORGANIZATION", contacts.get(0).getName());
		Assert.assertEquals("ORGANIZATION", contacts.get(0).getOrgName());
		Assert.assertEquals("http://www.organization.org", contacts.get(0).getUrl());
		Assert.assertEquals("address", contacts.get(0).getAddress());
		Assert.assertEquals("TheCity", contacts.get(0).getCity());
		Assert.assertEquals("TheCountry", contacts.get(0).getCountry());
		Assert.assertEquals("78", contacts.get(0).getPostalCode());
		
		Assert.assertEquals("someone@organization.org", contacts.get(0).getMainEmail());
		Assert.assertEquals("someone2@organization.org", contacts.get(1).getMainEmail());
		Assert.assertEquals("999", contacts.get(0).getMainPhone());
		Assert.assertEquals("999-2", contacts.get(1).getMainPhone());
		Assert.assertEquals("777", contacts.get(0).getFax());
		Assert.assertEquals("777-2", contacts.get(1).getFax());
		Assert.assertEquals("Some One", contacts.get(0).getIndividualName());
		Assert.assertEquals("Some One2", contacts.get(1).getIndividualName());
		Assert.assertEquals("His Position", contacts.get(0).getPositionName());
		Assert.assertEquals("His Position2", contacts.get(1).getPositionName());
		Assert.assertEquals("POINT_OF_CONTACT", contacts.get(0).getRole());
		Assert.assertEquals("POINT_OF_CONTACT", contacts.get(1).getRole());
		
	}
	
	@Test
	public void testTopicCategories(){
		List<String> categories = content.getTopicCategories();
		Assert.assertNotNull(categories);
		Assert.assertEquals(3, categories.size());
		Assert.assertEquals("BOUNDARIES", categories.get(0));
		Assert.assertEquals("OCEAN", categories.get(1));
		Assert.assertEquals("BIOTA", categories.get(2));
	}
	
	@Test
	public void testBiblioRef(){
		Assert.assertNotNull(content.getBiblioRef());
		Assert.assertEquals("DATASET",content.getBiblioRef().getScope());
		Assert.assertTrue(content.getBiblioRef().isCopyright());
	}
	
	
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/model/"+resource).toURI());
	}
	
}
