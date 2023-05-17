package inteligenca;

import logika.Igra;
import logika.Igralec;
import java.util.Random;

public class OceniPozicijo {
	
	public static int oceniPozicijo(Igra igra, Igralec jaz) {
		Random RANDOM = new Random();
		int evalvacija = RANDOM.nextInt(-100, 100);
		return evalvacija;
	}
	
}
