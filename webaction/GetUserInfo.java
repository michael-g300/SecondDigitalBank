package webaction;

import java.util.Arrays;
import java.util.Map;
import super_simple_web_server.SuperSimpleWebServer.Request;
import useraction.GetUserInfoAction;
import users.Client;
import java.util.logging.Level;
import java.util.logging.Logger;

import app.BasicParts;
import app.WrongLogin;

public class GetUserInfo implements WebAction {
	private static final Logger s_logger = Logger.getLogger(GetUserInfo.class.getCanonicalName());

	@Override
	public String doAction(Request request, String untrust_remainingUriParams, Client client) {
		String accountNum = request.getParams().get("accountnum");
		String password = request.getParams().get("psw");
		s_logger.log(Level.INFO, "account number is: " + accountNum);
		GetUserInfoAction act = new useraction.GetUserInfoAction();
		Map<String, Object> params = act.validate(Arrays.asList(accountNum, password));
		if (params.isEmpty()) {
			return WrongLogin.displayMessage();
		}
		client = act.doAction(client, params);
		
		return BasicParts.tasksHeader(client) + BasicParts.ClientInfoList(client);
	}

}
