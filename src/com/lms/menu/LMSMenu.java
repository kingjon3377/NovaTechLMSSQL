package com.lms.menu;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.dao.AuthorDaoImpl;
import com.lms.dao.BookDaoImpl;
import com.lms.dao.BookLoansDaoImpl;
import com.lms.dao.BorrowerDaoImpl;
import com.lms.dao.CopiesDaoImpl;
import com.lms.dao.LibraryBranchDaoImpl;
import com.lms.dao.PublisherDaoImpl;
import com.lms.service.AdministratorServiceImpl;
import com.lms.service.BorrowerServiceImpl;
import com.lms.service.LibrarianServiceImpl;

/**
 * A text-based main-menu UI for the library management system.
 *
 * @author Jonathan Lovelace
 */
public final class LMSMenu {
	/**
	 * The helper for interacting with the user.
	 */
	private final MenuHelper mh;
	/**
	 * The means of getting input from the user.
	 */
	private final Scanner stdin;
	/**
	 * The means of sending output to the user.
	 */
	private final PrintWriter stdout;
	/**
	 * Our connection to the database.
	 */
	private final Connection connection;
	/**
	 * Logger for logging unexpected errors.
	 */
	private static final Logger LOGGER = Logger.getLogger(LMSMenu.class.getName());

	/**
	 * To initialize the menu, the caller must provide I/O streams and the database
	 * connection. Note that the caller is responsible for ensuring that all of
	 * these resources are properly closed after the menu goes out of scope.
	 *
	 * @param inStream     how to get input from the user
	 * @param outStream    how to send output to the user
	 * @param dbConnection how to connect to the database
	 */
	public LMSMenu(final Reader inStream, final Writer outStream,
			final Connection dbConnection) {
		stdin = new Scanner(inStream);
		stdout = new PrintWriter(outStream);
		mh = new MenuHelper(stdin, stdout);
		connection = dbConnection;
	}

	/**
	 * Show the topmost-level menu, solicit user input, and call the appropriate
	 * sub-menu.
	 */
	public void chooseRole() {
		mh.println("Welcome to the GCIT Library Management System!");
		mh.println();
		while (true) {
			mh.println("Please choose your role:");
			mh.println("1) Patron");
			mh.println("2) Librarian");
			mh.println("3) Administrator");
			switch (mh.getString("Role:")) {
			case "0":
				return;
			case "1":
				new BorrowerMenu(
					new BorrowerServiceImpl(new LibraryBranchDaoImpl(connection),
								new BookLoansDaoImpl(connection),
								new CopiesDaoImpl(connection),
								new BorrowerDaoImpl(connection),
								Clock.systemDefaultZone()),
						mh).menu();
				return;
			case "2":
				new LibrarianMenu(
						new LibrarianServiceImpl(new LibraryBranchDaoImpl(connection),
								new BookDaoImpl(connection),
								new CopiesDaoImpl(connection)),
						mh).menu();
				return;
			case "3":
				new AdministratorMenu(new AdministratorServiceImpl(
						new LibraryBranchDaoImpl(connection),
						new BookDaoImpl(connection), new AuthorDaoImpl(connection),
						new PublisherDaoImpl(connection),
						new BookLoansDaoImpl(connection),
						new BorrowerDaoImpl(connection)), mh).menu();
				return;
			default:
				mh.println("Please select role, or type 0 to quit");
				break;
			}
		}
	}
}