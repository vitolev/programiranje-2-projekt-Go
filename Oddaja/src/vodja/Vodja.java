package vodja;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingWorker;
import java.util.concurrent.TimeUnit;

import gui.GlavnoOkno;
import logika.Igra;
import logika.Igralec;
import logika.Polje;
import logika.Stanje;
import logika.VrstaIgralca;
import splosno.Poteza;
import inteligenca.Inteligenca;
import inteligenca.Minimax;
import inteligenca.OceniPozicijo;

public class Vodja {	
	
	public static Map<Igralec,VrstaIgralca> vrstaIgralca;
	
	public static GlavnoOkno okno;
	
	public static Igra igra = null;
	
	public static boolean clovekNaVrsti = false;
	
	public static Inteligenca inteligenca = new Inteligenca();
	
	private static Stanje stanje;
	
	private static long startTime = 0;
	private static long endTime = 0;
		
	public static void igramoNovoIgro () { 
		igra = new Igra ();
		//inteligenca = new Inteligenca();	// Ustvarimo novo instanco razreda Inteligenca
		igramo ();
	}
	
	public static void igramo () {
		endTime = System.nanoTime();
		//System.out.println(Math.round((endTime - startTime) / 1000000000.0 * 100.0) / 100.0); // Izmerimo cas med potezami
		endTime = 0;
		startTime = System.nanoTime();
		// okno potrebuje stanje pri funkciji osvezuGUI zato je treba najprej posodobiti stanje in nato klicati osveziGUI()
		switch (igra.stanje()) {
		case ZMAGA_BELI: 
			stanje = Stanje.ZMAGA_BELI;
			okno.osveziGUI();
			return;
		case ZMAGA_CRNI: 
			stanje = Stanje.ZMAGA_CRNI;
			okno.osveziGUI();
			return;
		case NEODLOCENO:
			stanje = Stanje.NEODLOCENO;
			okno.osveziGUI();
			return;
		case V_TEKU: 
			stanje = Stanje.V_TEKU;
			okno.osveziGUI();
			Igralec igralec = igra.naPotezi();
			VrstaIgralca vrstaNaPotezi = vrstaIgralca.get(igralec);
			switch (vrstaNaPotezi) {
			case C: 
				clovekNaVrsti = true;
				break;
			case R:
				igrajRacunalnikovoPotezo();
				break;
			}
		}
	}
	
	public static void igrajRacunalnikovoPotezo() { 
		Igra zacetkaIgra = igra; 
		SwingWorker<Poteza,Void> worker = 
				new SwingWorker<Poteza,Void>(){ 
			@Override 
			protected Poteza doInBackground(){ 
				Poteza poteza = inteligenca.izberiPotezo(igra);
				try{TimeUnit.MILLISECONDS.sleep(1);} catch(Exception e){}; 
				return poteza; 
			} 
			@Override 
			protected void done(){ 
				Poteza poteza = null; 
				try{poteza = get();} catch(Exception e){}; 
				if(igra == zacetkaIgra){ 
					igra.odigraj(poteza);
					igramo(); 
				} 
			} 
		}; 
		worker.execute(); 
	}
		
	public static void igrajClovekovoPotezo(Poteza poteza) {
		Igra kopijaIgre = new Igra(igra);
		if (kopijaIgre.odigraj(poteza)) {
			clovekNaVrsti = false;
			igra.odigraj(poteza);
			igramo ();
		}
	}
	
	// Return stanje igre
	public static Stanje stanjeIgre() {
		return stanje;
	}
}