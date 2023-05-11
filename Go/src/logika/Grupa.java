package logika;

import java.util.HashSet;
import java.util.Set;

public class Grupa {
	
	public Set<Tocka> povezaneTocke;
	public Set<Tocka> sosednjeTocke;
	
	public Grupa() {
		povezaneTocke = new HashSet<Tocka>();
		sosednjeTocke = new HashSet<Tocka>();
	}
	
	public Grupa(Grupa grupa) {
		povezaneTocke = new HashSet<Tocka>();
		sosednjeTocke = new HashSet<Tocka>();
		
		for(Tocka tocka : grupa.povezaneTocke) {
			dodajTocko(tocka);
		}
	}
	
	public void dodajTocko(Tocka tocka) {
		povezaneTocke.add(tocka);
		posodobiSosednjeTocke(tocka);
	}
	
	// Dodaj sosednje tocke v množico sosednjih tock
	private void posodobiSosednjeTocke(Tocka tocka) {
		Tocka gor = new Tocka(tocka.x(), tocka.y() + 1);
		Tocka dol = new Tocka(tocka.x(), tocka.y() - 1);
		Tocka levo = new Tocka(tocka.x() - 1, tocka.y());
		Tocka desno = new Tocka(tocka.x() + 1, tocka.y());
		
		if(!vsebujePovezanoTocko(gor) && gor.jeTockaVeljavna()) {sosednjeTocke.add(gor);}
		if(!vsebujePovezanoTocko(dol) && dol.jeTockaVeljavna()) {sosednjeTocke.add(dol);}
		if(!vsebujePovezanoTocko(levo) && levo.jeTockaVeljavna()) {sosednjeTocke.add(levo);}
		if(!vsebujePovezanoTocko(desno) && desno.jeTockaVeljavna()) {sosednjeTocke.add(desno);}
		
		sosednjeTocke.remove(tocka);
	}
	
	// Funkcija preveri ali je podana tocka ze vsebovana v tej grupi v množici povezanih točk
	public boolean vsebujePovezanoTocko(Tocka tocka) {
		return povezaneTocke.contains(tocka);
	}
	
	// Funkcija preveri ali je podana tocka ze vsebovana v tej grupi v množici sosednjih točk
	public boolean vsebujeSosednjoTocko(Tocka tocka) {
		return sosednjeTocke.contains(tocka);
	}
	
	// Za vizualno reprezentacijo objekta v konzoli. Lahko se izboljsa
	@Override
	public String toString() {
		System.out.println("Povezane tocke:");
		for(Tocka t : povezaneTocke) {
			System.out.print(t);
		}
		System.out.println("");
		System.out.println("Sosednje tocke:");
		for(Tocka t : sosednjeTocke) {
			System.out.print(t);
		}
		return "";
	}
}
