package org.fao.fi.gems.model.content;

public class MetadataBiblioRef {
	
	private String scope = "DATASET";
	private boolean copyright = false;
	
	public MetadataBiblioRef(){
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * @return the copyright
	 */
	public boolean isCopyright() {
		return copyright;
	}

	/**
	 * @param copyright the copyright to set
	 */
	public void setCopyright(boolean copyright) {
		this.copyright = copyright;
	}
	
	

}
