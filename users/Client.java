package users;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
	private static final Logger s_logger = Logger.getLogger(Client.class.getCanonicalName());
	
	private final Connection m_connection;
	private final int m_contactID;
	private final int m_accountID;
	
	public Client() {
		this.m_connection = null;
		this.m_contactID = -1;
		this.m_accountID = -1;
	}
	
	public Client(final int accountNum, final Connection conn) {
		this.m_connection = conn;
		this.m_contactID = getContactID(accountNum);
		this.m_accountID = getAccountID(accountNum);
		s_logger.log(Level.INFO, "client connection successful!");
	}
	
	public int getaccountID() {
		return this.m_accountID;
	}
	
	public double TransMoneyIn(final double amount, final int employeeID) {
		try (final CallableStatement stmt = this.m_connection.prepareCall("call sp_transferMoneyIn(?, ?, ?);")) {
			s_logger.log(Level.WARNING, "connected for money transfer");
			stmt.setInt(1, this.m_accountID);
			stmt.setDouble(2, amount);
			stmt.setInt(3, employeeID);
			s_logger.log(Level.INFO, "assigned parameters : "+ stmt.toString());
			stmt.execute();
			s_logger.log(Level.INFO, "money transfer successful!");
		}
		catch (SQLException e) {
			s_logger.log(Level.WARNING, "money transfer unsuccessful. check accountID, amount to transfer, employeeID");
			e.printStackTrace();
		}
		return getBalance();
	}
	
	public double TransMoneyOut(final double amount, final int employeeID) {
		try (final CallableStatement stmt = this.m_connection.prepareCall("{call sp_transferMoneyOut(?, ?, ?)}")) {
			stmt.setInt(1, this.m_accountID);
			stmt.setDouble(2, amount);
			stmt.setInt(3, employeeID);
			stmt.executeQuery();
		}
		catch (SQLException e) {
			s_logger.log(Level.WARNING, "money transfer unsuccessful. check accountID, amount to transfer, employeeID");
			e.printStackTrace();
		}
		return getBalance();
	}
	
	private int getAccountID(final int accountNum) {
		try (final PreparedStatement stmt = this.m_connection.prepareStatement("select * from accounts where AccountNumber = ?;")) {
			stmt.setInt(1, accountNum);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
		            return rs.getInt("AccountID");
		           }
			}
		} 
		catch (SQLException e) {
			s_logger.log(Level.WARNING, "retrieving accountID failed. check account number");
			e.printStackTrace();
		}
		return -1;
	}
	
	private int getContactID(final int accountNum) {
		try (final PreparedStatement stmt = this.m_connection.prepareStatement("select * from accounts where AccountNumber = ?;")) {
			stmt.setInt(1, accountNum);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
		            return rs.getInt("ContactID");
		           }
			}
		} 
		catch (SQLException e) {
			s_logger.log(Level.WARNING, "retrieving contactID failed. check account number");
			e.printStackTrace();
		}
		return -1;
	}
	
	public double getBalance() {
		try (final PreparedStatement stmt = this.m_connection.prepareStatement("select * from accounts where AccountID = ?;")) {
			stmt.setInt(1, this.m_accountID);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
		            return rs.getInt("balance");
		           }
			}
		} 
		catch (SQLException e) {
			s_logger.log(Level.WARNING, "retrieving Balance failed. check account number and ID");
			e.printStackTrace();
		}
		return -1;
	}
	
	public ArrayList<String> getContactInfo() {
		ArrayList<String> contactInfo = new ArrayList<>();
		try (final PreparedStatement stmt = this.m_connection.prepareStatement("select * from contacts where ContactID = ?;")) {
			stmt.setInt(1, this.m_contactID);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					contactInfo.add(rs.getString("FirstName"));
					contactInfo.add(rs.getString("LastName"));
					contactInfo.add(rs.getString("City"));
					contactInfo.add(rs.getString("ZipCode"));
					contactInfo.add(rs.getString("Email"));
					contactInfo.add(rs.getString("Phone1"));
					contactInfo.add(rs.getString("Phone2"));
		           }
			}
		} 
		catch (SQLException e) {
			throw new  NoSuchElementException();
		}
		return contactInfo;
	}
	
	public ArrayList<String> getAccountInfo() {
		ArrayList<String> accountInfo = new ArrayList<>();
		try (final PreparedStatement stmt = this.m_connection.prepareStatement("select * from accounts where ContactID = ?;")) {
			stmt.setInt(1, this.m_contactID);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					accountInfo.add(String.valueOf(rs.getInt("AccountNumber")));
					accountInfo.add(String.valueOf(rs.getInt("AccountTypeID")));
					accountInfo.add(String.valueOf(rs.getInt("SecondContactID")));
					accountInfo.add(String.valueOf(rs.getInt("Balance")));
		           }
			}
		}
		catch (SQLException e) {
			throw new  NoSuchElementException();
		}
		String accountType = getAccountType(Integer.valueOf(accountInfo.get(1)));
		accountInfo.set(1, accountType);
		if (accountInfo.get(2) != "null") {
			accountInfo.set(2, getContactName(Integer.valueOf(accountInfo.get(2))));
		}
		for (String item : accountInfo) {
			s_logger.log(Level.INFO, item);
		}
		return accountInfo;
	}
	
	private String getContactName(final int contacdID) {
		String fullName = "";
		try (final PreparedStatement stmt = this.m_connection.prepareStatement("select * from contacts where ContactID = ?;")) {
			stmt.setInt(1, contacdID);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					fullName = rs.getString("FirstName") + " " + rs.getString("LastName");
		           }
			}
		} 
		catch (SQLException e) {
			throw new  NoSuchElementException();
		}
		return fullName;
	}
	
	public String getAccountType(final int accountTypeID) {
		String accountType = "";
		try (final PreparedStatement stmt = this.m_connection.prepareStatement("select * from account_types where AccountTypeID = ?;")) {
			stmt.setInt(1, accountTypeID);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					accountType = rs.getString("AccountTypeName");
		           }
			}
		} 
		catch (SQLException e) {
			throw new  NoSuchElementException();
		}
		return accountType;
	}
	
	public String getCustomerType(final int contactID) {
		String customerTypeName = "";
		int customerType = -1;
		try (final PreparedStatement stmt = this.m_connection.prepareStatement("select * from customers where AccountTypeID = ?;")) {
			stmt.setInt(1, contactID);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					customerType = rs.getInt("CustomerTypeID");
		           }
			}
		}
		catch (SQLException e) {
			throw new  NoSuchElementException();
		}
		try (final PreparedStatement stmt = this.m_connection.prepareStatement("select * from customer_types where CustomerTypeID = ?;")) {
			stmt.setInt(1, customerType);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					customerTypeName = rs.getString("CustomerTypeName");
		           }
			}
		}
		catch (SQLException e) {
			throw new  NoSuchElementException();
		}
		return customerTypeName;
	}

}
