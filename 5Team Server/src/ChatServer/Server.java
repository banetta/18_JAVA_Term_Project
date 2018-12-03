package ChatServer;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class Server extends JFrame implements ActionListener {

	// ���� Frame
	private JPanel contentPane;
	private JTextField textField_port;
	private JTextArea textArea = new JTextArea();
	private JButton button_start = new JButton("���� ����");
	private JButton button_stop = new JButton("���� ����");

	// ��Ʈ��ũ �ڿ�
	private ServerSocket serverSocket;
	private Socket socket;
	private int port = 7777;
	private Vector vector_user = new Vector();
	private Vector vector_room = new Vector();

	// ��Ÿ
	private StringTokenizer st;
	private int error;// =0; //���� Ȯ�� ����

	public Server() { // ������
		init(); // ȭ�� ���� �޼ҵ�
		start(); // ������ ���� �޼ҵ�
	}

	private void start() {// ���� ��ư �׼�
		button_start.addActionListener(this);
		button_stop.addActionListener(this);
	}

	private void init() { // ȭ�� ����

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 612);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 0, 102));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Messenger Server Processer");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("���� ���", Font.BOLD, 18));
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setBounds(17, 15, 394, 21);
		contentPane.add(lblNewLabel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(17, 46, 394, 408);
		contentPane.add(scrollPane);

		scrollPane.setViewportView(textArea);
		textArea.setEditable(false); // ȭ���� ������ �� ������ ��.

		JLabel label = new JLabel("��Ʈ ��ȣ");
		label.setFont(new Font("���� ���", Font.BOLD, 18));
		label.setForeground(new Color(255, 255, 255));
		label.setBounds(17, 469, 78, 21);
		contentPane.add(label);

		textField_port = new JTextField();
		textField_port.setBounds(109, 467, 302, 27);
		contentPane.add(textField_port);
		textField_port.setColumns(10);

		button_start.setFont(new Font("���� ���", Font.BOLD, 18));
		button_start.setBackground(new Color(255, 255, 255));
		button_start.setBounds(17, 507, 179, 29);
		contentPane.add(button_start);

		button_stop.setBackground(new Color(255, 255, 255));
		button_stop.setFont(new Font("���� ���", Font.BOLD, 18));
		button_stop.setBounds(232, 507, 179, 29);
		contentPane.add(button_stop);
		button_stop.setEnabled(false); // ���� ������ �ϱ����� ����������ư ��Ȱ��ȭ

		this.setVisible(true); // true = ȭ�鿡 ���̰� ��
	}

	private void server_start() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
		}

		try {
			serverSocket = new ServerSocket(port); // �Է¹��� ��Ʈ��ȣ ���
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			error = 1;
			JOptionPane.showMessageDialog(null, "�̹� ������� ��Ʈ�Դϴ�.", "�ߺ���Ʈ����", JOptionPane.ERROR_MESSAGE);
		}
		if (serverSocket != null) { // ���������� ��Ʈ�� ������ ���
			error = 0;
			connection();
		}
	}

	public class Socket_thread implements Runnable {
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try { // wait client
					textArea.append("��������������ä�� ����� ���� ����ߡ����������\n");
					socket = serverSocket.accept(); // ����� ���� ���� ���
					UserInfo userInfo = new UserInfo(socket);
					userInfo.start();
				} catch (IOException e) {
					textArea.append("��������������ä�� ������ ���� �Ǿ��������������\n");
					break;
				}
			}
		}
	}

	private void connection() {
		Thread thread = new Thread(new Socket_thread());
		thread.start();

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Server();
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == button_start) {
			port = Integer.parseInt(textField_port.getText().trim());
			server_start();
			if (error == 1) {
				
			} else if (error == 0) {
				button_start.setEnabled(false);
				textField_port.setEditable(false);
				button_stop.setEnabled(true);
				System.out.println("�� ����ä�ü��� ���� ��\n");
			}

		} else if (e.getSource() == button_stop) {
			button_start.setEnabled(true);
			textField_port.setEditable(true);
			button_stop.setEnabled(false);
			try {
				serverSocket.close();
				vector_user.removeAllElements();
				vector_room.removeAllElements();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("�� ����ä�ü��� ����  ��\n");
		}
	}

	private class UserInfo extends Thread {
		private InputStream inputStream;
		private OutputStream outputStream;
		private DataOutputStream dataOutputStream;
		private DataInputStream dataInputStream;
		private Socket socket_user;
		private String Nickname = "";
		private String CurrentRoom = null;
		private boolean RoomCheck = true; // �⺻������ ���� �� �ִ� ����

		public UserInfo(Socket socket) {
			this.socket_user = socket;
			userNetwork();
		}

		public String getNickname() {
			return Nickname;
		}

		public void userNetwork() {
			try {
				inputStream = socket_user.getInputStream();
				dataInputStream = new DataInputStream(inputStream);
				outputStream = socket_user.getOutputStream();
				dataOutputStream = new DataOutputStream(outputStream);
				Nickname = dataInputStream.readUTF();
				textArea.append("�� " + Nickname + "���� ����ä�÷κ� ����\n");

				// when a new user is connected

				BroadCast("NewUser/" + Nickname);
				for (int i = 0; i < vector_user.size(); i++) { // server alert at existing user and send the new user's
																// id
					UserInfo userInfo = (UserInfo) vector_user.elementAt(i);
					this.send_Message("OldUser/" + userInfo.getNickname());
				}
				vector_user.add(this);
				BroadCast("user_list_update/ ");
				SetOldRoom();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "Stream���� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
			}
		}

		private void BroadCast(String str) {
			for (int i = 0; i < vector_user.size(); i++) { // server alert at existing user and send the new user's id
				UserInfo userInfo = (UserInfo) vector_user.elementAt(i);
				userInfo.send_Message(str);
			}
		}

		private void SetOldRoom() {
			for (int i = 0; i < vector_room.size(); i++) {
				RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
				this.send_Message("OldRoom/" + roomInfo.getRoomName());
			}
			this.send_Message("room_list_update/ ");
		}

		private void ExitRoom(UserInfo userInfo) {
			for (int i = 0; i < vector_room.size(); i++) {
				RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
				if (roomInfo.getRoomName().equals(CurrentRoom)) {
					int size = roomInfo.Remove_User(userInfo);
					if (size == 0) {
						BroadCast("ExitRoom/" + CurrentRoom);
						vector_room.remove(i);
						BroadCast("room_list_update/ ");
					} else {
						roomInfo.BroadCast_Room("Chatting/�˸�/******    " + Nickname + "���� �����ϼ̽��ϴ�     ******");
					}
					CurrentRoom = null;
					break;
				}
			}

		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while (true) {
				try {
					String msg = dataInputStream.readUTF();
					textArea.append(Nickname + " : " + msg + "\n");
					InMessage(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					textArea.append(Nickname + " : ����� ���� ������\n");
					ExitRoom(this);
					try {
						dataInputStream.close();
						dataOutputStream.close();
						socket_user.close();
						vector_user.remove(this);
						BroadCast("UserOut/" + Nickname);
						BroadCast("user_list_update/ ");
					} catch (IOException e1) {
					}

					break;
				}

			}
		}

		private void InMessage(String str) { // handle the message from client
			st = new StringTokenizer(str, "/");
			String protocol = st.nextToken();
			String message = st.nextToken();
			System.out.println("protocol : " + protocol);
			if (protocol.equals("Note")) {
				// protocol = Note
				// message = user@contnents
				String note = st.nextToken();
				System.out.println("�޴� ��� : " + message);
				System.out.println("���� ���� : " + note);
				for (int i = 0; i < vector_user.size(); i++) {
					UserInfo userInfo = (UserInfo) vector_user.elementAt(i);
					if (userInfo.Nickname.equals(message)) {
						userInfo.send_Message("Note/" + Nickname + "/" + note);
					}
				}
			} else if (protocol.equals("CreateRoom")) {
				for (int i = 0; i < vector_room.size(); i++) {
					RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
					if (roomInfo.getRoomName().equals(message)) { // ���� �̹� ����
						send_Message("CreateRoomFail/ok");
						RoomCheck = false;
						break;
					}
				}
				if (RoomCheck) {
					RoomInfo roomInfo_new_room = new RoomInfo(message, this);
					vector_room.add(roomInfo_new_room);
					CurrentRoom = message;
					send_Message("CreateRoom/" + message);
					send_Message("Chatting/�˸�/******    " + Nickname + "���� �����ϼ̽��ϴ�     ******");
					BroadCast("NewRoom/" + message);
				} else {
					RoomCheck = true;
				}

			} else if (protocol.equals("Chatting")) {
				String msg = st.nextToken();
				for (int i = 0; i < vector_room.size(); i++) {
					RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
					if (roomInfo.getRoomName().equals(message)) { // �ش� ���� ã����
						roomInfo.BroadCast_Room("Chatting/" + Nickname + "/" + msg);
					}
				}
			} else if (protocol.equals("JoinRoom")) {
				for (int i = 0; i < vector_room.size(); i++) {
					RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
					if (roomInfo.getRoomName().equals(message)) {
						CurrentRoom = message;
						// ���ο� ����ڸ� �˸���
						roomInfo.Add_User(this);
						roomInfo.BroadCast_Room("Chatting/�˸�/******    " + Nickname + "���� �����ϼ̽��ϴ�     *****");
						send_Message("JoinRoom/" + message);
					}
				}
			} else if (protocol.equals("ExitRoom")) {
				ExitRoom(this);
			}
		}

		private void send_Message(String message) {
			try {
				dataOutputStream.writeUTF(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				textArea.append("�޼��� ���� ����");
			}
		}

	}

	private class RoomInfo {

		private String Room_name;
		private Vector vector_room_user = new Vector();

		RoomInfo(String str, UserInfo userInfo) {
			this.Room_name = str;
			this.vector_room_user.add(userInfo);
		}

		public String getRoomName() {
			return Room_name;
		}

		public Vector getRoom_user() {
			return vector_room_user;
		}

		private void Add_User(UserInfo userInfo) {
			this.vector_room_user.add(userInfo);
		}

		public int Remove_User(UserInfo userInfo) {
			this.vector_room_user.remove(userInfo);
			return vector_room_user.size();

		}

		public void BroadCast_Room(String str) { // ���� ���� ��� ������� �˸���.
			for (int i = 0; i < vector_room_user.size(); i++) {
				UserInfo userInfo = (UserInfo) vector_room_user.elementAt(i);
				userInfo.send_Message(str);
			}
		}

	}
}
