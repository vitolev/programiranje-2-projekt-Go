package inteligenca;

import logika.Igra;
import logika.Igralec;
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
		
		int mojaOgrozenost = mojaOgrozenost(mojeGrupe);
		int njegovaOgrozenost = mojaOgrozenost(njegoveGrupe);

		
		// Random RANDOM = new Random();
		// int evalvacija = RANDOM.nextInt(-100, 100);
		return njegovaOgrozenost - mojaOgrozenost;
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
			for (int i : mojeOgrozene) {
				vsotaZaOgrozenost += Math.pow(i, -2)  ;
			}
		}
		return Math.floorDiv((int)(100 * vsotaZaOgrozenost), 3);
	}
	
}
