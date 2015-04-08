package com.harvest.view;

import javax.swing.JButton;
import javax.swing.JTabbedPane;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;

public class PollStaionClientView extends JPanel implements ActionListener {
	private static JTextField getSocial, getFirstName, getLastName, getAddress,
			getFirstName1, getLastName1, getSocial1;
	private static JLabel sin, fname, lname, address, sin1, fname1, lname1;
	private JButton submit, vote;
	private JRadioButton cand1, cand2, cand3;
	private ButtonGroup buttonGroup;
	private String candOne, candTwo, candThree;
	private byte[] sendData = new byte[1024];
	byte[] receiveData = new byte[1024];
	private DatagramSocket socket;
	private DatagramPacket packet;
	private JPanel boxPanel = new JPanel(new GridLayout(0, 1));
	private JScrollPane scrollBox = new JScrollPane();
	
	
	public PollStaionClientView(ActionListener control) {
		super(new GridLayout(1, 1));
		setUpSocket();
		if (control == null){
			System.out.print("OUT!");
			control = this;
			
			
		}
		
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		buttonGroup = new ButtonGroup();
		submit = new JButton("Submit");
		submit.addActionListener(control);
		vote = new JButton("Vote");
		vote.addActionListener(control);
		getSocial = new JTextField("", 9);
		getFirstName = new JTextField("", 9);
		getLastName = new JTextField("", 9);
		getAddress = new JTextField("", 9);
		getSocial1 = new JTextField("", 9);
		getFirstName1 = new JTextField("", 9);
		getLastName1 = new JTextField("", 9);

		sin = new JLabel("Enter Social Insurance Number: ");
		fname = new JLabel("Enter First Name: ");
		lname = new JLabel("Enter Last Name: ");
		sin1 = new JLabel("Enter Social Insurance Number: ");
		fname1 = new JLabel("Enter First Name: ");
		lname1 = new JLabel("Enter Last Name: ");
		address = new JLabel("Enter Address: ");

		panel.add(submit);
		panel.add(getSocial);
		panel.add(getFirstName);
		panel.add(getLastName);
		panel.add(getAddress);
		panel.add(sin);
		panel.add(fname);
		panel.add(lname);
		panel.add(address);

		layout.putConstraint(SpringLayout.WEST, getSocial, 185,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, fname, 30, SpringLayout.NORTH,
				panel);
		layout.putConstraint(SpringLayout.NORTH, getFirstName, 30,
				SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, getFirstName, 135,
				SpringLayout.WEST, panel);

		layout.putConstraint(SpringLayout.NORTH, lname, 60, SpringLayout.NORTH,
				panel);
		layout.putConstraint(SpringLayout.NORTH, getLastName, 60,
				SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, getLastName, 135,
				SpringLayout.WEST, panel);

		layout.putConstraint(SpringLayout.NORTH, address, 90,
				SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.NORTH, getAddress, 90,
				SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, getAddress, 135,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, submit, 200,
				SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, submit, 100, SpringLayout.WEST,
				panel);

		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent panel1 = makeTextPanel("Register");
		tabbedPane.addTab("Register", panel);
		panel1.setPreferredSize(new Dimension(600, 400));
		
		JComponent panel2 = makeTextPanel("");

		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		// panel2.setLayout(new SpringLayout());
		tabbedPane.addTab("Vote", panel2);

		Dimension minSize = new Dimension(5, 20);
		Dimension prefSize = new Dimension(5, 20);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 20);
		

		
		
		cand1 = new JRadioButton(candOne);
		cand2 = new JRadioButton(candTwo);
		cand3 = new JRadioButton(candThree);
		
		buttonGroup.add(cand1);
		buttonGroup.add(cand2);
		buttonGroup.add(cand3);
		
		boxPanel.add(cand1);
		boxPanel.add(cand2);
		boxPanel.add(cand3);
		
		
		
		
		
		
		scrollBox.setViewportView(boxPanel);
		scrollBox.setSize(new Dimension(100, 200));
		panel2.add(scrollBox);
		panel2.add(new Box.Filler(minSize, prefSize, maxSize));
		panel2.add(sin1);
		panel2.add(getSocial1);
		panel2.add(fname1);
		panel2.add(getFirstName1);
		panel2.add(lname1);
		panel2.add(getLastName1);
		panel2.add(new Box.Filler(minSize, prefSize, maxSize));

		panel2.add(vote);

		add(tabbedPane);

		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

	}

	protected JComponent makeTextPanel(String text) {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(filler);
		return panel;
	}

	public static void createAndShowGUI(PollStaionClientView clientView) {
		JFrame frame = new JFrame("Voting System");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		if(clientView == null){
			clientView =new PollStaionClientView(null);
		}
		frame.add(clientView, BorderLayout.CENTER);
		
		frame.setPreferredSize(new Dimension(315, 300));
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// UIManager.put("swing.boldmetal", Boolean.FALSE);
				createAndShowGUI(null);
			}

		});
	}

	public static String getInfo() {
		String add, fn, ln, sin, output;
		String packetType = "1";
		if (getSocial.getText() != null && getSocial.getText() != null
				&& getSocial.getText() != null && getAddress.getText() != null) {
			add = getSocial.getText();
			fn = getSocial.getText();
			ln = getSocial.getText();
			sin = getAddress.getText();

			return output = (packetType + ":" + fn + ":" + ln + ":" + sin + ":" + add);
		}
		return null;
	}

	private void setUpSocket() {

		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}

	}

	private void loadCandidates() {
		
	}

	private void sendPacket(String toSend) {
		sendData = toSend.getBytes();
		packet = new DatagramPacket(sendData, sendData.length);
		try {
			socket.send(packet);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private String recievePacket() {
		DatagramPacket receivePacket = new DatagramPacket(receiveData,
				receiveData.length);
		try {
			socket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String answer = new String(receivePacket.getData(), 0,
				receivePacket.getLength());
		return answer;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == submit) {
			if (getInfo() == null) {
				JOptionPane.showMessageDialog(null,
						"Please fill in all fields", "Registration",
						JOptionPane.ERROR_MESSAGE);
			} else {
				String sendMessage = getInfo();
				sendPacket(sendMessage);
			}
			String confirmation = recievePacket();
			if (confirmation.equals("123")) {
				JOptionPane.showMessageDialog(null, "Successfully Registered",
						"Registration", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null,
						"Error Occured. Please register again.",
						"Registration", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (ae.getSource() == vote) {
			// send data from radio button vote
		}
	}
}
