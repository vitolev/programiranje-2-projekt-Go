package inteligenca;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import logika.Igra;
import logika.Igralec;
import logika.Stanje;
import splosno.Poteza;

public class MonteCarlo {
    private static final double C_PARAMETER = 1.4;
    // C parameter iz enačbe iz predavanj, kako rad bi expandal, poskušam lahko potem z več/manj, če je repov manj potem naj bi ta tudi bila manj
    
    private static Random random = new Random(); // Objekt za random stevila
    
    public static Poteza monteCarlo(Igra igra, Igralec jaz, long timeLimit) { // Osnovna funkcija, ki vrne željeno potezo v določenem času
    	long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimit;
        
        int numCores = Runtime.getRuntime().availableProcessors();
        
        MonteCarloTreeNode root = new MonteCarloTreeNode(igra);		  		  // Ustvarimo novo drevo, ki nima starša (parent), torej je osnovni node.
        while (System.currentTimeMillis() < endTime) {
        	ThreadPoolExecutor executor = new ThreadPoolExecutor(numCores, numCores, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
            MonteCarloTreeNode selectedNode = selectNode(root);				  // Kličemo select node, ki pogleda, s formulo išče 
            expandNode(selectedNode);
            MonteCarloTreeNode nodeToExplore = selectedNode;
            if (!selectedNode.getChildren().isEmpty()) {
                nodeToExplore = selectBestChild(selectedNode);
            }
            
            MonteCarloWorker.simulationResult = 0;
            MonteCarloWorker.numRuns = 0;
            // ustvarimo toliko threadov kolikor je procesorjev na voljo
            for(int i = 0; i < numCores; i++) {
            	MonteCarloWorker worker = new MonteCarloWorker(nodeToExplore);
            	executor.execute(worker);
            }
            executor.shutdown();
            // pocakamo, da se vsi threadi dokoncajo
    	    try {
    	        executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
    	        backpropagate(nodeToExplore, MonteCarloWorker.simulationResult, MonteCarloWorker.numRuns);
    	    } catch (InterruptedException e) {
    	        // Handle the exception appropriately
    	    }
        }
        return selectBestMove(root, jaz);
    }
    
    private static MonteCarloTreeNode selectNode(MonteCarloTreeNode rootNode) {
        MonteCarloTreeNode node = rootNode;     // Tukaj se sprehajam z algoritmu po drevesu do najnižjega in najboljšega, ki ni raziskan.
        while (!node.getChildren().isEmpty()) {
            node = selectBestChild(node);
        }
        return node;
    }
    
    private static MonteCarloTreeNode selectBestChild(MonteCarloTreeNode node) {
        double maxUTC = -1;
        MonteCarloTreeNode selectedChild = null;

        for (MonteCarloTreeNode child : node.getChildren().values()) {
            double utc = calculateUTC(child.getWins(), child.getPlays(), node.getPlays(), node.koeficientZmage());
            if (utc > maxUTC) {
                maxUTC = utc;
                selectedChild = child;
            }
        }

        return selectedChild;
    }

    private static void expandNode(MonteCarloTreeNode node) {
        if (node.isExpanded() || node.getPlays() == 0) {
            return;
        }

        List<Poteza> moves = node.getIgra().poteze();
        for (Poteza move : moves) {
            Igra clonedIgra = new Igra(node.getIgra());
            if (clonedIgra.odigraj(move)) { 
            	// Funkcija odigraj ne preveri ce se je stanje v igri slucajno ze ponovilo, zato moramo tukaj rocno 
            	// preveriti da se taksno stanje ne ponovi. Ampak se ne vem trenutno kako
            	
                MonteCarloTreeNode childNode = new MonteCarloTreeNode(clonedIgra, node); // Tule mislim da mora bit dodan argument za
                																		 // parent node, da potem backpropagation sploh dela
                node.getChildren().put(move, childNode);
            }
        }
        
        
        Poteza pass = new Poteza(-1,-1);
        Igra clonedIgra = new Igra(node.getIgra());
        clonedIgra.odigraj(pass);
        MonteCarloTreeNode childNode = new MonteCarloTreeNode(clonedIgra, node); 
        node.getChildren().put(pass, childNode);

        node.setExpanded(true);
    }
    
    public static int simulirajIgro(Igra igraOsnovna) {
    	if(igraOsnovna.stanje() == Stanje.V_TEKU) {
            List<Poteza> dovoljenePoteze = new ArrayList<Poteza>(igraOsnovna.poteze());
            dovoljenePoteze.add(new Poteza(-1,-1));
            int moveCounter = 0; // Števec potez za optimizacijo hitrosti (da koda ne dela nepotrebnih in neučinkovitih runnov)
            
            while(true) {
            	if (moveCounter >= 100) {
                    // Play pass for both players
                    Igra igra = new Igra(igraOsnovna);
                    igra.odigraj(new Poteza(-1, -1));
                    igra.odigraj(new Poteza(-1, -1));
                    return simulirajIgro(igra); // Da je loop vredu vseeno vrne igro, da potem evalueata igro do konca
            	}
            	
            	int randomIndex = random.nextInt(dovoljenePoteze.size());
            	Poteza randomMove = dovoljenePoteze.remove(randomIndex);
            	
            	Igra igra = new Igra(igraOsnovna);

            	if(igra.odigraj(randomMove)) {
            		return simulirajIgro(igra);
            	}
            }
    	}
    	else if (igraOsnovna.stanje() == Stanje.ZMAGA_BELI) {
        	return (igraOsnovna.naPotezi() == Igralec.BELI ? 1 : 0);
        }
        else {
        	return (igraOsnovna.naPotezi() == Igralec.CRNI ? 1 : 0);
        }
    }
    
    private static void backpropagate(MonteCarloTreeNode node, int result, int numRuns) {
        MonteCarloTreeNode currentNode = node;

        while (currentNode != null) {
            currentNode.incrementPlays(numRuns);
            currentNode.incrementWins(result);
            currentNode = currentNode.getParent();
        }
    }

    private static double calculateUTC(int wins, int plays, int parentPlays, boolean koeficientZmage) {
        if (plays == 0) {
            return Double.MAX_VALUE;
        }
        double winRate = (double) wins / plays;
        if (!koeficientZmage) {
        	winRate = 1 - winRate;
        }
        double explorationTerm = C_PARAMETER * Math.sqrt(Math.log(parentPlays) / plays);
        return winRate + explorationTerm;
    }

    private static Poteza selectBestMove(MonteCarloTreeNode rootNode, Igralec jaz) {
        double maxWinRate = -1;
        Poteza bestMove = null;

        for (Map.Entry<Poteza, MonteCarloTreeNode> entry : rootNode.getChildren().entrySet()) {
            Poteza move = entry.getKey();
            MonteCarloTreeNode childNode = entry.getValue();

            double winRate = (double) childNode.getWins() / childNode.getPlays();
            if (winRate > maxWinRate) {
                maxWinRate = winRate;
                bestMove = move;
            }
        }

        return bestMove;
    }
}

//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//-------------SPODAJ JE PRETEKLA VERZIJA KODE, OD KATERE SI NEKATERE DELE ŠE IZPOSOJAVA--------------------------------
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------

/*
package inteligenca;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logika.Igra;
import logika.Igralec;
import logika.Stanje;
import splosno.Poteza;

public class MonteCarlo {
    private static final int SIMULACIJSKI_STEVEC = 250; // Število ponavljanj pri eni potezi
    private static final double C_PARAMETER = 1.4; // Parameter preverjanje (na predavanju konstanta c)

    public static Poteza monteCarlo(Igra igra, Igralec jaz, long timeLimit) {
    	long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimit;
    	
        List<Poteza> moznePoteze = igra.poteze(); // Tukaj nekatere poteze vseeno niso možno, odpravimo kasneje
        int[] zmage = new int[moznePoteze.size()];
        int[] igranja = new int[moznePoteze.size()]; // Tukaj bi spet naleteli na problem, da poteze ne bi mogli narediti, zato bomo to rešili kasneje
        boolean vseNesmiselne = true; // Tukaj nastavimo, da če bi slučajno bile vse poteze ilegalne, potem passnemo
        
        while (System.currentTimeMillis() < endTime) {
        	double maxUTC = -1;
        	int najboljsaPotezaIndeks = 0;
        	int skupnaIgranja = 0;
        	
            for (int i = 0; i < moznePoteze.size(); i++) {
            	skupnaIgranja += igranja[i];
        	
		        for (int i = 0; i < moznePoteze.size(); i++) {
		            Poteza move = moznePoteze.get(i);
		            for (int j = 0; j < SIMULACIJSKI_STEVEC; j++) {
		                Igra simuliranaIgra = new Igra(igra);  // Tvori novo igro, ki jo bo potem igral, konstruktor skopira vredu
		                if (simuliranaIgra.odigraj(move)) { // Če je poteza legalna za narediti, potem 
		                	vseNesmiselne = false;
		                	Igralec zmagovalec = simulirajIgro(simuliranaIgra);
		                	if (zmagovalec == jaz) {
		                		zmage[i]++;
		                	}
		                	igranja[i]++;
		                }
		                else {					// V primeru, da ne moremo narediti poteze, potem je ta nesmiselna
		                	zmage[i] = -2;  	// Tukaj nujno pogled, da če so vse -2, da potem passne, tukaj poskrbi, da dalje ne razuskuje
		                	igranja[i] = SIMULACIJSKI_STEVEC;
		                	break; 				// Naj ne gre 1000-krat simulrati igro, če itak ne more narediti te poteze
		                }
		            }
		        }
        	}
        }
        
        
        double maxUCT = -1;
        int najboljsaPotezaIndeks = 0;
        for (int i = 0; i < moznePoteze.size(); i++) {
            double uct = izracunajUTC(zmage[i], igranja[i], igranja[najboljsaPotezaIndeks]);
            if (uct > maxUCT) {
                maxUCT = uct;
                najboljsaPotezaIndeks = i;
            }
        }
        if (vseNesmiselne) {
        	return new Poteza(-1, -1);
        }
        else {
        	return moznePoteze.get(najboljsaPotezaIndeks);
        }
    }
    
    // ________________________________________________________________________________________________
    private static double izracunajUTC(int zmage, int igranja, int totalPlays) {
        if (igranja == 0) {
            return Double.MAX_VALUE;
        }
        
        //Implementiramo formule, ki smo jih obravnavali na predavanju
        double t1 = (double) zmage / igranja;
        double t2 = Math.sqrt(Math.log(totalPlays) / igranja);
        return t1 + C_PARAMETER * t2;
    }

    
    private static Igralec simulirajIgro(Igra igra) {
        Random random = new Random();
        while (igra.stanje() == Stanje.V_TEKU) {
            List<Poteza> moznePoteze = igra.poteze(); // Najprej tvorimo seznam vseh potez
            List<Poteza> clonedMoznePoteze = new ArrayList<>(moznePoteze); // kloniramo osnovni seznam, da lahko mečemo ven poteze
            Poteza randomMove = new Poteza(-1, -1); // V osnovi je poteza kar pass, potem jo takoj spremeni, če jo lahko
            
            do {
            	if (clonedMoznePoteze.size() == 0) {
            		randomMove = new Poteza(-1, -1); // Če ni več možnosti, vrni pass
            	}
            	else {
                	int randomIndex = random.nextInt(clonedMoznePoteze.size());
                	randomMove = clonedMoznePoteze.remove(randomIndex);
            	}
            } while(!igra.odigraj(randomMove));
                        
        }
        return (igra.stanje() == Stanje.ZMAGA_BELI ? Igralec.BELI : Igralec.CRNI); // Neodloceno itak ne more bit.
    }
}

*/


/*
package inteligenca;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logika.Igra;
import logika.Igralec;
import logika.Stanje;
import splosno.Poteza;

public class MonteCarlo {
    private static final int SIMULACIJSKI_STEVEC = 1000; // Število ponavljanj pri eni potezi
    private static final double C_PARAMETER = 1.4; // Parameter preverjanje (na predavanju konstanta c)

    public static Poteza monteCarlo(Igra igra, Igralec jaz, long timeLimit) {
        long endTime = System.currentTimeMillis() + timeLimit;

        List<Poteza> moznePoteze = igra.poteze();
        int[] zmage = new int[moznePoteze.size()];
        int[] igranja = new int[moznePoteze.size()];
        boolean vseNesmiselne = true;
        int najboljsaPotezaIndeks = 0;
        int totalPlays = 0;

        while (System.currentTimeMillis() < endTime) {
            double maxUCT = -1;

            for (int i = 0; i < moznePoteze.size(); i++) {
                Poteza move = moznePoteze.get(i);
                int plays = 0;
                int wins = 0;

                for (int j = 0; j < SIMULACIJSKI_STEVEC; j++) {
                    Igra simuliranaIgra = new Igra(igra);
                    if (simuliranaIgra.odigraj(move)) {
                        vseNesmiselne = false;
                        plays++;
                        totalPlays++;
                        
                        Igralec zmagovalec = simulirajIgro(simuliranaIgra);
                        if (zmagovalec == jaz) {
                            wins++;
                        }
                    }
                }

                igranja[i] += plays;
                zmage[i] += wins;
                double uct = izracunajUTC(zmage[i], igranja[i], totalPlays);

                if (uct > maxUCT) {
                    maxUCT = uct;
                    najboljsaPotezaIndeks = i;
                }
            }

            Igra simuliranaIgra = new Igra(igra);
            Poteza najboljsaPoteza = moznePoteze.get(najboljsaPotezaIndeks);
            simuliranaIgra.odigraj(najboljsaPoteza);
            moznePoteze = simuliranaIgra.poteze();

            if (moznePoteze.isEmpty()) {
                break; // No more moves to explore
            }
        }

        if (vseNesmiselne) {
            return new Poteza(-1, -1);
        } else {
            return moznePoteze.get(0);
        }
    }

    private static double izracunajUTC(int zmage, int igranja, int totalPlays) {
        if (igranja == 0) {
            return Double.MAX_VALUE;
        }

        double t1 = (double) zmage / igranja;
        double t2 = Math.sqrt(Math.log(totalPlays) / igranja);
        return t1 + C_PARAMETER * t2;
    }

    private static Igralec simulirajIgro(Igra igra) {
        Random random = new Random();
        while (igra.stanje() == Stanje.V_TEKU) {
            List<Poteza> moznePoteze = igra.poteze(); // Najprej tvorimo seznam vseh potez
            List<Poteza> clonedMoznePoteze = new ArrayList<>(moznePoteze); // kloniramo osnovni seznam, da lahko mečemo ven poteze
            Poteza randomMove = new Poteza(-1, -1); // V osnovi je poteza kar pass, potem jo takoj spremeni, če jo lahko
            
            do {
            	if (clonedMoznePoteze.size() == 0) {
            		randomMove = new Poteza(-1, -1); // Če ni več možnosti, vrni pass
            	}
            	else {
                	int randomIndex = random.nextInt(clonedMoznePoteze.size());
                	randomMove = clonedMoznePoteze.remove(randomIndex);
            	}
            } while(!igra.odigraj(randomMove));
                        
        }
        return (igra.stanje() == Stanje.ZMAGA_BELI ? Igralec.BELI : Igralec.CRNI); // Neodloceno itak ne more bit.
    }
}
*/

//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------


    /*		
    Mislim da ta simulirajIgro() funkcija ni glih OK, zato sem napisal svojo. Predlagam da se to enkrat pregledama skup
    private static int simulirajIgro(Igra igraOsnovna) {
    	Igra igra = new Igra(igraOsnovna);
        Random random = new Random();
        while (igra.stanje() == Stanje.V_TEKU) {
            List<Poteza> moznePoteze = igra.poteze(); // Najprej tvorimo seznam vseh potez
            List<Poteza> clonedMoznePoteze = new ArrayList<>(moznePoteze); // kloniramo osnovni seznam, da lahko mečemo ven poteze
            Poteza randomMove = new Poteza(-1, -1); // V osnovi je poteza kar pass, potem jo takoj spremeni, če jo lahko
            
            do {

            	if (clonedMoznePoteze.size() == 0) {
            		randomMove = new Poteza(-1, -1); // Če ni več možnosti, vrni pass
            	}
            	else {
                	int randomIndex = random.nextInt(clonedMoznePoteze.size());
                	randomMove = clonedMoznePoteze.remove(randomIndex);
            	}
            } while(!igra.odigraj(randomMove));
                        
        }
        if (igra.stanje() == Stanje.ZMAGA_BELI) {
        	return (igraOsnovna.naPotezi() == Igralec.BELI ? 1 : 0);
        }
        else {
        	return (igraOsnovna.naPotezi() == Igralec.CRNI ? 1 : 0);
        }
    }
    */
    
    //____________________________________________________________________________________________________________
    /*
    private static int simulatePlayout(MonteCarloTreeNode node) {
        Random random = new Random();
        Igra igra = new Igra(node.getIgra()); 		//Skopiramo igro
        
        while (igra.stanje() == Stanje.V_TEKU) {
            List<Poteza> moves = igra.poteze();
            if (moves.isEmpty()) {
                break;
            }
            int randomIndex = random.nextInt(moves.size());
            Poteza randomMove = moves.get(randomIndex);
            igra.odigraj(randomMove);
        }

        return getSimulationResult(igra, node.getParent().getIgra());
    }
    
    
    private static int getSimulationResult(Igra igra, Igra parentIgra) {
        Stanje stanje = igra.stanje();
        if (stanje == Stanje.ZMAGA_BELI) {
            return igra.naPotezi() == Igralec.BELI ? 1 : 0;
        } else if (stanje == Stanje.ZMAGA_CRNI) {
            return igra.naPotezi() == Igralec.CRNI ? 1 : 0;
        }
        // For draws or unfinished games, use the parent game as a tiebreaker
        return getSimulationResult(parentIgra, null);
    }
    */

    //____________________________________________________________________________________________________________

//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------


    /*		Mislim da ta simulirajIgro() funkcija ni glih OK, zato sem napisal svojo. Predlagam da se to enkrat pregledama skup
    private static int simulirajIgro(Igra igraOsnovna) {
    	Igra igra = new Igra(igraOsnovna);
        Random random = new Random();
        while (igra.stanje() == Stanje.V_TEKU) {
            List<Poteza> moznePoteze = igra.poteze(); // Najprej tvorimo seznam vseh potez
            List<Poteza> clonedMoznePoteze = new ArrayList<>(moznePoteze); // kloniramo osnovni seznam, da lahko mečemo ven poteze
            Poteza randomMove = new Poteza(-1, -1); // V osnovi je poteza kar pass, potem jo takoj spremeni, če jo lahko
            
            do {

            	if (clonedMoznePoteze.size() == 0) {
            		randomMove = new Poteza(-1, -1); // Če ni več možnosti, vrni pass
            	}
            	else {
                	int randomIndex = random.nextInt(clonedMoznePoteze.size());
                	randomMove = clonedMoznePoteze.remove(randomIndex);
            	}
            } while(!igra.odigraj(randomMove));
                        
        }
        if (igra.stanje() == Stanje.ZMAGA_BELI) {
        	return (igraOsnovna.naPotezi() == Igralec.BELI ? 1 : 0);
        }
        else {
        	return (igraOsnovna.naPotezi() == Igralec.CRNI ? 1 : 0);
        }
    }
    */
    
    //____________________________________________________________________________________________________________
    /*
    private static int simulatePlayout(MonteCarloTreeNode node) {
        Random random = new Random();
        Igra igra = new Igra(node.getIgra()); 		//Skopiramo igro
        
        while (igra.stanje() == Stanje.V_TEKU) {
            List<Poteza> moves = igra.poteze();
            if (moves.isEmpty()) {
                break;
            }
            int randomIndex = random.nextInt(moves.size());
            Poteza randomMove = moves.get(randomIndex);
            igra.odigraj(randomMove);
        }

        return getSimulationResult(igra, node.getParent().getIgra());
    }
    
    
    private static int getSimulationResult(Igra igra, Igra parentIgra) {
        Stanje stanje = igra.stanje();
        if (stanje == Stanje.ZMAGA_BELI) {
            return igra.naPotezi() == Igralec.BELI ? 1 : 0;
        } else if (stanje == Stanje.ZMAGA_CRNI) {
            return igra.naPotezi() == Igralec.CRNI ? 1 : 0;
        }
        // For draws or unfinished games, use the parent game as a tiebreaker
        return getSimulationResult(parentIgra, null);
    }
    */

    //____________________________________________________________________________________________________________