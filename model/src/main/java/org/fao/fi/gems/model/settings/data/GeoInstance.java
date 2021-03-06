/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings.data;

/**
 * GeoInstance
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public abstract class GeoInstance {

	private String url;
	private String user;
	private String password;
	
	public GeoInstance(){
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
