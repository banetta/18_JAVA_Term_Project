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

	// 로그인 GUI 변수
	private JFrame Login_GUI = new JFrame();
	private JPanel Login_panel;

	private JTextField textField_ip; // ip받는 텍스트 필드
	private JTextField textField_port; // port받는 텍스트 필드
	private JTextField textField_id; // 닉네임을 받는 텍스트 필드
	private JButton button_login = new JButton("접 속");

	// Main GUI 변수
	private JPanel contentPane;
	private JTextField textField_message = new JTextField();
	private JLabel lblNewLabel = new JLabel("로 비 접 속 자");
	private JButton button_send_note = new JButton("1:1 쪽지");
	private JButton button_join_room = new JButton("오픈채팅방 참여");
	private JButton button_join_sroom = new JButton("비밀채팅방 참여");
	private JButton button_create_room = new JButton("오픈채팅방 만들기");
	private JButton button_create_sroom = new JButton("비밀채팅방 만들기");
	private JButton button_send_message = new JButton("전송");
	private JButton button_modify_name = new JButton("닉네임 변경");
	private JList list_user = new JList();
	private JList list_roomname = new JList();
	private JList list_sroomname = new JList();
	private JTextArea textArea_chat = new JTextArea();
	private JLabel label_1 = new JLabel("채 팅 창");

	// 네트워크를 위한 자원 변수
	private Socket socket = null;
	private String ip = "127.0.0.1";
	private int port = 7777;
	private String id = "noname";

	private InputStream inputStream;
	private OutputStream outputStream;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;

	// 그 외 변수들
	private Vector vector_user_list = new Vector();
	private Vector vector_room_list = new Vector();
	private Vector vector_sroom_list = new Vector();
	private StringTokenizer st;
	private String my_room = null; // 현재 자신의 오픈채팅방 이름
	private String my_sroom = null;
	private String my_sroompasswd = null;
	private int login_enter = 0;
	private int chat_enter = 0;

	public Client() { // 생성자 메소드
		super("5Team Client");
		Login_init(); // Login창 화면 구성 메소드
		Main_init(); // Main창 화면 구성 메소드
		start(); // ACTION
	}

	private void start() { // ACTION
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
		}
		button_login.addActionListener(this); // 로그인 버튼 리스너
		button_send_note.addActionListener(this); // 1:1 쪽지 리스너
		button_join_room.addActionListener(this); // 오픈채팅방 참여 버튼 리스너
		button_create_room.addActionListener(this); // 오픈채팅방 만들기 버튼 리스너
		button_join_sroom.addActionListener(this); // 비밀채팅방 참여 버튼 리스너
		button_create_sroom.addActionListener(this); // 비밀채팅방 만들기 버튼 리스너
		button_send_message.addActionListener(this);// 채팅 전송 버튼 리스너
		button_modify_name.addActionListener(this); // 닉네임 변경 버튼 리스너
		textField_message.addKeyListener(this); // 메시지 전송 텍스트필드 키 리스너
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
		lblNewLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		lblNewLabel.setBounds(861, 16, 140, 21);
		contentPane.add(lblNewLabel);

		list_user.setBounds(861, 52, 140, 203);
		contentPane.add(list_user);
		list_user.setListData(vector_user_list);
		list_user.setFont(new Font("맑은 고딕", Font.BOLD, 18));

		button_send_note.setForeground(new Color(0, 0, 0));
		button_send_note.setBackground(new Color(255, 255, 255));
		button_send_note.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button_send_note.setBounds(862, 266, 139, 29);
		contentPane.add(button_send_note); // 1:1 대화

		JLabel label = new JLabel("오픈 채팅방 목록");
		label.setForeground(new Color(255, 255, 255));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		label.setBounds(17, 16, 200, 21);
		contentPane.add(label);

		list_roomname.setBounds(17, 52, 200, 202);
		contentPane.add(list_roomname);
		list_roomname.setListData(vector_room_list);
		list_roomname.setFont(new Font("맑은 고딕", Font.BOLD, 18));

		button_join_room.setBackground(new Color(255, 255, 255));
		button_join_room.setForeground(new Color(0, 0, 0));
		button_join_room.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button_join_room.setBounds(18, 266, 199, 29);
		contentPane.add(button_join_room); // 채팅방 참여

		button_create_room.setBackground(new Color(255, 255, 255));
		button_create_room.setForeground(new Color(0, 0, 0));
		button_create_room.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button_create_room.setBounds(17, 306, 200, 29);
		contentPane.add(button_create_room); // 방만들기

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(234, 52, 610, 559);
		contentPane.add(scrollPane);

		scrollPane.setViewportView(textArea_chat);
		textArea_chat.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		textArea_chat.setEditable(false); // 오픈채팅창 수정 false

		textField_message.setBounds(234, 629, 385, 29);
		contentPane.add(textField_message);
		// textField_message.setColumns(10);
		textField_message.setEnabled(false); // 메시지 버튼 false
		textField_message.setFont(new Font("맑은 고딕", Font.BOLD, 18));

		button_send_message.setForeground(new Color(0, 0, 0));
		button_send_message.setBackground(new Color(255, 255, 255));
		button_send_message.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button_send_message.setBounds(626, 629, 76, 29);
		contentPane.add(button_send_message);
		button_send_message.setEnabled(false); // 보내기버튼 false

		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setForeground(Color.WHITE);
		label_1.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		label_1.setBounds(234, 17, 610, 21);
		contentPane.add(label_1);

		button_create_sroom.setForeground(Color.BLACK);
		button_create_sroom.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button_create_sroom.setBackground(Color.WHITE);
		button_create_sroom.setBounds(17, 622, 200, 29);
		contentPane.add(button_create_sroom);

		JLabel label_2 = new JLabel("비밀채팅방 목록");
		label_2.setHorizontalAlignment(SwingConstants.CENTER);
		label_2.setForeground(Color.WHITE);
		label_2.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		label_2.setBounds(17, 350, 200, 21);
		contentPane.add(label_2);

		list_sroomname.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		list_sroomname.setValueIsAdjusting(true);
		list_sroomname.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list_sroomname.setBounds(17, 386, 200, 185);
		contentPane.add(list_sroomname);

		button_join_sroom.setForeground(Color.BLACK);
		button_join_sroom.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button_join_sroom.setBackground(Color.WHITE);
		button_join_sroom.setBounds(17, 582, 200, 29);
		contentPane.add(button_join_sroom);

		button_modify_name.setForeground(new Color(0, 0, 0));
		button_modify_name.setBackground(new Color(255, 255, 255));
		button_modify_name.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button_modify_name.setBounds(709, 629, 136, 29);
		// button_modify_name.setEnabled(false);
		contentPane.add(button_modify_name);
		this.setVisible(false);
	}

	private void Login_init() { // 로그인 GUI

		Login_GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Login_GUI.setBounds(100, 100, 450, 364);

		Login_panel = new JPanel();

		Login_panel.setBackground(new Color(0, 0, 0));
		Login_panel.setForeground(Color.BLACK);
		Login_panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		Login_GUI.setContentPane(Login_panel);
		Login_panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("접속할 서버IP");
		lblNewLabel.setBackground(Color.WHITE);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		lblNewLabel.setBounds(17, 98, 152, 21);
		Login_panel.add(lblNewLabel);

		JLabel lblPort = new JLabel("접속할 Port번호");
		lblPort.setForeground(Color.WHITE);
		lblPort.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPort.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		lblPort.setBounds(17, 156, 152, 21);
		Login_panel.add(lblPort);

		textField_ip = new JTextField();
		textField_ip.setBounds(182, 96, 229, 27);
		Login_panel.add(textField_ip);
		textField_ip.setColumns(10);
		textField_ip.setFont(new Font("맑은 고딕", Font.BOLD, 18));

		textField_port = new JTextField();
		textField_port.setColumns(10);
		textField_port.setBounds(182, 154, 229, 27);
		textField_port.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		Login_panel.add(textField_port);

		JLabel lblteamChatServer = new JLabel("5Team Chat Program");
		lblteamChatServer.setForeground(Color.WHITE);
		lblteamChatServer.setIcon(new ImageIcon(
				"D:\\\uB300\uD559\uC790\uB8CC\\2\uD559\uB1442\uD559\uAE30\\\uC790\uBC14\\\uD300 \uD504\uB85C\uC81D\uD2B8\\\uBA54\uC2E0\uC800\uC544\uC774\uCF582.jfif"));
		lblteamChatServer.setHorizontalAlignment(SwingConstants.CENTER);
		lblteamChatServer.setFont(new Font("맑은 고딕", Font.BOLD, 24));
		lblteamChatServer.setBounds(17, 15, 394, 68);
		Login_panel.add(lblteamChatServer);

		JLabel label = new JLabel("닉네임");
		label.setForeground(Color.WHITE);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		label.setBounds(17, 214, 152, 21);
		Login_panel.add(label);

		textField_id = new JTextField();
		textField_id.setColumns(10);
		textField_id.setBounds(182, 212, 229, 27);
		textField_id.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		Login_panel.add(textField_id);

		button_login.setForeground(new Color(0, 0, 0));
		button_login.setBackground(new Color(255, 255, 255));
		button_login.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button_login.setBounds(155, 264, 125, 29);
		Login_panel.add(button_login);

		Login_GUI.setVisible(true);
		// true = 화면에 보이게
	}

	private void network() {
		try {
			socket = new Socket(ip, port);
			if (socket != null) { // 정상적으로 소켓이 연결되었을 경우
				connection();
			}
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "연결 실패", "알림", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "연결 실패", "알림", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void connection() {
		try {
			inputStream = socket.getInputStream();
			dataInputStream = new DataInputStream(inputStream);
			outputStream = socket.getOutputStream();
			dataOutputStream = new DataOutputStream(outputStream);
		} catch (IOException e) { // 예외 처리 부분
			JOptionPane.showMessageDialog(null, "연결 실패", "알림", JOptionPane.ERROR_MESSAGE);
		} // Stream 설정 끝
		this.setVisible(true); // main ui 표시
		this.Login_GUI.setVisible(false);
		login_enter = 1;
		send_message(id); // 처음 접속시 닉네임 전송
		vector_user_list.add("[나] " + id); // 사용자 벡터 리스트에 사용자 추가
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

						JOptionPane.showMessageDialog(null, "서버와 접속 끊어짐", "알림", JOptionPane.ERROR_MESSAGE);
					} catch (IOException e1) {
					}
					break;

				}
			}
		}
	}

	private void InMessage(String str) { // 서버로 부터 들어오는 모든 메시지
		st = new StringTokenizer(str, "/");
		String protocol = st.nextToken();
		String message = st.nextToken();
		System.out.println("프로토콜 : " + protocol);
		System.out.println("내용 : " + message);

		if (protocol.equals("NewUser")) { // 새로운 접속자
			vector_user_list.add(message);
		} else if (protocol.equals("NewName")) {
			for (int i = 0; i < vector_user_list.size(); i++) {
				if (vector_user_list.get(i).equals("[나] " + message)) {
					String nn = st.nextToken();
					vector_user_list.set(i, "[나] " + nn);
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
			System.out.println(message + " 사용자에게 온 메시지 " + note);
			String note2 = null;
			while (note2 == null || note2.equals("")) {
				note2 = JOptionPane.showInputDialog(message + " : " + note); // inputdialog
				if (note2 == null) {
					JOptionPane.showMessageDialog(null, "쪽지 답장 전송을 취소하셨습니다.", "알림", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (note2.equals(""))
					JOptionPane.showMessageDialog(null, "쪽지 답장내용을 입력하시오.", "알림", JOptionPane.ERROR_MESSAGE);
			}
			if (note2 != null) {
				send_message("Note/" + message + "/" + note2);
			}
			System.out.println("받는사람 : " + message + " | 보낼 내용 : " + note2); // support
			// dialog
		} else if (protocol.equals("user_list_update")) {
			list_user.setListData(vector_user_list);
		} else if (protocol.equals("CreateRoom")) { // 오픈채팅방을 만들었을 때
			my_room = message;
			JOptionPane.showMessageDialog(null, "채팅방에 입장했습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);

		} else if (protocol.equals("CreateRoomFail")) { // 오픈채팅방 만들기 실패하였을 경우
			JOptionPane.showMessageDialog(null, "방 만들기 실패", "알림", JOptionPane.ERROR_MESSAGE);
		} else if (protocol.equals("NewRoom")) { // 새로운 오픈채팅방을 만들었을 때
			// lblNewLabel.setText("방 접 속 자");
			vector_room_list.add(message);
			list_roomname.setListData(vector_room_list);
		} else if (protocol.equals("OldRoom")) {
			vector_room_list.add(message);
		} else if (protocol.equals("room_list_update")) {
			list_roomname.setListData(vector_room_list);
		} else if (protocol.equals("JoinRoom")) {
			my_room = message;

			JOptionPane.showMessageDialog(null, "채팅방에 입장했습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
			chat_enter = 1;

		} else if (protocol.equals("ExitRoom")) {

			textField_message.setEnabled(false); // 메시지 버튼 false
			vector_room_list.remove(message);

		} ////////////////////////// 비밀
		else if (protocol.equals("CreatesRoom")) { // 비밀채팅방을 만들었을 때
			my_sroom = message;
			JOptionPane.showMessageDialog(null, "채팅방에 입장했습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);

		} else if (protocol.equals("NewsRoom")) { // 새로운 비밀채팅방을 만들었을 때
			// lblNewLabel.setText("방 접 속 자");
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
				JOptionPane.showMessageDialog(null, "방 비밀번호가 일치하지 않습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
				send_message("ExitsRoom/" + my_sroom);
				my_sroom = null;
				my_sroompasswd = null;
				return;
			}
			JOptionPane.showMessageDialog(null, "채팅방에 입장했습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
			chat_enter = 2;

		} else if (protocol.equals("ExitsRoom")) {
			textField_message.setEnabled(false); // 메시지 버튼 false
			vector_sroom_list.remove(message);
		} else if (protocol.equals("Chatting")) {
			String msg = st.nextToken();
			textArea_chat.append(message + " : " + msg + "\n");
		} else if (protocol.equals("UserOut")) {
			vector_user_list.remove(message);
		} else if (protocol.equals("Modify_Name")) {
			String mod = st.nextToken();
			System.out.println(message + "님이 " + mod + "로  닉네임을 변경하였습니다.\n");
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

	private void send_message(String message) { // 서버에게 메시지를 보내는 부분
		try {
			dataOutputStream.writeUTF(message);
		} catch (IOException e) { // 에러 처리 부분
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Client();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button_login) { // button_login = 로그인 버튼
			System.out.println("로그인 시도");
			if (textField_ip.getText().length() == 0) {
				textField_ip.setText("IP를 입력해주세요");
				textField_ip.requestFocus();
			} else if (textField_port.getText().length() == 0) {
				textField_port.setText("Port번호를 입력해주세요");
				textField_port.requestFocus();
			} else if (textField_id.getText().length() == 0) {
				textField_id.setText("닉네임을 입력해주세요");
				textField_id.requestFocus();
			} else {
				ip = textField_ip.getText().trim(); // ip를 받아오는 부분
				port = Integer.parseInt(textField_port.getText().trim()); // int형으로 형변환
				id = textField_id.getText().trim();
				network();
				login_enter = 1;
			}
		} else if (e.getSource() == button_send_note) {
			System.out.println("send_note");
			String user = (String) list_user.getSelectedValue();
			if (user == null) {
				JOptionPane.showMessageDialog(null, "보낼 대상을 선택하시오.", "알림", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (user.equals("[나] " + id)) {
				JOptionPane.showMessageDialog(null, "자신에게는 쪽지를 보낼 수 없습니다.", "알림", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String note = null;
			while (note == null || note.equals("")) {
				note = JOptionPane.showInputDialog(user + "에게 보낼 메시지"); // inputdialog
				if (note == null) {
					JOptionPane.showMessageDialog(null, "쪽지 전송을 취소하셨습니다.", "알림", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (note.equals(""))
					JOptionPane.showMessageDialog(null, "쪽지 내용을 입력하시오.", "알림", JOptionPane.ERROR_MESSAGE);
			}
			if (note != null) {
				send_message("Note/" + user + "/" + note); // ex) Note/User2/나는 User1이야
			}
			System.out.println("받는사람 : " + user + " | 보낼 내용 : " + note);
		} else if (e.getSource() == button_join_room) {
			String JoinRoom = (String) list_roomname.getSelectedValue();

			if (my_room != null) {
				if (my_room.equals(JoinRoom)) {
					JOptionPane.showMessageDialog(null, "현재 채팅방입니다.", "알림", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					JOptionPane.showMessageDialog(null, "이미 채팅방에 접속중입니다.", "알림", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// send_message("ExitRoom/" + my_room);
				// chat_enter = 0;
				// textArea_chat.setText("");
			} else if (JoinRoom == null) {
				JOptionPane.showMessageDialog(null, "선택한 채팅방이 없습니다.", "알림", JOptionPane.ERROR_MESSAGE);
			} else {
				send_message("JoinRoom/" + JoinRoom);
				label_1.setText("채 팅 창 [" + JoinRoom + "]");
				lblNewLabel.setText("로 비 접 속 자");
				button_create_sroom.setEnabled(false);
				button_join_sroom.setEnabled(false);
				button_create_room.setText("오픈채팅방 나가기");
				button_send_message.setEnabled(true);
				// button_modify_name.setEnabled(true);
				textField_message.setEnabled(true);
				System.out.println("join_room");

			}

		} else if (e.getSource() == button_create_room) {
			String roomname = null;
			if (my_room == null) {
				while (roomname == null || roomname.equals("")) {
					roomname = JOptionPane.showInputDialog("방 이름");
					if (roomname == null) {
						JOptionPane.showMessageDialog(null, "방만들기를 취소하셨습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
						return;
					} else if (roomname.equals("")) {
						JOptionPane.showMessageDialog(null, "방 이름을 입력하시오.", "알림", JOptionPane.INFORMATION_MESSAGE);
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
				button_create_room.setText("오픈채팅방 나가기");
				button_create_sroom.setEnabled(false);
				button_join_sroom.setEnabled(false);
				label_1.setText("채 팅 창 [" + roomname + "]");
				lblNewLabel.setText("로 비 접 속 자");
			} else {
				send_message("ExitRoom/" + my_room);
				textField_message.setText("");
				textArea_chat.setText("");
				JOptionPane.showMessageDialog(null, "채팅방에서 퇴장했습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
				button_send_message.setEnabled(false);
				// button_modify_name.setEnabled(false);
				my_room = null;
				chat_enter = 0;
				lblNewLabel.setText("로 비  접 속 자");
				label_1.setText("채 팅 창");
				button_create_room.setText("오픈채팅방 만들기");
				button_create_sroom.setEnabled(true);
				button_join_sroom.setEnabled(true);

			}
			System.out.println("create_room");
		} else if (e.getSource() == button_send_message) {
			System.out.println("send_message");
			if (my_room == null && my_sroom == null) {
				JOptionPane.showMessageDialog(null, "채팅방에 참여해주세요", "알림", JOptionPane.ERROR_MESSAGE);
				return;
			} else if (my_room != null || my_sroom != null) {
				// 채팅 + 오픈채팅방 이름 + 내용
				String temp = textField_message.getText().trim();
				if (temp.equals("")) {
					textField_id.setText("보낼 채팅메시지를 입력해주세요");
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
			System.out.println("modify_name"); // 닉네임 변경
			String name = null;
			while (name == null || name.equals("")) {
				name = JOptionPane.showInputDialog("변경할 닉네임을 입력하시오.");
				if (name == null) {
					JOptionPane.showMessageDialog(null, "닉네임 변경을 취소하셨습니다.", "알림", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (name.equals(""))
					JOptionPane.showMessageDialog(null, "변경할 닉네임을 입력하시오.", "알림", JOptionPane.ERROR_MESSAGE);
				else {
					for (int i = 0; i < vector_user_list.size(); i++) {
						if (vector_user_list.get(i).equals("[나] " + name)) {
							JOptionPane.showMessageDialog(null, "현재 닉네임과 같습니다.", "알림", JOptionPane.ERROR_MESSAGE);
							return;
						} else if (vector_user_list.get(i).equals(name)) {
							JOptionPane.showMessageDialog(null, "이미 존재하는 닉네임입니다.", "알림", JOptionPane.ERROR_MESSAGE);
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
		/////////////// 비밀//////
		else if (e.getSource() == button_join_sroom) {
			String JoinsRoom = (String) list_sroomname.getSelectedValue();

			if (my_sroom != null) {
				if (my_sroom.equals(JoinsRoom)) {
					JOptionPane.showMessageDialog(null, "현재 채팅방입니다.", "알림", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					JOptionPane.showMessageDialog(null, "이미 채팅방에 접속중입니다.", "알림", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// send_message("ExitRoom/" + my_room);
				// chat_enter = 0;
				// textArea_chat.setText("");
			} else if (JoinsRoom == null) {
				JOptionPane.showMessageDialog(null, "선택한 채팅방이 없습니다.", "알림", JOptionPane.ERROR_MESSAGE);
			} else if (my_sroompasswd == null) {
				my_sroompasswd = JOptionPane.showInputDialog("방 비밀번호를 입력하시오.");
				send_message("JoinsRoom/" + JoinsRoom + "/" + my_sroompasswd);
				label_1.setText("채 팅 창 [" + JoinsRoom + "]");
				button_create_room.setEnabled(false);
				button_join_room.setEnabled(false);
				lblNewLabel.setText("로 비 접 속 자");
				button_create_sroom.setText("비밀채팅방 나가기");
				button_send_message.setEnabled(true);
				textField_message.setEnabled(true);
				System.out.println("join_sroom");
			}
		} else if (e.getSource() == button_create_sroom) {
			String sroomname = null;
			if (my_sroom == null) {
				while (sroomname == null || sroomname.equals("")) {
					sroomname = JOptionPane.showInputDialog("방 이름");
					if (sroomname == null) {
						JOptionPane.showMessageDialog(null, "방만들기를 취소하셨습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
						return;
					} else if (sroomname.equals("")) {
						JOptionPane.showMessageDialog(null, "방 이름을 입력하시오.", "알림", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			if (my_sroompasswd == null) {
				while (my_sroompasswd == null || my_sroompasswd.equals("")) {
					my_sroompasswd = JOptionPane.showInputDialog("방 비밀번호");
					if (my_sroompasswd == null) {
						JOptionPane.showMessageDialog(null, "방만들기를 취소하셨습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
						return;
					} else if (my_sroompasswd.equals("")) {
						JOptionPane.showMessageDialog(null, "방 비밀번호를 입력하시오.", "알림", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			if (sroomname != null && my_sroompasswd != null) {
				send_message("CreatesRoom/" + sroomname + "/" + my_sroompasswd);
				chat_enter = 2;
				button_create_sroom.setText("비밀채팅방 나가기");
				button_create_room.setEnabled(false);
				button_join_room.setEnabled(false);
				label_1.setText("채 팅 창 [" + sroomname + "]");
				lblNewLabel.setText("로 비 접 속 자");
				button_send_message.setEnabled(true);
				// button_modify_name.setEnabled(true);
				textField_message.setEnabled(true);
				button_login.setEnabled(false);
			} else {
				send_message("ExitsRoom/" + my_sroom);
				button_send_message.setEnabled(false);
				textField_message.setText("");
				textArea_chat.setText("");
				JOptionPane.showMessageDialog(null, "채팅방에서 퇴장했습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
				my_sroom = null;
				my_sroompasswd = null;
				chat_enter = 0;
				lblNewLabel.setText("로 비  접 속 자");
				label_1.setText("채 팅 창");
				button_create_sroom.setText("비밀채팅방 만들기");
				button_create_room.setEnabled(true);
				button_join_room.setEnabled(true);

			}
			System.out.println("create_sroom");
		}

	}

	public void keyPressed(KeyEvent arg0) { // 눌렀을 때
	}

	public void keyReleased(KeyEvent e) { // 눌렀다 땠을 때

		if (e.getKeyCode() == 10 && login_enter == 0) { // enter
			System.out.println("로그인 시도");
			if (textField_ip.getText().length() == 0) {
				textField_ip.setText("IP를 입력해주세요");
				textField_ip.requestFocus();
			} else if (textField_port.getText().length() == 0) {
				textField_port.setText("Port번호를 입력해주세요");
				textField_port.requestFocus();
			} else if (textField_id.getText().length() == 0) {
				textField_id.setText("닉네임을 입력해주세요");
				textField_id.requestFocus();
			} else {
				ip = textField_ip.getText().trim(); // ip를 받아오는 부분
				port = Integer.parseInt(textField_port.getText().trim()); // int형으로 형변환
				id = textField_id.getText().trim();
				network();
				login_enter = 1;
			}
		}

		if (e.getKeyCode() == 10 && (chat_enter == 1 || chat_enter == 2)) { // enter && chat_enter == 1
			String temp = textField_message.getText().trim();
			if (temp.equals("")) {
				textField_id.setText("보낼 채팅메시지를 입력해주세요");
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

	public void keyTyped(KeyEvent arg0) { // 타이핑

	}

}
