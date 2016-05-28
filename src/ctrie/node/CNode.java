package ctrie.node;

import ctrie.Constants;

/* Means Ctrie node */
public class CNode<K, V> extends MainNode<K, V> {
	private int bmp;
	private BasicNode[] array;


	public CNode(){
		bmp = 0;
		array = new BasicNode[ (int)Math.pow(2, Constants.W) ];
	}
	
	public CNode(int bmp, BasicNode[] array){
		this.bmp = bmp;
		this.array = array;
	}
	
	/* TODO */
	public CNode(SNode<K, V> sn, SNode<K, V> nsn, int level){
		
	}

	public int getBmp() {
		return bmp;
	}

	public BasicNode getArray(int pos) {
		return array[pos];
	}
	
	public CNode<K, V> inserted(int pos, int flag, SNode<K, V> node){
		int len = array.length;
        int bmp = this.bmp;
        BasicNode[] narr = new BasicNode[len + 1];
        System.arraycopy (array, 0, narr, 0, pos);
        narr [pos] = node;
        System.arraycopy (array, pos, narr, pos + 1, len - pos);
        return new CNode<K, V>(bmp | flag, narr);
	}
	
	/* TODO */
	public CNode<K, V> updated(int pos, Branch<K, V> node){
		
	}
	
	/* TODO */
	public CNode<K, V> removed(int pos, int flag){
		
	}
	
}
