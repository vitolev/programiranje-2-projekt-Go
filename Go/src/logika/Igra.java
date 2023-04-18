package logika;

import java.util.HashSet;
import java.util.Set;

import splosno.Poteza;

public class Igra {

	// Velikost igralne pološče je N x N.
	public static final int N = 9;
	
	// Igralec, ki je trenutno na potezi.
	// Vrednost je poljubna, če je igre konec (se pravi, lahko je napačna).
	private Igralec naPotezi;

	// Igralna plošča
	private Polje[][] plosca;
	
	// Grupe (skupki povezanih figur) posameznega igralca
	private Set<Grupa> grupeBelega;
	private Set<Grupa> grupeCrnega;
	
	// Mnozici vseh belih in crnih tock
	private Set<Tocka> vseBeleTocke;
	private Set<Tocka> vseCrneTocke;
	
	// Return naPotezi
	public Igralec naPotezi () {
		return naPotezi;
	}
	
	public Polje[][] getPlosca () {
		return plosca;
	}
	
	// KONSTRUKTOR
	public Igra() {
		plosca = new Polje[N][N];	// Ustvari novo prazno igralno ploščo
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				plosca[i][j] = Polje.PRAZNO;	// Na začetku je celotna plošča prazna, zato nastavimo vsako polje na tip prazno.
			}
		}
		grupeBelega = new HashSet<Grupa>(); // Ustvarimo prazni mnozici grup za oba igralca.
		grupeCrnega = new HashSet<Grupa>(); //
		
		vseBeleTocke = new HashSet<Tocka>();
		vseCrneTocke = new HashSet<Tocka>();
		
		naPotezi = Igralec.CRNI;	// Po pravilih začne črni igralec.
	}
	
	// Return trenutno stanje igre.
	public Stanje stanje() {
		// Preverimo, če je kdo zmagal. Ker je funkcija stanje() poklicana preden je izvedena naslednja poteza,
		// je potrebno pogledat ali je igralec iz prejšnje poteze zmagal, zato gledamo naPotezi.nasprotnik().
		if(naPotezi.nasprotnik() == Igralec.CRNI) {
			if(ZmagovalecCRNI()) {
				return Stanje.ZMAGA_CRNI;
			}
			// Preveri suicide move
			if(ZmagovalecBELI()) {
				return Stanje.ZMAGA_BELI;
			}
		}
		else { // naPotezi.nasprotnik() = Igralec.BELI
			if(ZmagovalecBELI()) {
				return Stanje.ZMAGA_BELI;
			}
			// Preveri suicide move
			if(ZmagovalecCRNI()) {
				return Stanje.ZMAGA_CRNI;
			}
		}
		
		// Preverimo, če je katero polje še prazno.
		// Če ga imamo, igre ni konec in je nekdo na potezi
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (plosca[i][j] == Polje.PRAZNO) return Stanje.V_TEKU;
			}
		}
		// Polje je polno, po pravilih Capture Go to pomeni, da je zmagal beli. 
		return Stanje.ZMAGA_BELI;
	}
	
	private boolean ZmagovalecCRNI() {
		for(Grupa belaGrupa : grupeBelega) {
			boolean jeTaGrupaObkrozena = true;
			for(Tocka sosednjaTocka : belaGrupa.sosednjeTocke) {
				if(!vseCrneTocke.contains(sosednjaTocka)) {
					jeTaGrupaObkrozena = false;
				}
			}
			if(jeTaGrupaObkrozena) {
				return true;
			}
		}
		return false;
	}
	
	private boolean ZmagovalecBELI() {
		for(Grupa crnaGrupa : grupeCrnega) {
			boolean jeTaGrupaObkrozena = true;
			for(Tocka sosednjaTocka : crnaGrupa.sosednjeTocke) {
				if(!vseBeleTocke.contains(sosednjaTocka)) {
					jeTaGrupaObkrozena = false;
				}
			}
			if(jeTaGrupaObkrozena) {
				return true;
			}
		}
		return false;
	}
	
	public boolean odigraj(Poteza poteza) {
		if (plosca[poteza.x()][poteza.y()] == Polje.PRAZNO) {
			plosca[poteza.x()][poteza.y()] = naPotezi.getPolje();
			
			// Ustvarimo novo tocko, ki ustreza odigrani potezi.
			// To tocko dodamo ustrezni grupi ustreznemu igralcu. Če take grupe še ni, ustvari novo grupo.
			Tocka izbranaTocka = new Tocka(poteza.x(), poteza.y());	
			if(naPotezi == Igralec.CRNI) {
				boolean dodalTockoObstojeciGrupi = false;
				for(Grupa grupa : grupeCrnega) {
					if(grupa.vsebujeSosednjoTocko(izbranaTocka)) {
						grupa.dodajTocko(izbranaTocka);
						dodalTockoObstojeciGrupi = true;
						break; // lahko skocimo ven iz zanke, ker smo tocko ze dodali vsaj eni grupi
					}
				}
				if(!dodalTockoObstojeciGrupi) {
					Grupa novaGrupa = new Grupa();
					novaGrupa.dodajTocko(izbranaTocka);
					grupeCrnega.add(novaGrupa);
				}
				vseCrneTocke.add(izbranaTocka);
			}
			else {
				// naPotezi = Igralec.BELI
				boolean dodalTockoObstojeciGrupi = false;
				for(Grupa grupa : grupeBelega) {
					if(grupa.vsebujeSosednjoTocko(izbranaTocka)) {
						grupa.dodajTocko(izbranaTocka);
						dodalTockoObstojeciGrupi = true;
						break; // lahko skocimo ven iz zanke, ker smo tocko ze dodali vsaj eni grupi
					}
				}
				if(!dodalTockoObstojeciGrupi) {
					Grupa novaGrupa = new Grupa();
					novaGrupa.dodajTocko(izbranaTocka);
					grupeBelega.add(novaGrupa);
				}
				vseBeleTocke.add(izbranaTocka);
			}
			
			ZdruziStikajoceGrupe();
			naPotezi = naPotezi.nasprotnik(); 
			return true;
		}
		else {
			return false;
		}
	}
	
	// Nekatere grupe istega igralca (iste barve) se morda stikajo. Združimo te grupe v eno
	private void ZdruziStikajoceGrupe() {
		if(naPotezi == Igralec.CRNI) {
			
		}
		else {
			
		}
	}
}
