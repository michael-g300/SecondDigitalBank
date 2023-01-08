package users;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientTest {
	private static final String DATABSE_URL = "jdbc:mysql://localhost/sdb";
	private static final String DATABASE_USERNAME = "root";
	private static final String DATABASE_PASSWORD = "cOmp@2023";

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testClient() {
		try (final Connection conn = DriverManager.getConnection(DATABSE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
			final Client client = new Client(1000013, conn);
			assertEquals(1000007, client.getaccountID());
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}	
	}
	
	@Test
	void testTransMoneyIn() {
		try (final Connection conn = DriverManager.getConnection(DATABSE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
			final Client client = new Client(1000013, conn);
			assertEquals(client.getBalance() + 500, client.TransMoneyIn(500, 1));
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}
	}
	
	@Test
	void testTransMoneyOut() {
		try (final Connection conn = DriverManager.getConnection(DATABSE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
			final Client client = new Client(1000013, conn);
			assertEquals(client.getBalance() - 50, client.TransMoneyOut(50, 1));
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}
	}

}
