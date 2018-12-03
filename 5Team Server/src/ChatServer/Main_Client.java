package ChatServer;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;
import javax.swing.JDesktopPane;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.Color;

public class Main_Client extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main_Client frame = new Main_Client();
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
	public Main_Client() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1040, 729);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 0, 102));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("\uD604 \uC7AC \uC811 \uC18D \uC790");
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 18));
		lblNewLabel.setBounds(861, 16, 140, 21);
		contentPane.add(lblNewLabel);
		
		JList list = new JList();
		list.setBounds(861, 52, 140, 203);
		contentPane.add(list);
		
		JButton btnNewButton = new JButton("\uCABD\uC9C0 \uBCF4\uB0B4\uAE30");
		btnNewButton.setForeground(new Color(0, 0, 102));
		btnNewButton.setBackground(new Color(255, 255, 255));
		btnNewButton.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 18));
		btnNewButton.setBounds(862, 270, 139, 29);
		contentPane.add(btnNewButton);
		
		JLabel label = new JLabel("\uC624\uD508 \uCC44\uD305\uBC29 \uBAA9\uB85D");
		label.setForeground(new Color(255, 255, 255));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 18));
		label.setBounds(17, 16, 200, 21);
		contentPane.add(label);
		
		JList list_1 = new JList();
		list_1.setBounds(17, 52, 200, 202);
		contentPane.add(list_1);
		
		JButton button = new JButton("\uC624\uD508 \uCC44\uD305\uBC29 \uCC38\uC5EC");
		button.setBackground(new Color(255, 255, 255));
		button.setForeground(new Color(0, 0, 102));
		button.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 18));
		button.setBounds(18, 270, 199, 29);
		contentPane.add(button);
		
		JButton button_1 = new JButton("\uBC29 \uB9CC\uB4E4\uAE30");
		button_1.setBackground(new Color(255, 255, 255));
		button_1.setForeground(new Color(0, 0, 102));
		button_1.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 18));
		button_1.setBounds(17, 314, 200, 29);
		contentPane.add(button_1);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(234, 52, 610, 559);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		textField = new JTextField();
		textField.setBounds(234, 629, 528, 29);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton button_2 = new JButton("\uC804\uC1A1");
		button_2.setBackground(new Color(255, 255, 255));
		button_2.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 18));
		button_2.setBounds(768, 628, 76, 29);
		contentPane.add(button_2);
		
		JLabel label_1 = new JLabel("\uC624\uD508 \uCC44\uD305\uCC3D");
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setForeground(Color.WHITE);
		label_1.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 18));
		label_1.setBounds(234, 17, 610, 21);
		contentPane.add(label_1);
		
		JButton button_3 = new JButton("\uC77C\uBC18 \uCC44\uD305\uBC29");
		button_3.setForeground(new Color(0, 0, 102));
		button_3.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 18));
		button_3.setBackground(Color.WHITE);
		button_3.setBounds(17, 358, 200, 29);
		contentPane.add(button_3);
		
		JButton button_4 = new JButton("\uBE44\uBC00 \uCC44\uD305\uBC29");
		button_4.setForeground(new Color(0, 0, 102));
		button_4.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 18));
		button_4.setBackground(Color.WHITE);
		button_4.setBounds(17, 402, 200, 29);
		contentPane.add(button_4);
	}
}
