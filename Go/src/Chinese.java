import java.util.Random;

import disjointSets.DisjointSets;
import disjointSets.FastSets;
import disjointSets.ListSets;

public class Chinese {

	private static Random RANDOMS = new Random();
	
	private static final int N =1000000;

	public static void main(String[] args) {
		DisjointSets<Integer> restaurant = new FastSets<Integer> ();
		for (int i = 0; i < N; i++) {
			restaurant.add(i);
			int j = RANDOMS.nextInt(i+1);
			int k = RANDOMS.nextInt(i+1);
			restaurant.union(k,j);
			int l = RANDOMS.nextInt(i+1);
			restaurant.find(l);
		}
		System.out.println("Number of tables = " + restaurant.numSets());
	}

}
