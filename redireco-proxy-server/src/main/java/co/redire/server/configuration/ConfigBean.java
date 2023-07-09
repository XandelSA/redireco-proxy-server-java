package co.redire.server.configuration;

public class ConfigBean {

	public static final int DEFAULT_PORT = 8765;
	public static final boolean DEFAULT_DEBUG = false;
	public static final String DEFAULT_URL = null;
	public static final String DEFAULT_URL_PARAMETER = "url";

	public int port = DEFAULT_PORT;
	public boolean debug = DEFAULT_DEBUG;
	public String url = DEFAULT_URL;
	public String urlParameter = DEFAULT_URL_PARAMETER;

	public ConfigBean() {

	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlParameter() {
		return urlParameter;
	}

	public void setUrlParameter(String urlParameter) {
		this.urlParameter = urlParameter;
	}
	
}
