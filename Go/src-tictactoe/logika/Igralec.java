package logika;

/**
 * Možni igralci.
 */

public enum Igralec {
	X, O;

	public Igralec nasprotnik() {
		return (this == X ? O : X);
	}

	public Polje getPolje() {
		return (this == X ? Polje.X : Polje.O);
	}
	
	@Override
	public String toString() {
		return (this == X ? "X" : "O");
	}
}
