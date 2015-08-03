/**
 * (c) 2015 FAO / UN (project: gems-validation)
 */
package org.fao.fi.gems.validation;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the Inspire Validator
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class InspireValidatorTest {
	
	File metadataFile;
	InspireValidator inspireValidator;
	Response response;
	
	@Before
	public void setUp() throws URISyntaxException, MalformedURLException {
		metadataFile = getResourceFile("metadata.xml");
		inspireValidator = new InspireValidator();
		response = inspireValidator.validate(metadataFile);	
	}

	@Test
	public void testRemoteValidation() {		
		assertEquals(201, response.getStatus());
		assertEquals("Created", response.getStatusInfo().getReasonPhrase());		
	}
	
	@Test
	public void testIsValid(){
		assertFalse(inspireValidator.isValid(response));
	}
	
	@Test
	public void testReport() throws Exception{
		InspireValidationReport report = inspireValidator.getReport(response);
		assertNotNull(report);
		assertNotNull(report.getOnlineReportUrl());
		assertEquals(1, report.getValidationErrors().size());
	}
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/"+resource).toURI());
	}
	
}
