/**
 * (c) 2015 FAO / UN (project: gems-publisher)
 */
package org.fao.fi.gems.publisher;

/**
 * Publication Exception
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class PublicationException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -902829684500591349L;

	public PublicationException(String message, Exception e){
		super(message, e);
	}
	
	public PublicationException(String message){
		super(message);
	}
	
}
