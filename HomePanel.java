import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class HomePanel extends JPanel {

	private JTextArea displayTA;
	private ClientGUI gui;
	private JTextField messageTF;

	// Constructor
	public HomePanel(ClientGUI gui) {
		this.gui = gui;

		setLayout(new BorderLayout());

		JPanel west = createWestPanel();
		add(west, BorderLayout.WEST);

		JPanel center = createCenterPanel();
		add(center, BorderLayout.CENTER);

	}

	private JPanel createCenterPanel() {
		JPanel center = new JPanel();

		displayTA = new JTextArea(20, 50);
		displayTA.setEditable(false);

		center.add(displayTA);
		return center;
	}

	private JPanel createWestPanel() {
		JPanel west = new JPanel(new GridLayout(10, 1));

		JButton createRoomB = new JButton("Create Chat Room");
		west.add(createRoomB);
		createRoomB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String roomName = JOptionPane.showInputDialog("Enter chat room name: ");
				gui.createChatRoom(roomName);
			}
		});

		JButton joinRoomB = new JButton("Join Chat Room");
		west.add(joinRoomB);
		joinRoomB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String roomName = JOptionPane.showInputDialog("Enter chat room name: ");
				gui.joinChatRoom(roomName);
			}
		});

		JButton changePasswordB = new JButton("Change Password");
		west.add(changePasswordB);

		return west;
	}

	public void display(String text) {
		displayTA.append(text);
		displayTA.append("\n");
	}

	public void displayLine() {
		display("-----------------");

	}
}
