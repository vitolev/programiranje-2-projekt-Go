package inteligenca;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import logika.Igra;
import logika.Igralec;
import logika.Tocka;
import splosno.Poteza;

public class Minimax extends Inteligenca {
	
	private static final int ZMAGA = 100; // vrednost zmage, ta bodo kasneje še spremenjeni glede na metodo inteligence
	private static final int ZGUBA = -ZMAGA;
	
    private int globina;
    
    public Minimax (int globina) {
        super(); // tu je bilo not passanje arguments za globino, zdaj zbrisal
        this.globina = globina;
    }
    
    @Override
    public Poteza izberiPotezo (Igra igra) {
        OcenjenaPoteza najboljsaPoteza =
        		// minimax(igra, this.globina, igra.naPotezi());
        		// alphabetaPoteze(igra, globina, Integer.MIN_VALUE, Integer.MAX_VALUE, igra.naPotezi());
        		alphabetaPoteze(igra, this.globina, Integer.MIN_VALUE, Integer.MAX_VALUE, igra.naPotezi()); 
        return najboljsaPoteza.poteza;						  			 
    }
    
    // Isto kot minimax samo da resuje vecnitno
    public OcenjenaPoteza minimaxMultithread(Igra igra, int globina, Igralec jaz) {
    	// naredi thread pool z max 80 nitmi in nameni vse procesorje ki so na voljo
    	int numCores = Runtime.getRuntime().availableProcessors();
    	ThreadPoolExecutor executor = new ThreadPoolExecutor(numCores, 80, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        
        // Naredi prakticno isto kot funkcija minimax, le da za vsako ocenjeno potezo jo da v list in na koncu ko
        // so vse niti izvedene pogleda katera poteza je najboljsa
        List<OcenjenaPoteza> potencialnePoteze = new ArrayList<OcenjenaPoteza>();
	    List<Poteza> moznePoteze = igra.poteze();

	    for (Poteza p: moznePoteze) {
	        Igra kopijaIgre = new Igra(igra);
	        kopijaIgre.odigraj(p);
	        int ocena;
	        
	        switch (kopijaIgre.stanje()) {
		        case ZMAGA_CRNI: {
		        	ocena = (jaz == Igralec.CRNI ? ZMAGA : ZGUBA); 
		        	potencialnePoteze.add(new OcenjenaPoteza(p, ocena));
		        	break;
		        }
		        case ZMAGA_BELI: {
		        	ocena = (jaz == Igralec.BELI ? ZMAGA : ZGUBA); 
		        	potencialnePoteze.add(new OcenjenaPoteza(p, ocena));
		        	break;
		        }
		        default: { // nekdo je na potezi
		            if (globina == 1) {
		            	// ce je globina 1 kar na tem threadu ovrednotimo oceno poteze
		            	ocena = OceniPozicijo.oceniPozicijo(kopijaIgre, jaz);
		            	potencialnePoteze.add(new OcenjenaPoteza(p, ocena));
		            	}
		            else {
		            	// ce pa globina ni 1 pa ustvarimo worker-ja ki bo pregledal v globino za to vejo
		            	MinimaxWorker worker = new MinimaxWorker(p, kopijaIgre, globina - 1, jaz);
		                executor.execute(worker);
		            }
		        }
		    }
	    }
	    
	    // sprozimo naj executor obravnava vse threade
	    executor.shutdown();
	    
	    // pocakamo, da se vsi threadi dokoncajo
	    try {
	        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	    } catch (InterruptedException e) {
	        // Handle the exception appropriately
	    }
	    
	    // potencialnim potezam dodamo vse poteze, ki so jih ovrednotili workerji za posamezno vejo
	    potencialnePoteze.addAll(MinimaxWorker.potencialnePoteze);
	    // resertiramo seznam potez kamor workerji shranjujeo poteze
	    MinimaxWorker.potencialnePoteze = new ArrayList<OcenjenaPoteza>();
        OcenjenaPoteza najboljsaPoteza = new OcenjenaPoteza(null, Integer.MIN_VALUE);
        
        // loopnemo skozi vse potencialne poteze in pogledamo katera je najvec ocenjena
        for(OcenjenaPoteza poteza : potencialnePoteze) {
        	if(poteza.ocena > najboljsaPoteza.ocena) {
        		najboljsaPoteza = poteza;
        	}
        }
	    
	    return najboljsaPoteza; 
    }
    
	public static OcenjenaPoteza minimax(Igra igra, int globina, Igralec jaz) {
	    	
	    OcenjenaPoteza najboljsaPoteza = null;
	    List<Poteza> moznePoteze = igra.poteze(); // Ideja: ko obtainas vse poteze in analiziras vse in če ni nobena zelo očitna da jo moraš naredit, potem 
	    										  // "širiš" svoje ebmočje in dodaš svoje na dve razliki z drugimi, to implementiraj samo, če ne bo inteligenca že sama te strategije
	    
	    for (Poteza p: moznePoteze) {
	        Igra kopijaIgre = new Igra(igra);
	        kopijaIgre.odigraj(p);
	        int ocena;
	        	        
	        switch (kopijaIgre.stanje()) {
		        case ZMAGA_CRNI: {
		        	ocena = (jaz == Igralec.CRNI ? ZMAGA : ZGUBA); 
		        	break;
		        }
		        case ZMAGA_BELI: {
		        	ocena = (jaz == Igralec.BELI ? ZMAGA : ZGUBA); 
		        	break;
		        }
		        default: { // nekdo je na potezi
		            if (globina == 1) {ocena = OceniPozicijo.oceniPozicijo(kopijaIgre, jaz);}
		            else {ocena = minimax(kopijaIgre, globina-1, jaz).ocena;}
		        }
		    }
	        
		    if (najboljsaPoteza == null
			|| igra.naPotezi()==jaz && ocena > najboljsaPoteza.ocena
			|| igra.naPotezi()!=jaz && ocena < najboljsaPoteza.ocena) 
		    {najboljsaPoteza = new OcenjenaPoteza (p, ocena);}
	    }
	        
	    return najboljsaPoteza;        
	}
	
	// globina pri alphabetaMultithread mora biti vedno vecja od 1. TRENUTNO TO NE DELA
	public OcenjenaPoteza alphabetaMultithread(Igra igra, int globina, int alpha, int beta, Igralec jaz) {
		// naredi thread pool z max 80 nitmi in nameni vse procesorje ki so na voljo
    	int numCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(numCores, 80, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		
		if (igra.naPotezi() == jaz) {AlphaBetaWorker.ocena = ZGUBA;} else {AlphaBetaWorker.ocena = ZMAGA;} 
		List<Poteza> moznePoteze = igra.poteze();
		AlphaBetaWorker.kandidat = moznePoteze.get(0);
		for (Poteza p: moznePoteze) {
				if(jeBliznjaTocka(p, igra)) {
					Igra kopijaIgre = new Igra(igra);
			        kopijaIgre.odigraj(p);
			        AlphaBetaWorker worker = new AlphaBetaWorker(p, kopijaIgre, globina - 1, jaz, executor);
	                executor.execute(worker);
				}
			}
		// sprozimo naj executor obravnava vse threade
	    executor.shutdown();
	    
	    // pocakamo, da se vsi threadi dokoncajo
	    try {
	        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	    } catch (InterruptedException e) {
	        // Handle the exception appropriately
	    }
	    System.out.println(AlphaBetaWorker.kandidat);
	    return new OcenjenaPoteza(AlphaBetaWorker.kandidat, AlphaBetaWorker.ocena);
	}
	
	// to je alphaBeta
	public static OcenjenaPoteza alphabetaPoteze(Igra igra, int globina, int alpha, int beta, Igralec jaz) {
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
				        case ZMAGA_CRNI: ocenap = (jaz == Igralec.CRNI ? ZMAGA : ZGUBA); break;
				        case ZMAGA_BELI: ocenap = (jaz == Igralec.BELI ? ZMAGA : ZGUBA); break;
				        default:
				            if (globina == 1) ocenap = OceniPozicijo.oceniPozicijo(kopijaIgre, jaz);
				            else ocenap = alphabetaPoteze
				            		(kopijaIgre, globina-1, alpha, beta, jaz).ocena;
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
