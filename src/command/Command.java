package command;

@FunctionalInterface
public interface Command {
	public void apply(String[] arg);
}
