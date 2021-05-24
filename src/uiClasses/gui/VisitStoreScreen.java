package uiClasses.gui;

import java.awt.EventQueue;  


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Button;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;

import coreClasses.*;
import uiClasses.GameUi;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JTable;
import javax.swing.JScrollBar;

public class VisitStoreScreen extends Screen {
	
	// Swing objects
	private JTextField numItemsTextField;
	
	private JTextPane receiptTextPane;
	private JLabel itemsJTableLabel;
	private JLabel balanceJLabel;
	private JLabel enterIntegerJLabel;
	
	private JButton buySellItemsButton;
	
	private JPanel mainStoreOptionsPanel;
	private JPanel buySellPanel;
	private JPanel tablePanel;
	
	private JLabel howManyItemsLabel;
	
	private String chosenItemName = "";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameEnvironment ge = new GameEnvironment(null, null, null, null, null);
					VisitStoreScreen window = new VisitStoreScreen(ge);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public VisitStoreScreen(GameEnvironment gameEnvironment) {
		super("Visit Store Screen ", gameEnvironment);
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	@Override
	protected void initialize() {
		frame.setBounds(100, 100, 1100, 800);
		
		// Initialize all labels and buttons that aren't in panels
		
		// Label for welcoming user to store
		JLabel welcomeLabel = new JLabel(String.format("Hello %s, welcome to the %s store", game.getPlayer().getName(), game.getCurrentIsland().getIslandStore().getName()));
		welcomeLabel.setBounds(301, 23, 555, 14);
		frame.getContentPane().add(welcomeLabel);
		
		// need to have a listener here, because cash could change!
//		this.balanceJLabel = new JLabel("Your current balance is <player.getBalance()>\r\n");
		this.balanceJLabel = new JLabel(String.format("Your current balance is %s", game.getPlayer().getMoneyBalance()));
		balanceJLabel.setBounds(34, 89, 265, 23);
		updatePlayerBalance();
		frame.getContentPane().add(balanceJLabel);
		
		// Button for going back
		JButton goBackButton = new JButton("Go Back");
		goBackButton.setBounds(949, 716, 118, 23);
		goBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Create a new coreOptions screen, and delete this current screen
				// If we want to use a parent screen in the future, ie having two ways to get into
				// The visit store screen, we will need to change the bellow code
				CoreOptionsScreen coreOptionsScreen = new CoreOptionsScreen(game);
				coreOptionsScreen.show();
				quit();
			}
		});
		frame.getContentPane().add(goBackButton);
		
		
		// Initialize main store options and buying and selling options
		initializeMainStoreOptions();
		initializeBuySellOptions();
		initializeTablePanel();
	}

	/** Initialize the panel containing all the options that a user has to interact with a store
	 * 
	 */
	private void initializeMainStoreOptions() {
		/* Panel containing core options with interacting with a store
		 * Ie, buy items, sell items, previously bought items
		 */
		// Create the Panel
		this.mainStoreOptionsPanel = new JPanel();
		mainStoreOptionsPanel.setBounds(35, 124, 504, 352);
		frame.getContentPane().add(mainStoreOptionsPanel);
		mainStoreOptionsPanel.setLayout(null);
		
		// Button to view Items that are for sale
		JButton viewItemsForSaleButton = new JButton("View Items that the store sells");
		viewItemsForSaleButton.setBounds(12, 12, 475, 100);
		mainStoreOptionsPanel.add(viewItemsForSaleButton);
		
		// Button to view Items that a store buys
		JButton viewItemsStoreBuysButton = new JButton("View Items that the store buys");
		viewItemsStoreBuysButton.setBounds(12, 124, 475, 100);
		mainStoreOptionsPanel.add(viewItemsStoreBuysButton);
		
		// Button to view previously bought items
		JButton viewPreviousItemsButton = new JButton("View previously bought Items");
		viewPreviousItemsButton.setBounds(12, 236, 475, 100);
		mainStoreOptionsPanel.add(viewPreviousItemsButton);
		
		// Add Action listeners to all the buttons above, this guides the flow of this screen
		viewPreviousItemsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewPrevItems();
			}
		});
		viewItemsStoreBuysButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buySellStart("buy");
			}
		});
		viewItemsForSaleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buySellStart("sell");
			}
		});
	}
	
	/** Method to initialize the panel that contains all the options for buying and selling items.
	 * Panel is set to invisible until a user chooses an Item from the JTable
	 */
	public void initializeBuySellOptions() {
		
		// Create the panel holding everything necessary
		this.buySellPanel = new JPanel();
		buySellPanel.setBounds(290, 507, 507, 242);
		frame.getContentPane().add(buySellPanel);
		buySellPanel.setLayout(null);
		
		// Set Panel to invisble until a user selects buy or sell items in the main options
		buySellPanel.setVisible(false);
		
		
		//TODO make colour red
		// Label to ask user how many items they want to buy
		this.howManyItemsLabel = new JLabel();
		howManyItemsLabel.setBounds(12, 12, 393, 35);
		buySellPanel.add(howManyItemsLabel);
		
		/* Label to tell user to enter an integer, is only visible if they
		 * input an invalid integer in numItemsTextField
		 */
		this.enterIntegerJLabel = new JLabel("Please enter an integer above 1!");
		enterIntegerJLabel.setForeground(Color.RED);
		enterIntegerJLabel.setBounds(12, 53, 174, 15);
		buySellPanel.add(enterIntegerJLabel);
		enterIntegerJLabel.setVisible(false);
		
		// TextField for inputting number of items that you want to buy
		this.numItemsTextField = new JTextField();
		numItemsTextField.setBounds(417, 20, 46, 20);
		buySellPanel.add(numItemsTextField);
		numItemsTextField.setColumns(10);
		
		/* Add a listener to numItemsTextField to check if the 
		 * inputed integer is valid. Calls another method to handle
		 * the checking itself
		 */
		numItemsTextField.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				handleNumItems(numItemsTextField.getText());
			  }
			  public void removeUpdate(DocumentEvent e) {
				handleNumItems(numItemsTextField.getText());
			  }
			  public void insertUpdate(DocumentEvent e) {
				handleNumItems(numItemsTextField.getText());
			  }
		});
		
		// Lable to contain the receipt a transaction with a store
		this.receiptTextPane = new JTextPane();
		receiptTextPane.setBounds(0, 85, 250, 115);
		buySellPanel.add(receiptTextPane);
		
		/* Button to press to buy or sell Items.
		 * Default is set to disabled until a user inputs a valid integer
		 * for the number of Items that they want to buy/sell
		 */
		this.buySellItemsButton = new JButton();
		buySellItemsButton.setBounds(265, 85, 250, 115);
		buySellPanel.add(buySellItemsButton);
		buySellItemsButton.setEnabled(false);
	}
	
	/** Initialize the panel holding all the items that are being displayed to a user
	 * 
	 */
	private void initializeTablePanel() {
		this.tablePanel = new JPanel();
		tablePanel.setBounds(562, 124, 504, 500);
		frame.getContentPane().add(tablePanel);
		tablePanel.setLayout(null);
		
		/* TODO put the label into the panel!!. find a way to drag the table down
		 * may be something to do with the ScrollPane that contains it
		 */
		
		// Label for j list. For buying.selling and showing previously bought items
		this.itemsJTableLabel = new JLabel();
		itemsJTableLabel.setBounds(673, 68, 248, 50);
		frame.getContentPane().add(itemsJTableLabel);
	}

	/** Create a table to display information about Items that a user has either bought,
	 * items that they can buy or sell. 
	 * 
	 * @param rows String[][] nested array containing all the data to be stored in the table
	 * @param columns String[] array containing the titles for each column in the table
	 * @param canSelect boolean value to ensure that a user can only select an item the table 
	 * has been initialized to sell or buy items, NOT to show previously bought items
	 * @return JTable that was created
	 */
	private JTable createTable(String [][] rows, String[] columns){
		// Remove content from panel from last use
		tablePanel.removeAll();
		
		// Create table
		JTable itemsTable = new JTable(rows, columns);
		itemsTable.setBounds(0, 100 , 447, 342);
		
		/* Create a ScrollPane that contains the Table, this is done to
		 * ensure column titles are shown 
		 */
		JScrollPane sp  = new JScrollPane(itemsTable);
		sp.setSize(447, 342);
		tablePanel.add(sp);
		
		// Return table that was created
		return itemsTable;
	}	
	
	/** Method to handle the displaying of previously bought items of a player 
	 * in a table opposite.
	 */
	private void viewPrevItems() {
		
		/* Adjust components that may have been adjusted from buying or selling items
		 * basically resets affected components to what they were before user selected anything
		 */
		buySellPanel.setVisible(false);
		itemsJTableLabel.setText("Previously bought items");
		howManyItemsLabel.setText("");
		
		// Get the purchased of a Player and create an array containing column titles
		String[][] purchasedItems = game.getPlayer().purchasedItemsToArray();
		if (purchasedItems == null){
			tablePanel.removeAll();
			JLabel noPrevItemsLabel = new JLabel("You haven't bought any items yet, you can buy some at any store!");
			noPrevItemsLabel.setBounds(33, 100, 200, 15);
			tablePanel.add(noPrevItemsLabel);
		}
		else {
			String[] columns = {"Name", "Purchase Price", "Consignment Price"};
			
			// Create the table containing all the previously bought items of a player
			createTable(purchasedItems, columns);
		}
	}
	
	/** Method to update the balance of a player that is shown at the top of the frame
	 * Is called every time Item(s) are sold or bought between a store and a player
	 */
	private void updatePlayerBalance() {
		//balanceJLabel.setText(String.format("Player has a balance of : %d", gameEnvironment.getPlayer().getMoneyBalance()));
		balanceJLabel.setText(String.format("Your current balance is %s", game.getPlayer().getMoneyBalance()));
	}
	
	/** Method to begin a transaction between a player and a store
	 * 
	 * @param operation String for what a user wants to do, either "buy" or "sell"
	 */
	private void buySellStart(String buyOrSell) {
		System.out.println();
		System.out.println("buyOrSell start :"+ buyOrSell);
		resetBuySellComponents();
		
		JTable itemsTable = setupBuySellTable(buyOrSell);
		
		// Create model and action listener for user to choose an Item from the table
		ListSelectionModel selectionModel = itemsTable.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
		    	// Item has been chosen, so update Label asking how many Items they would like to buy
		    	if (!e.getValueIsAdjusting()) {
			    	chosenItemName = chosenItemName(itemsTable);
			    	buySellStartHelper(buyOrSell, chosenItemName);
		    	}
		    }
		});
		
		// get the item name from the chosen item in the table
		buySellItemsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("buyOrSell: " + 	buyOrSell);
				buySellItemsButtonHandler(buyOrSell, chosenItemName);
			}
		});
	}
	
	private JTable setupBuySellTable(String buyOrSell) {
		/* Use html to display multi line button 
		 * from https://stackoverflow.com/questions/15746970/how-to-add-a-multi-line-text-to-a-jbutton-with-the-line-unknown-dynamically
		 */
		itemsJTableLabel.setText(String.format("<html>Items that the store %ss<br>Select one to continue</html>", buyOrSell));
		
		/* Get the catalogue for the particular operation that is being done with a store
		 * and column names to match!
		 */
		HashMap<String, HashMap<String, Integer>> catalogue =  game.getCurrentIsland().getIslandStore().getCatalogue(buyOrSell);
		String[][] catalogueNestedArray = game.getCurrentIsland().getIslandStore().catalogueToNestedArray(catalogue);
		String[] colNames = {"Name", "Price", "Space Taken", "Defense Boost"};
		
		// Create table holding all the information from catalogueArray
		return createTable(catalogueNestedArray, colNames);
	}
	
	/** Method to update the name of an item within the label that
	 * ask a user how many Items they would like to buy 
	 * 
	 * @param operation String for what a user wants to do with a store, either "buy" or "sell"
	 */
	private void buySellStartHelper(String buyOrSell, String itemName) {
		
		numItemsTextField.setText("");
	
		String buyOrSellAdj = "";
		
		System.out.println("buyOrSell in helper: " + 	buyOrSell);
		System.out.println("chosenItemName: " + chosenItemName);
		
		if (buyOrSell.equals("buy")) {
			// Switch the meaning of operation around, as within the store class, it is in the perspective of the store!
			// So it is now in the perspective of the player!
			buyOrSellAdj = "sell";
		}
		else {
			buyOrSellAdj = "buy";
		}
		
		String buyOrSellCap = buyOrSellAdj.substring(0, 1).toUpperCase() + buyOrSellAdj.substring(1);
		buySellItemsButton.setText(String.format("%s Items", buyOrSellCap));
		howManyItemsLabel.setText(String.format("How many %s would you like to %s>", itemName, buyOrSellAdj));
		numItemsTextField.setVisible(true);
	}
	
	/** Method to handle the choosing between either buying or selling from a store. 
	 * Rolls functionality that would otherwise be split into 2 methods for buying and 
	 * selling into one.
	 * 
	 * @param buyOrSell String for what a user wants to do with a store, either "buy" or "sell"
	 * @throws IllegalStateException if operation is neither "buy" or "sell"
	 */
	private void buySellItemsButtonHandler(String buyOrSell, String chosenItemName) throws IllegalStateException {
//		String chosenItemName = chosenItemName(itemsTable);
//		System.out.println("handler item name: " + chosenItemName);
		
		// receipt for a transaction, buying and selling
		
		if (chosenItemName.equals("")) {
			throw new IllegalArgumentException("Chosen item is empty!!");
		}
		String receipt = "";
		int numItems = Integer.parseInt(numItemsTextField.getText());
		
		System.out.println("Buy sell in handler :"+buyOrSell);
		// buyOrSell is in the perspective of the store
		if (buyOrSell.equals("buy")) {
			receipt = buyItemsFromPlayer(chosenItemName, numItems);
		}
		else if (buyOrSell.equals("sell")) {
			receipt = sellItemsToPlayer(chosenItemName, numItems);
		}
		else {
			throw new IllegalStateException("BUG invalid operation parameter!");
		}
		buySellFinish(receipt);
	}
	
	/** Method to handle the selling of Items from a Store to a Player
	 * 
	 * @param itemName String for the name of the item that is about to be sold to a player
	 * @param numItems Integer for the numbers of Items that a user wants to buy with the name 'itemName'
	 * @return String for the receipt of a transaction
	 */
	private String sellItemsToPlayer(String itemName, int numItems) {
		// Return Receipt from buying an item given by item name from the store
		return game.getCurrentIsland().getIslandStore().sellItemsToPlayer(game, itemName, numItems);
	}
	
	/** Method to handle the selling of Items from a Player to a Store
	 * 
	 * @param itemName String for the name of the item that is about to be sold to a Store
	 * @param numItems Integer for the numbers of Items that a user wants to sell with the name 'itemName'
	 * @return String for the receipt of a transaction
	 */
	private String buyItemsFromPlayer(String itemName, int numItems) {
		// Get receipt from selling an item to a Store
		return game.getCurrentIsland().getIslandStore().buyItemsFromPlayer(itemName, game.getPlayer(), numItems);
	}
	
	/** Method to handle the finishing of a transaction between a Player and a Store. 
	 * 
	 * @param receipt String for the receipt of a transaction between a Player and a Store
	 */
	private void buySellFinish(String receipt) {
		// Set the label for the receipt of transaction
		receiptTextPane.setText(receipt);
		// Update the balance of a player
		updatePlayerBalance();
	}
	
	/** Method to return the name of an Item has selected from the table.
	 * 
	 * @param itemsTable Table containing descriptions of the Items that a user can buy or sell
	 * @return String for the name of an Item that a user has chosen from the table
	 */
	private String chosenItemName(JTable itemsTable) {
		// Take selection from table and extract name of an Item
		int chosenRow = itemsTable.getSelectedRow();
		// Name of an Item is always stored at column 1 in a table
		String itemName = itemsTable.getModel().getValueAt(chosenRow, 0).toString();
		return itemName;
	}
	
	private void resetBuySellComponents() {
		/* Adjust components that may have been adjusted from viewing previously bought items
		 * basically resets affected components to what they were before user selected anything
		 */
		howManyItemsLabel.setText("");
		buySellPanel.setVisible(true);
		numItemsTextField.setText("");
		numItemsTextField.setVisible(false);
		buySellItemsButton.setEnabled(false);
		receiptTextPane.setText("");
		enterIntegerJLabel.setVisible(false);
		System.out.println("Number of action listeners for button: " +buySellItemsButton.getActionListeners().length);
		if (buySellItemsButton.getActionListeners().length > 0) {
			buySellItemsButton.removeActionListener(buySellItemsButton.getActionListeners()[0]);
		}
	}
	
	/** Method to handle the input of how many Items a user would like to buy
	 * Checks if a number is valid, and takes action to notify user that the number is invalid
	 * 
	 * @param num String representation of the number that a user has inputed that is to be checked. 
	 */
	private void handleNumItems(String num) {
		// Check if number is valid
		if (!checkValidInt(num)) {
			enterIntegerJLabel.setVisible(true);
			buySellItemsButton.setEnabled(false);
		}
		// If it isnt, then display the label describing the requirements of an inputed integer. 
		
		else {
			enterIntegerJLabel.setVisible(false);
			buySellItemsButton.setEnabled(true);
		}
	}
	
	
	/** Method to check if an inputed integer, (in the form of a String) from a player. 
	 * Helper method for handleNumItem(String)
	 * 
	 * @param num String representation of number from a player
	 * @return boolean value if the inputed number is valid
	 */
	private boolean checkValidInt(String num) {
		try {
			// Try parse String representation of the number
			Integer newNum = Integer.parseInt(num);
			if (newNum < 0 ) {
				return false;
			}
		}
		// This is thrown if the inputted String is not a valid Integer
		catch (NumberFormatException iie) {
			return false;
		}
		return true;
	
    }
}


