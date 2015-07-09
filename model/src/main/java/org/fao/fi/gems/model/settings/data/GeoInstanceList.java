/**
 * (c) 2015 FAO / UN (project: gems-model)
 */
package org.fao.fi.gems.model.settings.data;

import java.util.List;

/**
 * GeoInstanceList
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class GeoInstanceList {

	private GeoMasterInstance master;
	private List<GeoWorkerInstance> workers;
	
	public GeoInstanceList(){
	}

	public GeoMasterInstance getMaster() {
		return master;
	}

	public void setMaster(GeoMasterInstance master) {
		this.master = master;
	}

	public List<GeoWorkerInstance> getWorkers() {
		return workers;
	}

	public void setWorkers(List<GeoWorkerInstance> workers) {
		this.workers = workers;
	}
	
}
