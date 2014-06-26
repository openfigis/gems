package org.fao.fi.gems.authority;
/**
 * An enum of Authority Entity
 * 
 * @author Utilisateur
 *
 */
public enum AuthorityEntity {

	FAO("FAO", "http://www.fao.org/fi"),
	FIGIS("FIGIS", "http://www.fao.org/figis"),
	FLOD("FLOD", "http://www.fao.org/figis/flod"),
	ASFIS("ASFIS", "http://www.fao.org/fishery/collection/asfis"),
	VLIZ("VLIZ", "http://www.vliz.be"),
	WORMS("WORMS", "http://www.marinespecies.org"),
	HABITAT("HABITAT", null);

	private final String propertyName;
	private final String href;

	AuthorityEntity(String propertyName, String href) {
		this.propertyName = propertyName;
		this.href = href;
	}

	public String propertyName() {
		return this.propertyName;
	}

	public String href() {
		return this.href;
	}
}
