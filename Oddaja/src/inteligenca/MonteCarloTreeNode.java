package inteligenca;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import logika.Igra;
import splosno.Poteza;

class MonteCarloTreeNode {
	//
    private Igra igra;
    private int wins;
    private int plays;
    private MonteCarloTreeNode parent;
    private Map<Poteza, MonteCarloTreeNode> children;
    private boolean expanded;
    private boolean koeficientZmage; // Ker vemo, da se alternirajoče odločamo, ali je v enačbi štejemo več x ali 1-x pri zmagah, nam to pomaga pri razreševanju problema
   
    public MonteCarloTreeNode(Igra igra, MonteCarloTreeNode parent) {
        this.igra = igra;
        this.wins = 0;
        this.plays = 0;
        this.parent = parent;
        this.children = new HashMap<>();
        this.expanded = false;
        this.koeficientZmage = !parent.koeficientZmage;
    } 
    
    // Nekaj osnovnih funkcij za dostopanje in updateanje

	public MonteCarloTreeNode(Igra igra) {
		this.igra = igra;
        this.wins = 0;
        this.plays = 0;
        this.parent = null;
        this.children = new HashMap<>();
        this.expanded = false;
        this.koeficientZmage = true;
	}

	public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    public MonteCarloTreeNode getParent() {
        return parent;
    }

    public Map<Poteza, MonteCarloTreeNode> getChildren() {
        return children;
    }

    public int getWins() {
        return wins;
    }

    public int getPlays() {
        return plays;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public Igra getIgra() {
        return igra;
    }

    public void incrementWins(int wins) {
        this.wins += wins;
    }

    public void incrementPlays(int plays) {
        this.plays += plays;
    }
    
    public boolean koeficientZmage() {
    	return koeficientZmage;
    }
    
    

    // Other methods for manipulating the tree structure
}

/*
public class MonteCarloTree {
    private MonteCarloTreeNode root;

    public MonteCarloTree(Igra igra) {
        this.root = new MonteCarloTreeNode(igra, null);
    }

    public MonteCarloTreeNode getRoot() {
        return root;
    }

    public void setRoot(MonteCarloTreeNode root) {
        this.root = root;
    }

    // Other methods for manipulating the tree structure
}
*/