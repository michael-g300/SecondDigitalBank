package webaction;

import super_simple_web_server.SuperSimpleWebServer.Request;
import users.Client;

public class ChooseUserName implements WebAction {

	@Override
	public String doAction(Request request, String untrust_remainingUriParams, Client client) {
		return "<div><form action='/getuserinfo'>Enter account number: <input placeholder = 'account number' name='accountnum' required></div>"
				+ "Enter password: <input type = 'password' placeholder = 'password' name = 'psw' required>" 
				+ "<br><input type='submit' value='Submit'>" +"</form>";
	}

}
