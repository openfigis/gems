package org.fao.fi.gems.lod.client;

import static org.junit.Assert.*;

import org.fao.fi.gems.lod.client.LodClientImpl;
import org.junit.Test;

import com.google.gson.JsonObject;

/**
 * Unit tests for LodClientImpl
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class LodClientImplTest {
	
	@Test
	public void testWithFlod() throws Exception{
		
		String endpoint = "http://www.fao.org/figis/grade/service/prod/query/ce4species/results.json";
		LodClientImpl client = new LodClientImpl(endpoint, "COD");
		
		assertEquals(endpoint, client.endpoint());
		assertEquals("COD", client.code());
		
		JsonObject obj = client.content();
		assertNotNull(obj);

	}	

}
