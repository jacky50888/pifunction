package com.jpmorgan.spg.lambda.picalc;

public class PiCalcResult {

	int memLimitInMB;
	int path;
	int randomToSave;
	long runtimeInMinSec;
	boolean coldStart;
	double pi;
	
	public PiCalcResult() {
		
	}
	
	
	public PiCalcResult(int memLimit, int path, int randomToSave, long runtimeInMinSec, boolean coldStart, double pi) {
		super();
		this.path = path;
		this.randomToSave = randomToSave;
		this.runtimeInMinSec = runtimeInMinSec;
		this.memLimitInMB = memLimit;
		this.coldStart = coldStart;
		this.pi = pi;
	}
	
	

	public int getMemLimitInMB() {
		return memLimitInMB;
	}


	public void setMemLimitInMB(int memLimitInMB) {
		this.memLimitInMB = memLimitInMB;
	}


	public int getPath() {
		return path;
	}


	public void setPath(int path) {
		this.path = path;
	}


	public int getRandomToSave() {
		return randomToSave;
	}


	public void setRandomToSave(int randomToSave) {
		this.randomToSave = randomToSave;
	}


	public long getRuntimeInMinSec() {
		return runtimeInMinSec;
	}


	public void setRuntimeInMinSec(long runtimeInMinSec) {
		this.runtimeInMinSec = runtimeInMinSec;
	}

	

	public boolean isColdStart() {
		return coldStart;
	}


	public void setColdStart(boolean coldStart) {
		this.coldStart = coldStart;
	}


	public double getPi() {
		return pi;
	}


	public void setPi(double pi) {
		this.pi = pi;
	}


	@Override
	public String toString() {
		return "PiCalcResult [memLimitInMB=" + memLimitInMB + ", path=" + path + ", runtime(ms)=" + runtimeInMinSec
				+ ", pi=" + pi + "]";
	}
	
}
