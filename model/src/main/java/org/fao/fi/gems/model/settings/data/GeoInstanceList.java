package org.fao.fi.gems.model.settings.data;

import java.util.List;

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
