import java.awt.*;
import java.io.*;
import java.net.URI;

import javax.swing.event.*;
import javax.swing.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.event.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mpantazi
 */
public class gui extends javax.swing.JFrame {

    String home = new String(System.getProperty("user.home"));
    String lastPath = new String();
    File copyFile;
    boolean copyFlag = false;
    File cutFile;
    boolean cutFlag = false;
    boolean windows = false;
    boolean linux = false;
    JLabel clickedlabel = new JLabel(lastPath);
    List<String> result = new ArrayList<String>();
    String type;
    String key;
    String separator = new String(System.getProperty("file.separator"));
    String xmlPath = new String(home + separator + ".java-file-browser/properties.xml");
    String iconsPath = new String((System.getProperty("user.dir")) + separator + "icons");
    boolean homeRemoved = false;
    boolean opened = false;
    /**
     * Creates new form gui
     */
    public gui() {
    	String system = new String(System.getProperty("os.name"));
    	if(system.contains("Windows")){
    		windows = true;
    	}
    	else{
    		linux = true;
    	}

        initComponents();
        // home in favourites before anything.
        favHome();

        // scroll for filesPanel.
        scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // scroll for favouritesPanel.
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        filesPanel.setLayout(new WrapLayout(FlowLayout.LEFT));
        BoxLayout boxlayout = new BoxLayout(favouritesPanel, BoxLayout.Y_AXIS);
        favouritesPanel.setLayout(boxlayout);
        breadcrumb.setLayout(new BoxLayout(breadcrumb, BoxLayout.X_AXIS));

        // first of all read the XML file(if it exists) to specify favoutrites.
        readXMLFile();

        searchPanel.setVisible(false);
        searchText.setVisible(false);

        // Listener for Search MenuItem.
        MenuListener searchEar = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent me) {
                if (searchText.isVisible() == false) {
                    searchPanel.setVisible(true);
                    searchText.setVisible(true);

                    jButton1.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent event) {
                            String keyword = new String();
                            String type = new String();
                            String text = new String(searchText.getText());

                            if(text.contains(" ")){
                                keyword = text.substring(0, text.lastIndexOf(" "));
                                type = text.substring(text.lastIndexOf(" ") + 1, text.length());
                            }
                            else{
                                keyword = text;
                                type = "";
                            }
                            //Recursive search function call to look for keyword inside every director, starting from home.
                            searchKeyword(keyword, type);

                        }
                    });
                } 
                //The second time I click search button, search panel(textfield and button), disappears and opens home directory.
                else if (searchText.isVisible() == true) {
                    searchPanel.setVisible(false);
                    searchText.setVisible(false);

                    filesPanel.removeAll();
                    filesPanel.revalidate();
                    scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    filesPanel.setLayout(new WrapLayout(FlowLayout.LEFT));
                    SwingUtilities.updateComponentTreeUI(gui.this);
                    openDirectory(home);
                }
            }

            @Override
            public void menuCanceled(MenuEvent arg0) {
            }

            @Override
            public void menuDeselected(MenuEvent arg0) {
            }
        };

        searchMenu.addMenuListener(searchEar);
        newWindow.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new gui().setVisible(true);
            }

        });

        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }

        });

        lastPath = home;

        ActionListenerEdit mouseListenerEdit = new ActionListenerEdit();

        cut.addActionListener(mouseListenerEdit);
        cut.setActionCommand("cut");

        copy.addActionListener(mouseListenerEdit);
        copy.setActionCommand("copy");

        paste.addActionListener(mouseListenerEdit);
        paste.setActionCommand("paste");
        paste.setEnabled(false);

        rename.addActionListener(mouseListenerEdit);
        rename.setActionCommand("rename");

        delete.addActionListener(mouseListenerEdit);
        delete.setActionCommand("delete");

        addToFavourites.addActionListener(mouseListenerEdit);
        addToFavourites.setActionCommand("addToFavourites");

        properties.addActionListener(mouseListenerEdit);
        properties.setActionCommand("properties");

        //Workspace initialized.
        openDirectory(home);
    }

    //Method that adds Home directory in favouritesPanel. 
    public void favHome(){

        JButton favourite = new JButton("Home");
        favouritesPanel.add(favourite);

        favourite.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu fav = new JPopupMenu("Remove From Favourites");
                    JMenuItem remove = new JMenuItem("Remove From Favourites");
                    remove.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent event) {
                            favouritesPanel.removeAll();
                            favouritesPanel.revalidate();
                            homeRemoved = true;
                            readXMLFile();
                            SwingUtilities.updateComponentTreeUI(gui.this);
                        }
                    });
                    fav.add(remove);
                    fav.show(event.getComponent(), 25, 25);
                    repaint();
                }
            }
        });

        favourite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {

                try {
                    filesPanel.removeAll();
                    filesPanel.revalidate();
                    openDirectory(home);
                    SwingUtilities.updateComponentTreeUI(gui.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //Method called when search button is pressed, 
    public void searchKeyword(String key, String type) {

        File homeFile = new File(home);
        this.key = key.toLowerCase();
        this.type = type.toLowerCase();
        // First search for every file of this type.
        search(homeFile);

        int count = result.size();
        //If results found, workspace with files turns into a vertical list of buttons with links to wanted path.
        if(count > 0){
            JButton[] searchedFiles = new JButton[result.size()];

            filesPanel.removeAll();
            filesPanel.revalidate();
            scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            BoxLayout boxlayout = new BoxLayout(filesPanel, BoxLayout.Y_AXIS);
            filesPanel.setLayout(boxlayout);
            SwingUtilities.updateComponentTreeUI(gui.this);
            for (int i = 0; i < result.size(); i++) {
                searchedFiles[i] = new JButton(result.get(i));
                if ((new File(searchedFiles[i].getText())).isDirectory()) {
                    searchedFiles[i].setActionCommand(searchedFiles[i].getText());
                    searchedFiles[i].addActionListener(new ActionListenerTest());
                } else {
                    searchedFiles[i].setActionCommand(searchedFiles[i].getText());
                    searchedFiles[i].addActionListener(new ActionListenerFile());
                }
                filesPanel.add(searchedFiles[i]);
            }
            result.clear();
        }
        //Else if no results found, modal box shows up.
        else{
            JFrame window = new JFrame();
            JDialog d = new JDialog(window, "Search", true);
            d.setLayout(new FlowLayout());

            Button close = new Button("OK");
            close.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    d.setVisible(false);
                }
            });

            d.add(new Label("No result found!"));
            d.add(close);

            d.pack();
            d.setVisible(true);
        }
    }

    //Listener to open files.
    class ActionListenerFile implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                Desktop desktop = Desktop.getDesktop();
                File file = new File(event.getActionCommand());
                if (!Desktop.isDesktopSupported()) {
                    desktop.edit(file);
                    return;
                }
                desktop.open(file);
               
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    // Recursive function for searching, by checking if the name of a directory/file with given "type" contains "keyword". If "type" is dir, searches only for directories.
    public void search(File file) {
        if (!type.equals("dir")) {
            if (file.isDirectory()) {
                //Check for size and read permission of directory to search.
            	if(file.length() > 0){
	                if (file.canRead()) {
	                	if(file.listFiles() != null){
		                    for (File temp : file.listFiles()) {
		                    	if(temp != null){
			                        if (temp.isDirectory()) {
			                            search(temp);
			                        } else {
			                            if (((temp.getName()).toLowerCase()).contains(key)
			                                    && (((temp.getName()).toLowerCase()).contains(type))) {
			                                result.add(temp.getAbsolutePath());
			                            }
			                        }
			                    }
		                    }
		                }
	                } 
				}
            }
        } 
        else if (type.equals("dir")) {
            if (file.isDirectory()) {
            	if(file.length() > 0){
	                if (file.canRead()) {
	                	if(file.listFiles() != null){
		                    for (File temp : file.listFiles()) {
		                    	if(temp != null){
			                        if (temp.isDirectory()) {
			                            if (((temp.getName()).toLowerCase()).contains(key)) {
			                                result.add(temp.getAbsolutePath());
			                                search(temp);
			                            }
			                        }
			                    }
		                    }
	                	}
	                }
	            }
            }
        }
    }

    public class ActionListenerEdit implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getActionCommand() == "cut") {
                cutFlag = true;
                cutFile = new File(lastPath + separator + clickedlabel.getText());
                paste.setEnabled(true);
            }
            if (event.getActionCommand() == "copy") {
                copyFlag = true;
                copyFile = new File(lastPath + separator + clickedlabel.getText());
                paste.setEnabled(true);
            }
            if (event.getActionCommand() == "paste") {
                if (copyFlag) {
                    File pasteFile = new File(
                            lastPath + separator + clickedlabel.getText() + separator + findName(copyFile.getName()));
                    if (pasteFile.exists()) {
                        //Directory already exists.WARNING modal shows up.
                        JFrame window = new JFrame();
                        JDialog d = new JDialog(window, "Alert", true);
                        d.setLayout(new FlowLayout());

                        Button yes = new Button("YES");
                        Button no = new Button("NO");
                        yes.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                d.setVisible(false);
                                copyFolder(copyFile, pasteFile);
                            }
                        });
                        no.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                //Hide dialog
                                d.setVisible(false);
                                return;
                            }
                        });

                        d.add(new Label("Directory already exists!\nDo you want to replace it?"));
                        d.add(yes);
                        d.add(no);

                        // Show dialog
                        d.pack();
                        d.setVisible(true);
                    } else {
                        copyFolder(copyFile, pasteFile);
                    }

                    copyFlag = false;
                    paste.setEnabled(false);

                    filesPanel.removeAll();
                    filesPanel.revalidate();
                    clickedlabel.setText(findName(lastPath));
                    openDirectory(lastPath);
                } else if (cutFlag) {
                    File pasteFile = new File(
                            lastPath + separator + clickedlabel.getText() + separator + findName(cutFile.getName()));
                    if (pasteFile.exists()) {
                        //Directory already exists.WARNING modal shows up.
                        JFrame window = new JFrame();
                        JDialog d = new JDialog(window, "Alert", true);
                        d.setLayout(new FlowLayout());

                        Button yes = new Button("YES");
                        Button no = new Button("NO");
                        yes.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                            	d.setVisible(false);
                                copyFolder(cutFile, pasteFile);
                            }
                        });
                        no.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                //Hide dialog
                                d.setVisible(false);
                                return;
                            }
                        });

                        d.add(new Label("Directory already exists!\nDo you want to replace it?"));
                        d.add(yes);
                        d.add(no);

                        //Show dialog
                        d.pack();
                        d.setVisible(true);
                    } else {
                        copyFolder(cutFile, pasteFile);
                    }

                    cutFlag = false;
                    paste.setEnabled(false);
                    // File copied, now deleting it from previous directory to finish cut option.
                    deleteDirectory(cutFile);

                    filesPanel.removeAll();
                    filesPanel.revalidate();
                    clickedlabel.setText(findName(lastPath));
                    openDirectory(lastPath);
                } else {
                    paste.setEnabled(false);
                }
            }
            if (event.getActionCommand() == "rename") {
                File dir = new File(lastPath);
                File renameFile = new File(lastPath + separator + clickedlabel.getText());

                File files[] = dir.listFiles();
                if(files != null){
	                for (File element : files) {
	                    if (renameFile.equals(element)) {
	                        JFrame window = new JFrame();

	                        JDialog d = new JDialog(window, "Type the new name you want!", true);

	                        d.setLayout(new FlowLayout());

	                        JTextField newName = new JTextField(30);
	                        newName.setText(clickedlabel.getText());
	                        Button rename = new Button("Rename!");

	                        rename.addActionListener(new ActionListener() {
	                            public void actionPerformed(ActionEvent e) {
	                                // Hide dialog
	                                d.setVisible(false);

	                                File newFile = new File(lastPath +separator + newName.getText());
	                                renameFile.renameTo(newFile);

	                                filesPanel.removeAll();
	                                filesPanel.revalidate();
	                                openDirectory(lastPath);
	                            }
	                        });

	                        d.add(newName);
	                        d.add(rename);

	                        // Show dialog
	                        d.pack();
	                        d.setVisible(true);
	                    }
	                }
	            }
                filesPanel.revalidate();
            }
            if (event.getActionCommand() == "delete") {
                File dir = new File(lastPath);
                File deleteFile = new File(lastPath + separator + clickedlabel.getText());

                File files[] = dir.listFiles();
                if(files != null){
	                for (File element : files) {
	                    if (deleteFile.equals(element)) {

	                        JFrame window = new JFrame();
	                        JDialog d = new JDialog(window, "Alert", true);
	                        d.setLayout(new FlowLayout());

	                        Button yes = new Button("YES");
	                        Button no = new Button("NO");
	                        yes.addActionListener(new ActionListener() {
	                            public void actionPerformed(ActionEvent e) {
	                                // Hide dialog
	                                d.setVisible(false);

	                                if (!deleteFile.isDirectory()) {
	                                    deleteFile.delete();
	                                } else {
	                                    deleteDirectory(deleteFile);
	                                }

	                                filesPanel.removeAll();
	                                filesPanel.revalidate();
	                                openDirectory(lastPath);
	                            }
	                        });

	                        no.addActionListener(new ActionListener() {
	                            public void actionPerformed(ActionEvent e) {
	                                // Hide dialog
	                                d.setVisible(false);
	                            }
	                        });

	                        d.add(new Label("Are you sure you want to delete it?"));
	                        d.add(yes);
	                        d.add(no);

	                        // Show dialog
	                        d.pack();
	                        d.setVisible(true);
	                    }
	                }
	            }
            }
            if (event.getActionCommand() == "properties") {
                String propPath = new String();
                if(opened){
                    propPath = lastPath;
                }
                else{
                    propPath = lastPath + separator + clickedlabel.getText();
                }

                JFrame window = new JFrame();
                JDialog d = new JDialog(window, "Properties", true);
                d.setLayout(new GridLayout(4, 1));
                d.add(new Label("Name: " + clickedlabel.getText() + "\n"));
                d.add(new Label("Path: " + propPath));
                d.add(new Label("Size: " + findSize(propPath)));

                JPanel p = new JPanel();
                p.setLayout(new FlowLayout());
                JCheckBox read = new JCheckBox("Read", findStateRead(propPath));
                JCheckBox write = new JCheckBox("Write", findStateWrite(propPath));
                JCheckBox excecute = new JCheckBox("Excecute",
                        findStateExcecute(propPath));

                File file = new File(propPath);

                try {
                    if (!System.getProperty("user.name")
                            .equals(findName(getFileOwner(propPath)))){
                        read.setEnabled(false);
                        write.setEnabled(false);
                        excecute.setEnabled(false);
                    } else {
                        read.setEnabled(true);
                        write.setEnabled(true);
                        excecute.setEnabled(true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                read.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        JCheckBox cb = (JCheckBox) event.getSource();
                        if (cb.isSelected()) {
                            file.setReadable(true);
                        } else {
                            file.setReadable(false);
                        }
                    }
                });

                write.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        JCheckBox cb = (JCheckBox) event.getSource();
                        if (cb.isSelected()) {
                            file.setWritable(true);
                        } else {
                            file.setWritable(false);
                        }
                    }
                });
                excecute.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        JCheckBox cb = (JCheckBox) event.getSource();
                        if (cb.isSelected()) {
                            file.setExecutable(true);
                        } else {
                            file.setExecutable(false);
                        }
                    }
                });

                p.add(new Label("Permissions: "));
                p.add(read);
                p.add(write);
                p.add(excecute);
                p.setVisible(true);

                d.add(p);

                d.pack();
                d.setVisible(true);
            }

            if (event.getActionCommand() == "addToFavourites") {

                try {
                    File dir = new File(home + separator + ".java-file-browser");
                    if(!dir.exists()){
                        dir.mkdir();
                    }
                    File prop = new File(xmlPath);
                    if(!prop.exists()){
                        prop.createNewFile();
                        xmlFileInit();
                    }

                    File file = new File(lastPath + separator + clickedlabel.getText());
                    if (file.isDirectory()) {
                        String favouritePath = new String(lastPath + separator + clickedlabel.getText());
                        if (!findInXML(clickedlabel.getText())) {
                            addToXMLFile(clickedlabel.getText(), favouritePath);
                            favouritesInit(clickedlabel.getText());
                        }
                        favouritesPanel.revalidate();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getFileOwner(String path) throws IOException {
        return Files.getOwner(FileSystems.getDefault().getPath(path)).getName();
    }

    boolean findStateRead(String path) {
        File file = new File(path);
        return (file.canRead());
    }

    boolean findStateWrite(String path) {
        File file = new File(path);
        return (file.canWrite());
    }

    boolean findStateExcecute(String path) {
        File file = new File(path);
        return (file.canExecute());
    }

    public class ActionListenerTest implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            filesPanel.removeAll();
            filesPanel.revalidate();
            filesPanel.setLayout(new WrapLayout(FlowLayout.LEFT));
            openDirectory(e.getActionCommand());

            SwingUtilities.updateComponentTreeUI(gui.this);
        }
    }

    // Method to find the size of a directory/file.
    public static long findSize(String path) {
        long totalSize = 0;
        ArrayList<String> directory = new ArrayList<String>();
        File file = new File(path);

        if (file.isDirectory()) {
            directory.add(file.getAbsolutePath());
            while (directory.size() > 0) {
                String folderPath = directory.get(0);
                directory.remove(0);
                File folder = new File(folderPath);
                File[] filesInFolder = folder.listFiles();

                if(filesInFolder != null){
                	int noOfFiles = filesInFolder.length;
	                for (int i = 0; i < noOfFiles; i++) {
	                    File f = filesInFolder[i];
	                    if (f.isDirectory()) {
	                        directory.add(f.getAbsolutePath());
	                    } else {
	                        totalSize += f.length();
	                    }
	                }
	            }
            }
        } else {
            totalSize = file.length();
        }
        return totalSize;
    }

    public void openDirectory(String filepath) {

        ActionListenerTest actionEar = new ActionListenerTest();
        
        opened = true;

        // Define breadcrumb.
        if (lastPath != null) {
            breadcrumb.removeAll();
            breadcrumb.revalidate();
        }

        lastPath = filepath;
        // clickedlabel.setText(findName(lastPath));
        for (int j = 0; j < filepath.length(); j++) {
            char c = filepath.charAt(j);
            if (c == (this.separator).charAt(0)) {
                if (j == 0) {
                    JLabel separator = new JLabel(">");
                    breadcrumb.add(separator);

                } else {
                    String helper = new String(filepath.substring(0, j));

                    String folderName = new String(helper.substring(helper.lastIndexOf((this.separator).charAt(0)) + 1));

                    JButton folder = new JButton(folderName);
                    folder.setActionCommand(helper);
                    folder.addActionListener(actionEar);

                    breadcrumb.add(folder);
                    JLabel separator = new JLabel(">");
                    breadcrumb.add(separator);

                    helper = filepath.substring(j + 1, filepath.length());
                    if (helper.lastIndexOf((this.separator).charAt(0)) == -1) {
                        JButton curr = new JButton(helper);
                        curr.setActionCommand(filepath);
                        curr.addActionListener(actionEar);
                        breadcrumb.add(curr);
                    }
                }
            }
        }

        // Define folders and files of current workspace.
        File dir = new File(filepath);
        File[] files = dir.listFiles();
        List<File> directories = new ArrayList<File>();
        List<File> notDirectories = new ArrayList<File>();


        if(dir.length() > 0){
        	if(files != null){
		        for(File element: files){
		            if(element.isDirectory()){
		                directories.add(element);
		            }
		            else{
		                notDirectories.add(element);
		            }
		        }
                //Sort two lists alphabetiacally.
		        Collections.sort(directories, new Comparator<File>(){
		            public int compare(File f1, File f2){
		                return ((f1.getName()).toLowerCase()).compareTo(((f2.getName()).toLowerCase()));
		            } });
		        Collections.sort(notDirectories, new Comparator<File>(){
		            public int compare(File f1, File f2){
		                return ((f1.getName()).toLowerCase()).compareTo(((f2.getName()).toLowerCase()));
		            } });

		        JLabel[] directoryLabels = new JLabel[directories.size()];
		        JLabel[] fileLabels = new JLabel[notDirectories.size()];
		        MouseListener mouseListener = new MouseListener();
		        
		        for(int i = 0; i < directories.size(); i++){
		            directoryLabels[i] = new JLabel((directories.get(i)).getName());
		            directoryLabels[i].setPreferredSize(new Dimension(65, 90));
		            directoryLabels[i].setToolTipText(directoryLabels[i].getText());
		            if(directories.get(i).isHidden()){
		                directoryLabels[i].setVisible(false);
		            }
		            else{
		                directoryLabels[i].setVisible(true);
		                directoryLabels[i].addMouseListener(mouseListener);
		            }
		            selectIcon(directories.get(i), directoryLabels[i]);
		            filesPanel.add(directoryLabels[i]);
		        }

		        for(int i = 0; i < notDirectories.size(); i++){
                    fileLabels[i] = new JLabel((notDirectories.get(i)).getName());
                    //Set preferred size for folders to be aligned, full name appears as tooltip text.
		            fileLabels[i].setPreferredSize(new Dimension(110, 90));
		            if(notDirectories.get(i).isHidden()){
		                fileLabels[i].setVisible(false);
		            }
		            else{
		                fileLabels[i].setVisible(true);
		                fileLabels[i].addMouseListener(mouseListener);
		            }
		            selectIcon(notDirectories.get(i), fileLabels[i]);
		            filesPanel.add(fileLabels[i]);
		        }
		        directories.clear();
		        notDirectories.clear();
		    }
		}	
    }

    class MouseListener extends MouseAdapter {
        JLabel labelOld;
        Color background;

        MouseListener() {
            labelOld = new JLabel();
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                //MouseEvent occured by single click, pink frame appears.
                if (event.getClickCount() == 1) {
                    JLabel label = (JLabel) event.getSource();

                    clickedlabel = label;
                    opened = false;

                    background = label.getBackground();
                    label.setBackground(new Color(255, 229, 241));
                    label.setOpaque(true);

                    if (labelOld.getText() != label.getText()) {
                        labelOld.setBackground(background);
                        labelOld.setOpaque(true);
                    }

                    labelOld = label;
                }
                //MouseEvent occured by double click, if source was a directory, it oppens, else if it was a file, it runs it.
                if (event.getClickCount() == 2) {
                    JLabel labelClicked = (JLabel) event.getSource();

                    File fileClicked = new File(lastPath);

                    File folder[] = fileClicked.listFiles();
                    if(folder != null){
	                    for (File element : folder) {
	                        if (element.getName().equals(labelClicked.getText())) {
	                            if (element.isDirectory()) {
	                                String newPath = new String(lastPath + separator + labelClicked.getText());

	                                filesPanel.removeAll();
	                                filesPanel.revalidate();

	                                openDirectory(newPath);
	                            } else {
	                                if (!element.isDirectory()) {
	                                    try {
	                                        if (!Desktop.isDesktopSupported()) {
	                                            return;
	                                        }

	                                        Desktop desktop = Desktop.getDesktop();
	                                        desktop.open(element);
	                                    } catch (IOException e) {
	                                        e.printStackTrace();
	                                    }

	                                }
	                            }
	                        }
	                    }
	                }
                    SwingUtilities.updateComponentTreeUI(gui.this);
                }
            }
            //MouseEvent occured by right click on a selected label, pop up edit menu shows up.
            if (event.getButton() == MouseEvent.BUTTON3) {
                Color selectColor = new Color(255, 229, 241);
                JLabel label = (JLabel) event.getSource();

                if (label.getBackground().equals(selectColor)) {
                    JPopupMenu options = new JPopupMenu("Edit");

                    JMenuItem cutPop = new JMenuItem("Cut");
                    cutPop.setActionCommand(label.getText());
                    cutPop.addActionListener(new CutListener());

                    JMenuItem copyPop = new JMenuItem("Copy");
                    copyPop.setActionCommand(label.getText());
                    copyPop.addActionListener(new CopyListener());

                    JMenuItem pastePop = new JMenuItem("Paste");
                    pastePop.setActionCommand(label.getText());
                    pastePop.addActionListener(new PasteListener());

                    JMenuItem renamePop = new JMenuItem("Rename");
                    renamePop.setActionCommand(label.getText());
                    renamePop.addActionListener(new RenameListener());

                    JMenuItem deletePop = new JMenuItem("Delete");
                    deletePop.setActionCommand(label.getText());
                    deletePop.addActionListener(new DeleteListener());

                    JMenuItem favouritePop = new JMenuItem("Add To Favourites");
                    favouritePop.setActionCommand(label.getText());
                    favouritePop.addActionListener(new FavouritesListener());

                    JMenuItem propertiesPop = new JMenuItem("Properties");
                    propertiesPop.setActionCommand(label.getText());
                    propertiesPop.addActionListener(new PropertiesListener());

                    options.add(cutPop);
                    options.add(copyPop);
                    options.add(pastePop);
                    options.add(renamePop);
                    options.add(deletePop);
                    options.add(favouritePop);
                    options.add(propertiesPop);

                    options.show(event.getComponent(), 45, 45);
                    repaint();
                }
            }
        }
    }

    class CutListener implements ActionListener {

        public void actionPerformed(ActionEvent actionEvent) {

            cutFlag = true;
            cutFile = new File(lastPath + separator + actionEvent.getActionCommand());
        }
    }

    class CopyListener implements ActionListener {

        public void actionPerformed(ActionEvent actionEvent) {

            copyFlag = true;
            copyFile = new File(lastPath + separator + actionEvent.getActionCommand());
        }
    }

    public void copyFolder(File sourceFolder, File destinationFolder) {
        // Check if sourceFolder is a directory or file.
        if (sourceFolder.isDirectory()) {
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
            }
            String files[] = sourceFolder.list();

            // Iterate over all files and copy them to destinationFolder one by one
            for (String file : files) {
                File srcFile = new File(sourceFolder, file);
                File destFile = new File(destinationFolder, file);

                // Recursive function call
                copyFolder(srcFile, destFile);
            }

        } else {
            // Copy the file content from one place to another
            try {
                Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class PasteListener implements ActionListener {

        public void actionPerformed(ActionEvent actionEvent) {
            if (copyFlag) {
                File pasteFile = new File(
                        lastPath + separator + actionEvent.getActionCommand() + separator + findName(copyFile.getName()));

                if (pasteFile.exists()) {
                    // directory already exists.WARNING modal shows up.
                    JFrame window = new JFrame();
                    JDialog d = new JDialog(window, "Alert", true);
                    d.setLayout(new FlowLayout());

                    Button yes = new Button("YES");
                    Button no = new Button("NO");
                    yes.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            d.setVisible(false);
                            copyFolder(copyFile, pasteFile);
                        }
                    });
                    no.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            // Hide dialog
                            d.setVisible(false);
                            return;
                        }
                    });

                    d.add(new Label("Directory already exists!\nDo you want to replace it?"));
                    d.add(yes);
                    d.add(no);

                    // Show dialog
                    d.pack();
                    d.setVisible(true);
                } else {
                    copyFolder(copyFile, pasteFile);
                }

                copyFlag = false;

                filesPanel.removeAll();
                filesPanel.revalidate();
                openDirectory(lastPath);
            } else if (cutFlag) {
                File pasteFile = new File(
                        lastPath + separator + actionEvent.getActionCommand() + separator + findName(cutFile.getName()));

                if (pasteFile.exists()) {
                    // directory already exists.WARNING modal shows up.
                    JFrame window = new JFrame();
                    JDialog d = new JDialog(window, "Alert", true);
                    d.setLayout(new FlowLayout());

                    Button yes = new Button("YES");
                    Button no = new Button("NO");
                    yes.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            d.setVisible(false);
                            copyFolder(cutFile, pasteFile);
                        }
                    });
                    no.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            // Hide dialog
                            d.setVisible(false);
                            return;
                        }
                    });

                    d.add(new Label("Directory already exists!\nDo you want to replace it?"));
                    d.add(yes);
                    d.add(no);

                    // Show dialog
                    d.pack();
                    d.setVisible(true);
                } else {
                    copyFolder(cutFile, pasteFile);
                }

                cutFlag = false;
                // File copied, now deleting it from previous directory to finish cut option.
                deleteDirectory(cutFile);

                filesPanel.removeAll();
                filesPanel.revalidate();
                openDirectory(lastPath);
            }
        }
    }

    // Method to find the name of a directory/file given the path.
    public String findName(String path) {
        String name = new String();
        name = path.substring(path.lastIndexOf(separator) + 1, path.length());
        return name;
    }

    class RenameListener implements ActionListener {

        public void actionPerformed(ActionEvent actionEvent) {
            File dir = new File(lastPath);
            File renameFile = new File(lastPath + separator + actionEvent.getActionCommand());

            File files[] = dir.listFiles();
            if(files != null){
	            for (File element : files) {
	                if (renameFile.equals(element)) {
	                    JFrame window = new JFrame();
	                    JDialog d = new JDialog(window, "Type the new name you want!", true);
	                    d.setLayout(new FlowLayout());

	                    JTextField newName = new JTextField(30);
	                    newName.setText(actionEvent.getActionCommand());
	                    Button rename = new Button("Rename!");

	                    rename.addActionListener(new ActionListener() {
	                        public void actionPerformed(ActionEvent e) {
	                            // Hide dialog
	                            d.setVisible(false);

	                            File newFile = new File(lastPath + separator + newName.getText());
	                            renameFile.renameTo(newFile);

	                            filesPanel.removeAll();
	                            filesPanel.revalidate();
	                            openDirectory(lastPath);
	                        }
	                    });
	                    d.add(newName);
	                    d.add(rename);

	                    // Show dialog
	                    d.pack();
	                    d.setVisible(true);
	                }
	            }
	        }

            filesPanel.revalidate();
        }
    }

    class DeleteListener implements ActionListener {

        public void actionPerformed(ActionEvent actionEvent) {
            File dir = new File(lastPath);
            File deleteFile = new File(lastPath + separator + actionEvent.getActionCommand());

            File files[] = dir.listFiles();
            if(files != null){
	            for (File element : files) {
	                if (deleteFile.equals(element)) {
	                    JFrame window = new JFrame();
	                    JDialog d = new JDialog(window, "Alert", true);
	                    d.setLayout(new FlowLayout());

	                    // Create an OK button
	                    Button yes = new Button("YES");
	                    Button no = new Button("NO");
	                    yes.addActionListener(new ActionListener() {
	                        public void actionPerformed(ActionEvent e) {
	                            // Hide dialog
	                            d.setVisible(false);
	                            if (!deleteFile.isDirectory()) {
	                                deleteFile.delete();
	                            } else {
	                                deleteDirectory(deleteFile);
	                            }

	                            filesPanel.removeAll();
	                            filesPanel.revalidate();
	                            openDirectory(lastPath);
	                        }
	                    });

	                    no.addActionListener(new ActionListener() {
	                        public void actionPerformed(ActionEvent e) {
	                            // Hide dialog
	                            d.setVisible(false);
	                        }
	                    });

	                    d.add(new Label("Are you sure you want to delete it?"));
	                    d.add(yes);
	                    d.add(no);

	                    // Show dialog
	                    d.pack();
	                    d.setVisible(true);
	                }
	            }
	        }
        }
    }

    //Recursive function to delete everything in a directory.
    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    class FavouritesListener implements ActionListener {

        public void actionPerformed(ActionEvent actionEvent) {
            try {
                File dir = new File(home + separator + ".java-file-browser");
                if(!dir.exists()){
                    dir.mkdir();
                }
                File prop = new File(xmlPath);
                if(!prop.exists()){
                    prop.createNewFile();
                    xmlFileInit();
                }

                File file = new File(lastPath + separator + actionEvent.getActionCommand());
                if (file.isDirectory()) {
                    String favouritePath = new String(lastPath + separator + actionEvent.getActionCommand());
                    if(!findInXML(actionEvent.getActionCommand())){
                        addToXMLFile(actionEvent.getActionCommand(), favouritePath);
                        favouritesInit(actionEvent.getActionCommand());
                        favouritesPanel.revalidate();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Method to find if "name" is already in xml and therefore in favourites.
    public boolean findInXML(String name) {
        try {
            File xmlFile = new File(xmlPath);
            if (xmlFile.exists()) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder;
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("directory");
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        if (eElement.getElementsByTagName("name").item(0).getTextContent().equals(name)) {
                            return true;
                        }
                    }
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    class PropertiesListener implements ActionListener {

        public void actionPerformed(ActionEvent actionEvent) {
            JFrame window = new JFrame();
            JDialog d = new JDialog(window, "Properties", true);
            d.setLayout(new GridLayout(4, 1));

            d.add(new Label("Name: " + actionEvent.getActionCommand()));
            d.add(new Label("Path: " + lastPath + separator + actionEvent.getActionCommand()));
            d.add(new Label("Size: " + findSize(lastPath + separator + actionEvent.getActionCommand())));

            JPanel p = new JPanel();
            p.setLayout(new FlowLayout());
            JCheckBox read = new JCheckBox("Read", findStateRead(lastPath + separator + actionEvent.getActionCommand()));
            JCheckBox write = new JCheckBox("Write", findStateWrite(lastPath + separator + actionEvent.getActionCommand()));
            JCheckBox excecute = new JCheckBox("Excecute",
                    findStateExcecute(lastPath + separator + actionEvent.getActionCommand()));

            File file = new File(lastPath + separator + actionEvent.getActionCommand());

            try {
                if (!System.getProperty("user.name")
                        .equals(findName(getFileOwner(lastPath + separator + actionEvent.getActionCommand())))){
                    read.setEnabled(false);
                    write.setEnabled(false);
                    excecute.setEnabled(false);
                } else {
                    read.setEnabled(true);
                    write.setEnabled(true);
                    excecute.setEnabled(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            read.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    JCheckBox cb = (JCheckBox) event.getSource();
                    if (cb.isSelected()) {
                        file.setReadable(true);
                    } else {
                        file.setReadable(false);
                    }
                }
            });

            write.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    JCheckBox cb = (JCheckBox) event.getSource();
                    if (cb.isSelected()) {
                        file.setWritable(true);
                    } else {
                        file.setWritable(false);
                    }
                }
            });
            excecute.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    JCheckBox cb = (JCheckBox) event.getSource();
                    if (cb.isSelected()) {
                        file.setExecutable(true);
                    } else {
                        file.setExecutable(false);
                    }
                }
            });

            p.add(new Label("Permissions: "));
            p.add(read);
            p.add(write);
            p.add(excecute);
            p.setVisible(true);

            d.add(p);

            d.pack();
            d.setVisible(true);

        }
    }

    // Method to find the icon for every directory/file.
    public void selectIcon(File file, JLabel label) {

        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(JLabel.CENTER);

        String path = new String(iconsPath);

        if (file.isDirectory()) {
            path = path + separator + "folder.png";
            label.setIcon(new ImageIcon(path));
        } else {
            String extension = "";
            int i = (file.getName()).lastIndexOf('.');
            if (i >= 0) {
                extension = (file.getName()).substring(i + 1);
            }
            path = path + separator + extension + ".png";
            File temp = new File(path);
            if (temp.exists()) {
                label.setIcon(new ImageIcon(path));
            } else {
                path = iconsPath + separator + "question.png";
                label.setIcon(new ImageIcon(path));
            }
        }
    }

    //Method to initialize the XML file.
    public void xmlFileInit() {

        try {
            File xmlFile = new File(xmlPath);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("favourites");
            doc.appendChild(rootElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);

            transformer.transform(source, result);

        }
        catch (Exception pce) {
            pce.printStackTrace();
        }
    }

    //Method to add a node in the XML file.
    public void addToXMLFile(String favName, String favPath) {

        try {
            File xmlFile = new File(xmlPath);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(xmlFile);

            Element root = document.getDocumentElement();
            Element rootElement = document.getDocumentElement();

            // append type(= directory).
            Element type = document.createElement("directory");
            rootElement.appendChild(type);

            // append name.
            Element name = document.createElement("name");
            name.appendChild(document.createTextNode(favName));
            type.appendChild(name);

            // append path.
            Element pathDir = document.createElement("path");
            pathDir.appendChild(document.createTextNode(favPath));
            type.appendChild(pathDir);

            // append the whole element to the file.
            root.appendChild(type);

            // update the existing xmlFile.
            DOMSource source = new DOMSource(document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(xmlPath);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method to read the XML file in order to initialize favourites panel.
    public void readXMLFile() {

        try {
            File xmlFile = new File(xmlPath);
            if (xmlFile.exists()) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);

                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("directory");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        favouritesInit(eElement.getElementsByTagName("name").item(0).getTextContent());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method to search for a name in the XML file and return its path.
    public String findFavouritePath(String favouriteName) throws Exception {

        String path = new String();
        File xmlFile = new File(xmlPath);
        if (xmlFile.exists()) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("directory");
            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    if (eElement.getElementsByTagName("name").item(0).getTextContent().equals(favouriteName)) {
                        return (eElement.getElementsByTagName("path").item(0).getTextContent());
                    }
                }
            }
        }
        return path;
    }

    //Method to initialize favourites panel after reading the XML file.
    public void favouritesInit(String name) {

        MouseListenerFav fav = new MouseListenerFav();

        JButton favourite = new JButton(name);
        favouritesPanel.add(favourite);

        favourite.addMouseListener(fav);

        favourite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {

                try {
                    JButton button = new JButton();
                    button = (JButton) event.getSource();

                    String path;
                    path = findFavouritePath(button.getText());

                    filesPanel.removeAll();
                    filesPanel.revalidate();

                    openDirectory(path);

                    SwingUtilities.updateComponentTreeUI(gui.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    class MouseListenerFav extends MouseAdapter {
        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON3) {
                JButton button = (JButton) event.getSource();
                JPopupMenu fav = new JPopupMenu("Remove From Favourites");

                JMenuItem remove = new JMenuItem("Remove From Favourites");

                remove.setActionCommand(button.getText());
                remove.addActionListener(new removeListener());

                fav.add(remove);
                fav.show(event.getComponent(), 25, 25);
                repaint();
            }
        }
    }

    class removeListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                removeFromXMLFile(event.getActionCommand());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Method to remove a node given the name from the XML file.
    public void removeFromXMLFile(String name) throws Exception {
        
        File xmlFile = new File(xmlPath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("directory");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if(eElement.getElementsByTagName("name").item(0).getTextContent().equals(name)){

                    removeNode(eElement);
                    doc.normalize();

                    //Save the file.
                    DOMSource source = new DOMSource(doc);
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    StreamResult result = new StreamResult(xmlPath);
                    transformer.transform(source, result);

                    favouritesPanel.removeAll();
                    favouritesPanel.revalidate();
                    if(!homeRemoved){
                        favHome();
                    }
                    readXMLFile();
                    SwingUtilities.updateComponentTreeUI(gui.this);
                }
            }
        }
    }

    static private void removeNode(Node node){
        Node parent = node.getParentNode();
        if ( parent != null ) parent.removeChild(node);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        basicPanel = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        searchText = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        breadcrumb = new javax.swing.JPanel();
        scrollPanel = new javax.swing.JScrollPane();
        filesPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        favouritesPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newWindow = new javax.swing.JMenuItem();
        exit = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cut = new javax.swing.JMenuItem();
        copy = new javax.swing.JMenuItem();
        paste = new javax.swing.JMenuItem();
        rename = new javax.swing.JMenuItem();
        delete = new javax.swing.JMenuItem();
        addToFavourites = new javax.swing.JMenuItem();
        properties = new javax.swing.JMenuItem();
        searchMenu = new javax.swing.JMenu();

        basicPanel.setPreferredSize(new java.awt.Dimension(600, 500));

        javax.swing.GroupLayout basicPanelLayout = new javax.swing.GroupLayout(basicPanel);
        basicPanel.setLayout(basicPanelLayout);
        basicPanelLayout.setHorizontalGroup(
            basicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 591, Short.MAX_VALUE)
        );
        basicPanelLayout.setVerticalGroup(
            basicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 552, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gubler");
        setBackground(new java.awt.Color(26, 29, 43));

        searchText.setBackground(new java.awt.Color(195, 199, 209));
        searchText.setToolTipText("Type what you are searching for...");
        searchText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchTextActionPerformed(evt);
            }
        });

        jButton1.setForeground(new java.awt.Color(170, 85, 127));
        jButton1.setText("Search");

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addComponent(searchText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1))
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)))
        );

        breadcrumb.setBackground(new java.awt.Color(195, 199, 209));

        javax.swing.GroupLayout breadcrumbLayout = new javax.swing.GroupLayout(breadcrumb);
        breadcrumb.setLayout(breadcrumbLayout);
        breadcrumbLayout.setHorizontalGroup(
            breadcrumbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        breadcrumbLayout.setVerticalGroup(
            breadcrumbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 31, Short.MAX_VALUE)
        );

        scrollPanel.setForeground(new java.awt.Color(22, 25, 37));
        scrollPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        javax.swing.GroupLayout filesPanelLayout = new javax.swing.GroupLayout(filesPanel);
        filesPanel.setLayout(filesPanelLayout);
        filesPanelLayout.setHorizontalGroup(
            filesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 434, Short.MAX_VALUE)
        );
        filesPanelLayout.setVerticalGroup(
            filesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 388, Short.MAX_VALUE)
        );

        scrollPanel.setViewportView(filesPanel);

        favouritesPanel.setBackground(new java.awt.Color(170, 85, 127));

        javax.swing.GroupLayout favouritesPanelLayout = new javax.swing.GroupLayout(favouritesPanel);
        favouritesPanel.setLayout(favouritesPanelLayout);
        favouritesPanelLayout.setHorizontalGroup(
            favouritesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 192, Short.MAX_VALUE)
        );
        favouritesPanelLayout.setVerticalGroup(
            favouritesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 479, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(favouritesPanel);

        menuBar.setBackground(new java.awt.Color(195, 199, 209));
        menuBar.setForeground(new java.awt.Color(170, 85, 127));

        fileMenu.setBackground(new java.awt.Color(0, 0, 0));
        fileMenu.setForeground(new java.awt.Color(0, 0, 0));
        fileMenu.setText("File");

        newWindow.setBackground(new java.awt.Color(195, 199, 209));
        newWindow.setForeground(new java.awt.Color(0, 0, 0));
        newWindow.setText("New Window");
        newWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newWindowActionPerformed(evt);
            }
        });
        fileMenu.add(newWindow);

        exit.setBackground(new java.awt.Color(195, 199, 209));
        exit.setForeground(new java.awt.Color(0, 0, 0));
        exit.setText("Exit");
        fileMenu.add(exit);

        menuBar.add(fileMenu);

        editMenu.setForeground(new java.awt.Color(0, 0, 0));
        editMenu.setText("Edit");

        cut.setText("Cut");
        editMenu.add(cut);

        copy.setText("Copy");
        editMenu.add(copy);

        paste.setText("Paste");
        paste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteActionPerformed(evt);
            }
        });
        editMenu.add(paste);

        rename.setText("Rename");
        editMenu.add(rename);

        delete.setText("Delete");
        editMenu.add(delete);

        addToFavourites.setText("Add To Favourites");
        editMenu.add(addToFavourites);

        properties.setText("Properties");
        editMenu.add(properties);

        menuBar.add(editMenu);

        searchMenu.setForeground(new java.awt.Color(0, 0, 0));
        searchMenu.setText("Search");
        menuBar.add(searchMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(searchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollPanel)
                            .addComponent(breadcrumb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(breadcrumb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPanel)
                .addContainerGap())
            .addComponent(jScrollPane1)
        );

        setBounds(0, 0, 643, 534);
    }// </editor-fold>                        

    private void newWindowActionPerformed(java.awt.event.ActionEvent evt)     {                                          
       
    }                                         

    private void pasteActionPerformed(java.awt.event.ActionEvent evt) 
    { 

    }                                     

    private void searchTextActionPerformed(java.awt.event.ActionEvent evt) {                                           
       
    }                                          


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new gui().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JMenuItem addToFavourites;
    private javax.swing.JPanel basicPanel;
    private javax.swing.JPanel breadcrumb;
    private javax.swing.JMenuItem copy;
    private javax.swing.JMenuItem cut;
    private javax.swing.JMenuItem delete;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exit;
    private javax.swing.JPanel favouritesPanel;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPanel filesPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newWindow;
    private javax.swing.JMenuItem paste;
    private javax.swing.JMenuItem properties;
    private javax.swing.JMenuItem rename;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JMenu searchMenu;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTextField searchText;
    // End of variables declaration                   
}
