package org.fao.fi.gems.publisher;

import java.io.File;
import java.net.URISyntaxException;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.GN3Client;
import it.geosolutions.geonetwork.util.GNSearchResponse.GNMetadata;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Geonetwork-Manager tests on GN 3 instance
 * 
 * @author eblondel
 *
 */
@Ignore
public class GN3ClientTest {
	
	static GNClient client;
	static String MD_ID;
	static File MD_FILE;
	
	static String GN_URL = "url";
	static String GN_USERNAME = "username";
	static String GN_PASSWORD = "password";
	
	@BeforeClass
	static public void configure(){
		
		//geonetwork client
		String url = GN_URL;
		String user = GN_USERNAME;
		String pwd = GN_PASSWORD;
		client = new GN3Client(url, user, pwd);
		
		//metadata example
		MD_ID = "fao-species-map-grn";
		
	}
	
	@Test
	public void searchMetadata() throws Exception{	
		GNMetadata md = MetadataPublisher.checkMetadataExistence(client, MD_ID);
		Assert.assertEquals(MD_ID, md.getUUID());
		System.out.println(md.getId());
	}
	
	@Test
	public void deleteMetadata() throws Exception{
		GNMetadata md = MetadataPublisher.checkMetadataExistence(client, MD_ID);
		client.deleteMetadata(md.getId());
	}
	
	@Test
	public void insertMetadata() throws Exception{	
		MD_FILE = getResourceFile(MD_ID+".xml");
		GNInsertConfiguration cfg = new GNInsertConfiguration();
		cfg.setCategory("datasets");
		cfg.setGroup("1"); // group 1 is usually "all"
		cfg.setStyleSheet("_none_");
		cfg.setValidate(Boolean.FALSE);
		client.insertMetadata(cfg, MD_FILE);
	}
	
	@Test
	public void updateMetadata() throws Exception{
		MD_FILE = getResourceFile(MD_ID+".xml");
		GNMetadata md = MetadataPublisher.checkMetadataExistence(client, MD_ID);
		client.updateMetadata(md.getId(), MD_FILE);
	}
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-metadata/"+resource).toURI());
	}

}
