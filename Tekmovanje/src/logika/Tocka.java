package logika;

import java.util.Objects;

// Razred tocka samo predstavlja x in y koordinato odigrane figure
public class Tocka {
	
	// Ker želim, da je vrednost koordinat po konstrukciji konstanta, sta koordinati private.
	private int x;
	private int y;
	
	// KONSTRUKTOR
	public Tocka(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	// Funkcija za dostop do koordinate x
	public int x() {
		return x;
	}
	// Funkcija za dostop do koordinate y
	public int y() {
		return y;
	}
	
	// Igralna plosča je N*N, kjer je (0,0) zgoraj levo, (N-1,N-1) spodaj desno
	// Funkcija preveri ce je koordinata tocke na razponu [0,N - 1]
	public boolean jeTockaVeljavna() {
		return x >= 0 && x < Igra.N && y >= 0 && y < Igra.N;
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	
	@Override
    public boolean equals(Object o) {
 
        // If the object is compared with itself then return true 
        if (o == this) {
            return true;
        }
 
        /* Check if o is an instance of Tocka or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Tocka)) {
            return false;
        }
         
        // typecast o to Tocka so that we can compare data members
        Tocka tocka = (Tocka) o;
         
        // Compare the data members and return accordingly
        return x == tocka.x() && y == tocka.y();
    }
	
	@Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
