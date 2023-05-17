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
	
	private static int alpha = Integer.MIN_VALUE;
	private static int beta = Integer.MAX_VALUE;
	public static int ocena;
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
		int ocenap = Minimax.alphabetaPoteze(igra, globina, alpha, beta, jaz).ocena;
		if (igra.naPotezi() == jaz) { // Maksimiramo oceno
            if (ocenap > ocena) { // mora biti > namesto >=
                ocena = ocenap;
                kandidat = p;
                alpha = Math.max(alpha, ocena);
            }
        } else { // igra.naPotezi() != jaz, torej minimiziramo oceno
            if (ocenap < ocena) { // mora biti < namesto <=
                ocena = ocenap;
                kandidat = p;
                beta = Math.min(beta, ocena);
            }
        }
		
		if(alpha >= beta) {
			executor.shutdownNow();
		}
	}
}