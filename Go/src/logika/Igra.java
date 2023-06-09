package logika;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import splosno.Poteza;

public class Igra {

	// Velikost igralne pološče je N x N.
	public static int N = 9;
	
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
	
	private boolean crniPredalPotezo;
	private boolean beliPredalPotezo;
	
	
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
		
		crniPredalPotezo = false;
		beliPredalPotezo = false;
		
		naPotezi = Igralec.CRNI;	// Po pravilih začne črni igralec.
	}
	
	// Konstruktor za kopijo dane igre
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
		
		crniPredalPotezo = igra.crniPredalPotezo;
		beliPredalPotezo = igra.beliPredalPotezo;
		
		for(Grupa grupa : igra.grupeBelega) {
			grupeBelega.add(new Grupa(grupa));
		}

		for(Grupa grupa : igra.grupeCrnega) {
			grupeCrnega.add(new Grupa(grupa));
		}		
	}
	
	// tukaj vrne seznam vseh stolpcev in vrstic
	public Polje[][] vrsticeInStolpci() {
        Polje[][] vrsticeInStolpci = new Polje[N*2][N];

        // Get columns
        for (int j = 0; j < N; j++) {
            for (int i = 0; i < N; i++) {
            	vrsticeInStolpci[j][i] = plosca[i][j];
            }
        }

        // Get rows
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
            	vrsticeInStolpci[i + N][j] = plosca[i][j];
            }
        }

        return vrsticeInStolpci;
    }
	
	public static Polje[] reverseArray(Polje[] array) {
	    int start = 0;
	    int end = array.length - 1;

	    while (start < end) {
	        Polje temp = array[start];
	        array[start] = array[end];
	        array[end] = temp;
	        
	        start++;
	        end--;
	    }

	    return array;
	}

	
	// vrne list vseh moznih potez
	public List<Poteza> poteze(){
		return moznePoteze;
	}
	
	private int[][] offsets = {
			{1,0},
			{-1,0},
			{0,1},
			{0,-1}
	};
	
	private boolean isValidCoordinate(int i, int j) {
		return i >= 0 && i < N && j >= 0 && j < N;
	}
	
	// Return stanje igre.
	public Stanje stanje() {
		// Preverimo ce sta oba igralca predala potezo in tako koncamo igro.
		if(beliPredalPotezo && crniPredalPotezo) {
			// Ideja: Ustvarimo mnozico grup, ki vsebuje vse grupe praznih polj. Te grupe so bodisi obkoljene z belimi ali crnimi tockami
			// ali pa z robom same igralne plosce. Iz tega s cim so obkrozene dolocimo ali ta grupa praznih polj oz. teritorij
			// pripada belemu, crnemu ali pa je nevtralen
			Set<Grupa> nezasedeneGrupe = new HashSet<Grupa>();
			for(int i = 0; i < N; i++) {
				for(int j = 0; j < N; j++) {
					if(plosca[i][j] == Polje.PRAZNO) {
						boolean dodalTockoObstojeciGrupi = false;
						for(Grupa grupa : nezasedeneGrupe) {
							if(grupa.sosednjeTocke.contains(new Tocka(i,j))) {
								grupa.dodajTocko(new Tocka(i,j), new HashSet<Tocka>());
								dodalTockoObstojeciGrupi = true;
								break;
							}
						}
						if(!dodalTockoObstojeciGrupi) {
							Grupa novaGrupa = new Grupa();
							novaGrupa.dodajTocko(new Tocka(i,j), new HashSet<Tocka>());
							nezasedeneGrupe.add(novaGrupa);
						}
					}
				}
			}
			
			// Tocke posameznega igralca so enake vsoti stevilo vseh postavljenih figur in stevilo obmocja, ki ga omejujejo
			int tockeCrnega = vseCrneTocke.size();
			int tockeBelega = vseBeleTocke.size();
			
			// Za vsako grupo iz mnozice nezasedenih grup preverimo ali meji samo na crne ali samo na bele ali pa na oboje.
			// Ce grupa meji samo na crne potem tockam crnega pristejemo velikost te grupe (toliko obmocja crni omejuje),
			// ce pa meji samo na bele figure potem pa pristejemo belim. V primeru, da grupa meji na bele in crne figure to pomeni
			// da je to obmocje nevtralo in ne pripada nobeni grupi.
			for(Grupa grupa : nezasedeneGrupe) {
				boolean mejiNaCrno = false;
				boolean mejiNaBelo = false;
				for(Tocka tocka : grupa.sosednjeTocke) {
					if(vseCrneTocke.contains(tocka)) {
						mejiNaCrno = true;
					}
					else if(vseBeleTocke.contains(tocka)){
						mejiNaBelo = true;
					}
				}
				if(mejiNaCrno && !mejiNaBelo) {
					tockeCrnega += grupa.povezaneTocke.size();
				}
				else if(mejiNaBelo && !mejiNaCrno) {
					tockeBelega += grupa.povezaneTocke.size();
				}
			}
			
			//System.out.println("Tocke crnega: " + tockeCrnega);
			//System.out.println("Tocke belega: " + tockeBelega);
			
			if(tockeCrnega > tockeBelega) {
				return Stanje.ZMAGA_CRNI;
			}
			if(tockeCrnega < tockeBelega) {
				return Stanje.ZMAGA_BELI;
			}
			return Stanje.NEODLOCENO;
		}
		
		return Stanje.V_TEKU;
	}
	
	// Funkcija ki vrne list vseh grup belega, ki so popolnoma obkoljene s strani crnega
	private List<Grupa> ObkoljenaGrupaBELA() {
		List<Grupa> obkoljeneBeleGrupe = new ArrayList<Grupa>();
		for(Grupa belaGrupa : grupeBelega) {
			if(belaGrupa.sosednjeTocke.size() == 0) {
				obkoljeneBeleGrupe.add(belaGrupa);
			}
		}
		return obkoljeneBeleGrupe;
	}
	// Funkcija ki vrne list vseh grup crnega, ki so popolnoma obkoljene s strani belega	
	private List<Grupa> ObkoljenaGrupaCRNA() {
		List<Grupa> obkoljeneCrneGrupe = new ArrayList<Grupa>();
		for(Grupa crnaGrupa : grupeCrnega) {
			if(crnaGrupa.sosednjeTocke.size() == 0) {
				obkoljeneCrneGrupe.add(crnaGrupa);
			}
		}
		return obkoljeneCrneGrupe;
	}
	
	public boolean odigraj(Poteza poteza) {
		// Poteza (-1,-1) oznacuje predajo poteze.
		if(poteza.x() == -1 && poteza.y() == -1) {
			if(naPotezi == Igralec.CRNI) {
				crniPredalPotezo = true;
			}
			else {
				beliPredalPotezo = true;
			}
			naPotezi = naPotezi.nasprotnik();
			
			return true;
		}
		
		if (plosca[poteza.x()][poteza.y()] == Polje.PRAZNO) {
			crniPredalPotezo = false;
			beliPredalPotezo = false;
			
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
				}
				PosodobiObkoljenostNasprotnihGrup(izbranaTocka, Igralec.BELI);
				
				// Preverimo ali bi igralec s to potezo obkolil kakšne grupe in ustrezno spremeni stanje na polju
				List<Grupa> obkoljeneBeleGrupe = ObkoljenaGrupaBELA();
				if(obkoljeneBeleGrupe.size() > 0) {
					// Crni igralec je s to potezo obkolil neko belo grupo. Odstrani to belo grupo
					for(Grupa grupa : obkoljeneBeleGrupe) {
						for(Tocka tocka : grupa.povezaneTocke) {
							vseBeleTocke.remove(tocka);
							plosca[tocka.x()][tocka.y()] = Polje.PRAZNO;
						}
						grupeBelega.remove(grupa);
					}
					
					// Ker smo odstranili nekatere grupe od nasprotnika, smo s tem morda spremenili sosednje tocke lastnih grup
					PosodobiObkoljenostSvojihGrup(Igralec.CRNI);
				}
				else if (ObkoljenaGrupaCRNA().size() > 0) {
					// Crni igralec je s to potezo poskusal narediti samomorilno potezo, kar ni dovoljeno. Zato return false
					return false;
				}
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
				}
				PosodobiObkoljenostNasprotnihGrup(izbranaTocka, Igralec.CRNI);
				
				// Preverimo ali bi igralec s to potezo obkolil kakšne grupe in ustrezno spremeni stanje na polju
				List<Grupa> obkoljeneCrneGrupe = ObkoljenaGrupaCRNA();
				if(obkoljeneCrneGrupe.size() > 0) {
					// Crni igralec je s to potezo obkolil neko belo grupo. Odstrani to belo grupo
					for(Grupa grupa : obkoljeneCrneGrupe) {
						for(Tocka tocka : grupa.povezaneTocke) {
							vseCrneTocke.remove(tocka);
							plosca[tocka.x()][tocka.y()] = Polje.PRAZNO;
						}
						grupeCrnega.remove(grupa);
					}
					
					// Ker smo odstranili nekatere grupe od nasprotnika, smo s tem morda spremenili sosednje tocke lastnih grup
					PosodobiObkoljenostSvojihGrup(Igralec.BELI);
				}
				else if (ObkoljenaGrupaBELA().size() > 0) {
					// Crni igralec je s to potezo poskusal narediti samomorilno potezo, kar ni dovoljeno. Zato return false
					return false;
				}
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
	}
	
	private void PosodobiObkoljenostNasprotnihGrup(Tocka izbranaTocka, Igralec nasprotnik) {
		if(nasprotnik == Igralec.CRNI) {
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
	
	// Funkcija, ki posodobi mnozice sosednjih tock za vse grupe nekega igralca. To je vcasih potrebno ce kateri igralec "poje" katero 
	// obmocje od drugega
	private void PosodobiObkoljenostSvojihGrup(Igralec igralec) {
		if(igralec == Igralec.BELI) {
			Set<Grupa> noveGrupeBelega = new HashSet<Grupa>();
			for(Grupa grupa : grupeBelega) {
				noveGrupeBelega.add(new Grupa(grupa, vseCrneTocke));
			}
			grupeBelega.clear();
			grupeBelega = noveGrupeBelega;
		}
		else {
			Set<Grupa> noveGrupeCrnega = new HashSet<Grupa>();
			for(Grupa grupa : grupeCrnega) {
				noveGrupeCrnega.add(new Grupa(grupa, vseBeleTocke));
			}
			grupeCrnega.clear();
			grupeCrnega = noveGrupeCrnega;
		}
	}
	
	@Override
    public boolean equals(Object o) {
 
        // If the object is compared with itself then return true 
        if (o == this) {
            return true;
        }
 
        /* Check if o is an instance of Igra or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Igra)) {
            return false;
        }
         
        // typecast o to Tocka so that we can compare data members
        Igra igra = (Igra) o;
         
        // Compare the data members and return accordingly
        return vseCrneTocke.containsAll(igra.vseCrneTocke) && vseBeleTocke.containsAll(igra.vseBeleTocke);
    }
	
	@Override
    public int hashCode() {
        return Objects.hash(vseBeleTocke, vseCrneTocke);
    }
}



