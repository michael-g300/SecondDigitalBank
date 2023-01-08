package useraction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import users.Client;

public class GetUserInfoAction implements userAction {
	private static final Logger s_logger = Logger.getLogger(GetUserInfoAction.class.getCanonicalName());
	
	private static final String KEY_ACCOUNT_NUM = "accountnum";
	private static final String KEY_ACCOUNT_PSW = "password";
	private static final String DATABSE_URL = "jdbc:mysql://localhost/sdb";
	private static final String DATABASE_USERNAME = "root";
	private static final String DATABASE_PASSWORD_LOCATION = "C:\\Users\\Michael\\Desktop\\Java Course\\SQL_server_password.txt";
	private final String m_password = getDBPassword(DATABASE_PASSWORD_LOCATION);

	@Override
	public Client doAction(Client client, Map<String, Object> params) {
		String accountNumString = params.get(KEY_ACCOUNT_NUM).toString();
		accountNumString = accountNumString.replace("[", "");
		accountNumString = accountNumString.replace("]", "");
		int accountNum = Integer.valueOf(accountNumString);
		s_logger.log(Level.INFO, "account number is: " + accountNum);
		Client currentClient;
		try {
			final Connection conn = DriverManager.getConnection(DATABSE_URL, DATABASE_USERNAME, this.m_password);
			currentClient = new Client(accountNum, conn);
			s_logger.log(Level.INFO, "creating user connection - successful");
			return currentClient;
		} 
		catch (SQLException e) {
			s_logger.log(Level.INFO, "creating user connection - UNsuccessful");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> validate(List<String> loginInfo) {
		final Map<String, Object> params = new HashMap<>();
		if (userValidation(loginInfo)) {
			params.put(KEY_ACCOUNT_NUM, loginInfo.get(0));
			params.put(KEY_ACCOUNT_PSW, loginInfo.get(1));
			return params;
		}
		return params;
	}
	
	private final String getDBPassword(final String passwordLocation) {
		Path filePath = Paths.get(DATABASE_PASSWORD_LOCATION);
		InputStream fileContent;
		try {
			fileContent = Files.newInputStream(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent));
			return reader.readLine();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean userValidation(final List<String> loginInfo) {
		final String accountNum = loginInfo.get(0);
		s_logger.log(Level.INFO, "account number: " + loginInfo.get(0));
		final String psw = loginInfo.get(1);
		s_logger.log(Level.INFO, "password: " + loginInfo.get(1));
		int contactID = -1;
		try (final Connection conn = DriverManager.getConnection(DATABSE_URL, DATABASE_USERNAME, this.m_password)) {
			try (final PreparedStatement stmt = conn.prepareStatement("select * from accounts where AccountNumber = ?;")) {
				stmt.setInt(1, Integer.valueOf(accountNum));
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						contactID = rs.getInt("ContactID");
						s_logger.log(Level.INFO, "conactdID: " + String.valueOf(contactID));
					}
				}
			}
			try (final PreparedStatement stmt = conn.prepareStatement("select * from logins where ContactID = ? and UserPassword = ?;")) {
				stmt.setInt(1, contactID);
				stmt.setString(2, psw);
				try (ResultSet rs = stmt.executeQuery()) {
					s_logger.log(Level.INFO, stmt.toString());
					while(rs.next()) {
						return true;
					}
				}
			}
		} 
		catch (SQLException e) {
			throw new IllegalArgumentException();
		}
		return false;
	}

}
