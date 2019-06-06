package SimpleEditor;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * 此类用于展示全局UI
 *
 * @author pj
 */


public class UI extends JFrame implements ActionListener {
    //main page
    private final JTextArea textArea = new JTextArea("", 0, 0);
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu file = new JMenu("File"),
            edit = new JMenu("Edit"),
            search = new JMenu("Search"),
            setting = new JMenu("Setting"),
            about = new JMenu("About");
    private final JMenuItem newFile, openFile, saveFile, close, cut, copy, paste, clearFile, selectAll, quickFind,
            aboutMe, aboutSoftware, wordWrap,format;
    private final JToolBar toolBar= new JToolBar();
    private final JButton newButton, openButton, saveButton, clearButton, quickButton, aboutMeButton, aboutButton, closeButton, boldButton, italicButton,undoButton,redoButton,formatButton;
    private final Action selectAllAction;

    //icons
    private final ImageIcon boldIcon = new ImageIcon("icons/bold.png");
    private final ImageIcon italicIcon = new ImageIcon("icons/italic.png");

    // setup icons - File Menu
    private final ImageIcon newIcon = new ImageIcon("icons/new.png");
    private final ImageIcon openIcon = new ImageIcon("icons/open.png");
    private final ImageIcon saveIcon = new ImageIcon("icons/save.png");
    private final ImageIcon closeIcon = new ImageIcon("icons/close.png");

    // setup icons - Edit Menu
    private final ImageIcon clearIcon = new ImageIcon("icons/clear.png");
    private final ImageIcon cutIcon = new ImageIcon("icons/cut.png");
    private final ImageIcon copyIcon = new ImageIcon("icons/copy.png");
    private final ImageIcon pasteIcon = new ImageIcon("icons/paste.png");
    private final ImageIcon selectAllIcon = new ImageIcon("icons/selectall.png");
    private final ImageIcon wordwrapIcon = new ImageIcon("icons/wordwrap.png");
    private final ImageIcon undoIcon = new ImageIcon("icons/undo.png");
    private final ImageIcon redoIcon = new ImageIcon("icons/redo.png");
    private final ImageIcon formatIcon = new ImageIcon("icons/format.png");

    // setup icons - Search Menu
    private final ImageIcon searchIcon = new ImageIcon("icons/search.png");

    // setup icons - Help Menu
    private final ImageIcon aboutMeIcon = new ImageIcon("icons/about_me.png");
    private final ImageIcon aboutIcon = new ImageIcon("icons/about.png");

    //more function support
    private SupportedKeywords supportedKeywords = new SupportedKeywords();
    private HighLight highLight = new HighLight(Color.GRAY);
    private AutoComplete autocomplete;
    private boolean hasListener = false;
    private boolean canEdit = false;

    public UI() throws HeadlessException {
        setTitle("SimpleEditor");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        /* page main consist */

        // menus
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(search);
        menuBar.add(setting);
        menuBar.add(about);
        setJMenuBar(menuBar);

        //init textarea
        textArea.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        textArea.setTabSize(2);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        LineNumber lineNumber = new LineNumber();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setRowHeaderView(lineNumber);
        getContentPane().setLayout(new BorderLayout());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane);
        getContentPane().add(panel);

        //tools
        newFile = new JMenuItem("New", newIcon);
        openFile = new JMenuItem("Open", openIcon);
        saveFile = new JMenuItem("Save", saveIcon);
        close = new JMenuItem("Quit", closeIcon);
        clearFile = new JMenuItem("Clear", clearIcon);
        quickFind = new JMenuItem("Quick", searchIcon);
        aboutMe = new JMenuItem("About Me", aboutMeIcon);
        aboutSoftware = new JMenuItem("About Software", aboutIcon);
        format = new JMenuItem("Format",formatIcon);

        //tools bar
        this.add(toolBar, BorderLayout.NORTH);
        // used to create space between button groups
        newButton = new JButton(newIcon);
        newButton.setToolTipText("New");
        newButton.addActionListener(this);
        toolBar.add(newButton);
        toolBar.addSeparator();

        openButton = new JButton(openIcon);
        openButton.setToolTipText("Open");
        openButton.addActionListener(this);
        toolBar.add(openButton);
        toolBar.addSeparator();

        saveButton = new JButton(saveIcon);
        saveButton.setToolTipText("Save");
        saveButton.addActionListener(this);
        toolBar.add(saveButton);
        toolBar.addSeparator();

        clearButton = new JButton(clearIcon);
        clearButton.setToolTipText("Clear All");
        clearButton.addActionListener(this);
        toolBar.add(clearButton);
        toolBar.addSeparator();

        formatButton = new JButton(formatIcon);
        formatButton.setToolTipText("Format");
        formatButton.addActionListener(this);
        toolBar.add(formatButton);
        toolBar.addSeparator();

        quickButton = new JButton(searchIcon);
        quickButton.setToolTipText("Quick Search");
        quickButton.addActionListener(this);
        toolBar.add(quickButton);
        toolBar.addSeparator();

        aboutMeButton = new JButton(aboutMeIcon);
        aboutMeButton.setToolTipText("About Me");
        aboutMeButton.addActionListener(this);
        toolBar.add(aboutMeButton);
        toolBar.addSeparator();

        aboutButton = new JButton(aboutIcon);
        aboutButton.setToolTipText("About NotePad PH");
        aboutButton.addActionListener(this);
        toolBar.add(aboutButton);
        toolBar.addSeparator();

        closeButton = new JButton(closeIcon);
        closeButton.setToolTipText("Quit");
        closeButton.addActionListener(this);
        toolBar.add(closeButton);
        toolBar.addSeparator();

        boldButton = new JButton(boldIcon);
        boldButton.setToolTipText("Bold");
        boldButton.addActionListener(this);
        toolBar.add(boldButton);
        toolBar.addSeparator();

        italicButton = new JButton(italicIcon);
        italicButton.setToolTipText("Italic");
        italicButton.addActionListener(this);
        toolBar.add(italicButton);
        //toolBar.addSeparator();



        /* actions set */
        //file part
        // New File
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        newFile.addActionListener(this);  // Adding an action listener (so we know when it's been clicked).
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK)); // Set a keyboard shortcut
        file.add(newFile); // Adding the file menu

        // Open File
        openFile.addActionListener(this);
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        file.add(openFile);

        // Save File
        saveFile.addActionListener(this);
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        file.add(saveFile);

        // Close File
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        close.addActionListener(this);
        file.add(close);

        // edit part
        // Select All Text
        selectAllAction = new SelectAllAction("Select All", clearIcon, "Select all text", new Integer(KeyEvent.VK_A),
                textArea);
        selectAll = new JMenuItem(selectAllAction);
        selectAll.setText("Select All");
        selectAll.setIcon(selectAllIcon);
        selectAll.setToolTipText("Select All");
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        edit.add(selectAll);


        // Format Text
        format.addActionListener(this);
        format.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_MASK));
        edit.add(format);

        // Clear File (Code)
        clearFile.addActionListener(this);
        clearFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK));
        edit.add(clearFile);

        // Cut Text
        cut = new JMenuItem(new DefaultEditorKit.CutAction());
        cut.setText("Cut");
        cut.setIcon(cutIcon);
        cut.setToolTipText("Cut");
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        edit.add(cut);

        // WordWrap
        wordWrap = new JMenuItem();
        wordWrap.setText("Word Wrap");
        wordWrap.setIcon(wordwrapIcon);
        wordWrap.setToolTipText("Word Wrap");

        //Short cut key or key stroke
        wordWrap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        edit.add(wordWrap);

        // Copy Text
        copy = new JMenuItem(new DefaultEditorKit.CopyAction());
        copy.setText("Copy");
        copy.setIcon(copyIcon);
        copy.setToolTipText("Copy");
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        edit.add(copy);

        // Paste Text
        paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        paste.setIcon(pasteIcon);
        paste.setToolTipText("Paste");
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        edit.add(paste);

        // search part
        // Find Word
        quickFind.addActionListener(this);
        quickFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        search.add(quickFind);

        // setting part



        // about part
        // About Me
        aboutMe.addActionListener(this);
        aboutMe.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        about.add(aboutMe);

        // About Software
        aboutSoftware.addActionListener(this);
        aboutSoftware.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        about.add(aboutSoftware);



        /* support set */
        //listeners for support
        //highlight
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                canEdit = true;
                highLight.paint(textArea, supportedKeywords.getCppKeywords());
                highLight.paint(textArea, supportedKeywords.getJavaKeywords());
            }
        });
        //line wrap
        wordWrap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                // If wrapping is false then after clicking on menuitem the word wrapping will be enabled
                if (textArea.getLineWrap() == false) {
                    /* Setting word wrapping to true */
                    textArea.setLineWrap(true);
                } else {
                    // else  if wrapping is true then after clicking on menuitem the word wrapping will be disabled
                    /* Setting word wrapping to false */
                    textArea.setLineWrap(false);
                }
            }
        });
        //undo part
        Undo.undoManuInit(edit,undoIcon,redoIcon);
        undoButton = new JButton(undoIcon);
        undoButton.setToolTipText("Undo");
        redoButton = new JButton(redoIcon);
        redoButton.setToolTipText("Redo");
        Undo.UndoButtonInit(undoButton,redoButton,textArea);





    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newFile || e.getSource() == newButton) newFileFunction();
        else if (e.getSource() == openFile || e.getSource() == openButton) openFileFunction();
        else if (e.getSource() == saveFile || e.getSource() == saveButton) saveFile();
        else if (e.getSource() == close || e.getSource() == closeButton) closeFunction();
        else if (e.getSource() == boldButton) boldFontFunction();
        else if (e.getSource() == italicButton) italicFontFunction();
        else if (e.getSource() == clearFile || e.getSource() == clearButton) clearFileFunction();
        else if (e.getSource() == quickFind || e.getSource() == quickButton) new FindWord(textArea);
        else if (e.getSource() == format || e.getSource() == formatButton)Format.format(textArea);
        //else if (e.getSource() == aboutMe || e.getSource() == aboutMeButton) new About(this).me();
        //else if (e.getSource() == aboutSoftware || e.getSource() == aboutButton) new About(this).software();

    }

    private void closeFunction() {
        if (canEdit) {
            Object[] options = {"Save and exit", "No Save and exit", "Return"};
            int n = JOptionPane.showOptionDialog(this, "Do you want to save the file ?", "Question",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
            if (n == 0) {// save and exit
                saveFile();
                this.dispose();// dispose all resources and close the application
            } else if (n == 1) {// no save and exit
                this.dispose();// dispose all resources and close the application
            }
        } else {
            this.dispose();// dispose all resources and close the application
        }
    }

    private void newFileFunction() {
        if (canEdit) {
            Object[] options = {"Save", "No Save", "Return"};
            int n = JOptionPane.showOptionDialog(this, "Do you want to save the file at first ?", "Question",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
            if (n == 0) {// save
                saveFile();
                canEdit = false;
            } else if (n == 1) {
                canEdit = false;
                ClearAll.clear(textArea);
            }
        } else {
            ClearAll.clear(textArea);
        }
    }

    private void boldFontFunction() {
        if (textArea.getFont().getStyle() == Font.BOLD) {
            textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN));
        } else {
            textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));
        }
    }

    private void italicFontFunction() {
        if (textArea.getFont().getStyle() == Font.ITALIC) {
            textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN));
        } else {
            textArea.setFont(textArea.getFont().deriveFont(Font.ITALIC));
        }
    }

    private void clearFileFunction() {
        Object[] options = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(this, "Are you sure to clear the text Area ?", "Question",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (n == 0) {// clear
            ClearAll.clear(textArea);
        }
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    private void openFileFunction() {
        JFileChooser open = new JFileChooser(); // open up a file chooser (a dialog for the user to  browse files to open)
        int option = open.showOpenDialog(this); // get the option that the user selected (approve or cancel)
        if (option == JFileChooser.APPROVE_OPTION) {
            ClearAll.clear(textArea); // clear the TextArea before applying the file contents
            try {
                File openFile = open.getSelectedFile();
                setTitle(openFile.getName() + " | 打开文件" );
                Scanner scan = new Scanner(new FileReader(openFile.getPath()));
                while (scan.hasNext()) {
                    textArea.append(scan.nextLine() + "\n");
                }

                //enableAutoComplete(openFile);
            } catch (Exception ex) { // catch any exceptions, and...
                // ...write to the debug console
                System.err.println(ex.getMessage());
            }
        }

    }


    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (canEdit) {
                Object[] options = {"Save and exit", "No Save and exit", "Return"};
                int n = JOptionPane.showOptionDialog(this, "Do you want to save the file ?", "Question",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                if (n == 0) {// save and exit
                    saveFile();
                    this.dispose();// dispose all resources and close the application
                } else if (n == 1) {// no save and exit
                    this.dispose();// dispose all resources and close the application
                }
            } else {
                System.exit(99);
            }
        }
    }


    class SelectAllAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public SelectAllAction(String text, ImageIcon icon, String desc, Integer mnemonic, final JTextArea textArea) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public void actionPerformed(ActionEvent e) {
            textArea.selectAll();
        }
    }


    private void saveFile() {
        // Open a file chooser
        JFileChooser fileChoose = new JFileChooser();
        // Open the file, only this time we call
        int option = fileChoose.showSaveDialog(this);

        /*
         * ShowSaveDialog instead of showOpenDialog if the user clicked OK
         * (and not cancel)
         */
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                File openFile = fileChoose.getSelectedFile();
                setTitle(openFile.getName() + " | 文件保存");

                BufferedWriter out = new BufferedWriter(new FileWriter(openFile.getPath()));
                out.write(textArea.getText());
                out.close();

                //enableAutoComplete(openFile);
                canEdit = false;
            } catch (Exception ex) { // again, catch any exceptions and...
                // ...write to the debug console
                System.err.println(ex.getMessage());
            }
        }
    }
}