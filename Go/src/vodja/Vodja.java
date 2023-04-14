package vodja;

import java.util.Random;
import java.util.Map;
import java.util.List;

import javax.swing.SwingWorker;
import java.util.concurrent.TimeUnit;

import gui.GlavnoOkno;
import logika.Igra;
import logika.Igralec;
import logika.Stanje;
import logika.VrstaIgralca;
import splosno.Poteza;
import inteligenca.Inteligenca;

public class Vodja {	
	
	public static Map<Igralec,VrstaIgralca> vrstaIgralca;
	
	public static GlavnoOkno okno;
	
	public static Igra igra = null;
	
	public static boolean clovekNaVrsti = false;
	
	public static Inteligenca inteligenca;
	
	private static Stanje stanje;
		
	public static void igramoNovoIgro () {
		igra = new Igra ();
		inteligenca = new Inteligenca();	// Ustvarimo novo instanco razreda Inteligenca
		igramo ();
	}
	
	public static void igramo () {
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
		// Tukaj je že preverjeno, da je potezo možno narediti, saj bova to implementirala pri NEODLOCENO
		Poteza poteza = inteligenca.izberiPotezo(igra); // Okej če kaj ne dela samo preveri, da se lahko na igro tako sklicujem
		igra.odigraj(poteza);
		igramo ();
	}
		
	public static void igrajClovekovoPotezo(Poteza poteza) {
		if (igra.odigraj(poteza)) {
			clovekNaVrsti = false;
			igramo ();
		}
	}
	
	// Return stanje igre
	public static Stanje stanjeIgre() {
		return stanje;
	}
}