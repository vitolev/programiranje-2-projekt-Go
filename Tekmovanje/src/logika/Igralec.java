package logika;

/**
 *  Mozna sta le 2 tipa igralca. Crni ali pa beli
 */

public enum Igralec {
	CRNI, BELI;
	
	// Return nasprotni tip igralca
	public Igralec nasprotnik() {
		return (this == CRNI ? BELI : CRNI);
	}
	
	// Tip (barva) polja na katerem je figura igralca ustreza tipu (barvi) igralca.
	// Return tip (barvo) polja glede na igralca.
	public Polje getPolje() {
		return (this == CRNI ? Polje.CRNO : Polje.BELO);
	}
	
	
	@Override
	public String toString() {
		return (this == CRNI ? "Crni" : "Beli");
	}
}
