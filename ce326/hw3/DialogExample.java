import java.awt.*;
import java.awt.event.*;

public class DialogExample {
	private static Dialog d;

	public static void main(String args[])
	{
		Frame window = new Frame();

		// Create a modal dialog
		d = new Dialog(window, "Alert", true);

		// Use a flow layout
		d.setLayout( new FlowLayout() );

		// Create an OK button
		Button ok = new Button ("OK");
		ok.addActionListener ( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				// Hide dialog
				DialogExample.d.setVisible(false);
			}
		});

		d.add( new Label ("Click OK to continue"));
		d.add( ok );

		// Show dialog
		d.pack();
		d.setVisible(true);
		System.exit(0);
	}
}