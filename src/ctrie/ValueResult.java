package ctrie;

public class ValueResult<V>{
	
	private Result res;
	private V value;
	
	public ValueResult(Result res){
		this.res = res;
	}
	
	public ValueResult(V value){
		this.res = Result.OK;
		this.value = value;
	}

	public Result getRes() {
		return res;
	}

	public V getValue() {
		return value;
	}
}