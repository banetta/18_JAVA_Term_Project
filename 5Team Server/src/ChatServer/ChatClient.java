package ChatServer;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.JProgressBar;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ChatClient extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatClient frame = new ChatClient();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChatClient() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 364);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 0, 102));
		contentPane.setForeground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("\uC811\uC18D\uD560 \uC11C\uBC84IP");
		lblNewLabel.setBackground(Color.WHITE);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 18));
		lblNewLabel.setBounds(17, 98, 152, 21);
		contentPane.add(lblNewLabel);
		
		JLabel lblPort = new JLabel("\uC811\uC18D\uD560 PORT\uBC88\uD638");
		lblPort.setForeground(Color.WHITE);
		lblPort.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPort.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 18));
		lblPort.setBounds(17, 156, 152, 21);
		contentPane.add(lblPort);
		
		textField = new JTextField();
		textField.setBounds(182, 96, 229, 27);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(182, 154, 229, 27);
		contentPane.add(textField_1);
		
		JLabel lblteamChatServer = new JLabel("5Team Chat Program");
		lblteamChatServer.setForeground(Color.WHITE);
		lblteamChatServer.setIcon(new ImageIcon("D:\\\uB300\uD559\uC790\uB8CC\\2\uD559\uB1442\uD559\uAE30\\\uC790\uBC14\\\uD300 \uD504\uB85C\uC81D\uD2B8\\\uBA54\uC2E0\uC800\uC544\uC774\uCF582.jfif"));
		lblteamChatServer.setHorizontalAlignment(SwingConstants.CENTER);
		lblteamChatServer.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 24));
		lblteamChatServer.setBounds(17, 15, 394, 68);
		contentPane.add(lblteamChatServer);
		
		JLabel label = new JLabel("\uB2C9\uB124\uC784");
		label.setForeground(Color.WHITE);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 18));
		label.setBounds(17, 214, 152, 21);
		contentPane.add(label);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(182, 212, 229, 27);
		contentPane.add(textField_2);
		
		JButton btnNewButton = new JButton("\uC11C\uBC84 \uC811\uC18D");
		btnNewButton.setForeground(new Color(0, 0, 102));
		btnNewButton.setBackground(new Color(255, 255, 255));
		btnNewButton.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 18));
		btnNewButton.setBounds(155, 264, 125, 29);
		contentPane.add(btnNewButton);
	}
}
