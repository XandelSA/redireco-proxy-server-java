package co.redire.server.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.redire.server.App;
import co.redire.server.configuration.ConfigBean;

@RestController
public class ServerController {

	protected static final String[] FORBIDDEN_HEADERS = new String[] {
			HttpHeaders.ACCEPT_CHARSET.toUpperCase(),
			HttpHeaders.ACCEPT_ENCODING.toUpperCase(),
			HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS.toUpperCase(),
			HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD.toUpperCase(),
			HttpHeaders.CONNECTION.toUpperCase(),
			HttpHeaders.CONTENT_LENGTH.toUpperCase(),
			HttpHeaders.COOKIE.toUpperCase(),
			HttpHeaders.DATE.toUpperCase(),
			HttpHeaders.EXPECT.toUpperCase(),
			HttpHeaders.HOST.toUpperCase(),
			HttpHeaders.ORIGIN.toUpperCase(),
			HttpHeaders.PROXY_AUTHENTICATE.toUpperCase(),
			HttpHeaders.PROXY_AUTHORIZATION.toUpperCase(),
			HttpHeaders.REFERER.toUpperCase(),
			HttpHeaders.TE.toUpperCase(),
			HttpHeaders.TRAILER.toUpperCase(),
			HttpHeaders.TRANSFER_ENCODING.toUpperCase(),
			HttpHeaders.USER_AGENT.toUpperCase(),
			HttpHeaders.UPGRADE.toUpperCase(),
			HttpHeaders.VIA.toUpperCase(),
	};
	
	public ServerController() {

	}

	@RequestMapping(value = "/config", method = RequestMethod.GET, produces = "application/json")
	@CrossOrigin
	public ConfigBean config() {
		return App.config;
	}

	@RequestMapping("/proxy")
	@CrossOrigin
	public ResponseEntity<String> proxy(HttpServletRequest request, @RequestHeader Map<String, String> headerMap, @RequestParam Map<String,String> parameterMap, @RequestBody String body) {

		try {

			// Decide whether we are looking for the url parameter or using a hard-coded url
			String url;
			if (App.config.getUrl() != null) {
				url = App.config.getUrl();
			} else {
				url = parameterMap.remove(App.config.getUrlParameter()); // Fetch / remove so as not to forward
				if (url == null) {
					throw new IllegalArgumentException("Missing required url parameter " + "'" + App.config.getUrlParameter() + "'");
				}
			}
			
			// Build the request
			HttpRequest.Builder proxyRequestBuilder = HttpRequest.newBuilder();
			proxyRequestBuilder.uri(new URI(url));
			headerMap.entrySet().stream()
				.filter(e -> !Arrays.asList(FORBIDDEN_HEADERS).contains(e.getKey().toUpperCase()))
				.forEach(e -> proxyRequestBuilder.headers(e.getKey(), e.getValue()));
			if (request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
				proxyRequestBuilder.GET();
			} else {
				proxyRequestBuilder.method(request.getMethod(), HttpRequest.BodyPublishers.ofString(body));
			}
			HttpRequest httpRequest = proxyRequestBuilder.build();
			
			// Send the request
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
			
			// Return the response
			return new ResponseEntity<String>(httpResponse.body(), HttpStatus.valueOf(httpResponse.statusCode()));

		} catch (Exception ex) {
    		System.out.println("An unexpected error occurred. Start with option " + "-" + App.CLI_OPTION_DEBUG + " for more detailed information.");
    		System.out.println("Error: " + ex.getMessage());
    		if (App.config.isDebug()) {
    			ex.printStackTrace();
    		}
			// Return the response
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
}
