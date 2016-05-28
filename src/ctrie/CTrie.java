package ctrie;

import ctrie.node.*;

public class CTrie<K, V> {
	private INode<K, V> root;
	
	public CTrie(){
		CNode<K, V> cn = new CNode<>();
		root = new INode<K, V>(cn);
	}
	
	public ValueResult<V> lookup(K key){
		INode<K, V> r = root;
		ValueResult<V> res = r.ilookup(key, 0, null);
		if (res.getRes() != Result.RESTART){
			return res;
		}else {
			return lookup(key);
		}
	}
	
	public void insert (K key, V value){
		INode<K, V> r = root;
		if (r.iinsert(key, value, 0, null) == Result.RESTART){
			insert(key, value);
		}
	}
	
	public ValueResult<V> remove (K key){
		INode<K, V> r = root;
		ValueResult<V> res = r.iremove(key, 0, null);
		if (res.getRes() != Result.RESTART){
			return res;
		}else {
			return remove(key);
		}
	}
}
