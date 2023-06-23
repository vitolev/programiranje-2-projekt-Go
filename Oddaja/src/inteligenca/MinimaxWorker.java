package inteligenca;

import java.util.ArrayList;
import java.util.List;

import logika.Igra;
import logika.Igralec;
import splosno.Poteza;

public class MinimaxWorker implements Runnable{
	private Poteza p;
	private Igra igra;
	private int globina;
	private Igralec jaz;
	
	public static List<OcenjenaPoteza> potencialnePoteze = new ArrayList<OcenjenaPoteza>();
	
	public MinimaxWorker(Poteza p, Igra igra, int globina, Igralec jaz) {
		this.p = p;
		this.igra = igra;
		this.globina = globina;
		this.jaz = jaz;
	}

	@Override
	public void run() {
		OcenjenaPoteza najboljsaPoteza = new OcenjenaPoteza(p, Minimax.minimax(igra, globina, jaz).ocena);
		potencialnePoteze.add(najboljsaPoteza);
	}
}
