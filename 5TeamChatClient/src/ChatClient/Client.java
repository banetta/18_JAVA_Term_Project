package ChatClient;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.ImagingOpException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

public class Client extends JFrame implements ActionListener, KeyListener {

	// �α��� GUI ����
	private JFrame Login_GUI = new JFrame();
	private JPanel Login_panel;

	private JTextField textField_ip; // ip�޴� �ؽ�Ʈ �ʵ�
	private JTextField textField_port; // port�޴� �ؽ�Ʈ �ʵ�
	private JTextField textField_id; // �г����� �޴� �ؽ�Ʈ �ʵ�
	private JButton button_login = new JButton("�� ��");

	// Main GUI ����
	private JPanel contentPane;
	private JTextField textField_message = new JTextField();
	private JLabel lblNewLabel = new JLabel("�� �� �� �� ��");
	private JButton button_send_note = new JButton("1:1 ����");
	private JButton button_join_room = new JButton("����ä�ù� ����");
	private JButton button_join_sroom = new JButton("���ä�ù� ����");
	private JButton button_create_room = new JButton("����ä�ù� �����");
	private JButton button_create_sroom = new JButton("���ä�ù� �����");
	private JButton button_send_message = new JButton("����");
	private JButton button_modify_name = new JButton("�г��� ����");
	private JList list_user = new JList();
	private JList list_roomname = new JList();
	private JList list_sroomname = new JList();
	private JTextArea textArea_chat = new JTextArea();
	private JLabel label_1 = new JLabel("ä �� â");

	// ��Ʈ��ũ�� ���� �ڿ� ����
	private Socket socket = null;
	private String ip = "127.0.0.1";
	private int port = 7777;
	private String id = "noname";

	private InputStream inputStream;
	private OutputStream outputStream;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;

	// �� �� ������
	private Vector vector_user_list = new Vector();
	private Vector vector_room_list = new Vector();
	private Vector vector_sroom_list = new Vector();
	private StringTokenizer st;
	private String my_room = null; // ���� �ڽ��� ����ä�ù� �̸�
	private String my_sroom = null;
	private String my_sroompasswd = null;
	private int login_enter = 0;
	private int chat_enter = 0;

	public Client() { // ������ �޼ҵ�
		super("5Team Client");
		Login_init(); // Loginâ ȭ�� ���� �޼ҵ�
		Main_init(); // Mainâ ȭ�� ���� �޼ҵ�
		start(); // ACTION
	}

	private void start() { // ACTION
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
		}
		button_login.addActionListener(this); // �α��� ��ư ������
		button_send_note.addActionListener(this); // 1:1 ���� ������
		button_join_room.addActionListener(this); // ����ä�ù� ���� ��ư ������
		button_create_room.addActionListener(this); // ����ä�ù� ����� ��ư ������
		button_join_sroom.addActionListener(this); // ���ä�ù� ���� ��ư ������
		button_create_sroom.addActionListener(this); // ���ä�ù� ����� ��ư ������
		button_send_message.addActionListener(this);// ä�� ���� ��ư ������
		button_modify_name.addActionListener(this); // �г��� ���� ��ư ������
		textField_message.addKeyListener(this); // �޽��� ���� �ؽ�Ʈ�ʵ� Ű ������
		textField_ip.addKeyListener(this);
		textField_port.addKeyListener(this);
		textField_id.addKeyListener(this);

	}

	private void Main_init() { // Main GUI

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1040, 729);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 0, 0));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("���� ���", Font.BOLD, 18));
		lblNewLabel.setBounds(861, 16, 140, 21);
		contentPane.add(lblNewLabel);

		list_user.setBounds(861, 52, 140, 203);
		contentPane.add(list_user);
		list_user.setListData(vector_user_list);
		list_user.setFont(new Font("���� ���", Font.BOLD, 18));

		button_send_note.setForeground(new Color(0, 0, 0));
		button_send_note.setBackground(new Color(255, 255, 255));
		button_send_note.setFont(new Font("���� ���", Font.BOLD, 18));
		button_send_note.setBounds(862, 266, 139, 29);
		contentPane.add(button_send_note); // 1:1 ��ȭ

		JLabel label = new JLabel("���� ä�ù� ���");
		label.setForeground(new Color(255, 255, 255));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("���� ���", Font.BOLD, 18));
		label.setBounds(17, 16, 200, 21);
		contentPane.add(label);

		list_roomname.setBounds(17, 52, 200, 202);
		contentPane.add(list_roomname);
		list_roomname.setListData(vector_room_list);
		list_roomname.setFont(new Font("���� ���", Font.BOLD, 18));

		button_join_room.setBackground(new Color(255, 255, 255));
		button_join_room.setForeground(new Color(0, 0, 0));
		button_join_room.setFont(new Font("���� ���", Font.BOLD, 18));
		button_join_room.setBounds(18, 266, 199, 29);
		contentPane.add(button_join_room); // ä�ù� ����

		button_create_room.setBackground(new Color(255, 255, 255));
		button_create_room.setForeground(new Color(0, 0, 0));
		button_create_room.setFont(new Font("���� ���", Font.BOLD, 18));
		button_create_room.setBounds(17, 306, 200, 29);
		contentPane.add(button_create_room); // �游���

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(234, 52, 610, 559);
		contentPane.add(scrollPane);

		scrollPane.setViewportView(textArea_chat);
		textArea_chat.setFont(new Font("���� ���", Font.BOLD, 18));
		textArea_chat.setEditable(false); // ����ä��â ���� false

		textField_message.setBounds(234, 629, 385, 29);
		contentPane.add(textField_message);
		// textField_message.setColumns(10);
		textField_message.setEnabled(false); // �޽��� ��ư false
		textField_message.setFont(new Font("���� ���", Font.BOLD, 18));

		button_send_message.setForeground(new Color(0, 0, 0));
		button_send_message.setBackground(new Color(255, 255, 255));
		button_send_message.setFont(new Font("���� ���", Font.BOLD, 18));
		button_send_message.setBounds(626, 629, 76, 29);
		contentPane.add(button_send_message);
		button_send_message.setEnabled(false); // �������ư false

		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setForeground(Color.WHITE);
		label_1.setFont(new Font("���� ���", Font.BOLD, 18));
		label_1.setBounds(234, 17, 610, 21);
		contentPane.add(label_1);

		button_create_sroom.setForeground(Color.BLACK);
		button_create_sroom.setFont(new Font("���� ���", Font.BOLD, 18));
		button_create_sroom.setBackground(Color.WHITE);
		button_create_sroom.setBounds(17, 622, 200, 29);
		contentPane.add(button_create_sroom);

		JLabel label_2 = new JLabel("���ä�ù� ���");
		label_2.setHorizontalAlignment(SwingConstants.CENTER);
		label_2.setForeground(Color.WHITE);
		label_2.setFont(new Font("���� ���", Font.BOLD, 18));
		label_2.setBounds(17, 350, 200, 21);
		contentPane.add(label_2);

		list_sroomname.setFont(new Font("���� ���", Font.BOLD, 18));
		list_sroomname.setValueIsAdjusting(true);
		list_sroomname.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list_sroomname.setBounds(17, 386, 200, 185);
		contentPane.add(list_sroomname);

		button_join_sroom.setForeground(Color.BLACK);
		button_join_sroom.setFont(new Font("���� ���", Font.BOLD, 18));
		button_join_sroom.setBackground(Color.WHITE);
		button_join_sroom.setBounds(17, 582, 200, 29);
		contentPane.add(button_join_sroom);

		button_modify_name.setForeground(new Color(0, 0, 0));
		button_modify_name.setBackground(new Color(255, 255, 255));
		button_modify_name.setFont(new Font("���� ���", Font.BOLD, 18));
		button_modify_name.setBounds(709, 629, 136, 29);
		// button_modify_name.setEnabled(false);
		contentPane.add(button_modify_name);
		this.setVisible(false);
	}

	private void Login_init() { // �α��� GUI

		Login_GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Login_GUI.setBounds(100, 100, 450, 364);

		Login_panel = new JPanel();

		Login_panel.setBackground(new Color(0, 0, 0));
		Login_panel.setForeground(Color.BLACK);
		Login_panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		Login_GUI.setContentPane(Login_panel);
		Login_panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("������ ����IP");
		lblNewLabel.setBackground(Color.WHITE);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setFont(new Font("���� ���", Font.BOLD, 18));
		lblNewLabel.setBounds(17, 98, 152, 21);
		Login_panel.add(lblNewLabel);

		JLabel lblPort = new JLabel("������ Port��ȣ");
		lblPort.setForeground(Color.WHITE);
		lblPort.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPort.setFont(new Font("���� ���", Font.BOLD, 18));
		lblPort.setBounds(17, 156, 152, 21);
		Login_panel.add(lblPort);

		textField_ip = new JTextField();
		textField_ip.setBounds(182, 96, 229, 27);
		Login_panel.add(textField_ip);
		textField_ip.setColumns(10);
		textField_ip.setFont(new Font("���� ���", Font.BOLD, 18));

		textField_port = new JTextField();
		textField_port.setColumns(10);
		textField_port.setBounds(182, 154, 229, 27);
		textField_port.setFont(new Font("���� ���", Font.BOLD, 18));
		Login_panel.add(textField_port);

		JLabel lblteamChatServer = new JLabel("5Team Chat Program");
		lblteamChatServer.setForeground(Color.WHITE);
		lblteamChatServer.setIcon(new ImageIcon(
				"D:\\\uB300\uD559\uC790\uB8CC\\2\uD559\uB1442\uD559\uAE30\\\uC790\uBC14\\\uD300 \uD504\uB85C\uC81D\uD2B8\\\uBA54\uC2E0\uC800\uC544\uC774\uCF582.jfif"));
		lblteamChatServer.setHorizontalAlignment(SwingConstants.CENTER);
		lblteamChatServer.setFont(new Font("���� ���", Font.BOLD, 24));
		lblteamChatServer.setBounds(17, 15, 394, 68);
		Login_panel.add(lblteamChatServer);

		JLabel label = new JLabel("�г���");
		label.setForeground(Color.WHITE);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setFont(new Font("���� ���", Font.BOLD, 18));
		label.setBounds(17, 214, 152, 21);
		Login_panel.add(label);

		textField_id = new JTextField();
		textField_id.setColumns(10);
		textField_id.setBounds(182, 212, 229, 27);
		textField_id.setFont(new Font("���� ���", Font.BOLD, 18));
		Login_panel.add(textField_id);

		button_login.setForeground(new Color(0, 0, 0));
		button_login.setBackground(new Color(255, 255, 255));
		button_login.setFont(new Font("���� ���", Font.BOLD, 18));
		button_login.setBounds(155, 264, 125, 29);
		Login_panel.add(button_login);

		Login_GUI.setVisible(true);
		// true = ȭ�鿡 ���̰�
	}

	private void network() {
		try {
			socket = new Socket(ip, port);
			if (socket != null) { // ���������� ������ ����Ǿ��� ���
				connection();
			}
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "���� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "���� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void connection() {
		try {
			inputStream = socket.getInputStream();
			dataInputStream = new DataInputStream(inputStream);
			outputStream = socket.getOutputStream();
			dataOutputStream = new DataOutputStream(outputStream);
		} catch (IOException e) { // ���� ó�� �κ�
			JOptionPane.showMessageDialog(null, "���� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
		} // Stream ���� ��
		this.setVisible(true); // main ui ǥ��
		this.Login_GUI.setVisible(false);
		login_enter = 1;
		send_message(id); // ó�� ���ӽ� �г��� ����
		vector_user_list.add("[��] " + id); // ����� ���� ����Ʈ�� ����� �߰�
		Thread thread = new Thread(new Socket_thread());
		thread.start();

	}

	public class Socket_thread implements Runnable {
		public void run() {
			while (true) {
				try {

					InMessage(dataInputStream.readUTF());
				} catch (IOException e) {
					try {
						outputStream.close();
						inputStream.close();
						dataInputStream.close();
						dataOutputStream.close();
						socket.close();

						JOptionPane.showMessageDialog(null, "������ ���� ������", "�˸�", JOptionPane.ERROR_MESSAGE);
					} catch (IOException e1) {
					}
					break;

				}
			}
		}
	}

	private void InMessage(String str) { // ������ ���� ������ ��� �޽���
		st = new StringTokenizer(str, "/");
		String protocol = st.nextToken();
		String message = st.nextToken();
		System.out.println("�������� : " + protocol);
		System.out.println("���� : " + message);

		if (protocol.equals("NewUser")) { // ���ο� ������
			vector_user_list.add(message);
		} else if (protocol.equals("NewName")) {
			for (int i = 0; i < vector_user_list.size(); i++) {
				if (vector_user_list.get(i).equals("[��] " + message)) {
					String nn = st.nextToken();
					vector_user_list.set(i, "[��] " + nn);
				} else if (vector_user_list.get(i).equals(message)) {
					String nn = st.nextToken();
					vector_user_list.set(i, nn);
				}
			}
			// list_user.setListData(vector_user_list);

		} else if (protocol.equals("OldUser")) {
			vector_user_list.add(message);
		} else if (protocol.equals("Note")) {
			String note = st.nextToken();
			System.out.println(message + " ����ڿ��� �� �޽��� " + note);
			String note2 = null;
			while (note2 == null || note2.equals("")) {
				note2 = JOptionPane.showInputDialog(message + " : " + note); // inputdialog
				if (note2 == null) {
					JOptionPane.showMessageDialog(null, "���� ���� ������ ����ϼ̽��ϴ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (note2.equals(""))
					JOptionPane.showMessageDialog(null, "���� ���峻���� �Է��Ͻÿ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
			}
			if (note2 != null) {
				send_message("Note/" + message + "/" + note2);
			}
			System.out.println("�޴»�� : " + message + " | ���� ���� : " + note2); // support
			// dialog
		} else if (protocol.equals("user_list_update")) {
			list_user.setListData(vector_user_list);
		} else if (protocol.equals("CreateRoom")) { // ����ä�ù��� ������� ��
			my_room = message;
			JOptionPane.showMessageDialog(null, "ä�ù濡 �����߽��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);

		} else if (protocol.equals("CreateRoomFail")) { // ����ä�ù� ����� �����Ͽ��� ���
			JOptionPane.showMessageDialog(null, "�� ����� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
		} else if (protocol.equals("NewRoom")) { // ���ο� ����ä�ù��� ������� ��
			// lblNewLabel.setText("�� �� �� ��");
			vector_room_list.add(message);
			list_roomname.setListData(vector_room_list);
		} else if (protocol.equals("OldRoom")) {
			vector_room_list.add(message);
		} else if (protocol.equals("room_list_update")) {
			list_roomname.setListData(vector_room_list);
		} else if (protocol.equals("JoinRoom")) {
			my_room = message;

			JOptionPane.showMessageDialog(null, "ä�ù濡 �����߽��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
			chat_enter = 1;

		} else if (protocol.equals("ExitRoom")) {

			textField_message.setEnabled(false); // �޽��� ��ư false
			vector_room_list.remove(message);

		} ////////////////////////// ���
		else if (protocol.equals("CreatesRoom")) { // ���ä�ù��� ������� ��
			my_sroom = message;
			JOptionPane.showMessageDialog(null, "ä�ù濡 �����߽��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);

		} else if (protocol.equals("NewsRoom")) { // ���ο� ���ä�ù��� ������� ��
			// lblNewLabel.setText("�� �� �� ��");
			vector_sroom_list.add(message);
			list_sroomname.setListData(vector_sroom_list);
		} else if (protocol.equals("OldsRoom")) {
			vector_sroom_list.add(message);
			list_sroomname.setListData(vector_sroom_list);
		} else if (protocol.equals("sroom_list_update")) {
			list_sroomname.setListData(vector_sroom_list);
		} else if (protocol.equals("JoinsRoom")) {
			my_sroom = message;
			if (my_sroom.equals(my_sroompasswd)) {
				my_sroom = st.nextToken();
				textField_message.setEnabled(false);
				// vector_sroom_list.remove(my_sroom);
				JOptionPane.showMessageDialog(null, "�� ��й�ȣ�� ��ġ���� �ʽ��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
				send_message("ExitsRoom/" + my_sroom);
				my_sroom = null;
				my_sroompasswd = null;
				return;
			}
			JOptionPane.showMessageDialog(null, "ä�ù濡 �����߽��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
			chat_enter = 2;

		} else if (protocol.equals("ExitsRoom")) {
			textField_message.setEnabled(false); // �޽��� ��ư false
			vector_sroom_list.remove(message);
		} else if (protocol.equals("Chatting")) {
			String msg = st.nextToken();
			textArea_chat.append(message + " : " + msg + "\n");
		} else if (protocol.equals("UserOut")) {
			vector_user_list.remove(message);
		} else if (protocol.equals("Modify_Name")) {
			String mod = st.nextToken();
			System.out.println(message + "���� " + mod + "��  �г����� �����Ͽ����ϴ�.\n");
			list_user.setListData(vector_user_list);
		}
	}

	private String getLocalServerIp() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
							&& inetAddress.isSiteLocalAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}

	private void send_message(String message) { // �������� �޽����� ������ �κ�
		try {
			dataOutputStream.writeUTF(message);
		} catch (IOException e) { // ���� ó�� �κ�
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Client();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button_login) { // button_login = �α��� ��ư
			System.out.println("�α��� �õ�");
			if (textField_ip.getText().length() == 0) {
				textField_ip.setText("IP�� �Է����ּ���");
				textField_ip.requestFocus();
			} else if (textField_port.getText().length() == 0) {
				textField_port.setText("Port��ȣ�� �Է����ּ���");
				textField_port.requestFocus();
			} else if (textField_id.getText().length() == 0) {
				textField_id.setText("�г����� �Է����ּ���");
				textField_id.requestFocus();
			} else {
				ip = textField_ip.getText().trim(); // ip�� �޾ƿ��� �κ�
				port = Integer.parseInt(textField_port.getText().trim()); // int������ ����ȯ
				id = textField_id.getText().trim();
				network();
				login_enter = 1;
			}
		} else if (e.getSource() == button_send_note) {
			System.out.println("send_note");
			String user = (String) list_user.getSelectedValue();
			if (user == null) {
				JOptionPane.showMessageDialog(null, "���� ����� �����Ͻÿ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (user.equals("[��] " + id)) {
				JOptionPane.showMessageDialog(null, "�ڽſ��Դ� ������ ���� �� �����ϴ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String note = null;
			while (note == null || note.equals("")) {
				note = JOptionPane.showInputDialog(user + "���� ���� �޽���"); // inputdialog
				if (note == null) {
					JOptionPane.showMessageDialog(null, "���� ������ ����ϼ̽��ϴ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (note.equals(""))
					JOptionPane.showMessageDialog(null, "���� ������ �Է��Ͻÿ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
			}
			if (note != null) {
				send_message("Note/" + user + "/" + note); // ex) Note/User2/���� User1�̾�
			}
			System.out.println("�޴»�� : " + user + " | ���� ���� : " + note);
		} else if (e.getSource() == button_join_room) {
			String JoinRoom = (String) list_roomname.getSelectedValue();

			if (my_room != null) {
				if (my_room.equals(JoinRoom)) {
					JOptionPane.showMessageDialog(null, "���� ä�ù��Դϴ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					JOptionPane.showMessageDialog(null, "�̹� ä�ù濡 �������Դϴ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// send_message("ExitRoom/" + my_room);
				// chat_enter = 0;
				// textArea_chat.setText("");
			} else if (JoinRoom == null) {
				JOptionPane.showMessageDialog(null, "������ ä�ù��� �����ϴ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
			} else {
				send_message("JoinRoom/" + JoinRoom);
				label_1.setText("ä �� â [" + JoinRoom + "]");
				lblNewLabel.setText("�� �� �� �� ��");
				button_create_sroom.setEnabled(false);
				button_join_sroom.setEnabled(false);
				button_create_room.setText("����ä�ù� ������");
				button_send_message.setEnabled(true);
				// button_modify_name.setEnabled(true);
				textField_message.setEnabled(true);
				System.out.println("join_room");

			}

		} else if (e.getSource() == button_create_room) {
			String roomname = null;
			if (my_room == null) {
				while (roomname == null || roomname.equals("")) {
					roomname = JOptionPane.showInputDialog("�� �̸�");
					if (roomname == null) {
						JOptionPane.showMessageDialog(null, "�游��⸦ ����ϼ̽��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
						return;
					} else if (roomname.equals("")) {
						JOptionPane.showMessageDialog(null, "�� �̸��� �Է��Ͻÿ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}

			if (roomname != null) {
				send_message("CreateRoom/" + roomname);
				chat_enter = 1;
				button_send_message.setEnabled(true);
				// button_modify_name.setEnabled(true);
				textField_message.setEnabled(true);
				button_login.setEnabled(false);
				button_create_room.setText("����ä�ù� ������");
				button_create_sroom.setEnabled(false);
				button_join_sroom.setEnabled(false);
				label_1.setText("ä �� â [" + roomname + "]");
				lblNewLabel.setText("�� �� �� �� ��");
			} else {
				send_message("ExitRoom/" + my_room);
				textField_message.setText("");
				textArea_chat.setText("");
				JOptionPane.showMessageDialog(null, "ä�ù濡�� �����߽��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
				button_send_message.setEnabled(false);
				// button_modify_name.setEnabled(false);
				my_room = null;
				chat_enter = 0;
				lblNewLabel.setText("�� ��  �� �� ��");
				label_1.setText("ä �� â");
				button_create_room.setText("����ä�ù� �����");
				button_create_sroom.setEnabled(true);
				button_join_sroom.setEnabled(true);

			}
			System.out.println("create_room");
		} else if (e.getSource() == button_send_message) {
			System.out.println("send_message");
			if (my_room == null && my_sroom == null) {
				JOptionPane.showMessageDialog(null, "ä�ù濡 �������ּ���", "�˸�", JOptionPane.ERROR_MESSAGE);
				return;
			} else if (my_room != null || my_sroom != null) {
				// ä�� + ����ä�ù� �̸� + ����
				String temp = textField_message.getText().trim();
				if (temp.equals("")) {
					textField_id.setText("���� ä�ø޽����� �Է����ּ���");
					textField_message.setText("");
					textField_message.requestFocus();

				} else if (chat_enter == 1) {
					send_message("Chatting/" + my_room + "/" + textField_message.getText().trim());
					textField_message.setText("");
					textField_message.requestFocus();
				} else if (chat_enter == 2) {
					send_message("Chatting/" + my_sroom + "/" + textField_message.getText().trim());
					textField_message.setText("");
					textField_message.requestFocus();
				}
			}
		} else if (e.getSource() == button_modify_name) {
			System.out.println("modify_name"); // �г��� ����
			String name = null;
			while (name == null || name.equals("")) {
				name = JOptionPane.showInputDialog("������ �г����� �Է��Ͻÿ�.");
				if (name == null) {
					JOptionPane.showMessageDialog(null, "�г��� ������ ����ϼ̽��ϴ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (name.equals(""))
					JOptionPane.showMessageDialog(null, "������ �г����� �Է��Ͻÿ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
				else {
					for (int i = 0; i < vector_user_list.size(); i++) {
						if (vector_user_list.get(i).equals("[��] " + name)) {
							JOptionPane.showMessageDialog(null, "���� �г��Ӱ� �����ϴ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
							return;
						} else if (vector_user_list.get(i).equals(name)) {
							JOptionPane.showMessageDialog(null, "�̹� �����ϴ� �г����Դϴ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				}
			}

			if (name != null) {
				send_message("Modify_Name/" + id + "/" + name);
				id = name;
				System.out.println(vector_user_list.elementAt(0));
			}
		}
		/////////////// ���//////
		else if (e.getSource() == button_join_sroom) {
			String JoinsRoom = (String) list_sroomname.getSelectedValue();

			if (my_sroom != null) {
				if (my_sroom.equals(JoinsRoom)) {
					JOptionPane.showMessageDialog(null, "���� ä�ù��Դϴ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					JOptionPane.showMessageDialog(null, "�̹� ä�ù濡 �������Դϴ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// send_message("ExitRoom/" + my_room);
				// chat_enter = 0;
				// textArea_chat.setText("");
			} else if (JoinsRoom == null) {
				JOptionPane.showMessageDialog(null, "������ ä�ù��� �����ϴ�.", "�˸�", JOptionPane.ERROR_MESSAGE);
			} else if (my_sroompasswd == null) {
				my_sroompasswd = JOptionPane.showInputDialog("�� ��й�ȣ�� �Է��Ͻÿ�.");
				send_message("JoinsRoom/" + JoinsRoom + "/" + my_sroompasswd);
				label_1.setText("ä �� â [" + JoinsRoom + "]");
				button_create_room.setEnabled(false);
				button_join_room.setEnabled(false);
				lblNewLabel.setText("�� �� �� �� ��");
				button_create_sroom.setText("���ä�ù� ������");
				button_send_message.setEnabled(true);
				textField_message.setEnabled(true);
				System.out.println("join_sroom");
			}
		} else if (e.getSource() == button_create_sroom) {
			String sroomname = null;
			if (my_sroom == null) {
				while (sroomname == null || sroomname.equals("")) {
					sroomname = JOptionPane.showInputDialog("�� �̸�");
					if (sroomname == null) {
						JOptionPane.showMessageDialog(null, "�游��⸦ ����ϼ̽��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
						return;
					} else if (sroomname.equals("")) {
						JOptionPane.showMessageDialog(null, "�� �̸��� �Է��Ͻÿ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			if (my_sroompasswd == null) {
				while (my_sroompasswd == null || my_sroompasswd.equals("")) {
					my_sroompasswd = JOptionPane.showInputDialog("�� ��й�ȣ");
					if (my_sroompasswd == null) {
						JOptionPane.showMessageDialog(null, "�游��⸦ ����ϼ̽��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
						return;
					} else if (my_sroompasswd.equals("")) {
						JOptionPane.showMessageDialog(null, "�� ��й�ȣ�� �Է��Ͻÿ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			if (sroomname != null && my_sroompasswd != null) {
				send_message("CreatesRoom/" + sroomname + "/" + my_sroompasswd);
				chat_enter = 2;
				button_create_sroom.setText("���ä�ù� ������");
				button_create_room.setEnabled(false);
				button_join_room.setEnabled(false);
				label_1.setText("ä �� â [" + sroomname + "]");
				lblNewLabel.setText("�� �� �� �� ��");
				button_send_message.setEnabled(true);
				// button_modify_name.setEnabled(true);
				textField_message.setEnabled(true);
				button_login.setEnabled(false);
			} else {
				send_message("ExitsRoom/" + my_sroom);
				button_send_message.setEnabled(false);
				textField_message.setText("");
				textArea_chat.setText("");
				JOptionPane.showMessageDialog(null, "ä�ù濡�� �����߽��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
				my_sroom = null;
				my_sroompasswd = null;
				chat_enter = 0;
				lblNewLabel.setText("�� ��  �� �� ��");
				label_1.setText("ä �� â");
				button_create_sroom.setText("���ä�ù� �����");
				button_create_room.setEnabled(true);
				button_join_room.setEnabled(true);

			}
			System.out.println("create_sroom");
		}

	}

	public void keyPressed(KeyEvent arg0) { // ������ ��
	}

	public void keyReleased(KeyEvent e) { // ������ ���� ��

		if (e.getKeyCode() == 10 && login_enter == 0) { // enter
			System.out.println("�α��� �õ�");
			if (textField_ip.getText().length() == 0) {
				textField_ip.setText("IP�� �Է����ּ���");
				textField_ip.requestFocus();
			} else if (textField_port.getText().length() == 0) {
				textField_port.setText("Port��ȣ�� �Է����ּ���");
				textField_port.requestFocus();
			} else if (textField_id.getText().length() == 0) {
				textField_id.setText("�г����� �Է����ּ���");
				textField_id.requestFocus();
			} else {
				ip = textField_ip.getText().trim(); // ip�� �޾ƿ��� �κ�
				port = Integer.parseInt(textField_port.getText().trim()); // int������ ����ȯ
				id = textField_id.getText().trim();
				network();
				login_enter = 1;
			}
		}

		if (e.getKeyCode() == 10 && (chat_enter == 1 || chat_enter == 2)) { // enter && chat_enter == 1
			String temp = textField_message.getText().trim();
			if (temp.equals("")) {
				textField_id.setText("���� ä�ø޽����� �Է����ּ���");
				textField_message.requestFocus();
			} else if (chat_enter == 1) {
				send_message("Chatting/" + my_room + "/" + textField_message.getText().trim());
				textField_message.setText("");
				textField_message.requestFocus();
			} else if (chat_enter == 2) {
				send_message("Chatting/" + my_sroom + "/" + textField_message.getText().trim());
				textField_message.setText("");
				textField_message.requestFocus();
			}
		}
	}

	public void keyTyped(KeyEvent arg0) { // Ÿ����

	}

}
