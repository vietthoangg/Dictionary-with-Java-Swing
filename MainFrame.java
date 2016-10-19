import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.DefaultCaret;

import org.eclipse.wb.swing.FocusTraversalOnArray;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final static String frameName = "VAV Dictionary";
	
	private JPanel contentPane;

	//frameName
	private Dictionary evDict, veDict;
	private JTabbedPane dictionaryPane;
	private JPanel engVietPanel, vietEngPanel;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
					frame.setIconImage(new ImageIcon(".\\src\\icon\\dictionary-icon.png").getImage());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		
		super(frameName);
		
		//Import data for dictionaries
		evDict = new Dictionary("English - Vietnamese Dictionary", ".\\src\\data\\eng_viet.txt");
		veDict = new Dictionary("Vietnamese - English Dictionary", ".\\src\\data\\viet_eng.txt");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 615, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//Set location for window
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 599, 21);
		contentPane.add(menuBar);
		
		JMenu fileMenu = new JMenu("   File   ");
		menuBar.add(fileMenu);
		
		JMenuItem importMenuItem = new JMenuItem("Import...");
		importMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(evDict.isModified() || veDict.isModified()) {
					int importOption = JOptionPane.showConfirmDialog(null, "Dictionaries are modified. This will delete all current data.\nWould you like to continue?", frameName, JOptionPane.YES_NO_CANCEL_OPTION);
					if(importOption == JOptionPane.NO_OPTION || importOption == JOptionPane.CANCEL_OPTION) {
						return; //Cancel if user don't want to import
					}
				}
				evDict.importData();
				veDict.importData();
				//Reset modify condition
				evDict.setModify(false);
				veDict.setModify(false);
				JOptionPane.showMessageDialog(null, "Import data successfully!", frameName, JOptionPane.INFORMATION_MESSAGE);
			}
		});
		fileMenu.add(importMenuItem);
		
		JMenuItem exportMenuItem = new JMenuItem("Export...");
		exportMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				evDict.exportData();
				veDict.exportData();
				//Reset modify condition
				evDict.setModify(false);
				veDict.setModify(false);
				JOptionPane.showMessageDialog(null, "Export data successfully!", frameName, JOptionPane.INFORMATION_MESSAGE);
			}
		});
		fileMenu.add(exportMenuItem);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.LIGHT_GRAY);
		fileMenu.add(separator);
		
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		fileMenu.add(exitMenuItem);
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Check if two dictionaries are exported if they are modified
				if(evDict.isModified() || veDict.isModified()) {
					int exportOption = JOptionPane.showConfirmDialog(null, "Dictionaries are modified. Do you want to save?", frameName, JOptionPane.YES_NO_CANCEL_OPTION);
					
					if(exportOption == JOptionPane.YES_OPTION) {
						evDict.exportData();
						veDict.exportData();
						JOptionPane.showMessageDialog(null, "Export data successfully!", frameName, JOptionPane.INFORMATION_MESSAGE);
					}
				}
				System.exit(0);
			}
		});
		
		JMenu editMenu = new JMenu("   Edit   ");
		menuBar.add(editMenu);
		
		JMenu addNewWordsMenu = new JMenu("Add new words to   ");
		editMenu.add(addNewWordsMenu);
		
		JMenuItem addEngVietMenu = new JMenuItem("English - Vietnamese Dictionary");
		addEngVietMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AddWordFrame(evDict);
				evDict.setModify(true);
				
				
			}
		});
		addNewWordsMenu.add(addEngVietMenu);
		
		JMenuItem addVietEngMenu = new JMenuItem("Vietnamese - English Dictionary");
		addNewWordsMenu.add(addVietEngMenu);
		addVietEngMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AddWordFrame(veDict);
				veDict.setModify(true);
				
			}
		});
		
		JSeparator separatorEditMenu = new JSeparator();
		separatorEditMenu.setBackground(Color.LIGHT_GRAY);
		editMenu.add(separatorEditMenu);
		
		JMenu deleteWordsMenu = new JMenu("Delete words from    ");
		editMenu.add(deleteWordsMenu);
		
		JMenuItem deleteEngVietMenu = new JMenuItem("English - Vietnamese Dictionary");
		deleteWordsMenu.add(deleteEngVietMenu);
		deleteEngVietMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String word;
				 do {
					 if( (word = JOptionPane.showInputDialog(null, "Enter the word you want to delete: ", frameName, JOptionPane.QUESTION_MESSAGE)) != null ) {
						 word = word.trim().toLowerCase(); //normalize word
						 if(evDict.search(word) == null) { //Can not find word in dictionary
							 JOptionPane.showMessageDialog(null, "Can not find this word in " + evDict.getName(), frameName, JOptionPane.ERROR_MESSAGE);
						 } else {
							 break; //Find the word in dictionary
						 }
					 } else {
						 return; //If user click cancel button in input dialog
					 }
				} while(true);
				new DeleteWordFrame(evDict, word);
				evDict.setModify(true);
				
			}
		});
		
		JMenuItem deleteVietEngMenu = new JMenuItem("Vietnamese - English Dictionary");
		deleteWordsMenu.add(deleteVietEngMenu);
		deleteVietEngMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String word;
				 do {
					 if( (word = JOptionPane.showInputDialog("Enter the word you want to delete: ")) != null ) {
						 word = word.trim().toLowerCase(); //normalize word
						 if(veDict.search(word) == null) { //Can not find word in dictionary
							 JOptionPane.showMessageDialog(null, "Can not find this word in " + veDict.getName(), frameName, JOptionPane.ERROR_MESSAGE);
						 } else {
							 break; //Find the word in dictionary
						 }
					 } else {
						 return; //If user click cancel button in input dialog
					 }
				} while(true);
				new DeleteWordFrame(veDict, word);
				veDict.setModify(true);
				
			}
		});
		
		JMenu helpMenu = new JMenu("   Help   ");
		menuBar.add(helpMenu);
		
		JMenuItem aboutMenuItem = new JMenuItem("About " + frameName);
		helpMenu.add(aboutMenuItem);
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String aboutStr = frameName + " 2.0.0\nNguyen Viet Hoang"
						+ "\n\nInformation:\nDatabase: Free Vietnamese Dictionary Project\n\n   - " + evDict.getName() + ": " + evDict.size() + " words\n   - " + veDict.getName() + ": " + veDict.size() + " words\n\n";
				JOptionPane.showMessageDialog(null, aboutStr, frameName, JOptionPane.INFORMATION_MESSAGE);				
			}
		});
		
		dictionaryPane = new JTabbedPane(JTabbedPane.TOP);
		dictionaryPane.setBounds(10, 35, 579, 415);
		contentPane.add(dictionaryPane);
		
		//Eng - Viet Dictionary panel-------------------------------------
		engVietPanel = new JPanel();
		engVietPanel = new DictionaryPanel(evDict);
		dictionaryPane.addTab("English - Vietnamese", null, engVietPanel, null);
		
		//Viet - Eng Dictionary panel-------------------------------------
		vietEngPanel = new JPanel();
		vietEngPanel = new DictionaryPanel(veDict);
		dictionaryPane.addTab("Vietnamese - English", null, vietEngPanel, null);
		contentPane.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{dictionaryPane}));
		
		//Closing Window Listener
		//Check if two dictionaries are exported if they are modified
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(evDict.isModified() || veDict.isModified()) {
					int exportOption = JOptionPane.showConfirmDialog(null, "Dictionaries are modified. Do you want to save?", frameName, JOptionPane.YES_NO_CANCEL_OPTION);
					
					if(exportOption == JOptionPane.YES_OPTION) {
						evDict.exportData();
						veDict.exportData();
						JOptionPane.showMessageDialog(null, "Export data successfully!", frameName, JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		
	}
	
	/*
	 * Set up panel for each dictionary
	 */
	private class DictionaryPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		private Dictionary dict;
		private ArrayList<String> words;
		
		//Create ListModel 
		private DefaultListModel<String> createListModel() {
			DefaultListModel<String> model = new DefaultListModel<>();
			
			dict.update();
			//Add WordMap to model
			for(String word: words) {
				model.addElement(word);
			}
			return model;
		}
		
		//Search in word in wordList to suggest user
		//Binary search
		private int search(String str) {
			if(str.equals(words.get(0))) return 0; //Check the first word
			
			int start = 0;
			int end = words.size();
			while(start < end - 1) {
				int mid = (start + end) / 2;
				if(words.get(mid).compareToIgnoreCase(str) >= 0) {
					end = mid;
				} else {
					start = mid;
				}
			}
			if(end == words.size()) return -1; //Can not find the word
			return end;
		}

		public DictionaryPanel(Dictionary dict) {	
			this.dict = dict;
			this.words = dict.getWordList();
			
			//Display meaning of the word
			JEditorPane meaningEditorPane = new JEditorPane();
			meaningEditorPane.setContentType("text/html");
			meaningEditorPane.setEditable(false);
			meaningEditorPane.setBounds(1, 1, 6, 20);
			//Keep scroll on top
			DefaultCaret caret = (DefaultCaret) meaningEditorPane.getCaret();
			caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
			this.add(meaningEditorPane);
			
			JScrollPane meaningScrollPane = new JScrollPane(meaningEditorPane);
			meaningScrollPane.setBounds(210, 52, 354, 324);
			this.add(meaningScrollPane);
			
			//List of words in Dictionary
			JList<String> wordList = new JList<>(createListModel());
			wordList.setBounds(10, 56, 226, 194);
			wordList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			this.setLayout(null);
			
			JScrollPane scrollPaneList = new JScrollPane();
			scrollPaneList.setViewportView(wordList);
			scrollPaneList.setSize(190, 324);
			scrollPaneList.setLocation(10, 52);
			this.add(scrollPaneList);
			
			//Word to search
			JTextField textField = new JTextField();
			textField.setBorder(new MatteBorder(1, 0, 1, 1, Color.GRAY));
			textField.setBounds(34, 16, 425, 25);
			this.add(textField);
			
			//Search icon
			JLabel searchLabel = new JLabel("");
			searchLabel.setToolTipText("Search Word");
			searchLabel.setIcon(new ImageIcon(".\\src\\icon\\search-icon.png"));
			searchLabel.setBounds(10, 16, 25, 25);
			searchLabel.setBorder(new MatteBorder(1, 1, 1, 0, Color.GRAY));
			this.add(searchLabel);
			
			//Add new word button
			JButton addWordButton = new JButton("");
			addWordButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(meaningEditorPane.getText().contains("Not found!")) {
						new AddWordFrame(dict, textField.getText().trim().toLowerCase());
					} else {
						new AddWordFrame(dict);
					}
				}
			});
			addWordButton.setBounds(469, 16, 25, 25);
			addWordButton.setToolTipText("Add new word");
			addWordButton.setIcon(new ImageIcon(".\\src\\icon\\add-icon.png"));
			this.add(addWordButton);
			
			//Modify word button
			JButton modifyBtn = new JButton("");
			modifyBtn.setBounds(504, 16, 25, 25);
			modifyBtn.setToolTipText("Modify this word");
			modifyBtn.setIcon(new ImageIcon(".\\src\\icon\\modify-icon.png"));
			this.add(modifyBtn);
			modifyBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(wordList.getSelectedValue() == null) {
						JOptionPane.showMessageDialog(null, "Please choose a word to modify!", frameName, JOptionPane.ERROR_MESSAGE);
					} else {
						new ModifyWordFrame(dict, wordList.getSelectedValue());
					}
				}
			});
			
			//Delete word button
			JButton deleteWordButton = new JButton("");
			deleteWordButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(wordList.getSelectedValue() == null) {
						JOptionPane.showMessageDialog(null, "Please choose a word to delete!", frameName, JOptionPane.ERROR_MESSAGE);
					} else {
						new DeleteWordFrame(dict, wordList.getSelectedValue());
					}
				}
			});
			deleteWordButton.setBounds(539, 16, 25, 25);
			deleteWordButton.setToolTipText("Delete this word");
			deleteWordButton.setIcon(new ImageIcon(".\\src\\icon\\trash-icon.png"));
			this.add(deleteWordButton);
			
			//Event listener-----------------------------------------------------------
			
			wordList.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					String selectedWord = wordList.getSelectedValue();
					
					String result = dict.search(selectedWord);
					if(result != null) {
						meaningEditorPane.setText("<b>" + selectedWord + "</b><br/>" + dict.search(selectedWord));
					} else {
						meaningEditorPane.setText("<font color=\"red\">Not found!</font>");
					}
				}
			});
			
			textField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String typingWord = textField.getText().trim().toLowerCase();
					if(!typingWord.equals("")) {
						String result = dict.search(typingWord);
						if(result != null) {
							wordList.setSelectedValue(typingWord, true);
							wordList.requestFocus();
						} else {
							meaningEditorPane.setText("<font color=\"red\">Not found!</font>");						
						}
					}
				}
			});
			
			textField.addCaretListener(new CaretListener() {
				@Override
				public void caretUpdate(CaretEvent e) {
					String typingWord = textField.getText().trim().toLowerCase();
					if(!typingWord.equals("")) { //If textField is available
						wordList.setSelectedValue( words.get(search(typingWord)), true ); //Set list
					} else {
						wordList.setSelectedValue( words.get(0), true ); //Set the first word
					}
				}
			});
			
			/*
			 * 
			wordList.addMouseListener(new MouseAdapter() {		
				private String selectedWord;
				
				@Override
				public void mouseClicked(MouseEvent e) {
					selectedWord = wordList.getSelectedValue();
					
					String result = dict.search(selectedWord);
					if(result != null) {
						meaningEditorPane.setText("<b>" + selectedWord + "</b><br/>" + dict.search(selectedWord));
					} else {
						meaningEditorPane.setText("<font color=\"red\">Not found!</font>");
					}
					SwingUtilities.invokeLater(setTextField);	
				}
			
				//Fix bug: IllegalStateException when setText for TextField
				private Runnable setTextField = new Runnable() {
					@Override
					public void run() {
						textField.setText(selectedWord);
					}
				};
			});
			*/
			
		}
		
	}

	/*
	 * 
	 * Add new word to the Dictionary
	 */
	private class AddWordFrame extends JFrame {
		
		private static final long serialVersionUID = 1L;
		
		private JPanel contentPane;
		private Font font = new Font("Tahoma", Font.PLAIN, 14);
		
		private Dictionary dict;
		private String word;
		private StringBuilder meaningBuilder = new StringBuilder();
		
		public AddWordFrame(Dictionary dict,  String word) {
			super(frameName);
			this.dict = dict;
			this.word = word;
			
			newFrame();
		}
		
		public AddWordFrame(Dictionary dict) {
			super(frameName);
			this.dict = dict;
			newFrame();
		}
		
		public void newFrame() {			
			
			setVisible(true);
			setBounds(100, 100, 450, 410);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			//Set location for window
	        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
			
			JLabel mainLabel = new JLabel("Add new word to " + dict.getName());
			mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
			mainLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
			mainLabel.setBounds(10, 11, 414, 30);
			contentPane.add(mainLabel);
			
			JPanel panel = new JPanel();
			panel.setBounds(10, 52, 414, 260);
			contentPane.add(panel);
			panel.setLayout(null);
			
			//Labels
			JLabel wordLabel = new JLabel("Word: ");
			wordLabel.setFont(font);
			wordLabel.setBounds(10, 11, 95, 30);
			panel.add(wordLabel);
			
			JLabel pronunciationLabel = new JLabel("Pronunciation: ");
			pronunciationLabel.setFont(font);
			pronunciationLabel.setBounds(10, 52, 95, 30);
			panel.add(pronunciationLabel);
			
			JLabel categoryLabel = new JLabel("Word category: ");
			categoryLabel.setFont(font);
			categoryLabel.setBounds(10, 93, 105, 30);
			panel.add(categoryLabel);
			
			JLabel definitionLabel = new JLabel("Definition: ");
			definitionLabel.setFont(font);
			definitionLabel.setBounds(10, 134, 95, 30);
			panel.add(definitionLabel);
			
			JLabel exampleLabel = new JLabel("Example: ");
			exampleLabel.setFont(font);
			exampleLabel.setBounds(10, 175, 95, 30);
			panel.add(exampleLabel);
			
			JLabel explainExpLabel = new JLabel("Explain: ");
			explainExpLabel.setFont(font);
			explainExpLabel.setBounds(10, 216, 95, 32);
			panel.add(explainExpLabel);
			
			JTextField wordField = new JTextField();
			wordField.setBounds(109, 11, 295, 30);
			wordField.setFont(font);
			if(word != null) {
				wordField.setText(word);
			}
			panel.add(wordField);
			
			JTextField pronunciationField = new JTextField();
			pronunciationField.setBounds(109, 52, 295, 30);
			pronunciationField.setFont(font);
			panel.add(pronunciationField);
			
			JTextField categoryField = new JTextField();
			categoryField.setFont(font);
			categoryField.setBounds(109, 93, 295, 30);
			panel.add(categoryField);
			
			JTextField definitionField = new JTextField();
			definitionField.setBounds(109, 134, 295, 30);
			definitionField.setFont(font);
			panel.add(definitionField);
			
			JTextField exampleField = new JTextField();
			exampleField.setBounds(109, 175, 295, 30);
			exampleField.setFont(font);
			panel.add(exampleField);
			
			JTextField explainExpField = new JTextField();
			explainExpField.setFont(font);
			explainExpField.setBounds(109, 216, 295, 30);
			panel.add(explainExpField);
			
			JButton cancelBtn = new JButton("Cancel");
			cancelBtn.setBounds(335, 337, 89, 23);
			contentPane.add(cancelBtn);
			cancelBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose(); //Only close this frame
				}
			});
			
			//Add new word to this Dictionary
			JButton okBtn = new JButton("OK");
			okBtn.setBounds(236, 337, 89, 23);
			contentPane.add(okBtn);
			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//User must enter the word field
					if(wordField.getText().equals("")) {
						JOptionPane.showMessageDialog(null, "Please enter the word you want to add!", frameName, JOptionPane.ERROR_MESSAGE);
						return;
					}
					word = wordField.getText().trim().toLowerCase();
					//Pronunciation field
					String pron = pronunciationField.getText().trim();
					if(!pron.equals("")) {
						meaningBuilder.append(pron + "<br/>");
					}
					//Word category field
					String category = categoryField.getText().trim();
					if(!category.equals("")) {
						meaningBuilder.append("<br/><font color=\"blue\">&#9755" + category + "</font><br/>");
					}
					//User must enter the definition area
					if(definitionField.getText().equals("")) {
						JOptionPane.showMessageDialog(null, "Please enter the definition!", frameName, JOptionPane.ERROR_MESSAGE);
						return;
					} 
					//Ask to continue to ask if the word has already existed
					if(dict.search(word) != null) {
						int replaceOption = JOptionPane.showConfirmDialog(null, "This word has already existed. Do you want to replace it?");
						if(replaceOption == JOptionPane.NO_OPTION || replaceOption == JOptionPane.CANCEL_OPTION) {
							return;
						}
					} 
					meaningBuilder.append("<font color=\"red\">&#9656" + definitionField.getText().trim() + "</font><br/>");
					//Add examples(not necessary)
					String example = exampleField.getText().trim();
					if(!example.equals("")) {
						meaningBuilder.append("&#8901<b>" + example + "</b><br/>");
						
						String explainExp = explainExpField.getText().trim();
						if(!explainExp.equals("")) {
							meaningBuilder.append(": " + explainExp + "<br/>");
						}
					}					
					//Add new word to this dictionary
					dict.addNewWord(word, meaningBuilder.toString());
					dict.setModify(true);
					meaningBuilder.delete(0, meaningBuilder.length()); //Clear string builder
					JOptionPane.showMessageDialog(null, "Add new word successfully to " + dict.getName(), frameName, JOptionPane.INFORMATION_MESSAGE);
					//Clear all test fields after add new word successfully
					wordField.setText("");
					pronunciationField.setText("");
					categoryField.setText("");
					definitionField.setText("");
					exampleField.setText("");
					explainExpField.setText("");
				}
			});	
		}
		
	}

	/*
	 * Delete word from Dictionary
	 * 
	 */
	
	private class DeleteWordFrame extends JFrame {

		private static final long serialVersionUID = 1L;
		
		private JPanel contentPane;
		
		public DeleteWordFrame(Dictionary dict, String word) {
			super(frameName);
			
			setBounds(100, 100, 375, 360);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			setVisible(true);
			
			//Set location for window
	        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
			
			JLabel mainLabel = new JLabel("Delete this word from " + dict.getName());
			mainLabel.setHorizontalAlignment(JLabel.CENTER);
			mainLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
			mainLabel.setBounds(10, 11, 339, 37);
			contentPane.add(mainLabel);
			
			JEditorPane editorPane = new JEditorPane();
			editorPane.setBounds(0, 0, 106, 20);
			editorPane.setContentType("text/html");
			editorPane.setEditable(false);
			//Keep scroll on top
			DefaultCaret caret = (DefaultCaret) editorPane.getCaret();
			caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
			editorPane.setText("<b>" + word + "</b><br/>" + dict.search(word));
			contentPane.add(editorPane);
			
			JScrollPane scrollPane = new JScrollPane(editorPane);
			scrollPane.setBounds(10, 59, 339, 211);
			contentPane.add(scrollPane);
			
			JButton cancelBtn = new JButton("Cancel");
			cancelBtn.setBounds(260, 287, 89, 23);
			contentPane.add(cancelBtn);
			cancelBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			
			JButton okBtn = new JButton("OK");
			okBtn.setBounds(161, 287, 89, 23);
			contentPane.add(okBtn);
			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int deleteOption = JOptionPane.showConfirmDialog(null, "Do you really want to delete this word?");
					if(deleteOption == JOptionPane.YES_OPTION) {
						dict.remove(word);
						dict.setModify(true);
						JOptionPane.showMessageDialog(null, "Delete this word successfully from " + dict.getName(), frameName, JOptionPane.INFORMATION_MESSAGE);
					}
					//Cancel this frame
					dispose();
				}
			});
		}
		
	}
	
	/*
	 * Modify the chosen word
	 */
	
	private class ModifyWordFrame extends JFrame {
		
		private static final long serialVersionUID = 1L;
		
		private JPanel contentPane;
		
		public ModifyWordFrame(Dictionary dict, String word) {
			super(frameName);
			
			setBounds(100, 100, 375, 360);
			setVisible(true);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			//Set location for window
	        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
			
			JLabel mainLabel = new JLabel("Modify this word in " + dict.getName());
			mainLabel.setHorizontalAlignment(JLabel.CENTER);
			mainLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
			mainLabel.setBounds(10, 11, 339, 37);
			contentPane.add(mainLabel);
			
			JEditorPane editorPane = new JEditorPane();
			editorPane.setBounds(0, 0, 106, 20);
			editorPane.setContentType("text/html");
			editorPane.setEditable(true);
			editorPane.setText("<b>" + word + "</b><br/>" + dict.search(word));
			contentPane.add(editorPane);
			
			JScrollPane scrollPane = new JScrollPane(editorPane);
			scrollPane.setBounds(10, 59, 339, 211);
			contentPane.add(scrollPane);
			
			JButton cancelBtn = new JButton("Cancel");
			cancelBtn.setBounds(260, 287, 89, 23);
			contentPane.add(cancelBtn);
			cancelBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			
			JButton okBtn = new JButton("OK");
			okBtn.setBounds(161, 287, 89, 23);
			contentPane.add(okBtn);
			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int modifyOption = JOptionPane.showConfirmDialog(null, "Do you want to modify this word?");
					if(modifyOption == JOptionPane.YES_OPTION) {
						//Edit editorPane to get word
						//!!!Lose all html color font 
						String editor = Convert.toUnicode(editorPane);
						String userWord = editor.substring(editor.indexOf("<b>"));
						//Modify dictionary
						dict.remove(word);
						dict.addWord(userWord);
		
						dict.setModify(true);
						
						JOptionPane.showMessageDialog(null, "Modify word successfully in " + dict.getName(), frameName, JOptionPane.INFORMATION_MESSAGE);
						//Cancel this frame
						dispose();
					} else if(modifyOption == JOptionPane.NO_OPTION) {
						//Cancel this frame
						dispose();
					}	
				}
				
				
			});
		}
		
	}
}
