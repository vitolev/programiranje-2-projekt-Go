package logika;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	public Set<Grupa> grupeBelega;
	public Set<Grupa> grupeCrnega;
	
	// Mnozici vseh belih in crnih tock
	public Set<Tocka> vseBeleTocke;
	public Set<Tocka> vseCrneTocke;
	
	private ArrayList<Poteza> moznePoteze;
	
	// Return naPotezi
	public Igralec naPotezi () {
		return naPotezi;
	}
	
	public Polje[][] getPlosca () {
		return plosca;
	}
	
	// KONSTRUKTOR
	public Igra() {
		moznePoteze = new ArrayList<Poteza>();
		plosca = new Polje[N][N];	// Ustvari novo prazno igralno ploščo
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				plosca[i][j] = Polje.PRAZNO;	// Na začetku je celotna plošča prazna, zato nastavimo vsako polje na tip prazno.
				moznePoteze.add(new Poteza(i,j));
			}
		}
		grupeBelega = new HashSet<Grupa>(); // Ustvarimo prazni mnozici grup za oba igralca.
		grupeCrnega = new HashSet<Grupa>(); //
		
		vseBeleTocke = new HashSet<Tocka>();
		vseCrneTocke = new HashSet<Tocka>();
		
		naPotezi = Igralec.CRNI;	// Po pravilih začne črni igralec.
	}
	
	public Igra(Igra igra) {
		this.plosca = new Polje[N][N]; 
		for(int i = 0; i < N; i++) { 
			for(int j = 0; j < N; j++) { 
				this.plosca[i][j] = igra.plosca[i][j]; 
			} 
		}
		this.moznePoteze = (ArrayList<Poteza>) igra.moznePoteze.clone(); 
		//this.moznePoteze = new ArrayList<Poteza>(igra.moznePoteze); // According to stackoverflow bi to moralo narediti kopijo lista,
																	// ampak nisem ziher. Če kaj ne bo delalo preveri to. 
		this.naPotezi = igra.naPotezi;
		
		grupeBelega = new HashSet<Grupa>(); // Ustvarimo prazni mnozici grup za oba igralca.
		grupeCrnega = new HashSet<Grupa>(); //
		
		vseBeleTocke = new HashSet<Tocka>(igra.vseBeleTocke);
		vseCrneTocke = new HashSet<Tocka>(igra.vseCrneTocke);
		
		for(Grupa grupa : igra.grupeBelega) {
			grupeBelega.add(new Grupa(grupa));
		}

		for(Grupa grupa : igra.grupeCrnega) {
			grupeCrnega.add(new Grupa(grupa));
		}		
	}
	
	// vrne list vseh moznih potez
	public List<Poteza> poteze(){
		return moznePoteze;
	}
	
	// Return stanje igre.
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
		
		/* Mislim da je ta del nepotreben. Preveri se enkrat pravila
		
		// Preverimo, če je katero polje še prazno.
		// Če ga imamo, igre ni konec in je nekdo na potezi
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (plosca[i][j] == Polje.PRAZNO) return Stanje.V_TEKU;
			}
		}
		// Polje je polno, po pravilih Capture Go to pomeni, da je zmagal beli. 
		return Stanje.ZMAGA_BELI;
		
		*/
		
		return Stanje.V_TEKU;
	}
	
	private boolean ZmagovalecCRNI() {
		for(Grupa belaGrupa : grupeBelega) {
			if(belaGrupa.sosednjeTocke.size() == 0) {
				return true;
			}
		}
		return false;
	}
	
	private boolean ZmagovalecBELI() {
		for(Grupa crnaGrupa : grupeCrnega) {
			if(crnaGrupa.sosednjeTocke.size() == 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean odigraj(Poteza poteza) {
		if (plosca[poteza.x()][poteza.y()] == Polje.PRAZNO) {
			moznePoteze.remove(poteza);
			plosca[poteza.x()][poteza.y()] = naPotezi.getPolje();
			
			List<Grupa> grupeZaZdruzit = new ArrayList<Grupa>();
			
			// Ustvarimo novo tocko, ki ustreza odigrani potezi.
			// To tocko dodamo ustrezni grupi ustreznemu igralcu. Če take grupe še ni, ustvari novo grupo.
			Tocka izbranaTocka = new Tocka(poteza.x(), poteza.y());	
			if(naPotezi == Igralec.CRNI) {
				for(Grupa grupa : grupeCrnega) {
					if(grupa.sosednjeTocke.contains(izbranaTocka)) {
						grupa.dodajTocko(izbranaTocka, vseBeleTocke);
						grupeZaZdruzit.add(grupa);
					}
				}
				vseCrneTocke.add(izbranaTocka);
				
				// ce nismo tocke dodalni nobeni obstojeci grupi, ustvarimo novo grupo s to tocko.
				if(grupeZaZdruzit.size() == 0) {
					Grupa novaGrupa = new Grupa();
					novaGrupa.dodajTocko(izbranaTocka, vseBeleTocke);
					grupeCrnega.add(novaGrupa);
				}
				else {
					// zdruzimo grupe ce smo tocko dodali vecim grupam. Ce ne pa samo posodobimo stevilo obkoljenosti.
					if(grupeZaZdruzit.size() > 1) {
						ZdruziStikajoceCrneGrupe(grupeZaZdruzit);
					}
					else { // grupeZaZdruzit.size() == 1
						
					}
				}
				PosodobiObkoljenostNasprotnihGrup(izbranaTocka, false);
			}
			else {
				// naPotezi = Igralec.BELI
				for(Grupa grupa : grupeBelega) {
					if(grupa.sosednjeTocke.contains(izbranaTocka)) {
						grupa.dodajTocko(izbranaTocka, vseCrneTocke);
						grupeZaZdruzit.add(grupa);
					}
				}
				vseBeleTocke.add(izbranaTocka);
				
				if(grupeZaZdruzit.size() == 0) {
					Grupa novaGrupa = new Grupa();
					novaGrupa.dodajTocko(izbranaTocka, vseCrneTocke);
					grupeBelega.add(novaGrupa);
				}
				else {
					if(grupeZaZdruzit.size() > 1) {
						ZdruziStikajoceBeleGrupe(grupeZaZdruzit);
					}
					else {
						
					}
				}
				PosodobiObkoljenostNasprotnihGrup(izbranaTocka, true);
				
			}
			naPotezi = naPotezi.nasprotnik();
			
			return true;
		}
		else {
			return false;
		}
	}
	
	// Nekatere grupe istega igralca (iste barve) se morda stikajo. Združimo te grupe v eno
	private void ZdruziStikajoceBeleGrupe(List<Grupa> grupeZaZdruzit) {
		Grupa zdruzenaGrupa = new Grupa();
		
		for(Grupa grupa : grupeZaZdruzit) {
			for(Tocka tocka : grupa.povezaneTocke) {
				zdruzenaGrupa.dodajTocko(tocka, vseCrneTocke);
			}			
		}
		
		grupeBelega.removeAll(grupeZaZdruzit);
		grupeBelega.add(zdruzenaGrupa);
	}
	
	private void ZdruziStikajoceCrneGrupe(List<Grupa> grupeZaZdruzit) {
		
		Grupa zdruzenaGrupa = new Grupa();
		
		for(Grupa grupa : grupeZaZdruzit) {
			for(Tocka tocka : grupa.povezaneTocke) {
				zdruzenaGrupa.dodajTocko(tocka, vseBeleTocke);
			}			
		}
		
		grupeCrnega.removeAll(grupeZaZdruzit);
		grupeCrnega.add(zdruzenaGrupa);
		
		//PosodobiSteviloDoObkoljenosti(zdruzenaGrupa, true);
	}
	/*
	private void PosodobiSteviloDoObkoljenosti(Grupa grupa, boolean crnaGrupa) {
		if(crnaGrupa) {
			int stSosednjihTock = grupa.sosednjeTocke.size();
			for(Tocka sosednjaTocka : grupa.sosednjeTocke) {
				if(vseBeleTocke.contains(sosednjaTocka)) {
					stSosednjihTock--;
				}
			}
			grupa.steviloDoObkoljenosti = stSosednjihTock;
		}
	}
	*/
	
	private void PosodobiObkoljenostNasprotnihGrup(Tocka izbranaTocka, boolean aliJeCrniNasprotnik) {
		if(aliJeCrniNasprotnik) {
			for(Grupa grupa : grupeCrnega) {
				if(grupa.sosednjeTocke.contains(izbranaTocka)) {
					grupa.sosednjeTocke.remove(izbranaTocka);
				}
			}
		}
		else {
			for(Grupa grupa : grupeBelega) {
				if(grupa.sosednjeTocke.contains(izbranaTocka)) {
					grupa.sosednjeTocke.remove(izbranaTocka);
				}
			}
		}
	}
}



