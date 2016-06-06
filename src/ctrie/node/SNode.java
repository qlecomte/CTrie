package ctrie.node;

/* Means Singleton Node */
public class SNode<K, V> extends Branch<K, V> {
	private K key;
	private V value;
	
	public SNode(K key, V value){
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public void setValue(V value) {
		this.value = value;
	}
	
	public MainNode<K, V> enTomb(){
		return new TNode<K, V>(new SNode<K, V>(key, value));
	}
}
