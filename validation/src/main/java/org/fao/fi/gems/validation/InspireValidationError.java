/**
 * (c) 2015 FAO / UN (project: gems-validation)
 */
package org.fao.fi.gems.validation;

/**
 * Inspire validation error
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class InspireValidationError {
	
	private String element;
	private String message;
	
	public InspireValidationError(){
	}
	
	public InspireValidationError(String element, String message){
		setElement(element);
		setMessage(message);
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
