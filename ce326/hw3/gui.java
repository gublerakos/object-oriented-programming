import java.awt.*;
import java.io.*;
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
 * @author gublerakos
 */
public class gui extends javax.swing.JFrame {

    // JPopupMenu popup;
    String home = new String(System.getProperty("user.home"));
    String lastPath = new String();
    File copyFile;
    boolean copyFlag = false;

    File cutFile;
    boolean cutFlag = false;

    JLabel clickedlabel = new JLabel(lastPath);
    List<String> result = new ArrayList<String>();
    String type;
    String key;

    /**
     * Creates new form gui
     */
    public gui() {
        // first of all read the XML file(if it exists) to specify favoutrites.
        initComponents();

        // scroll for filesPanel.
        scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // scroll for favouritesPanel.
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        filesPanel.setLayout(new WrapLayout(FlowLayout.LEFT));

        BoxLayout boxlayout = new BoxLayout(favouritesPanel, BoxLayout.Y_AXIS);

        favouritesPanel.setLayout(boxlayout);

        // favouritesPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        breadcrumb.setLayout(new BoxLayout(breadcrumb, BoxLayout.X_AXIS));

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

                            System.out.println("Search button pressed");

                            keyword = text.substring(0, text.lastIndexOf(" "));

                            type = text.substring(text.lastIndexOf(" ") + 1, text.length());
                            System.out.println("Keyword = " + keyword);
                            System.out.println("Type = " + type);

                            // RECURSIVE SEARCH FUNCTION CALL
                            searchKeyword(keyword, type);

                        }
                    });
                } else if (searchText.isVisible() == true) {
                    searchPanel.setVisible(false);
                    searchText.setVisible(false);

                    filesPanel.removeAll();
                    filesPanel.revalidate();

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

        rename.addActionListener(mouseListenerEdit);
        rename.setActionCommand("rename");

        delete.addActionListener(mouseListenerEdit);
        delete.setActionCommand("delete");

        addToFavourites.addActionListener(mouseListenerEdit);
        addToFavourites.setActionCommand("addToFavourites");

        properties.addActionListener(mouseListenerEdit);
        properties.setActionCommand("properties");

        openDirectory(home);
    }

    public void searchKeyword(String key, String type) {

        File homeFile = new File(home);
        this.key = key.toLowerCase();
        this.type = type.toLowerCase();
        // First search for every file of this type.
        search(homeFile);

        int count = result.size();
        if (count == 0) {
            System.out.println("\nNo result found!");
        } else {
            System.out.println("\nFound " + count + " result!\n");
            for (String matched : result) {
                System.out.println("Found : " + matched);
            }
        }

        JButton[] searchedFiles = new JButton[result.size()];

        filesPanel.removeAll();
        filesPanel.revalidate();

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

    class ActionListenerFile implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                if (!Desktop.isDesktopSupported()) {
                    System.out.println("Desktop is not supported");
                    return;
                }
                File file = new File(event.getActionCommand());
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void search(File file) {

        if (!type.equals("dir")) {
            if (file.isDirectory()) {
                // do you have permission to read this directory?
                if (file.canRead()) {
                    for (File temp : file.listFiles()) {
                        if (temp.isDirectory()) {
                            search(temp);
                        } else {
                            if (((temp.getName()).toLowerCase()).contains(key)
                                    && (((temp.getName()).toLowerCase()).contains(type))) {
                                result.add(temp.getAbsolutePath());
                            }
                        }
                    }
                } else {
                    System.out.println(file.getAbsoluteFile() + "Permission Denied");
                }
            }
        } else if (type.equals("dir")) {
            if (file.isDirectory()) {
                if (file.canRead()) {
                    for (File temp : file.listFiles()) {
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

    public class ActionListenerEdit implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            // System.out.println(clickedlabel.getText());

            if (event.getActionCommand() == "cut") {
                cutFlag = true;
                cutFile = new File(lastPath + "/" + clickedlabel.getText());
            }
            if (event.getActionCommand() == "copy") {
                copyFlag = true;
                copyFile = new File(lastPath + "/" + clickedlabel.getText());
            }
            if (event.getActionCommand() == "paste") {
                if (copyFlag) {
                    File pasteFile = new File(
                            lastPath + "/" + clickedlabel.getText() + "/" + findName(copyFile.getName()));
                    System.out.println(copyFile);
                    System.out.println(pasteFile);
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
                                System.out.println(copyFile);
                                System.out.println(pasteFile);
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
                    clickedlabel.setText(findName(lastPath));
                    openDirectory(lastPath);
                } else if (cutFlag) {
                    File pasteFile = new File(
                            lastPath + "/" + clickedlabel.getText() + "/" + findName(cutFile.getName()));
                    if (pasteFile.exists()) {
                        // System.out.println(pasteFile);
                        // directory already exists.WARNING modal shows up.
                        JFrame window = new JFrame();
                        JDialog d = new JDialog(window, "Alert", true);
                        d.setLayout(new FlowLayout());

                        Button yes = new Button("YES");
                        Button no = new Button("NO");
                        yes.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
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
                    clickedlabel.setText(findName(lastPath));
                    openDirectory(lastPath);
                } else {
                    System.out.println("Nothing to paste!");
                }
            }
            if (event.getActionCommand() == "rename") {
                File dir = new File(lastPath);
                File renameFile = new File(lastPath + "/" + clickedlabel.getText());

                File files[] = dir.listFiles();
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

                                File newFile = new File(lastPath + "/" + newName.getText());
                                System.out.println(newName.getText());
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

                filesPanel.revalidate();
            }
            if (event.getActionCommand() == "delete") {
                File dir = new File(lastPath);
                File deleteFile = new File(lastPath + "/" + clickedlabel.getText());

                File files[] = dir.listFiles();

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
            if (event.getActionCommand() == "properties") {
                JFrame window = new JFrame();
                JDialog d = new JDialog(window, "Properties", true);
                d.setLayout(new GridLayout(4, 1));
                // System.out.println(clickedlabel.getText());
                // JLabel label = new JLabel(clickedlabel.getText());
                d.add(new Label("Name: " + clickedlabel.getText() + "\n"));
                d.add(new Label("Path: " + lastPath + "/" + clickedlabel.getText()));
                d.add(new Label("Size: " + findSize(lastPath + "/" + clickedlabel.getText())));

                JPanel p = new JPanel();
                p.setLayout(new FlowLayout());
                JCheckBox read = new JCheckBox("Read", findStateRead(lastPath + "/" + clickedlabel.getText()));
                JCheckBox write = new JCheckBox("Write", findStateWrite(lastPath + "/" + clickedlabel.getText()));
                JCheckBox excecute = new JCheckBox("Excecute",
                        findStateExcecute(lastPath + "/" + clickedlabel.getText()));

                File file = new File(lastPath + "/" + clickedlabel.getText());

                try {
                    System.out.println(System.getProperty("user.name"));
                    System.out.println(getFileOwner(lastPath + "/" + clickedlabel.getText()));
                    if (!System.getProperty("user.name")
                            .equals(getFileOwner(lastPath + "/" + clickedlabel.getText()))) {
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

                System.out.println(clickedlabel.getText());

                try {
                    File file = new File(lastPath + "/" + clickedlabel.getText());
                    if (!file.isDirectory()) {
                        System.out.println("Not a directory!");
                        // pop up menu
                    } else {
                        String favouritePath = new String(lastPath + "/" + clickedlabel.getText());
                        if(findInXML(clickedlabel.getText())){
                            System.out.println(clickedlabel.getText() + " is already in favourites!");
                        }
                        else{
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
                // System.out.println("Size of this :"+folderPath);
                directory.remove(0);
                File folder = new File(folderPath);
                File[] filesInFolder = folder.listFiles();
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
        } else {
            totalSize = file.length();
        }
        return totalSize;
    }

    public void openDirectory(String filepath) {

        ActionListenerTest actionEar = new ActionListenerTest();

        // Define breadcrumb.
        if (lastPath != null) {
            breadcrumb.removeAll();
            breadcrumb.revalidate();
        }

        lastPath = filepath;
        // System.out.println(filepath);
        for (int j = 0; j < filepath.length(); j++) {
            char c = filepath.charAt(j);
            if (c == '/') {
                if (j == 0) {
                    JLabel separator = new JLabel(">");
                    breadcrumb.add(separator);

                } else {
                    String helper = new String(filepath.substring(0, j));

                    String folderName = new String(helper.substring(helper.lastIndexOf("/") + 1));

                    JButton folder = new JButton(folderName);
                    folder.setActionCommand(helper);
                    // System.out.println(helper);
                    folder.addActionListener(actionEar);

                    breadcrumb.add(folder);
                    JLabel separator = new JLabel(">");
                    breadcrumb.add(separator);

                    helper = filepath.substring(j + 1, filepath.length());
                    if (helper.lastIndexOf("/") == -1) {
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

        for(File element: files){
            if(element.isDirectory()){
                directories.add(element);
            }
            else{
                notDirectories.add(element);
            }
        }

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

    class MouseListener extends MouseAdapter {
        JLabel labelOld;
        Color background;

        MouseListener() {
            labelOld = new JLabel();
        }

        public void mousePressed(MouseEvent e) {
            // maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            // maybeShowPopup(e);
        }

        // private void maybeShowPopup(MouseEvent e) {
        // if (e.isPopupTrigger()) {
        // popup.show(e.getComponent(),e.getX(), e.getY());
        // }
        // }

        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                if (event.getClickCount() == 1) {
                    // System.out.println("Single left click!");
                    JLabel label = (JLabel) event.getSource();

                    clickedlabel = label;

                    background = label.getBackground();
                    label.setBackground(new Color(255, 229, 241));
                    label.setOpaque(true);

                    if (labelOld.getText() != label.getText()) {
                        labelOld.setBackground(background);
                        labelOld.setOpaque(true);
                    }

                    labelOld = label;
                }

                if (event.getClickCount() == 2) {
                    // System.out.println("Double left click!");
                    JLabel labelClicked = (JLabel) event.getSource();

                    File fileClicked = new File(lastPath);

                    File folder[] = fileClicked.listFiles();

                    for (File element : folder) {
                        if (element.getName().equals(labelClicked.getText())) {

                            if (element.isDirectory()) {
                                System.out.println(element.getName() + " is a directory!");
                                String newPath = new String(lastPath + "/" + labelClicked.getText());

                                filesPanel.removeAll();
                                filesPanel.revalidate();

                                openDirectory(newPath);
                            } else {
                                System.out.println(element.getName() + " is a file!");

                                if (!element.isDirectory()) {
                                    try {
                                        if (!Desktop.isDesktopSupported()) {
                                            System.out.println("Desktop is not supported");
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
                    SwingUtilities.updateComponentTreeUI(gui.this);
                }
            }
            if (event.getButton() == MouseEvent.BUTTON3) {
                Color selectColor = new Color(255, 229, 241);
                JLabel label = (JLabel) event.getSource();

                if (label.getBackground().equals(selectColor)) {
                    JPopupMenu options = new JPopupMenu("Edit");
                    // System.out.println("popup");

                    JMenuItem cutPop = new JMenuItem("Cut");
                    cutPop.setActionCommand(label.getText());
                    cutPop.addActionListener(new CutListener());

                    JMenuItem copyPop = new JMenuItem("Copy");
                    System.out.println(label.getText());
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

            System.out.println("Selected: Cut");
            // System.out.println("action" + actionEvent.getActionCommand());

            cutFile = new File(lastPath + "/" + actionEvent.getActionCommand());

            System.out.println("file's path to cut = " + cutFile);
        }
    }

    class CopyListener implements ActionListener {

        public void actionPerformed(ActionEvent actionEvent) {

            copyFlag = true;

            System.out.println("Selected: Copy");
            System.out.println(actionEvent.getActionCommand());

            copyFile = new File(lastPath + "/" + actionEvent.getActionCommand());

            System.out.println("file's path to copy = " + copyFile);

        }
    }

    public void copyFolder(File sourceFolder, File destinationFolder) {
        // Check if sourceFolder is a directory or file.
        if (sourceFolder.isDirectory()) {
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
                System.out.println("Directory created :: " + destinationFolder);
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
            // System.out.println("File copied: " + destinationFolder);
        }
    }

    class PasteListener implements ActionListener {

        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Selected: Paste");
            System.out.println(actionEvent.getActionCommand());

            if (copyFlag) {
                File pasteFile = new File(
                        lastPath + "/" + actionEvent.getActionCommand() + "/" + findName(copyFile.getName()));
                System.out.println(pasteFile);

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
                            System.out.println(copyFile);
                            System.out.println(pasteFile);
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
                        lastPath + "/" + actionEvent.getActionCommand() + "/" + findName(cutFile.getName()));

                if (pasteFile.exists()) {
                    // System.out.println(pasteFile);
                    // directory already exists.WARNING modal shows up.
                    JFrame window = new JFrame();
                    JDialog d = new JDialog(window, "Alert", true);
                    d.setLayout(new FlowLayout());

                    Button yes = new Button("YES");
                    Button no = new Button("NO");
                    yes.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
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
                System.out.println(lastPath);
                openDirectory(lastPath);
            } else {
                System.out.println("Nothing to paste!");
            }
        }
    }

    // Method to find the name of a directory/file given the path.
    public String findName(String path) {
        String name = new String();

        name = path.substring(path.lastIndexOf("/") + 1, path.length());
        // System.out.println(name);

        return name;
    }

    class RenameListener implements ActionListener {

        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Selected: Rename");
            System.out.println(actionEvent.getActionCommand());

            File dir = new File(lastPath);
            File renameFile = new File(lastPath + "/" + actionEvent.getActionCommand());

            // System.out.println(deleteFile);
            File files[] = dir.listFiles();
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

                            File newFile = new File(lastPath + "/" + newName.getText());
                            System.out.println(newName.getText());
                            renameFile.renameTo(newFile);

                            filesPanel.removeAll();
                            filesPanel.revalidate();
                            openDirectory(lastPath);
                        }
                    });
                    // d.add(new Label ("Type the new name you want!"));
                    d.add(newName);
                    d.add(rename);

                    // Show dialog
                    d.pack();
                    d.setVisible(true);
                }
            }

            filesPanel.revalidate();
        }
    }

    class DeleteListener implements ActionListener {

        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Selected: Delete");
            // System.out.println(actionEvent.getActionCommand());

            File dir = new File(lastPath);
            File deleteFile = new File(lastPath + "/" + actionEvent.getActionCommand());

            // System.out.println(deleteFile);
            File files[] = dir.listFiles();

            for (File element : files) {
                if (deleteFile.equals(element)) {

                    // System.out.println("geia");

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
                            // System.out.println();

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
            System.out.println("Selected: Add To Favourites");
            System.out.println(actionEvent.getActionCommand());

            try {
                File file = new File(lastPath + "/" + actionEvent.getActionCommand());
                if (!file.isDirectory()) {
                    System.out.println("Not a directory!");
                    // pop up menu
                } else {
                    String favouritePath = new String(lastPath + "/" + actionEvent.getActionCommand());
                    if(findInXML(actionEvent.getActionCommand())){
                        System.out.println(actionEvent.getActionCommand() + " is already in favourites!");
                    }
                    else{
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

    public boolean findInXML(String name) {
        try {
            File xmlFile = new File(home + "/properties.xml");
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
            System.out.println("Selected: Properties");
            System.out.println(actionEvent.getActionCommand());

            JFrame window = new JFrame();
            JDialog d = new JDialog(window, "Properties", true);
            d.setLayout(new GridLayout(4, 1));

            d.add(new Label("Name: " + actionEvent.getActionCommand()));
            d.add(new Label("Path: " + lastPath + "/" + actionEvent.getActionCommand()));
            d.add(new Label("Size: " + findSize(lastPath + "/" + actionEvent.getActionCommand())));

            JPanel p = new JPanel();
            p.setLayout(new FlowLayout());
            JCheckBox read = new JCheckBox("Read", findStateRead(lastPath + "/" + actionEvent.getActionCommand()));
            JCheckBox write = new JCheckBox("Write", findStateWrite(lastPath + "/" + actionEvent.getActionCommand()));
            JCheckBox excecute = new JCheckBox("Excecute",
                    findStateExcecute(lastPath + "/" + actionEvent.getActionCommand()));

            File file = new File(lastPath + "/" + actionEvent.getActionCommand());

            try {
                if (!System.getProperty("user.name")
                        .equals(getFileOwner(lastPath + "/" + actionEvent.getActionCommand()))) {
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

    public void selectIcon(File file, JLabel label) {

        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(JLabel.CENTER);

        String path = new String(home + "/.icons" + "/icons");

        if (file.isDirectory()) {
            path = path + "/folder.png";
            label.setIcon(new ImageIcon(path));
        } else {
            String extension = "";
            int i = (file.getName()).lastIndexOf('.');
            if (i >= 0) {
                extension = (file.getName()).substring(i + 1);
            }
            path = path + "/" + extension + ".png";
            File temp = new File(path);
            if (temp.exists()) {
                label.setIcon(new ImageIcon(path));
            } else {
                path = home + "/.icons" + "/icons" + "/question.png";
                label.setIcon(new ImageIcon(path));
            }
        }
    }

    public void xmlFileInit(File file) {

        try (FileWriter fileWriter = new FileWriter(file);) {
            StringBuilder content = new StringBuilder();

            content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?");

            content.append("<favourites>");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToXMLFile(String favName, String favPath) {

        try {
            File xmlFile = new File(home + "/properties.xml");
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
            // System.out.println(path.getText());
            Element pathDir = document.createElement("path");
            pathDir.appendChild(document.createTextNode(favPath));
            type.appendChild(pathDir);

            // append the whole element to the file.
            root.appendChild(type);

            // update the existing xmlFile.
            DOMSource source = new DOMSource(document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(home + "/properties.xml");
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readXMLFile() {

        try {
            File xmlFile = new File(home + "/properties.xml");
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
                        // System.out.println("Name : " +
                        // eElement.getElementsByTagName("name").item(0).getTextContent());

                        // System.out.println("Path : " +
                        // eElement.getElementsByTagName("path").item(0).getTextContent());

                        favouritesInit(eElement.getElementsByTagName("name").item(0).getTextContent());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String findFavouritePath(String favouriteName) throws Exception {

        String path = new String();
        File xmlFile = new File(home + "/properties.xml");
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
            System.out.println(event.getActionCommand());
            // JButton button = new JButton(event.getActionCommand());

            try {
                removeFromXMLFile(event.getActionCommand());
            } catch (Exception e) {
                e.printStackTrace();
            }

            
        }

    }

    public void removeFromXMLFile(String name) throws Exception {
        
        File xmlFile = new File(home + "/properties.xml");
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

                    System.out.println("name is = " + eElement.getElementsByTagName("name").item(0).getTextContent());

                    removeNode(eElement);
                    doc.normalize();

                    //Save the file.
                    DOMSource source = new DOMSource(doc);

                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    StreamResult result = new StreamResult(home + "/properties.xml");
                    transformer.transform(source, result);

                    favouritesPanel.removeAll();
                    favouritesPanel.revalidate();

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

    public static final void prettyPrint(Document xml) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(xml), new StreamResult(out));
        System.out.println(out.toString());
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

    private void newWindowActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void pasteActionPerformed(java.awt.event.ActionEvent evt) {                                      
        // TODO add your handling code here:
    }                                     

    private void searchTextActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
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
