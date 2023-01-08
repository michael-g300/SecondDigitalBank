package app;

public class PageNotFoundException extends Exception {
	private final String m_uri;
	
	public PageNotFoundException(final String uri) {
		m_uri = uri;
	}
	
	public final String getUri() {
		return m_uri;
	}
}
