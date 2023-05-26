package inteligenca;

import logika.Igra;
import logika.Igralec;
import logika.Polje;
import logika.Grupa;
import java.util.Random;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;

public class OceniPozicijo {
	
	@SuppressWarnings("null")
	public static int oceniPozicijo(Igra igra, Igralec jaz) {
		// V oceno pozicije bova štela ogroženost svojih grup (-), ogroženost nasprotnih grup (+) in zasedno območje.
		// Vse tri posebej bomo najprej ocenili od -100 do 100;
		Set<Grupa> mojeGrupe = (jaz == Igralec.CRNI ? igra.grupeCrnega : igra.grupeBelega);
		Set<Grupa> njegoveGrupe = (jaz == Igralec.CRNI ? igra.grupeBelega : igra.grupeCrnega);
		Polje mojaBarva = (jaz == Igralec.CRNI ? Polje.CRNO : Polje.BELO);
		Polje njegovaBarva = (jaz == Igralec.CRNI ? Polje.BELO : Polje.CRNO);

		int k = 1;
		int mojaOgrozenost = mojaOgrozenost(mojeGrupe);
		int njegovaOgrozenost = mojaOgrozenost(njegoveGrupe);
		int razlikaOgrozenosti = njegovaOgrozenost - mojaOgrozenost; //vec imas boljse je

		int mojeObmocje = mojeObmocje(igra, mojaBarva);
		int njegovoObmocje = mojeObmocje(igra, njegovaBarva);
		int razlikaObmocja = (mojeObmocje - njegovoObmocje) / 1000;
		
		int evalvacija = razlikaOgrozenosti;
		if (razlikaObmocja > 0) {
			evalvacija += k;
		}
		else {
			evalvacija -= k;
		}
		
		// Random RANDOM = new Random();
		// int evalvacija = RANDOM.nextInt(-100, 100);
		
		// Mogoce pol to skini
		if (evalvacija > 100){
			evalvacija = 99;
		}
		
		if (evalvacija < -100){
			evalvacija = -99;
		}
		return evalvacija;
	}
	
	// Funkcija, ki vzame množico grup in vrne ogroženost glede na to grupo
	
	// Funkcija bo vzela obratne vrednosti od treh najmanjših vrednosti, nato pa jih seštela, najmanjša vredsnost je potem kar 3, zato bomo na koncu
	// pomnožili s 100/3
	// OPOMBA: Paziti moramo, da tretji parameter ne bom dosegel velikih vrednosti, saj ta parameter ne bo dostikrat dosegel vrednsoti nad 100, saj bi
	// potem lahko program zmagal, osredotočiti se še moramo na to, da če je vrednost ogroženosti zelo nizek, se osredotoči na območje, ki ga dosega
	// Posebej bomo pogledali 3 najbolj ogrožene moje grupe in 3 najbolj ogrožene njegove grupe, osredotočili se bomo le na ogroćenost 1, 2 in 3

	public static int mojaOgrozenost(Set<Grupa> mojeGrupe) { // Ime moje grupe pomeni, da vrne mogo ogroženost, torej pri nasprotniku mora vrniti njegovo ogroženost
		Set<Integer> mojeOgrozene = new HashSet<Integer>();
		for (Grupa grupa : mojeGrupe) {
			int steviloSosednih;
			if (mojeOgrozene.size() == 0) {
				mojeOgrozene.add(grupa.sosednjeTocke.size());
			} 
			else if ((steviloSosednih = grupa.sosednjeTocke.size()) < 4) {
				if (mojeOgrozene.size() < 3) {
					mojeOgrozene.add(steviloSosednih);
				}
				else {
					int maksimum = Collections.max(mojeOgrozene);
					if (steviloSosednih < maksimum) {
						mojeOgrozene.remove(maksimum);
						mojeOgrozene.add(steviloSosednih);
					}
				}
			}
		}
		double vsotaZaOgrozenost = 0;
		if (mojeOgrozene != null) {
			int maksimalno = Collections.max(mojeOgrozene); 
			for (int i : mojeOgrozene) {
				if (i == maksimalno) {
					vsotaZaOgrozenost += Math.pow(i, -1); // Tista polja, ki so najbolj ogrožena prispevajo veliko več k ogroženosti
				}
				else {
					vsotaZaOgrozenost += Math.pow(i, -2) ;
				}
			}
		}
		return Math.floorDiv((int)(100 * vsotaZaOgrozenost), 3);
	}
	
	public static int ovrednotiSeznam (Polje[] seznamPolj, Polje mojaBarva) { // potem še samo naredi reversed da pogleda
		Polje trenutnoPolje = Polje.PRAZNO;
		int stevec = 0;
		for (Polje polje : seznamPolj) {
			if (polje == Polje.PRAZNO) { // Najprej pogledamo, če je polje prazno
				stevec += 1;
			}
			else { // Če ni prazno, potem ločimo na nekaj primerov:
				if (trenutnoPolje == Polje.PRAZNO) { //Če je trenutno prazno, potem spremenimo trenutno polje, k temu se bo pripisovalo
					trenutnoPolje = polje;
					stevec += 1;
				}
				else if (trenutnoPolje == polje) { // če je enako kot je bilo skos, torej še en isti kamenček, štejemo naprej
					stevec += 2; // !!!!!! TUKAJ MOGOČE DODAJ DA +2, saj je to boljše, ker ima več območja, če ima še pokrito, vseeno premisli kaj je boljše
				}
				else { // zadnji primer je, ko naleti na nasprotnikivo polje, tukaj vrne vrednost
					break;
				}
			}
		}
		if (trenutnoPolje != mojaBarva) {
			stevec = (-1) * stevec;
		}
		return stevec;
	}
	
	public static int mojeObmocje(Igra igra, Polje mojaBarva) { // območje prispeva k zmagi v primeru igranja do konca in prispeva k večji obkoljenosti nasprotnika
		int vsota = 0;
		for (Polje[] seznam : igra.vrsticeInStolpci()) {
			vsota += ovrednotiSeznam(seznam, mojaBarva);
			vsota += ovrednotiSeznam(Igra.reverseArray(seznam), mojaBarva);
		}
		return vsota;
	}
}
