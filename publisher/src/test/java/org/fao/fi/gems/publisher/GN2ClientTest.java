package org.fao.fi.gems.publisher;

import java.io.File;
import java.net.URISyntaxException;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.GN2Client;
import it.geosolutions.geonetwork.util.GNSearchResponse.GNMetadata;
import it.geosolutions.geonetwork.util.HTTPUtils;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class GN2ClientTest {
	
	static GNClient client;
	static String MD_ID;
	static File MD_FILE;
	
	static String GN_URL = "url";
	static String GN_USERNAME = "user";
	static String GN_PASSWORD = "pwd";
	
	
	@BeforeClass
	static public void configure(){
		
		//geonetwork client
		String url = GN_URL;
		String user = GN_USERNAME;
		String pwd = GN_PASSWORD;
		client = new GN2Client(url, user, pwd);
		
		//metadata example
		MD_ID = "fao-species-map-grn";
		
	}
	
	@Test
	public void testConnection() throws Exception{
		
		HTTPUtils connection = new HTTPUtils(GN_USERNAME, GN_PASSWORD);
		connection.setIgnoreResponseContentOnSuccess(false);
		connection.setXmlContentType("application/xml");

		GNMetadata md = MetadataPublisher.checkMetadataExistence(client, MD_ID);
		String serviceURL = "http://www.fao.org/geonetwork/srv/eng/metadata.edit!?id="+md.getId();
		String response = connection.get(serviceURL);
		
		Assert.assertEquals(200, connection.getLastHttpStatus());
		Assert.assertNotNull(response);
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
		GNMetadata md = MetadataPublisher.checkMetadataExistence(client, MD_ID);
		client.updateMetadata(md.getId(), MD_FILE);
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
