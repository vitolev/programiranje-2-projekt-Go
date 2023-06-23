package inteligenca;

import splosno.Poteza;

public class OcenjenaPoteza {
	
    Poteza poteza;
    int ocena;
    
    public OcenjenaPoteza (Poteza poteza, int ocena) {
        this.poteza = poteza;
        this.ocena = ocena;
    	} 
    
    @Override
    public String toString() {
    	return "Poteza: (" + poteza.x() + "," + poteza.y() + ") -- Ocena: " + ocena;
    	}
    
    }
