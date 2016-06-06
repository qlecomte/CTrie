package ctrie.node;

import ctrie.Constants;

/* Means Ctrie node */
public class CNode<K, V> extends MainNode<K, V> {
	private int bmp;
	private BasicNode[] array;
	
	public CNode(int bmp, BasicNode[] array){
		this.bmp = bmp;
		this.array = array;
	}
	
	static <K, V> MainNode<K,V> dual (final SNode<K, V> sn, final SNode<K, V> nsn, int level) {
        if (level < 33) {
            int xidx = (sn.getKey().hashCode() >> level) & 0x1f;
            int yidx = (nsn.getKey().hashCode() >> level) & 0x1f;
            int bmp = ((1 << xidx) | (1 << yidx));

            if (xidx == yidx) {
                INode<K, V> in = new INode<K, V> ( dual (sn, nsn, level + Constants.W) );
                return new CNode<K, V> (bmp, new BasicNode[] { in });
            } else {
                if (xidx < yidx)
                    return new CNode<K, V> (bmp, new BasicNode[] { sn, nsn });
                else
                    return new CNode<K, V> (bmp, new BasicNode[] { nsn, sn });
            }
        } else {
        	LNode<K, V> ln = new LNode<>(sn);
        	ln.inserted(nsn.getKey(), nsn.getValue());
            return ln;
        }
    }

	public int getBmp() {
		return bmp;
	}
	
	public BasicNode[] getArray() {
		return array;
	}

	public BasicNode getArray(int pos) {
		return array[pos];
	}
	
	public CNode<K, V> inserted(int pos, int flag, SNode<K, V> node){
		BasicNode[] arr = array;
		int len = arr.length;
        int bmp = this.bmp;
        BasicNode[] narr = new BasicNode[len + 1];
        System.arraycopy (arr, 0, narr, 0, pos);
        narr [pos] = node;
        System.arraycopy (arr, pos, narr, pos + 1, len - pos);
        
        return new CNode<K, V>(bmp | flag, narr);
	}
	
	public CNode<K, V> updated(int pos, Branch<K, V> node){
		BasicNode[] arr = array;
		int len = arr.length;
        BasicNode[] narr = new BasicNode[len];
        System.arraycopy (arr, 0, narr, 0, len);
        narr [pos] = node;
        return new CNode<K, V> (bmp, narr);
	}
	
	public CNode<K, V> removed(int pos, int flag){
		BasicNode[] arr = array;
        int len = arr.length;
        BasicNode[] narr = new BasicNode[len - 1];
        System.arraycopy (arr, 0, narr, 0, pos);
        System.arraycopy (arr, pos + 1, narr, pos, len - pos - 1);
        return new CNode<K, V> (bmp ^ flag, narr);
	}
	
	public CNode<K, V> toCompressed(int level){
        int i = 0;
        BasicNode[] tmparray = new BasicNode[array.length];
        while (i < array.length) { // construct new bitmap
            BasicNode sub = array [i];
            if (sub instanceof INode) {
                @SuppressWarnings("unchecked")
				INode<K, V> in = (INode<K, V>) sub;
                MainNode<K, V> iMain = in.getMain();
                assert (iMain != null);
                tmparray [i] = resurrect(in, iMain);
            } else if (sub instanceof SNode) {
                tmparray [i] = sub;
            }
            i += 1;
        }

        return new CNode<K, V> (bmp, tmparray).toContracted(level);
	}
	
	public CNode<K, V> toContracted(int level){
		if (array.length == 1 && level > 0) {
            if (array [0] instanceof SNode) {
                @SuppressWarnings("unchecked")
				SNode<K, V> sn = (SNode<K, V>)array[0];
                return (CNode<K, V>) sn.enTomb();
            } else
                return this;

        } else
            return this;
	}
	
	private BasicNode resurrect (final INode<K, V> inode, final Object inodemain) {
        if (inodemain instanceof TNode) {
            @SuppressWarnings("unchecked")
			TNode<K, V> tn = (TNode<K, V>) inodemain;
            return tn.resurrect();
        } else
            return inode;
    }
}
