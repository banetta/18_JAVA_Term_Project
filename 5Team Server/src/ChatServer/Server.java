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

	// 서버 Frame
	private JPanel contentPane;
	private JTextField textField_port;
	private JTextArea textArea = new JTextArea();
	private JButton button_start = new JButton("서버 시작");
	private JButton button_stop = new JButton("서버 종료");

	// 네트워크 자원
	private ServerSocket serverSocket;
	private Socket socket;
	private int port = 7777;
	private Vector vector_user = new Vector();
	private Vector vector_room = new Vector();

	// 기타
	private StringTokenizer st;
	private int error;// =0; //에러 확인 변수

	public Server() { // 생성자
		init(); // 화면 생성 메소드
		start(); // 리스너 설정 메소드
	}

	private void start() {// 서버 버튼 액션
		button_start.addActionListener(this);
		button_stop.addActionListener(this);
	}

	private void init() { // 화면 구성

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 612);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 0, 102));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Messenger Server Processer");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setBounds(17, 15, 394, 21);
		contentPane.add(lblNewLabel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(17, 46, 394, 408);
		contentPane.add(scrollPane);

		scrollPane.setViewportView(textArea);
		textArea.setEditable(false); // 화면을 수정할 수 없도록 함.

		JLabel label = new JLabel("포트 번호");
		label.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		label.setForeground(new Color(255, 255, 255));
		label.setBounds(17, 469, 78, 21);
		contentPane.add(label);

		textField_port = new JTextField();
		textField_port.setBounds(109, 467, 302, 27);
		contentPane.add(textField_port);
		textField_port.setColumns(10);

		button_start.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button_start.setBackground(new Color(255, 255, 255));
		button_start.setBounds(17, 507, 179, 29);
		contentPane.add(button_start);

		button_stop.setBackground(new Color(255, 255, 255));
		button_stop.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button_stop.setBounds(232, 507, 179, 29);
		contentPane.add(button_stop);
		button_stop.setEnabled(false); // 서버 실행을 하기전엔 서버중지버튼 비활성화

		this.setVisible(true); // true = 화면에 보이게 함
	}

	private void server_start() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
		}

		try {
			serverSocket = new ServerSocket(port); // 입력받은 포트번호 사용
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			error = 1;
			JOptionPane.showMessageDialog(null, "이미 사용중인 포트입니다.", "중복포트오류", JOptionPane.ERROR_MESSAGE);
		}
		if (serverSocket != null) { // 정상적으로 포트가 열렸을 경우
			error = 0;
			connection();
		}
	}

	public class Socket_thread implements Runnable {
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try { // wait client
					textArea.append("〓〓〓〓〓〓〓〓〓〓오픈채팅 사용자 접속 대기중〓〓〓〓〓〓〓〓〓〓\n");
					socket = serverSocket.accept(); // 사용자 접속 무한 대기
					UserInfo userInfo = new UserInfo(socket);
					userInfo.start();
				} catch (IOException e) {
					textArea.append("〓〓〓〓〓〓〓〓〓〓오픈채팅 서버가 종료 되었음〓〓〓〓〓〓〓〓〓〓\n");
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
				System.out.println("▶ 오픈채팅서버 시작 ◀\n");
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
			System.out.println("▶ 오픈채팅서버 종료  ◀\n");
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
		private boolean RoomCheck = true; // 기본적으로 만들 수 있는 상태

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
				textArea.append("▶ " + Nickname + "님이 오픈채팅로비에 접속\n");

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
				JOptionPane.showMessageDialog(null, "Stream설정 에러", "알림", JOptionPane.ERROR_MESSAGE);
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
						roomInfo.BroadCast_Room("Chatting/알림/******    " + Nickname + "님이 퇴장하셨습니다     ******");
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
					textArea.append(Nickname + " : 사용자 접속 끊어짐\n");
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
				System.out.println("받는 사람 : " + message);
				System.out.println("보낼 내용 : " + note);
				for (int i = 0; i < vector_user.size(); i++) {
					UserInfo userInfo = (UserInfo) vector_user.elementAt(i);
					if (userInfo.Nickname.equals(message)) {
						userInfo.send_Message("Note/" + Nickname + "/" + note);
					}
				}
			} else if (protocol.equals("CreateRoom")) {
				for (int i = 0; i < vector_room.size(); i++) {
					RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
					if (roomInfo.getRoomName().equals(message)) { // 방이 이미 존재
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
					send_Message("Chatting/알림/******    " + Nickname + "님이 입장하셨습니다     ******");
					BroadCast("NewRoom/" + message);
				} else {
					RoomCheck = true;
				}

			} else if (protocol.equals("Chatting")) {
				String msg = st.nextToken();
				for (int i = 0; i < vector_room.size(); i++) {
					RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
					if (roomInfo.getRoomName().equals(message)) { // 해당 방을 찾으면
						roomInfo.BroadCast_Room("Chatting/" + Nickname + "/" + msg);
					}
				}
			} else if (protocol.equals("JoinRoom")) {
				for (int i = 0; i < vector_room.size(); i++) {
					RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
					if (roomInfo.getRoomName().equals(message)) {
						CurrentRoom = message;
						// 새로운 사용자를 알린다
						roomInfo.Add_User(this);
						roomInfo.BroadCast_Room("Chatting/알림/******    " + Nickname + "님이 입장하셨습니다     *****");
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
				textArea.append("메세지 전송 실패");
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

		public void BroadCast_Room(String str) { // 현재 방의 모든 사람에게 알린다.
			for (int i = 0; i < vector_room_user.size(); i++) {
				UserInfo userInfo = (UserInfo) vector_room_user.elementAt(i);
				userInfo.send_Message(str);
			}
		}

	}
}
