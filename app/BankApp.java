package app;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import super_simple_web_server.Status;
import super_simple_web_server.SuperSimpleWebServer;
import super_simple_web_server.SuperSimpleWebServer.Request;
import users.Client;
import webaction.ChooseUserName;
import webaction.GetUserInfo;
import webaction.WebAction;

public class BankApp {
	private static final Logger s_logger = Logger.getLogger(BankApp.class.getCanonicalName());
	
	private static final List<Entry<String, WebAction>> PARAMETERIZED_PAGES = new ArrayList<>();
	private static final List<Entry<String, WebAction>> EXACT_PAGES = new ArrayList<>();
	static {
		PARAMETERIZED_PAGES.add(new AbstractMap.SimpleImmutableEntry<String, WebAction>("/getuserinfo", new GetUserInfo()));
		EXACT_PAGES.add(new AbstractMap.SimpleImmutableEntry<String, WebAction>("/", new ChooseUserName()));
	}

	public static void main(String[] args) {
		final BankApp server = new BankApp();
		server.serverLoop();

	}
	
	private final Client client = new Client();
	
	private void serverLoop() {
		try (SuperSimpleWebServer server = new SuperSimpleWebServer(9797, s_logger)) {
			while (true) {
				try (SuperSimpleWebServer.Request request = server.waitForRequest()) {
					handleRequest(request);
				}
			}
		}
		catch (IOException ex) {
			s_logger.log(Level.SEVERE, "IOException " + ex.getMessage());
			return;
		}
	}
	
	private void handleRequest(final Request request) {
		try {
			try {
				final String page = resolvePage(request);
				request.getWriter(Status.OK).write(page);
			}
			catch (PageNotFoundException ex) {
				request.getWriter(Status.NOT_FOUND).write("<H1>Page not found</H1>" + ex.getUri());
			}
		} 
		catch (IOException e) {
			s_logger.log(Level.WARNING, e.getMessage());
		}
	}
	
	private String resolvePage(final Request request) throws PageNotFoundException {
		final String untrust_uri = request.getUri();

		for (Entry<String, WebAction> endPointMapping : EXACT_PAGES) {
			if (untrust_uri.equals(endPointMapping.getKey())) {
				s_logger.log(Level.FINE, "Resolved exactly " + untrust_uri + " to page " + endPointMapping.getKey());
				return endPointMapping.getValue().doAction(request, "", client);
			}
		}
		
		for (Entry<String, WebAction> endPointMapping : PARAMETERIZED_PAGES) {
			if (untrust_uri.startsWith(endPointMapping.getKey())) {
				final String untrust_remainingUriParams = untrust_uri.substring(endPointMapping.getKey().length());
				s_logger.log(Level.FINE, "Resolved parameterized " + untrust_uri + " to page " + endPointMapping.getKey());
				return endPointMapping.getValue().doAction(request, untrust_remainingUriParams, client);
			}
		}
		
		throw new PageNotFoundException(untrust_uri);
	}

}
