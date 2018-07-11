package sanavesa.model.versionControl;

public interface ICommand
{
	void execute();
	void undo();
}
