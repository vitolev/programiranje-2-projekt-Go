package inteligenca;

import logika.Igra;
import splosno.Poteza;
import logika.Igra;
import logika.Igralec;

public class Inteligenca extends splosno.KdoIgra {
	
    public Inteligenca() {
    	super("Algebros");	// Tukaj sem najino skupino poimenoval Algebros.
    						// Ta super mora bit da poklice konstruktor iz razreda KdoIgra, ker ga tukaj extendamo
    						// in za parameter potrebuje ime skupine.
    }
    
    public Poteza izberiPotezo(Igra igra) {
        // TODO: Tukaj bo algoritem za inteligenco
        return null;
    } 
}