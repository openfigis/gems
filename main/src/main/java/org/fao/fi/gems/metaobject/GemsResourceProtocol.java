/**
 * (c) 2015 FAO / UN (project: gems-main)
 */
package org.fao.fi.gems.metaobject;

/**
 * A enumeration of common standard web protocols to use for setting
 * GeographicMetadata online resources.
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public enum GemsResourceProtocol{
	
	W3C_HTTP_1_0_LINK ("WWW:LINK-1.0-http--link"),
	OGC_WMS_1_3_0_GETMAP ("OGC:WMS-1.3.0-http-get-map"),
	OGC_WFS_1_0_0_GETFEATURE ("OGC:WFS-1.0.0-http-get-feature");
	
	private final String protocol;
	
	GemsResourceProtocol(String protocol){
		this.protocol = protocol;
		
	}
	
	public String protocol(){
		return this.protocol;
	}

}