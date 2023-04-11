package logika;

import java.util.LinkedList;
import java.util.List;

public class Igra {

	
	// Velikost igralne pološče je N x N.
	public static final int N = 3;

	
	// Pomožen seznam vseh vrstah na plošči.
	private static final List<Vrsta> VRSTE = new LinkedList<Vrsta>();

	static {
		// Ta koda se izvede na začetku, ko se prvič požene program.
		// Njena naloga je, da inicializira vrednosti statičnih
		// spremenljivk.
		
		// Iniciraliziramo N-vrste
		int[][] smer = {{1,0}, {0,1}, {1,1}, {1,-1}};
		for (int x = 0; x < N; x++) {
			for (int y = 0; y < N; y++) {
				for (int[] s : smer) {
					int dx = s[0];
					int dy = s[1];
					// če je skrajno polje terice še na plošči, jo dodamo med terice
					if ((0 <= x + (N-1) * dx) && (x + (N-1) * dx < N) && 
						(0 <= y + (N-1) * dy) && (y + (N-1) * dy < N)) {
						int[] vrsta_x = new int[N];
						int[] vrsta_y = new int[N];
						for (int k = 0; k < N; k++) {
							vrsta_x[k] = x + dx * k;
							vrsta_y[k] = y + dy * k;
						}
						VRSTE.add(new Vrsta(vrsta_x, vrsta_y));
					}
				}
			}
		}
	}
	

	// Igralno polje
	private Polje[][] plosca;
	
		
	// Igralec, ki je trenutno na potezi.
	// Vrednost je poljubna, če je igre konec (se pravi, lahko je napačna).
	private Igralec naPotezi;


	/**
	 * Nova igra, v začetni poziciji je prazna in na potezi je O.
	 */
	public Igra() {
		plosca = new Polje[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				plosca[i][j] = Polje.PRAZNO;
			}
		}
		naPotezi = Igralec.O;
	}
	
	/**
	 * @return igralca na potezi
	 */
	public Igralec naPotezi () {
		return naPotezi;
	}
	
	/**
	 * @return igralna plosca
	 */
	public Polje[][] getPlosca () {
		return plosca;
	}

	/**
	 * @return seznam možnih potez
	 */
	public List<Koordinati> poteze() {
		LinkedList<Koordinati> ps = new LinkedList<Koordinati>();
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (plosca[i][j] == Polje.PRAZNO) {
					ps.add(new Koordinati(i, j));
				}
			}
		}
		return ps;
	}

	/**
	 * @param t
	 * @return igralec, ki ima zapolnjeno vrsto @{t}, ali {@null}, če nihče
	 */
	private Igralec cigavaVrsta(Vrsta t) {
		int count_X = 0;
		int count_O = 0;
		for (int k = 0; k < N && (count_X == 0 || count_O == 0); k++) {
			switch (plosca[t.x[k]][t.y[k]]) {
			case O: count_O += 1; break;
			case X: count_X += 1; break;
			case PRAZNO: break;
			}
		}
		if (count_O == N) { return Igralec.O; }
		else if (count_X == N) { return Igralec.X; }
		else { return null; }
	}

	/**
	 * @return zmagovalna vrsta, ali {@null}, če je ni
	 */
	public Vrsta zmagovalnaVrsta() {
		for (Vrsta t : VRSTE) {
			Igralec lastnik = cigavaVrsta(t);
			if (lastnik != null) return t;
		}
		return null;
	}
	
	/**
	 * @return trenutno stanje igre
	 */
	public Stanje stanje() {
		// Ali imamo zmagovalca?
		Vrsta t = zmagovalnaVrsta();
		if (t != null) {
			switch (plosca[t.x[0]][t.y[0]]) {
			case O: return Stanje.ZMAGA_O; 
			case X: return Stanje.ZMAGA_X;
			case PRAZNO: assert false;
			}
		}
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

	/**
	 * Odigraj potezo p.
	 * 
	 * @param p
	 * @return true, če je bila poteza uspešno odigrana
	 */
	public boolean odigraj(Koordinati p) {
		if (plosca[p.x()][p.y()] == Polje.PRAZNO) {
			plosca[p.x()][p.y()] = naPotezi.getPolje();
			naPotezi = naPotezi.nasprotnik();
			return true;
		}
		else {
			return false;
		}
	}
}
