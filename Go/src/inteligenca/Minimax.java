package inteligenca;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import logika.Igra;
import logika.Igralec;
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
                minimaxMultithread(igra, this.globina, igra.naPotezi()); // ce v tej vrstici namesto minimax zberes funkcijo minimaxMultithread
        return najboljsaPoteza.poteza;						  			 // potem bo resevalo z vsemi procesorji torej hitreje
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
	        
	        int n = 2*(3 - globina);
	        //System.out.println(" ".repeat(n) + "(" + p.x() + "," + p.y() + ")");
	        
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
}
