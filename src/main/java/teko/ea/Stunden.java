package teko.ea;

import java.util.Date;

public class Stunden {

	private long id;
	private int uid;
	private String stundenTyp;
	private float totalStunden;
	private Date date;
	private int fromTime;
	private int toTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getStundenTyp() {
		return stundenTyp;
	}

	public void setStundenTyp(String stundenTyp) {
		this.stundenTyp = stundenTyp;
	}

	public float getTotalStunden() {
		return totalStunden;
	}

	public void setTotalHours(float totalStunden) {
		this.totalStunden = totalStunden;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getFromTime() {
		return fromTime;
	}

	public void setFromTime(int fromTime) {
		this.fromTime = fromTime;
	}

	public int getToTime() {
		return toTime;
	}

	public void setToTime(int toTime) {
		this.toTime = toTime;
	}

}
