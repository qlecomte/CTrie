package ctrie.node;

import ctrie.Result;
import ctrie.ValueResult;
import ctrie.Constants;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/* Means Indirection Node */

public class INode<K, V> extends Branch<K, V>{
	private volatile MainNode<K, V> main;
	
    public static final AtomicReferenceFieldUpdater<INode, MainNode> updater = AtomicReferenceFieldUpdater.newUpdater(INode.class, MainNode.class, "main");
	
	public INode(CNode<K, V> cn){
		this.main = cn;
	}
	
	public boolean isNull(){
		return (main == null);
	}
	
	public int computeFlag(int hashKey, int level){
		int idx = (hashKey >>> level) & 0x1f;
        return 1 << idx;
	}
	public int computePos(int flag, int bmp){
		int mask = flag - 1;
		return Integer.bitCount (bmp & mask);
	}
	
	public Result iinsert(K key, V value, int level, INode<K, V> parent){
		if (main instanceof CNode){
			CNode<K, V> cn = (CNode<K, V>)main;
			int flag = computeFlag(key.hashCode(), level);
			int pos = computePos(flag, cn.getBmp());
			if ((cn.getBmp() & flag) == 0){
				CNode<K, V> ncn = cn.inserted(pos, flag, new SNode<K, V>(key, value));
				if (CAS(cn, ncn)){
					return Result.OK;
				}else{
					return Result.RESTART;
				}
			}
			
			Branch<K, V> b = (Branch<K, V>)cn.getArray(pos);
			if (b instanceof INode){
				INode<K, V> sin = (INode<K, V>)b;
				return sin.iinsert(key, value, level, this);
			}else if (b instanceof SNode){
				SNode<K, V> sn = (SNode<K, V>)b;
				if (sn.getKey() != key){
					SNode<K, V> nsn = new SNode<>(key, value);
					INode<K, V> nin = new INode<>(new CNode<K, V>(sn, nsn, level + Constants.W));
					CNode<K, V> ncn = cn.updated(pos, nin);
					if (CAS(cn, ncn)){
						return Result.OK;
					}else{
						return Result.RESTART;
					}
				}else{
					CNode<K, V> ncn = cn.updated(pos, new SNode<K, V>(key, value));
					if (CAS(cn, ncn)){
						return Result.OK;
					}else{
						return Result.RESTART;
					}
				}
			}
		}else if(main instanceof TNode){
			parent.clean(level - Constants.W);
			return Result.RESTART;
		}else if(main instanceof LNode){
			LNode<K, V> ln = (LNode<K, V>)main;
			if (CAS(ln, ln.inserted(key, value))){
				return Result.OK;
			}else{
				return Result.RESTART;
			}
		}
		return Result.NOTFOUND;
	}
    
	public ValueResult<V> ilookup(K key, int level, INode<K, V> parent){
		//MainNode<K, V> main = READ(main);
		if (main instanceof CNode){
			CNode<K, V> cn = (CNode<K, V>)main;
			int flag = computeFlag(key.hashCode(), level);
			int pos = computePos(flag, cn.getBmp());
			if ((cn.getBmp() & flag) == 0){
				return new ValueResult<V>(Result.NOTFOUND);
			}
			
			Branch <K, V> b = (Branch<K, V>)cn.getArray(pos);
			if (b instanceof INode){
				INode<K, V> in = (INode<K, V>)b;
				return in.ilookup(key, level + Constants.W, this);
			}else if (b instanceof SNode){
				SNode<K, V> sn = (SNode<K, V>)b;
				if (sn.getKey() == key){
					return new ValueResult<V>(sn.getValue());
				}else{
					return new ValueResult<V>(Result.NOTFOUND);
				}
			}
			
		}else if(main instanceof TNode){
			parent.clean(level - Constants.W);
			return new ValueResult<V>(Result.RESTART);
		}else if(main instanceof LNode){
			LNode<K, V> ln = (LNode<K, V>)main;
			return ln.lookup(key);
		}
		
		return new ValueResult<V>(Result.NOTFOUND);
	}
	
	public ValueResult<V> iremove(K key, int level, INode<K, V> parent){
		if (main instanceof CNode){
			CNode<K, V> cn = (CNode<K, V>)main;
			int flag = computeFlag(key.hashCode(), level);
			int pos = computePos(flag, cn.getBmp());
			if ((cn.getBmp() & flag) == 0){
				return new ValueResult<V>(Result.NOTFOUND);
			}
			
			ValueResult<V> res = null;
			Branch <K, V> b = (Branch<K, V>)cn.getArray(pos);
			if (b instanceof INode){
				INode<K, V> in = (INode<K, V>)b;
				res = in.iremove(key, level + Constants.W, this);
			}else if (b instanceof SNode){
				SNode<K, V> sn = (SNode<K, V>)b;
				if (!sn.getKey().equals(key)){
					res = new ValueResult<V>(Result.NOTFOUND);
				}else {
					CNode<K, V> ncn = cn.removed(pos, flag);
					CNode<K, V> cntr = toContracted(ncn, level);
					if (CAS(cn, cntr)){
						res = new ValueResult<V>(sn.getValue());
					}else{
						res = new ValueResult<V>(Result.RESTART);
					}
				}
			}
			
			if (res.getRes().equals(Result.NOTFOUND) || res.getRes().equals(Result.RESTART)){
				return res;
			}
			
			if (main instanceof TNode){
				cleanParent(parent, this, key.hashCode(), level - Constants.W);
			}
			
		}else if(main instanceof TNode){
			parent.clean(level - Constants.W);
			return new ValueResult<V>(Result.RESTART);
		}else if(main instanceof LNode){
			LNode<K, V> ln = (LNode<K, V>)main;
			LNode<K, V> nln = ln.removed(key);
			if (nln.length() == 1){
				nln = (LNode<K, V>) nln.getSn().enTomb();
			}
			if (CAS(ln, nln)){
				return ln.lookup(key);
			}else{
				return new ValueResult<V>(Result.RESTART);
			}
		}
		
		return new ValueResult<V>(Result.NOTFOUND);
	}
	
	/*public MainNode<K, V> READ(){
		
	}*/

	public boolean CAS(MainNode<K, V> old, MainNode<K, V> n){
		return updater.compareAndSet(this, old, n);
	}
	
	
	public CNode<K, V> toCompressed(CNode<K, V> cn, int level){
		int num = Integer.bitCount(cn.getBmp());
		CNode<K, V> ncn = cn.mapped(resurrect(_));
		return toContracted(ncn, level);
	}
	
	public CNode<K, V> toContracted(CNode<K, V> cn, int level){
		if (level > 0 && cn.getArray(pos)){
			if (cn.getArray(0) instanceof SNode){
				return (CNode<K, V>) ((SNode<K, V>)cn.getArray(0)).enTomb();
			}
		}
		
		return cn;
	}
	
	public void clean(int level){
		if (main instanceof CNode){
			CAS(main, toCompressed((CNode<K, V>)main, level));
		}
	}
	
	public void cleanParent(INode<K, V> parent, INode<K, V> i, int hashKey, int level){
		MainNode<K, V> m = i.main;
		MainNode<K, V> pm = parent.main;
		if (pm instanceof CNode){
			CNode<K, V> cn = (CNode<K, V>)pm;
			int flag = computeFlag(hashKey, level);
			int pos = computePos(flag, cn.getBmp());
			if ((cn.getBmp() & flag) == 0){
				return;
			}
			Branch<K, V> sub = cn.getArray(pos);
			if (!sub.equals(i)){
				return;
			}
			
			if (m instanceof TNode){
				CNode<K, V> ncn = cn.updated(pos, ((TNode<K, V>)m).resurrect());
				if (!CAS(cn, toContracted(ncn, level))){
					cleanParent(parent, i, hashKey, level);
				}
			}
			
		}else{
			return;
		}
	}
	
	
}
