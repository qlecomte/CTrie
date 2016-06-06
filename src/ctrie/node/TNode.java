package ctrie.node;

/* Means Tomb Node */
public class TNode<K, V> extends MainNode<K, V> {
	private SNode<K, V> sn;
	
	public TNode(SNode<K, V> sn){
		this.sn = sn;
	}
	
	public Branch<K, V> resurrect(){
		return new SNode<K, V>(sn.getKey(), sn.getValue());
	}
}
