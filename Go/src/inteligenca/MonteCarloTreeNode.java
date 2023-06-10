package inteligenca;

import logika.Igra;
import logika.Igralec;

import splosno.Poteza;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MonteCarloTreeNode {
	//
    private Igra igra;
    private int wins;
    private int plays;
    private MonteCarloTreeNode parent;
    private Map<Poteza, MonteCarloTreeNode> children;
    private boolean expanded;
    private boolean koeficientZmage; // Ker vemo, da se alternirajoče odločamo, ali je v enačbi štejemo več x ali 1-x pri zmagah, nam to pomaga pri razreševanju problema
    
    
    private static final int SIMULACIJSKI_STEVEC = 500;
    // Stevilo ponovitev, ko expendam, če je premalo, se preveč random stvari raziskujejo in se vrnejo na točko, ko bi jo lahko rej reši z večimi reS
    private static final double C_PARAMETER = 1.4;
    // C parameter iz enačbe iz predavanj, kako rad bi expandal, poskušam lahko potem z več/manj, če je repov manj potem naj bi ta tudi bila manj
    
   
    public MonteCarloTreeNode(Igra igra, MonteCarloTreeNode parent) {
        this.igra = igra;
        this.wins = 0;
        this.plays = 0;
        this.parent = parent;
        this.children = new HashMap<>();
        this.expanded = false;
        this.koeficientZmage = true;
    } 
    
    // Nekaj osnovnih funkcij za dostopanje in updateanje

	public MonteCarloTreeNode(Igra igra) {
		this.igra = igra;
        this.wins = 0;
        this.plays = 0;
        this.parent = null;
        this.children = new HashMap<>();
        this.expanded = false;
        this.koeficientZmage = !parent.koeficientZmage;
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

    public void incrementWins() {
        wins++;
    }

    public void incrementPlays() {
        plays++;
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