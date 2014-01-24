package autoKonfiguration;

import java.io.Serializable;

import einzelteile.Achse;
import einzelteile.Bodenplatte;
import einzelteile.Karosserie;
import einzelteile.Lenkrad;
import einzelteile.ReifenPaar;
import einzelteile.Sitz;

/**
 * Jedes Auto besteht aus einer Bodenplatte, einer Karosserie, zwei Achsen, zwei
 * Reifenpaaren, einem Sitz und einem Lenkrad.
 * <p>
 * In jeder Abteilung (Produktion, Montage, Pr\u00FCfung, Logistik) wird
 * festgehalten, welcher Roboter die Arbeiten ausgef\u00FChrt hat. Jeder Roboter
 * wird dabei durch eine eigene ID identifiziert.
 * 
 * @author Michael Borko
 * 
 */
public class Auto implements Serializable {

	private static final long serialVersionUID = -6146701141107900492L;

	private final long autoID;
	private final long monteurID;

	private long prueferGewichtID = -1;
	private long prueferDefekteID = -1;
	private long lieferantID = -1;

	private Achse achseVorn;
	private Achse achseHinten;
	private ReifenPaar reifenPaarVorn;
	private ReifenPaar reifenPaarHinten;
	private Bodenplatte bodenplatte;
	private Sitz sitz;
	private Karosserie karosserie;
	private Lenkrad lenkrad;

	private boolean defekt = false;

	public Auto(final long autoID, final long monteurID) {
		this.autoID = autoID;
		this.monteurID = monteurID;
	}

	public long getPrueferGewichtID() {
		return prueferGewichtID;
	}

	public void setPrueferGewichtID(long prueferGewichtID) {
		this.prueferGewichtID = prueferGewichtID;
	}

	public long getPrueferDefekteID() {
		return prueferDefekteID;
	}

	public void setPrueferDefekteID(long prueferDefekteID) {
		this.prueferDefekteID = prueferDefekteID;
	}

	public long getLieferantID() {
		return lieferantID;
	}

	public void setLieferantID(long lieferantID) {
		this.lieferantID = lieferantID;
	}

	public Achse getAchseVorn() {
		return achseVorn;
	}

	public void setAchseVorn(Achse achseVorn) {
		this.achseVorn = achseVorn;
	}

	public Achse getAchseHinten() {
		return achseHinten;
	}

	public void setAchseHinten(Achse achseHinten) {
		this.achseHinten = achseHinten;
	}

	public ReifenPaar getReifenPaarVorn() {
		return reifenPaarVorn;
	}

	public void setReifenPaarVorn(ReifenPaar reifenPaarVorn) {
		this.reifenPaarVorn = reifenPaarVorn;
	}

	public ReifenPaar getReifenPaarHinten() {
		return reifenPaarHinten;
	}

	public void setReifenPaarHinten(ReifenPaar reifenPaarHinten) {
		this.reifenPaarHinten = reifenPaarHinten;
	}

	public Bodenplatte getBodenplatte() {
		return bodenplatte;
	}

	public void setBodenplatte(Bodenplatte bodenplatte) {
		this.bodenplatte = bodenplatte;
	}

	public Sitz getSitz() {
		return sitz;
	}

	public void setSitz(Sitz sitz) {
		this.sitz = sitz;
	}

	public Karosserie getKarosserie() {
		return karosserie;
	}

	public void setKarosserie(Karosserie karosserie) {
		this.karosserie = karosserie;
	}

	public Lenkrad getLenkrad() {
		return lenkrad;
	}

	public void setLenkrad(Lenkrad lenkrad) {
		this.lenkrad = lenkrad;
	}

	public boolean isDefekt() {
		return defekt;
	}

	public void setDefekt(boolean defekt) {
		this.defekt = defekt;
	}

	public long getAutoID() {
		return autoID;
	}

	public long getMonteurID() {
		return monteurID;
	}

	/**
	 * Gibt alle Informationen (IDs der Einzelteile und beteiligte Roboter) des
	 * hergestellten Autos als verkettete Zeichenkette getrennt durch
	 * Leerzeichen aus. ProduzentenIDs der Einzelteile werden jedoch mit
	 * Beistrichen getrennt gesammelt.
	 * <p>
	 * Folgende Struktur wird eingehalten:
	 * <p>
	 * autoID achseVorn achseHinten reifenPaarVorn reifenPaarHinten bodenplatte
	 * sitz karosserie lenkrad produzentenID,pID,pID,... monteurID
	 * prueferGewichtID,prueferDefektID lieferantID
	 */
	@Override
	public String toString() {

		StringBuffer ret = new StringBuffer();
		StringBuffer produzenten = new StringBuffer();

		ret.append(autoID);
		ret.append(" " + achseVorn.toString().split(";")[0]);
		produzenten.append(achseVorn.toString().split(";")[1] + ",");
		ret.append(" " + achseHinten.toString().split(";")[0]);
		produzenten.append(achseHinten.toString().split(";")[1] + ",");
		ret.append(" " + reifenPaarVorn.toString().split(";")[0]);
		produzenten.append(reifenPaarVorn.toString().split(";")[1] + ",");
		ret.append(" " + reifenPaarHinten.toString().split(";")[0]);
		produzenten.append(reifenPaarHinten.toString().split(";")[1] + ",");
		ret.append(" " + bodenplatte.toString().split(";")[0]);
		produzenten.append(bodenplatte.toString().split(";")[1] + ",");
		ret.append(" " + sitz.toString().split(";")[0]);
		produzenten.append(sitz.toString().split(";")[1] + ",");
		ret.append(" " + karosserie.toString().split(";")[0]);
		produzenten.append(karosserie.toString().split(";")[1] + ",");
		ret.append(" " + lenkrad.toString().split(";")[0]);
		produzenten.append(lenkrad.toString().split(";")[1]);
		ret.append(" " + produzenten.toString());
		ret.append(" " + monteurID);
		ret.append(" " + prueferGewichtID + "," + prueferDefekteID);
		ret.append(" " + lieferantID);

		return ret.toString();
	}
}
