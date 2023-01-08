package app;

import java.util.ArrayList;
import users.Client;

public class BasicParts {
	
	public static String tasksHeader(final Client client) {
		return "<H1>Second Disgital Bank Inc.</H1><H2>" 
				+ client.getContactInfo().get(0) + " " + client.getContactInfo().get(1) + "</H2> ";
	}
		
	public static String ClientInfoList(final Client client) {
		final ArrayList<String> contactInfo = client.getContactInfo();
		final ArrayList<String> accountInfo = client.getAccountInfo();
		String page = "<ul>";
		page += "<div>";

		page += "<b>City of residance:</b> " + contactInfo.get(2) + " (<b>zip code </b>" + contactInfo.get(3) + ")<br>";
		page += "<b>Email address: </b>" + contactInfo.get(4) + "<br>";
		page += "<b>Phone number: </b>" + contactInfo.get(5) + (contactInfo.get(6) != null ? (" (" + contactInfo.get(6) + ")") : "") + "<br>";
		page += "</div><hr style=\"width:30%;text-align:left;margin-left:0\">";
		
		page += "<b>Account number: </b>" + accountInfo.get(0) + " (" + accountInfo.get(1) + " type)<br>";
		page += (accountInfo.get(2).length() > 0 ? ("<b>Partner for current account: </b>" + accountInfo.get(2) + "<br>") : "");
		page += "<b>Current account balance: </b>" + accountInfo.get(3) + "<br>";
		
		page += "<div>";
		page += "<a href='/'>" + "<img style='width: 100px; height: 100px;' src='https://st.depositphotos.com/1005920/2667/i/950/depositphotos_26678809-stock-photo-logout-icon.jpg'></a>";
		page += "</ul>";
		return page;
	}
}
