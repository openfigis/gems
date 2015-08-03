/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import org.fao.fi.gems.model.settings.validation.ValidationSettings;
import org.junit.Before;
import org.junit.Test;

/**
 * ValidationSettingsTest
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class ValidationSettingsTest {

	ValidationSettings validation;
	
	@Before
	public void setUp() throws URISyntaxException{
		File fileName = this.getResourceFile("validation.xml");
		validation = (ValidationSettings) ValidationSettings.fromXML(fileName);
	}
	
	@Test
	public void testValidation(){
		assertNotNull(validation);
	}
	
	@Test
	public void testInspire(){
		assertTrue(validation.isInspire());
	}
	
	@Test
	public void testStrict(){
		assertFalse(validation.isStrict());
	}
	
	private File getResourceFile(String resource) throws URISyntaxException {
		return new File(this.getClass().getResource("/test-data/model/"+resource).toURI());
	}
	
}
