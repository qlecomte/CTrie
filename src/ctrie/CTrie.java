package ctrie;

import ctrie.node.*;

public class CTrie<K, V> {
	
	private CTrie(){
		CNode<K, V> cn = new CNode<>(0, new BasicNode[]{});
		root = new INode<K, V>(cn);
	}
	
	/** Holder */
	private static class SingletonHolder<K, V>
	{		
		/** Instance unique non préinitialisée */
		private final static CTrie<String, String> instance = new CTrie<>();
	}
	
	/** Point d'accès pour l'instance unique du singleton */
	public static CTrie<String, String> getInstance()
	{
		return SingletonHolder.instance;
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
	
	public Result insert (K key, V value){
		INode<K, V> r = root;
		if (r.iinsert(key, value, 0, null) == Result.RESTART){
			insert(key, value);
		}
		return Result.OK;
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
	
	private INode<K, V> root;
}
