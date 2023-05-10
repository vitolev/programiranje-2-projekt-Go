package disjointSets;

import java.util.LinkedList;
import java.util.List;

public class ListSets<E> implements DisjointSets<E> {

	private List<List<E>> sets;
	private int numSets;
	
	public ListSets() {
		sets = new LinkedList<List<E>>();
		numSets = 0;
	}
	
	@Override
	public void add (E e) {
		for (List<E> set : sets) {
			if (set.contains(e)) return;
		}
		LinkedList<E> b = new LinkedList<E> ();
		b.add(e);
		sets.add(b);
		numSets++;
	}
	
	@Override
	public E find (E e) {
		for (List<E> set : sets) {
			if (set.contains(e)) return set.get(0);
		}
		return null;
	}
	
	@Override
	public void union (E e1, E e2) {
		List<E> block1 = null, block2 = null;
		for (List<E> block : sets) {
			if (block.contains(e1)) block1 = block;
			if (block.contains(e2)) block2 = block;
		}
		if (block1 == null || block2 == null || block1 == block2) return;
		for (E e : block2) {
			block1.add(e);
		}
		sets.remove(block2);
		numSets--;
	}
	
	@Override 
	public int numSets() {
		return numSets;
	}

}
