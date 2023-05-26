package inteligenca;

import java.util.List;

import logika.Igra;
import splosno.Poteza;
import logika.Igra;
import logika.Igralec;
import logika.Tocka;

public class Inteligenca extends splosno.KdoIgra {
	
	private static final int ZMAGA = 100; // vrednost zmage, ta bodo kasneje še spremenjeni glede na metodo inteligence
	private static final int ZGUBA = -ZMAGA;
	
    private int globina;

    private static long startTime;
    
    private static boolean prvaPonovitev = true;
    
    public Inteligenca() {
    	super("Algebros");	// Tukaj sem najino skupino poimenoval Algebros.
    						// Ta super mora bit da poklice konstruktor iz razreda KdoIgra, ker ga tukaj extendamo
    						// in za parameter potrebuje ime skupine.
    	globina = 5;
    }
    
    public Poteza izberiPotezo(Igra igra) {
    	OcenjenaPoteza najboljsaPoteza =
        		// minimax(igra, this.globina, igra.naPotezi());
        		prvaIteracijaAlphaBeta(igra, globina, Integer.MIN_VALUE, Integer.MAX_VALUE, igra.naPotezi());
        		//alphabetaMultithread(igra, this.globina, Integer.MIN_VALUE, Integer.MAX_VALUE, igra.naPotezi());
    	prvaPonovitev = true;
        return najboljsaPoteza.poteza;	
    }
    public OcenjenaPoteza prvaIteracijaAlphaBeta(Igra igra, int globina, int alpha, int beta, Igralec jaz) {
		startTime = System.nanoTime();
		int ocena;
		if (igra.naPotezi() == jaz) {ocena = ZGUBA;} else {ocena = ZMAGA;} 
		List<Poteza> moznePoteze = igra.poteze(); // tukaj dodaj dodatno obrezovanje potez, torej samo sosednje itd.
		if(moznePoteze.size() == 81) {
			return new OcenjenaPoteza(new Poteza(4,4), 100);
		}
		Poteza kandidat = null;
		for (Poteza p: moznePoteze) {
				if(jeBliznjaTocka(p, igra)) {
					if(kandidat == null) {
						kandidat = p;
					}
					Igra kopijaIgre = new Igra(igra);
			        kopijaIgre.odigraj(p);
			        int ocenap;
			        switch (kopijaIgre.stanje()) {
			        case ZMAGA_CRNI: {
			        	ocenap = (jaz == Igralec.CRNI ? ZMAGA : ZGUBA); 
			        	break;
			        }
			        case ZMAGA_BELI: {
			        	ocenap = (jaz == Igralec.BELI ? ZMAGA : ZGUBA);
			        	break;
			        }
			        default:
			            if (globina == 1) ocenap = OceniPozicijo.oceniPozicijo(kopijaIgre, jaz);
			            else {
			            	try {
			            		// poskusi resit s podano globino in ce ne resi v zglednem casu (mislim da je 5s max) vrze izjemo
			            		// ter naj sedaj resi z globino 4. Po najinemu testiranju to resi v manj kot 1s torej ni panike 
			            		// da bi tudi v tem primeru vrglo izjemo. Ampak da smo ziher ce slucajno se to vrze izjemo
			            		// naj tedaj pogleda kar za globino 1 kar pa sploh ne more vreci izjeme
			            		ocenap = alphabetaPoteze(kopijaIgre, globina-1, alpha, beta, jaz).ocena;
			            	}
			            	catch(Exception e) {
			            		if(prvaPonovitev) {
			            			prvaPonovitev = false;
			            			System.out.println("Globina 4");
			            			return prvaIteracijaAlphaBeta(igra, 4, Integer.MIN_VALUE, Integer.MAX_VALUE, igra.naPotezi());
			            		}
			            		else {
			            			System.out.println("Globina 1");
			            			return prvaIteracijaAlphaBeta(igra, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, igra.naPotezi());
			            		}
			            	}
			            }
			        }
			        if (igra.naPotezi() == jaz) { // Maksimiramo oceno
			            if (ocenap > ocena) { // mora biti > namesto >=
			                ocena = ocenap;
			                kandidat = p;
			                alpha = Math.max(alpha, ocena);
			            }
			        	if(ocenap == ZMAGA) {
			        		return new OcenjenaPoteza(p, ZMAGA);
			        	}
			        } else { // igra.naPotezi() != jaz, torej minimiziramo oceno
			            if (ocenap < ocena) { // mora biti < namesto <=
			                ocena = ocenap;
			                kandidat = p;
			                beta = Math.min(beta, ocena);
			            }
			        	if(ocenap == ZGUBA) {
			        		return new OcenjenaPoteza(p, ZGUBA);
			        	}
			        }
			        if (alpha >= beta) // Ostale poteze ne pomagajo
			        	break;
				}
			}
	    	return new OcenjenaPoteza(kandidat, ocena);
	}
	
	// to je alphaBeta
	public static OcenjenaPoteza alphabetaPoteze(Igra igra, int globina, int alpha, int beta, Igralec jaz) throws TimeException {
		if(System.nanoTime() - startTime > 5000000000.0) {
			System.out.println("Zmanjkalo casa");
			throw new TimeException("Out of time");
		}
			int ocena;
			if (igra.naPotezi() == jaz) {ocena = ZGUBA;} else {ocena = ZMAGA;} 
			List<Poteza> moznePoteze = igra.poteze(); // tukaj dodaj dodatno obrezovanje potez, torej samo sosednje itd.
			Poteza kandidat = null;
			for (Poteza p: moznePoteze) {
					if(jeBliznjaTocka(p, igra)) {
						if(kandidat == null) {
							kandidat = p;
						}
						Igra kopijaIgre = new Igra(igra);
				        kopijaIgre.odigraj(p);
				        int ocenap;
				        switch (kopijaIgre.stanje()) {
				        case ZMAGA_CRNI: {
				        	ocenap = (jaz == Igralec.CRNI ? ZMAGA : ZGUBA); 
				        	break;
				        }
				        case ZMAGA_BELI: {
				        	ocenap = (jaz == Igralec.BELI ? ZMAGA : ZGUBA);
				        	break;
				        }
				        default:
				            if (globina == 1) ocenap = OceniPozicijo.oceniPozicijo(kopijaIgre, jaz);
				            else ocenap = alphabetaPoteze(kopijaIgre, globina-1, alpha, beta, jaz).ocena;
				        }
				        if (igra.naPotezi() == jaz) { // Maksimiramo oceno
				        	if(ocenap == ZMAGA) {
				        		return new OcenjenaPoteza(p, ZMAGA);
				        	}
				            if (ocenap > ocena) { // mora biti > namesto >=
				                ocena = ocenap;
				                kandidat = p;
				                alpha = Math.max(alpha, ocena);
				            }
				        } else { // igra.naPotezi() != jaz, torej minimiziramo oceno
				        	if(ocenap == ZGUBA) {
				        		return new OcenjenaPoteza(p, ZGUBA);
				        	}
				            if (ocenap < ocena) { // mora biti < namesto <=
				                ocena = ocenap;
				                kandidat = p;
				                beta = Math.min(beta, ocena);
				            }
				        }
				        if (alpha >= beta) // Ostale poteze ne pomagajo
				        	break;
					}
				}
		    	return new OcenjenaPoteza(kandidat, ocena);
			}
	
	private static boolean jeBliznjaTocka(Poteza p, Igra igra) {
	    Tocka tocka = new Tocka(p.x(), p.y());
	    
	    int[][] offsets = {
	        {1, 0},   // Right
	        {0, 1},   // Up
	        {-1, 0},  // Left
	        {0, -1},  // Down
	        {2, 0},   // Right 2
	        {-2, 0},  // Left 2
	        {0, 2},   // Up 2
	        {0, -2},  // Down 2
	        {1, 1},   // Up-Right
	        {1, -1},  // Down-Right
	        {-1, 1},  // Up-Left
	        {-1, -1}  // Down-Left
	    };
	    
	    for (int[] offset : offsets) {
	        Tocka sosed = new Tocka(tocka.x() + offset[0], tocka.y() + offset[1]);
	        if (igra.vseBeleTocke.contains(sosed) || igra.vseCrneTocke.contains(sosed)) {
	            return true;
	        }
	    }
	    
	    return false;
	}
}

class TimeException extends Exception {
    public TimeException(String s)
    {
        // Call constructor of parent Exception
        super(s);
    }
}
