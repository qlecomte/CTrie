package ctrie.node;

import ctrie.Result;
import ctrie.ValueResult;

/* Means Linked List Nodes, utilisés pour la gestion de collision */

public class LNode<K, V> extends MainNode<K, V>{
	private SNode<K, V> sn;
	private LNode<K, V> next;
	private LNode<K, V> previous;
	
	public LNode(SNode<K, V> sn){
		this.sn = sn;
	}

	public SNode<K, V> getSn() {
		return sn;
	}

	public void setSn(SNode<K, V> sn) {
		this.sn = sn;
	}

	public LNode<K, V> getNext() {
		return next;
	}
	
	public LNode<K, V> getPrevious() {
		return previous;
	}
	
	public int length(){
		if (next == null){
			return 1;
		}else{
			return next.length() + 1;
		}
	}
	
	public LNode<K, V> inserted(K key, V value){
		if (sn.getKey() == key){
			sn.setValue(value);
			
			return this;
		}else if(next == null){
			SNode<K, V> sn = new SNode<>(key, value);
			LNode<K, V> nln = new LNode<>(sn);
			
			this.next = nln;
			nln.previous = this;
			
			return nln;
		}else{
			return next.inserted(key, value);
		}
	}
	
	public ValueResult<V> lookup(K key){
		if (sn.getKey() == key){
			return new ValueResult<V>(sn.getValue());
		}else if( next == null){
			return new ValueResult<V>(Result.NOTFOUND);
		}else{
			return next.lookup(key);
		}
	}
	
	public LNode<K, V> removed(K key){
		if (sn.getKey() == key){
			previous.next = this.next;
			next.previous = this.previous;
			
			this.next = null;
			this.previous = null;
			
			return this;
			
		}else if( next == null){
			return null;
		}else{
			return next.removed(key);
		}
	}
	
	
}
