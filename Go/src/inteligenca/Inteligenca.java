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

    public static long startTime;
    
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
		Poteza kandidat = moznePoteze.get(0);
		for (Poteza p: moznePoteze) {
				if(jeBliznjaTocka(p, igra)) {
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
			            		ocenap = alphabetaPoteze(kopijaIgre, globina-1, alpha, beta, jaz).ocena;
			            	}
			            	catch(Exception e) {
			            		return new OcenjenaPoteza(kandidat, 0);
			            	}
			            }
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
	
	// to je alphaBeta
	public static OcenjenaPoteza alphabetaPoteze(Igra igra, int globina, int alpha, int beta, Igralec jaz) throws TimeException {
		if(System.nanoTime() - startTime > 5500000000.0) {
			throw new TimeException("Out of time");
		}
			int ocena;
			if (igra.naPotezi() == jaz) {ocena = ZGUBA;} else {ocena = ZMAGA;} 
			List<Poteza> moznePoteze = igra.poteze(); // tukaj dodaj dodatno obrezovanje potez, torej samo sosednje itd.
			Poteza kandidat = moznePoteze.get(0);
			for (Poteza p: moznePoteze) {
					if(jeBliznjaTocka(p, igra)) {
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
