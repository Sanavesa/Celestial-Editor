package sanavesa.util;

public  interface Observer<T>
{
	void onChanged(Observable<T> observable, T oldValue, T newValue);
}