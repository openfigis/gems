/**
 * (c) 2015 FAO / UN (project: gems-validation)
 */
package org.fao.fi.gems.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the Inspire Validation Report
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class InspireValidationReportTest {
	
	File reportFile;
	InspireValidationReport report;
	
	@Before
	public void setUp() throws Exception {
		reportFile = getResourceFile("report.xml");
		report = InspireValidationReport.fromXML(reportFile);
	}

	@Test
	public void testOnlineUrl(){
		assertEquals("/sandbox/INSPIRE-uuid/datasets/1/resourceReport", report.getOnlineReportUrl());
	}
	
	@Test
	public void testValidationErrors(){
		assertEquals(1, report.getValidationErrors().size());
		InspireValidationError error = report.getValidationErrors().get(0);
		assertEquals("Coordinate Reference System", error.getElement());
		assertEquals("Coordinate Reference System code is missing or empty", error.getMessage());
	}
	
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/"+resource).toURI());
	}
	
}
