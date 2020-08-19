import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
public class SwingDemo {
   public static void main(final String args[]) {
      JFrame frame = new JFrame("MenuBar Demo");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      JMenuBar menuBar = new JMenuBar();
      UIManager.put("MenuBar.background", Color.ORANGE);
      JMenu fileMenu = new JMenu("File");
      fileMenu.setMnemonic(KeyEvent.VK_F);
      menuBar.add(fileMenu);
      JMenuItem menuItem1 = new JMenuItem("New", KeyEvent.VK_N);
      fileMenu.add(menuItem1);
      JMenuItem menuItem2 = new JMenuItem("Open File", KeyEvent.VK_O);
      fileMenu.add(menuItem2);
      JMenu editMenu = new JMenu("Edit"); editMenu.setMnemonic(KeyEvent.VK_E); menuBar.add(editMenu);
      JMenuItem menuItem3 = new JMenuItem("Cut", KeyEvent.VK_C); editMenu.add(menuItem3);
      JMenu searchMenu = new JMenu("Search");
      searchMenu.setMnemonic(KeyEvent.VK_S);
      menuBar.add(searchMenu);
      JMenu projectMenu = new JMenu("Project");
      projectMenu.setMnemonic(KeyEvent.VK_P);
      menuBar.add(projectMenu);
      JMenu runMenu = new JMenu("Run");
      runMenu.setMnemonic(KeyEvent.VK_R);
      menuBar.add(runMenu);
      menuBar.revalidate();
      frame.setJMenuBar(menuBar);
      frame.setSize(550, 350);
      frame.setVisible(true);
   }
}