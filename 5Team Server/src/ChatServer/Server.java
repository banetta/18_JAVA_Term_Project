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
import javax.swing.JProgressBar;
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
	private JProgressBar progressBar = new JProgressBar();

	// ��Ʈ��ũ �ڿ�
	private ServerSocket serverSocket;
	private Socket socket;
	private int port = 0;
	private Vector vector_user = new Vector();
	private Vector vector_room = new Vector();
	private Vector vector_sroom = new Vector();

	// ��Ÿ
	private StringTokenizer st;
	private int error; // ���� Ȯ�� ����

	public Server() { // ������
		super("5Team Server");
		init(); // ȭ�� ���� �޼ҵ�
		start(); // ������ ���� �޼ҵ�
	}

	private void start() {// ���� ��ư �׼�
		textField_port.requestFocus();
		button_start.addActionListener(this);
		button_stop.addActionListener(this);
	}

	private void init() { // ȭ�� ����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 645);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 0, 0));
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
		textArea.setFont(new Font("���� ���", Font.BOLD, 18));
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
		textField_port.setFont(new Font("���� ���", Font.BOLD, 18));
		button_start.setFont(new Font("���� ���", Font.BOLD, 18));
		button_start.setBackground(new Color(255, 255, 255));
		button_start.setBounds(17, 507, 179, 29);
		contentPane.add(button_start);

		button_stop.setBackground(new Color(255, 255, 255));
		button_stop.setFont(new Font("���� ���", Font.BOLD, 18));
		button_stop.setBounds(232, 507, 179, 29);
		contentPane.add(button_stop);
		button_stop.setEnabled(false); // ���� ������ �ϱ����� ����������ư ��Ȱ��ȭ

		progressBar.setBackground(new Color(255, 255, 255));
		progressBar.setFont(new Font("���� ���", Font.BOLD, 18));
		progressBar.setForeground(new Color(0, 0, 0));
		progressBar.setIndeterminate(false); // ���� ������ �ϱ����� �����ִϸ��̼� ��Ȱ��ȭ
		progressBar.setBounds(17, 551, 394, 23);
		contentPane.add(progressBar);

		this.setVisible(true); // true = ȭ�鿡 ���̰� ��
	}

	private void server_start() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
		}

		try {
			serverSocket = new ServerSocket(port); // �Է¹��� ��Ʈ��ȣ ���
			error = 0;
		} catch (IOException e) {
			error = 1;
			JOptionPane.showMessageDialog(null, "�̹� ������� ��Ʈ�Դϴ�.", "�ߺ���Ʈ", JOptionPane.ERROR_MESSAGE);
		}
		if (serverSocket != null) { // ���������� ��Ʈ�� ������ ���
			connection();
		}
	}

	public class Socket_thread implements Runnable {
		public void run() { // �����忡�� ó���� ��
			while (true) {
				try {
					textArea.append("�� ����ä�� ����� ���� ����� ��\n");
					socket = serverSocket.accept(); // ����� ���� ���� ���
					UserInfo userInfo = new UserInfo(socket);
					userInfo.start(); // ��ü�� ������ ����
				} catch (IOException e) {
					textArea.append("�� ����ä�� ������ ���� �Ǿ��� ��\n");
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
		new Server();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button_start) {
			if (textField_port.getText().equals("") || textField_port.getText().length() == 0)// textField�� ���� ������� ������
			{
				JOptionPane.showMessageDialog(null, "��Ʈ��ȣ�� �Է����ּ���.", "��Ʈ���Է�", JOptionPane.ERROR_MESSAGE);
				textField_port.requestFocus(); // ��Ŀ���� �ٽ� textField�� �־��ش�
				error = 1;
			} else {
				try {
					port = Integer.parseInt(textField_port.getText()); // ���ڷ� �Է����� ������ ���� �߻� ��Ʈ�� ���� ����.
					server_start(); // ����ڰ� ����ε� ��Ʈ��ȣ�� �־����� ���� ���������� �޼ҵ� ȣ��
				} catch (Exception er) {
					// ����ڰ� ���ڷ� �Է����� �ʾ����ÿ��� ���Է��� �䱸�Ѵ�
					JOptionPane.showMessageDialog(null, "��Ʈ��ȣ�� ���ڷ� �Է����ּ���", "��Ʈ�Է¿���", JOptionPane.ERROR_MESSAGE);
					textField_port.requestFocus(); // ��Ŀ���� �ٽ� textField�� �־��ش�
					error = 1;
				}
			}
			if (error == 1) {

			} else if (error == 0) {
				button_start.setEnabled(false);
				textField_port.setEditable(false);
				button_stop.setEnabled(true);
				progressBar.setIndeterminate(true);
				System.out.println("�� ����ä�ü��� ���� ��\n");
			}

		} else if (e.getSource() == button_stop) {
			button_start.setEnabled(true);
			textField_port.setEditable(true);
			button_stop.setEnabled(false);
			progressBar.setIndeterminate(false);
			try {
				serverSocket.close();
				vector_user.removeAllElements();
				vector_room.removeAllElements();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("�� ����ä�ü��� ����  ��\n");
		}
	} // �׼� �̺�Ʈ ��

	private class UserInfo extends Thread {
		private InputStream inputStream;
		private OutputStream outputStream;
		private DataOutputStream dataOutputStream;
		private DataInputStream dataInputStream;
		private Socket socket_user;
		private String Nickname = "";
		private String CurrentRoom = null;
		private String CurrentsRoom = null;
		private boolean RoomCheck = true; // �⺻������ ���� �� �ִ� ����
		private boolean sRoomCheck = true;

		public UserInfo(Socket socket) { // ������ �޼ҵ�
			this.socket_user = socket;
			userNetwork();
		}

		public String getNickname() {
			return Nickname;
		}

		public void userNetwork() { // ��Ʈ��ũ �ڿ� ����
			try {
				inputStream = socket_user.getInputStream();
				dataInputStream = new DataInputStream(inputStream);
				outputStream = socket_user.getOutputStream();
				dataOutputStream = new DataOutputStream(outputStream);
				Nickname = dataInputStream.readUTF(); // ������� �г����� �޴´�.
				textArea.append(Nickname + "���� ����ä�÷κ� ����\n");

				BroadCast("NewUser/" + Nickname); // ���� ����ڿ��� �ڽ��� �˸���.
				// �ڽſ��� ��������ڸ� �޾ƿ��� �κ�
				for (int i = 0; i < vector_user.size(); i++) {
					UserInfo userInfo = (UserInfo) vector_user.elementAt(i);
					this.send_Message("OldUser/" + userInfo.getNickname());
				}
				vector_user.add(this); // ����ڿ��� �˸� �� Vector�� �ڽ��� �߰�
				BroadCast("user_list_update/ ");
				SetOldRoom();
				SetOldsRoom();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Stream���� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
			}
		}

		private void BroadCast(String str) { // ��ü ����ڿ��� �޼��� ������ �κ�
			for (int i = 0; i < vector_user.size(); i++) {
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

		private void SetOldsRoom() {
			for (int i = 0; i < vector_sroom.size(); i++) {
				sRoomInfo sroomInfo = (sRoomInfo) vector_sroom.elementAt(i);
				this.send_Message("OldsRoom/" + sroomInfo.getsRoomName());
			}
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
						roomInfo.BroadCast_Room("Chatting/�˸�/�ܡܡܡܡܡ�    " + Nickname + "���� �����ϼ̽��ϴ�.     �ܡܡܡܡܡ�");
					}
					CurrentRoom = null;
					break;
				}
			}

		}

		private void ExitsRoom(UserInfo userInfo) {
			for (int i = 0; i < vector_sroom.size(); i++) {
				sRoomInfo sroomInfo = (sRoomInfo) vector_sroom.elementAt(i);
				if (sroomInfo.getsRoomName().equals(CurrentsRoom)) {
					int size = sroomInfo.Remove_sUser(userInfo);
					if (size == 0) {
						BroadCast("ExitsRoom/" + CurrentsRoom);
						vector_sroom.remove(i);
						BroadCast("sroom_list_update/ ");
					} else {
						sroomInfo.BroadCast_sRoom("Chatting/�˸�/�ܡܡܡܡܡ�    " + Nickname + "���� �����ϼ̽��ϴ�.     �ܡܡܡܡܡ�");
					}
					CurrentsRoom = null;
					break;
				}
			}

		}

		@Override
		public void run() { // Thread���� ó���� ����
			super.run();
			while (true) {
				try {
					String msg = dataInputStream.readUTF();
					textArea.append(Nickname + " : " + msg + "\n"); // ����ڷκ��� ���� �޽���
					InMessage(msg);
				} catch (IOException e) {
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
				} // �޽��� ����
			}
		} // run �޼ҵ� ��

		private void InMessage(String str) { // Ŭ���̾�Ʈ�� ���� ������ �޽��� ó��
			st = new StringTokenizer(str, "/");
			String protocol = st.nextToken();
			String message = st.nextToken();
			System.out.println("protocol : " + protocol);
			if (protocol.equals("Note")) {
				String note = st.nextToken();
				System.out.println("�޴� ��� : " + message);
				System.out.println("���� ���� : " + note);
				// ���Ϳ��� �ش� ����ڸ� ã�Ƽ� �޽��� ����
				for (int i = 0; i < vector_user.size(); i++) {
					UserInfo userInfo = (UserInfo) vector_user.elementAt(i);
					if (userInfo.Nickname.equals(message)) {
						userInfo.send_Message("Note/" + Nickname + "/" + note);
					}
				}
			} // if�� ��
			else if (protocol.equals("CreateRoom")) { // ���� �������� �����ϴ��� Ȯ��
				for (int i = 0; i < vector_room.size(); i++) {
					RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
					if (roomInfo.getRoomName().equals(message)) { // �����ڰ��ϴ� ���� �̹� ������ ���
						send_Message("CreateRoomFail/ok");
						RoomCheck = false;
						break;
					}
				} // for ��
				if (RoomCheck) { // ���� ���� �� ���� ��
					RoomInfo roomInfo_new_room = new RoomInfo(message, this);
					vector_room.add(roomInfo_new_room); // ��ü �� ���Ϳ� ���� �߰�
					CurrentRoom = message;
					send_Message("CreateRoom/" + message);
					send_Message("Chatting/�˸�/�ܡܡܡܡܡ�    " + Nickname + "���� �����ϼ̽��ϴ�.     �ܡܡܡܡܡ�");
					BroadCast("NewRoom/" + message);
				} else {
					RoomCheck = true;
				} // else if �� ��

			} else if (protocol.equals("Chatting")) {
				String msg = st.nextToken();
				for (int i = 0; i < vector_room.size(); i++) {
					RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
					if (roomInfo.getRoomName().equals(message)) { // �ش� ���� ã������
						roomInfo.BroadCast_Room("Chatting/" + Nickname + "/" + msg);
					}
				}
				for (int i = 0; i < vector_sroom.size(); i++) {
					sRoomInfo sroomInfo = (sRoomInfo) vector_sroom.elementAt(i);
					if (sroomInfo.getsRoomName().equals(message)) { // �ش� ���� ã������
						sroomInfo.BroadCast_sRoom("Chatting/" + Nickname + "/" + msg);
					}
				}
			} else if (protocol.equals("JoinRoom")) {
				for (int i = 0; i < vector_room.size(); i++) {
					RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
					if (roomInfo.getRoomName().equals(message)) {
						CurrentRoom = message;
						roomInfo.Add_User(this); // ����� �߰�
						// ���ο� ����ڸ� �˸���
						roomInfo.BroadCast_Room("Chatting/�˸�/�ܡܡܡܡܡ�    " + Nickname + "���� �����ϼ̽��ϴ�.     �ܡܡܡܡܡ�");
						send_Message("JoinRoom/" + message);
					}
				}
			} else if (protocol.equals("JoinsRoom")) {
				String name;
				String passwd;
				for (int i = 0; i < vector_sroom.size(); i++) {
					sRoomInfo sroomInfo = (sRoomInfo) vector_sroom.elementAt(i);
					if (sroomInfo.getsRoomName().equals(message)) {
						name = message;
						passwd = st.nextToken();
						if(passwd.equals(sroomInfo.sRoom_passwd)) {
							CurrentsRoom = message;
							sroomInfo.Add_sUser(this); // ����� �߰�
							// ���ο� ����ڸ� �˸���
							sroomInfo.BroadCast_sRoom("Chatting/�˸�/�ܡܡܡܡܡ�    " + Nickname + "���� �����ϼ̽��ϴ�.     �ܡܡܡܡܡ�");
							send_Message("JoinsRoom/" + name);
						}
						else {
							send_Message("JoinsRoom/" +  passwd + "/" + name);
							ExitsRoom(this);
						}
					}
				}
			} else if (protocol.equals("CreatesRoom")) { // ���� �������� �����ϴ��� Ȯ��
				for (int i = 0; i < vector_sroom.size(); i++) {
					sRoomInfo sroomInfo = (sRoomInfo) vector_sroom.elementAt(i);
					if (sroomInfo.getsRoomName().equals(message)) { // �����ڰ��ϴ� ���� �̹� ������ ���
						send_Message("CreateRoomFail/ok");
						sRoomCheck = false;
						break;
					}
				} // for ��
				if (sRoomCheck) { // ���� ���� �� ���� ��
					String name = message;
					String passwd = st.nextToken();
					sRoomInfo sroomInfo_new_room = new sRoomInfo(name, passwd, this);
					vector_sroom.add(sroomInfo_new_room); // ��ü �� ���Ϳ� ���� �߰�
					CurrentsRoom = message;
					send_Message("CreatesRoom/" + message);
					send_Message("Chatting/�˸�/�ܡܡܡܡܡ�    " + Nickname + "���� �����ϼ̽��ϴ�.     �ܡܡܡܡܡ�");
					BroadCast("NewsRoom/" + message);
				} else {
					sRoomCheck = true;
				} // else if �� ��

			} else if (protocol.equals("ExitRoom")) {
				ExitRoom(this);
			}

			else if (protocol.equals("ExitsRoom")) {
				ExitsRoom(this);
			} else if (protocol.equals("Modify_Name")) {
				String modify = st.nextToken();
				System.out.println("���� �г��� : " + message);
				System.out.println("���� �г��� : " + modify);
				for (int i = 0; i < vector_user.size(); i++) {
					UserInfo userInfo = (UserInfo) vector_user.elementAt(i);
					if (userInfo.Nickname.equals(message)) {
						Nickname = modify;
						send_Message("Modify_Name/" + message + "/" + Nickname);
					}
				}
				BroadCast("NewName/" + message + "/" + Nickname); // ���� ����ڿ��� �ٲ�г����� �˸���.
				BroadCast("user_list_update/ ");

				// SetOldRoom();
			}
		}

		private void send_Message(String message) {
			try {
				dataOutputStream.writeUTF(message);
			} catch (IOException e) {
				textArea.append("�޽��� ���� ����\n");
			}
		}

	}

	private class sRoomInfo {

		private String sRoom_name;
		private String sRoom_passwd;
		private Vector vector_sroom_user = new Vector();

		sRoomInfo(String str, String passwd, UserInfo userInfo) {
			this.sRoom_name = str;
			this.sRoom_passwd = passwd;
			this.vector_sroom_user.add(userInfo);
		}

		public String getsRoomName() {
			return sRoom_name;
		}

		public Vector getsRoom_user() {
			return vector_sroom_user;
		}

		private void Add_sUser(UserInfo userInfo) {
			this.vector_sroom_user.add(userInfo);
		}

		public int Remove_sUser(UserInfo userInfo) {
			this.vector_sroom_user.remove(userInfo);
			return vector_sroom_user.size();

		}

		public void BroadCast_sRoom(String str) { // ���� ���� ��� ������� �˸���.
			for (int i = 0; i < vector_sroom_user.size(); i++) {
				UserInfo userInfo = (UserInfo) vector_sroom_user.elementAt(i);
				userInfo.send_Message(str);
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
