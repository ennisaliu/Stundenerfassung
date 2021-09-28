package teko.ea;

import java.util.Date;

public class PersInfo {
	
	private int uid;
	private Date eintritt;
	private Date austritt;
	private String rolle;
	private float ferienSoll;
	private float wochenArbZeit;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public Date getEintritt() {
		return eintritt;
	}
	public void setEintritt(Date eintritt) {
		this.eintritt = eintritt;
	}
	public Date getAustritt() {
		return austritt;
	}
	public void setAustritt(Date austritt) {
		this.austritt = austritt;
	}
	public String getRolle() {
		return rolle;
	}
	public void setRolle(String rolle) {
		this.rolle = rolle;
	}
	public float getFerienSoll() {
		return ferienSoll;
	}
	public void setFerienSoll(float ferienSoll) {
		this.ferienSoll = ferienSoll;
	}
	public float getWochenArbZeit() {
		return wochenArbZeit;
	}
	public void setWochenArbZeit(float wochenArbZeit) {
		this.wochenArbZeit = wochenArbZeit;
	}


}
