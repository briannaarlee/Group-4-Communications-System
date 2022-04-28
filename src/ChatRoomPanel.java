import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * 
 * @author Brianna Jarvis
 *
 */
public class ChatRoomPanel extends JPanel implements FocusListener{

	private JTextArea chatMessagesTA;
	private ClientGUI gui;
	private JTextField messageTF;
	private Socket socket;
	private ObjectInputStream objectInputStream;
	private Thread receiveMessagesThread;
	private ChatRoomPanel.ChatRoomMessageReceiver messageReceiver;
	private String currentRoom;
	private String currentUser;

	// Constructor
	public ChatRoomPanel(ClientGUI gui, Socket socket, ObjectInputStream objectInputStream, String currentUser, String currentRoom) {
		this.gui = gui;
		this.socket = socket;
		this.objectInputStream = objectInputStream;
		this.currentUser = currentUser;
		this.currentRoom = currentRoom;

		setLayout(new BorderLayout());

		JPanel west = createWestPanel();
		add(west, BorderLayout.WEST);

		JPanel center = createCenterPanel();
		add(center, BorderLayout.CENTER);

		JPanel south = createSouthPanel();
		add(south, BorderLayout.SOUTH);
		
		displayMessage("Joined chat room: " + currentRoom);
		displayLine();

		// Create parallel process (thread) to read/receive message from server.
		messageReceiver = new ChatRoomMessageReceiver(socket, objectInputStream, this);
		// start the thread
		receiveMessagesThread = new Thread(messageReceiver);
		receiveMessagesThread.start();

	}

	private JPanel createSouthPanel() {
		JPanel south = new JPanel();

		messageTF = new JTextField(25);
		JButton sendB = new JButton("Send");

		south.add(new JLabel("Enter Message: "));
		south.add(messageTF);
		south.add(sendB);

		sendB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String userMessage = messageTF.getText();
				// check if the user message is either blank or empty
				if (userMessage.isBlank() || userMessage.isEmpty()) {
					JOptionPane.showMessageDialog(gui, "You must enter a non-blank message.");
					return;
				}
				gui.sendMessage("CHATROOM", userMessage);
				//clear message input textfield
				messageTF.setText("");
			}
		});

		// keep the send message DISABLED by default
		// to be enabled ONLY AFTER chatroom
//		enableSendingMessage(false);

		return south;
	}

	private JPanel createCenterPanel() {
		JPanel center = new JPanel();

		chatMessagesTA = new JTextArea(20, 50);
		chatMessagesTA.setEditable(false);

		center.add(chatMessagesTA);
		return center;
	}

	private JPanel createWestPanel() {
		JPanel west = new JPanel(new GridLayout(10, 1));

		JButton leaveRoomB = new JButton("Leave Chat Room");
		west.add(leaveRoomB);
		leaveRoomB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String error = gui.leaveChatRoom();
				if(error != null) {
					displayLine();
					displayMessage("FAILED to Leave Chat Room: " + currentRoom);
					displayMessage("Reason: " + error);
				}
			}
		});

		JButton lockRoomB = new JButton("Lock Chat Room");
		west.add(lockRoomB);

		JButton unlockRoomB = new JButton("Unlock Chat Room");
		west.add(unlockRoomB);

		return west;
	}

	public void displayMessage(String text) {
		chatMessagesTA.append(text);
		chatMessagesTA.append("\n");
		messageTF.requestFocus();
	}

	public void displayLine() {
		displayMessage("-----------------");

	}

//---INNNER CLASS

	private class ChatRoomMessageReceiver implements Runnable {
		private final Socket clientSocket;
		private ObjectInputStream objectInputStream;
		private ChatRoomPanel chatRoomPanel;
		private boolean running = true;

		public ChatRoomMessageReceiver(Socket socket, ObjectInputStream objectInputStream, ChatRoomPanel chatRoomPanel) {
			this.clientSocket = socket;
			this.objectInputStream = objectInputStream;
			this.chatRoomPanel = chatRoomPanel;
			System.out.println("Creating the receive thread...");

		}

		public void run() {
			try {
				System.out.println("TRYING");
				Message returnedMessage;

				while (running) {
					returnedMessage = (Message) objectInputStream.readObject();
					System.out.println(returnedMessage.getText());
					displayChatRoomMessage(returnedMessage.getText());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void stopReceivingMessages() {
			running = false;
		}
	}

	public void stopReceivingMessages() {
		messageReceiver.stopReceivingMessages();
	}

	public void displayChatRoomMessage(String receivedMessage) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				displayMessage(receivedMessage);
			}
		});
		
	}

	@Override
	public void focusGained(FocusEvent e) {
		messageTF.requestFocus();
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

}
