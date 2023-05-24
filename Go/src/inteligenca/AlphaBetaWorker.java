package inteligenca;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import logika.Igra;
import logika.Igralec;
import splosno.Poteza;

public class AlphaBetaWorker implements Runnable{
	private Poteza p;
	private Igra igra;
	private int globina;
	private Igralec jaz;
	private ThreadPoolExecutor executor;
	
	public static int alpha = Integer.MIN_VALUE;
	public static int beta = Integer.MAX_VALUE;
	public static int ocena = Integer.MIN_VALUE;
	public static Poteza kandidat;
	
	public static List<OcenjenaPoteza> potencialnePoteze = new ArrayList<OcenjenaPoteza>();
	
	public AlphaBetaWorker(Poteza p, Igra igra, int globina, Igralec jaz, ThreadPoolExecutor executor) {
		this.p = p;
		this.igra = igra;
		this.globina = globina;
		this.jaz = jaz;
		this.executor = executor;
	}

	@Override
	public void run() {
		Igra kopijaIgre = new Igra(igra);
        kopijaIgre.odigraj(p);
		int ocenap = Minimax.alphabetaPoteze(kopijaIgre, globina - 1, alpha, beta, jaz).ocena;
		// Maksimiramo oceno
		if(ocenap == 100) {
    		kandidat = p;
    		executor.shutdownNow();
    	}
		
		if (ocenap > ocena) { // mora biti > namesto >=
            ocena = ocenap;
            kandidat = p;
            alpha = Math.max(alpha, ocena);
        }
	}
}