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

Collections.sort(directories);
Collections.sort(notDirectories);

JLabel[] directoryLabels = new JLabel[directories.size()];
JLabel[] fileLabels = new JLabel[notDirectories.size()];
MouseListener mouseListener = new MouseListener();

for(int i = 0; i < directories.size(); i++){
    directoryLabels[i] = new JLabel((directories.get(i)).getName());
    directoryLabels[i].setPreferredSize(new Dimension(110, 90));
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