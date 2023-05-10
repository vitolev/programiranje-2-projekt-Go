package disjointSets;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class FastSets<E> implements DisjointSets<E> {

	private Map<E,E> parents;
	
	private static final Random RANDOMS = new Random();
	
	private int numSets;
	
	public FastSets() {
		parents = new HashMap<E,E> ();
		numSets = 0;
	}
	
	@Override
	public void add(E e) {
		if (parents.containsKey(e)) return;
		parents.put(e, e);
		numSets++;		
	}
	
	private E findNoCompress (E e) {
		if (!parents.containsKey(e)) return null;
		E f = e;
		while (true) {
			E g = parents.get(f);
			if (g == f) break;
			f = g;		
		}
		return f;
	}

	private void pathCompress(E e, E r) {
		E f = parents.get(e);
		parents.put(e, r);
		if (e != f) pathCompress(f,r);
	}
	
	@Override
	public E find (E e) {
		E r = findNoCompress (e);
		pathCompress(e,r);
		return r;
	}
	
	
	@Override 
	public void union(E e1, E e2) {
		if (parents.containsKey(e1) && parents.containsKey(e2)) {
			E r1 = findNoCompress(e1);
			E r2 = findNoCompress(e2);
			E r = (RANDOMS.nextBoolean()?r1:r2);
		    pathCompress(e1,r);
		    pathCompress(e2,r);
		    if (r1 != r2) numSets--;
		}
	}
	
	@Override
	public int numSets() {
		return numSets;
	}
	
	public Set<E> keys(){
		return parents.keySet();
	}

}
