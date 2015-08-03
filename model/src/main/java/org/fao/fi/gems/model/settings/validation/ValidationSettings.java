/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings.validation;

import java.io.File;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Configures the validation Settings
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class ValidationSettings {
	
	private boolean inspire;
	private boolean strict;
	
	public ValidationSettings(){}

	public boolean isInspire() {
		return inspire;
	}

	public void setInspire(boolean inspire) {
		this.inspire = inspire;
	}

	public boolean isStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	/**
	 * Parsing from XML
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static ValidationSettings fromXML(File file){
			
		XStream xstream = new XStream(new StaxDriver());
		xstream.aliasType("Validation", ValidationSettings.class);
		
		ValidationSettings settings = (ValidationSettings) xstream.fromXML(file);			
        
		return settings;
	}
}
