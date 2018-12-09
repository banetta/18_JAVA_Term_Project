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

	// 서버 Frame
	private JPanel contentPane;
	private JTextField textField_port;
	private JTextArea textArea = new JTextArea();
	private JButton button_start = new JButton("서버 시작");
	private JButton button_stop = new JButton("서버 종료");
	private JProgressBar progressBar = new JProgressBar();

	// 네트워크 자원
	private ServerSocket serverSocket;
	private Socket socket;
	private int port = 0;
	private Vector vector_user = new Vector();
	private Vector vector_room = new Vector();
	private Vector vector_sroom = new Vector();

	// 기타
	private StringTokenizer st;
	private int error; // 에러 확인 변수

	public Server() { // 생성자
		super("5Team Server");
		init(); // 화면 생성 메소드
		start(); // 리스너 설정 메소드
	}

	private void start() {// 서버 버튼 액션
		textField_port.requestFocus();
		button_start.addActionListener(this);
		button_stop.addActionListener(this);
	}

	private void init() { // 화면 구성
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 645);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 0, 0));
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
		textArea.setFont(new Font("맑은 고딕", Font.BOLD, 18));
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
		textField_port.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button_start.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button_start.setBackground(new Color(255, 255, 255));
		button_start.setBounds(17, 507, 179, 29);
		contentPane.add(button_start);

		button_stop.setBackground(new Color(255, 255, 255));
		button_stop.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button_stop.setBounds(232, 507, 179, 29);
		contentPane.add(button_stop);
		button_stop.setEnabled(false); // 서버 실행을 하기전엔 서버중지버튼 비활성화

		progressBar.setBackground(new Color(255, 255, 255));
		progressBar.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		progressBar.setForeground(new Color(0, 0, 0));
		progressBar.setIndeterminate(false); // 서버 실행을 하기전엔 과정애니매이션 비활성화
		progressBar.setBounds(17, 551, 394, 23);
		contentPane.add(progressBar);

		this.setVisible(true); // true = 화면에 보이게 함
	}

	private void server_start() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
		}

		try {
			serverSocket = new ServerSocket(port); // 입력받은 포트번호 사용
			error = 0;
		} catch (IOException e) {
			error = 1;
			JOptionPane.showMessageDialog(null, "이미 사용중인 포트입니다.", "중복포트", JOptionPane.ERROR_MESSAGE);
		}
		if (serverSocket != null) { // 정상적으로 포트가 열렸을 경우
			connection();
		}
	}

	public class Socket_thread implements Runnable {
		public void run() { // 쓰레드에서 처리할 일
			while (true) {
				try {
					textArea.append("● 오픈채팅 사용자 접속 대기중 ●\n");
					socket = serverSocket.accept(); // 사용자 접속 무한 대기
					UserInfo userInfo = new UserInfo(socket);
					userInfo.start(); // 객체의 쓰레드 실행
				} catch (IOException e) {
					textArea.append("● 오픈채팅 서버가 종료 되었음 ●\n");
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
			if (textField_port.getText().equals("") || textField_port.getText().length() == 0)// textField에 값이 들어있지 않을때
			{
				JOptionPane.showMessageDialog(null, "포트번호를 입력해주세요.", "포트미입력", JOptionPane.ERROR_MESSAGE);
				textField_port.requestFocus(); // 포커스를 다시 textField에 넣어준다
				error = 1;
			} else {
				try {
					port = Integer.parseInt(textField_port.getText()); // 숫자로 입력하지 않으면 에러 발생 포트를 열수 없다.
					server_start(); // 사용자가 제대로된 포트번호를 넣었을때 서버 실행을위헤 메소드 호출
				} catch (Exception er) {
					// 사용자가 숫자로 입력하지 않았을시에는 재입력을 요구한다
					JOptionPane.showMessageDialog(null, "포트번호를 숫자로 입력해주세요", "포트입력오류", JOptionPane.ERROR_MESSAGE);
					textField_port.requestFocus(); // 포커스를 다시 textField에 넣어준다
					error = 1;
				}
			}
			if (error == 1) {

			} else if (error == 0) {
				button_start.setEnabled(false);
				textField_port.setEditable(false);
				button_stop.setEnabled(true);
				progressBar.setIndeterminate(true);
				System.out.println("▶ 오픈채팅서버 시작 ◀\n");
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
			System.out.println("▶ 오픈채팅서버 종료  ◀\n");
		}
	} // 액션 이벤트 끝

	private class UserInfo extends Thread {
		private InputStream inputStream;
		private OutputStream outputStream;
		private DataOutputStream dataOutputStream;
		private DataInputStream dataInputStream;
		private Socket socket_user;
		private String Nickname = "";
		private String CurrentRoom = null;
		private String CurrentsRoom = null;
		private boolean RoomCheck = true; // 기본적으로 만들 수 있는 상태
		private boolean sRoomCheck = true;

		public UserInfo(Socket socket) { // 생성자 메소드
			this.socket_user = socket;
			userNetwork();
		}

		public String getNickname() {
			return Nickname;
		}

		public void userNetwork() { // 네트워크 자원 설정
			try {
				inputStream = socket_user.getInputStream();
				dataInputStream = new DataInputStream(inputStream);
				outputStream = socket_user.getOutputStream();
				dataOutputStream = new DataOutputStream(outputStream);
				Nickname = dataInputStream.readUTF(); // 사용자의 닉네임을 받는다.
				textArea.append(Nickname + "님이 오픈채팅로비에 접속\n");

				BroadCast("NewUser/" + Nickname); // 기존 사용자에게 자신을 알린다.
				// 자신에게 기존사용자를 받아오는 부분
				for (int i = 0; i < vector_user.size(); i++) {
					UserInfo userInfo = (UserInfo) vector_user.elementAt(i);
					this.send_Message("OldUser/" + userInfo.getNickname());
				}
				vector_user.add(this); // 사용자에게 알린 후 Vector에 자신을 추가
				BroadCast("user_list_update/ ");
				SetOldRoom();
				SetOldsRoom();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Stream설정 에러", "알림", JOptionPane.ERROR_MESSAGE);
			}
		}

		private void BroadCast(String str) { // 전체 사용자에게 메세지 보내는 부분
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
						roomInfo.BroadCast_Room("Chatting/알림/●●●●●●    " + Nickname + "님이 퇴장하셨습니다.     ●●●●●●");
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
						sroomInfo.BroadCast_sRoom("Chatting/알림/●●●●●●    " + Nickname + "님이 퇴장하셨습니다.     ●●●●●●");
					}
					CurrentsRoom = null;
					break;
				}
			}

		}

		@Override
		public void run() { // Thread에서 처리할 내용
			super.run();
			while (true) {
				try {
					String msg = dataInputStream.readUTF();
					textArea.append(Nickname + " : " + msg + "\n"); // 사용자로부터 들어온 메시지
					InMessage(msg);
				} catch (IOException e) {
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
				} // 메시지 수신
			}
		} // run 메소드 끝

		private void InMessage(String str) { // 클라이언트로 부터 들어오는 메시지 처리
			st = new StringTokenizer(str, "/");
			String protocol = st.nextToken();
			String message = st.nextToken();
			System.out.println("protocol : " + protocol);
			if (protocol.equals("Note")) {
				String note = st.nextToken();
				System.out.println("받는 사람 : " + message);
				System.out.println("보낼 내용 : " + note);
				// 벡터에서 해당 사용자를 찾아서 메시지 전송
				for (int i = 0; i < vector_user.size(); i++) {
					UserInfo userInfo = (UserInfo) vector_user.elementAt(i);
					if (userInfo.Nickname.equals(message)) {
						userInfo.send_Message("Note/" + Nickname + "/" + note);
					}
				}
			} // if문 끝
			else if (protocol.equals("CreateRoom")) { // 현재 같은방이 존재하는지 확인
				for (int i = 0; i < vector_room.size(); i++) {
					RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
					if (roomInfo.getRoomName().equals(message)) { // 만들자고하는 방이 이미 존재할 경우
						send_Message("CreateRoomFail/ok");
						RoomCheck = false;
						break;
					}
				} // for 끝
				if (RoomCheck) { // 방을 만들 수 있을 때
					RoomInfo roomInfo_new_room = new RoomInfo(message, this);
					vector_room.add(roomInfo_new_room); // 전체 방 벡터에 방을 추가
					CurrentRoom = message;
					send_Message("CreateRoom/" + message);
					send_Message("Chatting/알림/●●●●●●    " + Nickname + "님이 입장하셨습니다.     ●●●●●●");
					BroadCast("NewRoom/" + message);
				} else {
					RoomCheck = true;
				} // else if 문 끝

			} else if (protocol.equals("Chatting")) {
				String msg = st.nextToken();
				for (int i = 0; i < vector_room.size(); i++) {
					RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
					if (roomInfo.getRoomName().equals(message)) { // 해당 방을 찾았을때
						roomInfo.BroadCast_Room("Chatting/" + Nickname + "/" + msg);
					}
				}
				for (int i = 0; i < vector_sroom.size(); i++) {
					sRoomInfo sroomInfo = (sRoomInfo) vector_sroom.elementAt(i);
					if (sroomInfo.getsRoomName().equals(message)) { // 해당 방을 찾았을때
						sroomInfo.BroadCast_sRoom("Chatting/" + Nickname + "/" + msg);
					}
				}
			} else if (protocol.equals("JoinRoom")) {
				for (int i = 0; i < vector_room.size(); i++) {
					RoomInfo roomInfo = (RoomInfo) vector_room.elementAt(i);
					if (roomInfo.getRoomName().equals(message)) {
						CurrentRoom = message;
						roomInfo.Add_User(this); // 사용자 추가
						// 새로운 사용자를 알린다
						roomInfo.BroadCast_Room("Chatting/알림/●●●●●●    " + Nickname + "님이 입장하셨습니다.     ●●●●●●");
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
							sroomInfo.Add_sUser(this); // 사용자 추가
							// 새로운 사용자를 알린다
							sroomInfo.BroadCast_sRoom("Chatting/알림/●●●●●●    " + Nickname + "님이 입장하셨습니다.     ●●●●●●");
							send_Message("JoinsRoom/" + name);
						}
						else {
							send_Message("JoinsRoom/" +  passwd + "/" + name);
							ExitsRoom(this);
						}
					}
				}
			} else if (protocol.equals("CreatesRoom")) { // 현재 같은방이 존재하는지 확인
				for (int i = 0; i < vector_sroom.size(); i++) {
					sRoomInfo sroomInfo = (sRoomInfo) vector_sroom.elementAt(i);
					if (sroomInfo.getsRoomName().equals(message)) { // 만들자고하는 방이 이미 존재할 경우
						send_Message("CreateRoomFail/ok");
						sRoomCheck = false;
						break;
					}
				} // for 끝
				if (sRoomCheck) { // 방을 만들 수 있을 때
					String name = message;
					String passwd = st.nextToken();
					sRoomInfo sroomInfo_new_room = new sRoomInfo(name, passwd, this);
					vector_sroom.add(sroomInfo_new_room); // 전체 방 벡터에 방을 추가
					CurrentsRoom = message;
					send_Message("CreatesRoom/" + message);
					send_Message("Chatting/알림/●●●●●●    " + Nickname + "님이 입장하셨습니다.     ●●●●●●");
					BroadCast("NewsRoom/" + message);
				} else {
					sRoomCheck = true;
				} // else if 문 끝

			} else if (protocol.equals("ExitRoom")) {
				ExitRoom(this);
			}

			else if (protocol.equals("ExitsRoom")) {
				ExitsRoom(this);
			} else if (protocol.equals("Modify_Name")) {
				String modify = st.nextToken();
				System.out.println("현재 닉네임 : " + message);
				System.out.println("변경 닉네임 : " + modify);
				for (int i = 0; i < vector_user.size(); i++) {
					UserInfo userInfo = (UserInfo) vector_user.elementAt(i);
					if (userInfo.Nickname.equals(message)) {
						Nickname = modify;
						send_Message("Modify_Name/" + message + "/" + Nickname);
					}
				}
				BroadCast("NewName/" + message + "/" + Nickname); // 기존 사용자에게 바뀐닉네임을 알린다.
				BroadCast("user_list_update/ ");

				// SetOldRoom();
			}
		}

		private void send_Message(String message) {
			try {
				dataOutputStream.writeUTF(message);
			} catch (IOException e) {
				textArea.append("메시지 전송 실패\n");
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

		public void BroadCast_sRoom(String str) { // 현재 방의 모든 사람에게 알린다.
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

		public void BroadCast_Room(String str) { // 현재 방의 모든 사람에게 알린다.
			for (int i = 0; i < vector_room_user.size(); i++) {
				UserInfo userInfo = (UserInfo) vector_room_user.elementAt(i);
				userInfo.send_Message(str);
			}
		}

	}
}
