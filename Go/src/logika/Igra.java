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
	private Set<Grupa> grupeBelega;
	private Set<Grupa> grupeCrnega;
	
	// Mnozici vseh belih in crnih tock
	private Set<Tocka> vseBeleTocke;
	private Set<Tocka> vseCrneTocke;
	
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
			moznePoteze.remove(poteza);
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
				ZdruziStikajoceCrneGrupe(izbranaTocka);
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
				ZdruziStikajoceBeleGrupe(izbranaTocka);
			}
			naPotezi = naPotezi.nasprotnik(); 
			return true;
		}
		else {
			return false;
		}
	}
	
	// Nekatere grupe istega igralca (iste barve) se morda stikajo. Združimo te grupe v eno
	private void ZdruziStikajoceBeleGrupe(Tocka tocka) {
		Grupa grupa1 = null; // Grupa, ki vsebuje izbrano tocko. Takšna grupa zagotovo mora obstajati, ker tocka pripada eni grupi.
		Grupa grupa2 = null; // Grupa, ki med sosednjimi točkami vsebuje izbrano tocko. Takšna grupa ni nujno da obstaja.
					  		 // V primeru, da ne obstaja, to pomeni, da ne rabimo zdruziti ničesar.
					  		 // Če pa obstaja pa moramo zdruziti ti dve grupi.
		for(Grupa grupa : grupeBelega) {
			if(grupa.vsebujePovezanoTocko(tocka)) {
				grupa1 = grupa;
			}
			if(grupa.vsebujeSosednjoTocko(tocka)) {
				grupa2 = grupa;
			}
		}
		
		if(grupa2 != null) {
			Grupa zdruzenaGrupa = new Grupa();
			for(Tocka tocka_ : grupa1.povezaneTocke) {
				zdruzenaGrupa.dodajTocko(tocka_);
			}
			for(Tocka tocka_ : grupa2.povezaneTocke) {
				zdruzenaGrupa.dodajTocko(tocka_);
			}
			
			grupeBelega.remove(grupa1);
			grupeBelega.remove(grupa2);
			grupeBelega.add(zdruzenaGrupa);
			
			ZdruziStikajoceBeleGrupe(tocka);
		}
	}
	
	private void ZdruziStikajoceCrneGrupe(Tocka tocka) {
		Grupa grupa1 = null; // Grupa, ki vsebuje izbrano tocko. Takšna grupa zagoto mora obstajati, ker tocka pripada eni grupi.
		Grupa grupa2 = null; // Grupa, ki med sosednjimi točkami vsebuje izbrano tocko. Takšna grupa ni nujno da obstaja.
					  		 // V primeru, da ne obstaja, to pomeni, da ne rabimo zdruziti ničesar.
					  		 // Če pa obstaja pa moramo zdruziti ti dve grupi.
		for(Grupa grupa : grupeCrnega) {
			if(grupa.vsebujePovezanoTocko(tocka)) {
				grupa1 = grupa;
			}
			if(grupa.vsebujeSosednjoTocko(tocka)) {
				grupa2 = grupa;
			}
		}
		
		if(grupa2 != null) {
			Grupa zdruzenaGrupa = new Grupa();
			for(Tocka tocka_ : grupa1.povezaneTocke) {
				zdruzenaGrupa.dodajTocko(tocka_);
			}
			for(Tocka tocka_ : grupa2.povezaneTocke) {
				zdruzenaGrupa.dodajTocko(tocka_);
			}
			
			grupeCrnega.remove(grupa1);
			grupeCrnega.remove(grupa2);
			grupeCrnega.add(zdruzenaGrupa);
			
			ZdruziStikajoceCrneGrupe(tocka);
		}
	}
}



