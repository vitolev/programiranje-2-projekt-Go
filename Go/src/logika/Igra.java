package logika;

import splosno.Poteza;

public class Igra {

	// Velikost igralne pološče je N x N.
	public static final int N = 9;
	
	// Igralec, ki je trenutno na potezi.
	// Vrednost je poljubna, če je igre konec (se pravi, lahko je napačna).
	private Igralec naPotezi;

	// Igralno polje
	private Polje[][] plosca;
	
	// Return naPotezi
	public Igralec naPotezi () {
		return naPotezi;
	}
	
	public Polje[][] getPlosca () {
		return plosca;
	}
	
	// KONSTRUKTOR
	public Igra() {
		plosca = new Polje[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				plosca[i][j] = Polje.PRAZNO;
			}
		}
		naPotezi = Igralec.CRNI;
	}
	
	// Return trenutno stanje igre. Trenutno je to copy-paste iz TicTacToe. Treba implementirat pravila Go.
	public Stanje stanje() {
		// Ali imamo zmagovalca? Treba implementirat kdaj kdo zmaga.
		/*
		Vrsta t = zmagovalnaVrsta();
		if (t != null) {
			switch (plosca[t.x[0]][t.y[0]]) {
			case BELI: return Stanje.ZMAGA_BELI; 
			case CRNI: return Stanje.ZMAGA_CRNI;
			case PRAZNO: assert false;
			}
		}
		*/
		// Ali imamo kakšno prazno polje?
		// Če ga imamo, igre ni konec in je nekdo na potezi
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (plosca[i][j] == Polje.PRAZNO) return Stanje.V_TEKU;
			}
		}
		// Polje je polno, rezultat je neodločen
		return Stanje.NEODLOCENO;
	}
	
	public boolean odigraj(Poteza poteza) {
		if (plosca[poteza.x()][poteza.y()] == Polje.PRAZNO) {
			plosca[poteza.x()][poteza.y()] = naPotezi.getPolje();
			naPotezi = naPotezi.nasprotnik();
			return true;
		}
		else {
			return false;
		}
	}
}
