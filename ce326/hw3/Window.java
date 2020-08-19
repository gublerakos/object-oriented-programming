/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hlias
 */

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.*;

public class Window extends JFrame implements ActionListener {

    private JPopupMenu popup;
    private MouseListener popupListener;
    private String BreadcrumbStr;
    private String lastPathUsed;

    public Window() {
        
        initComponents();
        
        textField1.setVisible(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        //Set Preffered Size for labels
        jPanel1.setPreferredSize(new Dimension(251, 180));
        jScrollPane1.setPreferredSize(new Dimension(251, 180));
        pack();
        
        //
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        //Defining layout for the current directory viewer
        jPanel1.setLayout(new WrapLayout());
        
        //Definig layout for Breadcrumb
        jPanel3.setLayout(new BoxLayout(jPanel3 ,BoxLayout.X_AXIS));

        //Initialising the contents of the pop-up menu
         popup = new JPopupMenu();
        JMenuItem menuItem;
        menuItem = new JMenuItem("Cut");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        menuItem = new JMenuItem("Copy");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        menuItem = new JMenuItem("Paste");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        menuItem = new JMenuItem("Rename");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        menuItem = new JMenuItem("Add to Favourites");
        menuItem.addActionListener(this);
         popup.add(menuItem);
        menuItem = new JMenuItem("Properties");
        menuItem.addActionListener(this);
        popup.add(menuItem);

        popupListener = new PopupListener(popup);

        //Open Home directory and show it's contents
        String userHome = "user.home";
        String path = System.getProperty(userHome);
        System.out.println("Your Home Path: " + path);
        
        //Checking the type of archives included
        lastPathUsed = path;
        FolderContents(path);
        
    }
    
    //This function traces the specified folder and returns it's contents
    void FolderContents(String path){
        
        MouseListener popupListener = new PopupListener(popup);

        if(lastPathUsed != null){
            jPanel3.removeAll();
            jPanel3.revalidate();
        }
            
        File f = new File(path);
        File[] archives = f.listFiles();
        lastPathUsed = path;
        
        //Breadcrumb synthesis
        for(int i = 0; i < path.length(); i++){
            char symbol = path.charAt(i);
            if(symbol == '/'){

                if(i == 0){

                    JButton C = new JButton("C:");
                    
                    /*C.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            
                        }
                    }*/

                    jPanel3.add(C);
                    JLabel crumb = new JLabel(">");
                    jPanel3.add(crumb);
                }

                else{
                    System.out.println(i);
                    String helper = path.substring(0, i);
                    System.out.println(helper);
                    String linkName = helper.substring(helper.lastIndexOf("/") + 1);
                
                    System.out.println(linkName);
                    
                    JButton folderLink = new JButton(linkName);
                    /*folderLink.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            
                        }
                    }*/

                    jPanel3.add(folderLink);
                    JLabel crumb = new JLabel(">");
                    jPanel3.add(crumb);
                    
                    //Checking if the next folder the terminal folder 
                    helper = path.substring(i+1, path.length());
                    if(helper.lastIndexOf("/") == -1){
                        JLabel termFolder = new JLabel(helper);
                        jPanel3.add(termFolder);
                    }
                }
            }
        }

        for(File element : archives){
            if(element.isDirectory()){
                //System.out.println(element.getName()+ " is a Directory!");
                ImageIcon dir = new ImageIcon("icons/folder.png", element.getName());
                
                JButton dct = new JButton(dir);
                dct.setText(element.getName());
                dct.addMouseListener(popupListener); 
                
                dct.setVerticalTextPosition(SwingConstants.BOTTOM);
                dct.setHorizontalTextPosition(SwingConstants.CENTER);

                jPanel1.add(dct);
            }

            else if(element.isFile()){
                
                //Seperating the substring which denotes the type 
                String fileType = element.getName().substring(element.getName().lastIndexOf(".") + 1, element.getName().length()); 
                
                if(fileType.equals("pdf")){
                    ImageIcon pdf = new ImageIcon("icons/pdf.png", element.getName());

                    JButton prf = new JButton(pdf);
                    prf.setText(element.getName());
                    prf.addMouseListener(popupListener);
                    prf.setVerticalTextPosition(SwingConstants.BOTTOM);
                    prf.setHorizontalTextPosition(SwingConstants.CENTER);
                    jPanel1.add(prf);
                }

                if(fileType.equals("png")){
                    ImageIcon pdf = new ImageIcon("icons/png.png", element.getName());
                    
                    JButton png = new JButton(pdf);
                    png.setText(element.getName());
                    png.addMouseListener(popupListener);
                    png.setVerticalTextPosition(SwingConstants.BOTTOM);
                    png.setHorizontalTextPosition(SwingConstants.CENTER);
                    jPanel1.add(png);
                }
            }

            else
                System.out.println("I don't know what this is..");

        }

    }

    //invoke PopupListener(Works as a MouseListener too)
    class PopupListener extends MouseAdapter {
        JPopupMenu popup;
        //Constructor
        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        public void mouseClicked(MouseEvent event){
            
            if (event.getClickCount() == 2) {
            //Some print tests, i'll delete it later
            System.out.println("double clicked");
            String folderPath = System.getProperty("user.dir");
            System.out.println("Working Directory = " + folderPath);
            

            //Find the button that has been affected by the event
            JButton buttonClicked = (JButton)event.getSource();
            System.out.println("You selected the archive: " + buttonClicked.getText());

            File f = new File(folderPath);
            File[] Names = f.listFiles();
            
            for(File element: Names){
                System.out.println(element.getName());
                if(element.getName().equals(buttonClicked.getText())){
                    if(element.isDirectory())
                        System.out.println(buttonClicked.getText() + " is a Directoy!");
                }
            }
            
            for(File element: Names){
                if(element.getName().equals(buttonClicked.getText())){
                    if(element.isDirectory())
                        System.out.println(buttonClicked.getText() + " is a Directoy!");
                }
            }
            String newPath = lastPathUsed + "/" + buttonClicked.getText();
            jPanel1.removeAll();
            jPanel1.revalidate();
            FolderContents(newPath);
            
            //Refresh Window
            SwingUtilities.updateComponentTreeUI(Window.this);
        }
    }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
    
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
    
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                    e.getX(), e.getY());
            }
        }
    }
     /***********Action event handler**************/
    public void actionPerformed(ActionEvent e){
        
        String menuString = e.getActionCommand();
    
        if(menuString.equals("Exit") )
            //Exiting only from the specified window
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

        if(menuString.equals("New Window")){
            //Open a new window with contents of users home directory
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new Window().setVisible(true);
                }
            });
        }

        if(menuString.equals("Search")){
            textField1.setVisible(true);

            //Refresh Window
            SwingUtilities.updateComponentTreeUI(this);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textField1 = new java.awt.TextField();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(34, 45, 48));

        //textField1.setText("textField1");

        jButton1.setText("Search");
        jButton1.addActionListener(this);

        jPanel3.setBackground(new java.awt.Color(247, 252, 253));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 22, Short.MAX_VALUE)
        );

        jPanel2.setBackground(new java.awt.Color(242, 251, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jScrollPane1.setBackground(new java.awt.Color(254, 254, 254));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jPanel1.setBackground(new java.awt.Color(250, 253, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 251, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel1);

        jMenuBar1.setBackground(new java.awt.Color(8, 92, 120));

        jMenu1.setText("File");

        jMenuItem1.setText("New Window");
        jMenuItem1.addActionListener(this);
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Exit");
        jMenuItem2.addActionListener(this);
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem3.setText("Cut");
        jMenu2.add(jMenuItem3);

        jMenuItem4.setText("Copy");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem5.setText("Paste");
        jMenu2.add(jMenuItem5);

        jMenuItem6.setText("Rename");
        jMenu2.add(jMenuItem6);

        jMenuItem7.setText("Delete");
        jMenu2.add(jMenuItem7);

        jMenuItem8.setText("Add to Favourites");
        jMenu2.add(jMenuItem8);

        jMenuItem9.setText("Properties");
        jMenu2.add(jMenuItem9);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Search");
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(textField1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addGap(4, 4, 4))
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(textField1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Window().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private java.awt.TextField textField1;
    // End of variables declaration//GEN-END:variables
}
