package inteligenca;

import java.util.List;

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
                minimax(igra, this.globina, igra.naPotezi());
        return najboljsaPoteza.poteza;
    }
    
	public OcenjenaPoteza minimax(Igra igra, int globina, Igralec jaz) {
	    	
	    OcenjenaPoteza najboljsaPoteza = null;
	    List<Poteza> moznePoteze = igra.poteze(); // Ideja: ko obtainas vse poteze in analiziras vse in če ni nobena zelo očitna da jo moraš naredit, potem 
	    										  // "širiš" svoje ebmočje in dodaš svoje na dve razliki z drugimi, to implementiraj samo, če ne bo inteligenca že sama te strategije
	        
	    for (Poteza p: moznePoteze) {

	        Igra kopijaIgre = new Igra(igra);
	        kopijaIgre.odigraj(p);
	        int ocena;
	        
	        switch (kopijaIgre.stanje()) {
		        case ZMAGA_CRNI: {
		        	ocena = (jaz == Igralec.CRNI ? ZMAGA : ZGUBA); break;
		        }
		        case ZMAGA_BELI: {
		        	ocena = (jaz == Igralec.BELI ? ZMAGA : ZGUBA); break;
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