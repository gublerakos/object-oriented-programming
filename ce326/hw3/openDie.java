// Define folders and files of current workspace.
File dir = new File(filepath);
File[] files = dir.listFiles();
JLabel[] fileLabels = new JLabel[files.length];

MouseListener mouseListener = new MouseListener();

for (int i = 0; i < files.length; i++) {
    fileLabels[i] = new JLabel(files[i].getName());
    // fileLabels[i].setPreferredSize(new Dimension(110, 90));
    if (files[i].isHidden()) {
        fileLabels[i].setVisible(false);
    } else {
        fileLabels[i].setVisible(true);
        fileLabels[i].addMouseListener(mouseListener);
    }
    selectIcon(files[i], fileLabels[i]);
    filesPanel.add(fileLabels[i]);
}