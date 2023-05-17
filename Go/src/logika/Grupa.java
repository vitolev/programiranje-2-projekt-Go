package logika;

import java.util.HashSet;
import java.util.Set;

public class Grupa {
	
	public Set<Tocka> povezaneTocke;
	public Set<Tocka> sosednjeTocke; // Te sosedne točke so proste sosednje točke
	
	public Grupa() {
		povezaneTocke = new HashSet<Tocka>();
		sosednjeTocke = new HashSet<Tocka>();
	}
	
	public Grupa(Grupa grupa) {
		povezaneTocke = new HashSet<Tocka>();
		sosednjeTocke = new HashSet<Tocka>();
		
		for(Tocka tocka : grupa.povezaneTocke) {
			povezaneTocke.add(tocka);
		}
		for(Tocka tocka : grupa.sosednjeTocke) {
			sosednjeTocke.add(tocka);
		}
	}
	
	public void dodajTocko(Tocka tocka, Set<Tocka> mnozicaNasprotnihTock) {
		povezaneTocke.add(tocka);
		dodajSosednjeTocke(tocka, mnozicaNasprotnihTock);
	}
	
	// Dodaj sosednje tocke v množico sosednjih tock
	private void dodajSosednjeTocke(Tocka tocka, Set<Tocka> mnozicaNasprotnihTock) {
		Tocka gor = new Tocka(tocka.x(), tocka.y() + 1);
		Tocka dol = new Tocka(tocka.x(), tocka.y() - 1);
		Tocka levo = new Tocka(tocka.x() - 1, tocka.y());
		Tocka desno = new Tocka(tocka.x() + 1, tocka.y());
		
		if(!povezaneTocke.contains(gor) && gor.jeTockaVeljavna() && !mnozicaNasprotnihTock.contains(gor)) {sosednjeTocke.add(gor);}
		if(!povezaneTocke.contains(dol) && dol.jeTockaVeljavna() && !mnozicaNasprotnihTock.contains(dol)) {sosednjeTocke.add(dol);}
		if(!povezaneTocke.contains(levo) && levo.jeTockaVeljavna() && !mnozicaNasprotnihTock.contains(levo)) {sosednjeTocke.add(levo);}
		if(!povezaneTocke.contains(desno) && desno.jeTockaVeljavna() && !mnozicaNasprotnihTock.contains(desno)) {sosednjeTocke.add(desno);}
		
		sosednjeTocke.remove(tocka);
	}
	
	// Za vizualno reprezentacijo objekta v konzoli.
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
