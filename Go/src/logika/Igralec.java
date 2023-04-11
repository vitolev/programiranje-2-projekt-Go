package logika;

/**
 * Možni igralci.
 */

public enum Igralec {
	CRNI, BELI;

	public Igralec nasprotnik() {
		return (this == CRNI ? BELI : CRNI);
	}
	
	public Polje getPolje() {
		return (this == CRNI ? Polje.CRNI : Polje.BELI);
	}
	
	
	@Override
	public String toString() {
		return (this == CRNI ? "Crni" : "Beli");
	}
}
