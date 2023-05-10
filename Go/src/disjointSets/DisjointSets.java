package disjointSets;

public interface DisjointSets<E> {
	
	public void add(E e);
	
	public E find(E e);
	
	public void union(E e1, E e2);
	
	public int numSets();

}
