package model;

public class Votazione{
	boolean ref, vo, vc, vcp;
	
	public Votazione() {
		ref = false;
		vo = false;
		vc = false;
		vcp = false;
	}
	
	public boolean isSet() {
		return ref || vo || vc || vcp;
	}

	public boolean isRef() {
		return ref;
	}

	public void setRef(boolean ref) {
		this.ref = ref;
	}

	public boolean isVo() {
		return vo;
	}

	public void setVo(boolean vo) {
		this.vo = vo;
	}

	public boolean isVc() {
		return vc;
	}

	public void setVc(boolean vc) {
		this.vc = vc;
	}

	public boolean isVcp() {
		return vcp;
	}

	public void setVcp(boolean vcp) {
		this.vcp = vcp;
	}
	
}