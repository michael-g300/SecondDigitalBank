package webaction;

import super_simple_web_server.SuperSimpleWebServer.Request;
import users.Client;

public interface WebAction {
	String doAction(final Request request, final String untrust_remainingUriParams, final Client client);
}
