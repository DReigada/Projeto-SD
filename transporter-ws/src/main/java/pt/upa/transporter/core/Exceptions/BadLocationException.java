package pt.upa.transporter.core.Exceptions;

@SuppressWarnings("serial")
public class BadLocationException extends Exception {
	
	private String _location;
	
	public BadLocationException(String message, String location) {
		super(message);
		_location = location;
	}
	
	/**
	 * @return the invalid location
	 */
	public String getLocation(){
		return _location;
	}
}
