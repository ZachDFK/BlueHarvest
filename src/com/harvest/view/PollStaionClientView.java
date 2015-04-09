package com.harvest.view;

import javax.swing.AbstractButton;
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
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Map;

import javax.swing.JOptionPane;

import com.harvest.controller.EPollingStationClient;
import com.harvest.shared.Constant;

/**
 * The UI for the polling station
 *
 */
public class PollStaionClientView extends JPanel {

	private static final long serialVersionUID = 6858823818599347069L;
	
	private static JTextField getSocial, getFirstName, getLastName, getAddress,
			getFirstName1, getLastName1, getSocial1;
	private static JLabel sin, fname, lname, address, sin1, fname1, lname1;
	private JButton submit, vote;
	private ButtonGroup buttonGroup;
	byte[] receiveData = new byte[1024];
	private JPanel boxPanel = new JPanel(new GridLayout(0, 1));
	private JScrollPane scrollBox = new JScrollPane();
	private EPollingStationClient modelReference;
	

	private Map<String, String> candidateIdMap;
	
	
	public PollStaionClientView(ActionListener control) {
		super(new GridLayout(1, 1));

		if (control == null){
			System.out.print("OUT!");
		}
		modelReference = (EPollingStationClient)control;
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
	public void loadCandidatesToRadioButtons(){
		this.candidateIdMap = modelReference.getCandidateIdMap();
		for(String c:candidateIdMap.keySet()){
			
			JRadioButton tempRadioB = new JRadioButton(c);
			buttonGroup.add(tempRadioB);
			boxPanel.add(tempRadioB);
		}
		
		
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
	
	public String getInfoRegister() {
		String add, fn, ln, sin;
		add = getAddress.getText();
		fn = getFirstName.getText();
		ln = getLastName.getText();
		sin = getSocial.getText();

		return (Constant.REGISTER_VOTER_PACKET_ID + ":" + fn + ":" + ln + ":" + sin + ":" + add);
	}
	
	public String getInfoVote(){
		String fn,ln,sin,vote = "";
		fn = getFirstName1.getText();
		ln = getLastName1.getText();
		sin = getSocial1.getText();
		
		for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                vote = candidateIdMap.get(button.getText());
            }
        }

		return (Constant.VOTE_CANDIDATE_PACKET_ID + Constant.DATA_DELIMITER 
				+ fn + Constant.DATA_DELIMITER
				+ ln + Constant.DATA_DELIMITER
				+ sin + Constant.DATA_DELIMITER
				+ vote);
	}
	
	public void showAlertBox(String alertMessage) {
		JOptionPane.showMessageDialog(null, alertMessage,
				"District Message", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void clearInputBoxes() {
		getAddress.setText("");
		getFirstName.setText("");
		getLastName.setText("");
		getSocial.setText("");
		
		getFirstName1.setText("");
		getLastName1.setText("");
		getSocial1.setText("");
		
		buttonGroup.clearSelection();
	}
}