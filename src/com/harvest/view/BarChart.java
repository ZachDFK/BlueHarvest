package com.harvest.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import javax.print.CancelablePrintJob;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.harvest.shared.Constant;

public class BarChart extends JPanel {
	private Map<String, Integer> bars = new LinkedHashMap<String, Integer>();
	private DatagramSocket mediaViewToHeadSocket;
	private int headServerPort;
	private InetAddress headServerAddress;
	private JPanel scores;
	private ArrayList<String> partiesNames;
	private ArrayList<JTextField> textFields;
	private JScrollPane scrollBox;
	private BarChart chart;

	public void addBar(String name, int value) {
		bars.put(name, value);
		repaint();
	}

	public void establichConnectionWithHeadServer() {
		Scanner input = new Scanner(System.in);
		boolean successInput = false;

		try {
			mediaViewToHeadSocket = new DatagramSocket();

			byte[] payload = Constant.HEAD_SERVER_MEDIA_REGISTRATION_CODE
					.getBytes();

			DatagramPacket registrationPacket;
			DatagramPacket registrationAcknowledgementPacket = new DatagramPacket(
					new byte[Constant.DATAGRAM_BUFFER_SIZE],
					Constant.DATAGRAM_BUFFER_SIZE);

			String ip;
			while (!successInput) {
				System.out.println("Enter the IP of the head server");
				ip = input.nextLine();

				// Register polling station to district server
				registrationPacket = new DatagramPacket(payload,
						payload.length, InetAddress.getByName(ip),
						Constant.HEAD_SERVER_PORT);
				mediaViewToHeadSocket.send(registrationPacket);
				System.out
						.println("Sending media registration packet to head server.");

				// Receive acknowledgment from district server
				registrationAcknowledgementPacket = new DatagramPacket(
						new byte[Constant.DATAGRAM_BUFFER_SIZE],
						Constant.DATAGRAM_BUFFER_SIZE);
				mediaViewToHeadSocket.setSoTimeout(Constant.DATAGRAM_TIMEOUT);

				try {
					mediaViewToHeadSocket
							.receive(registrationAcknowledgementPacket);
					System.out
							.println("Connected media server to head server.");
					successInput = true;
				} catch (SocketTimeoutException e) {
					System.out.println("Could not connect to head server.");
					successInput = false;
				}
			}

			headServerAddress = registrationAcknowledgementPacket.getAddress();
			headServerPort = Constant.HEAD_SERVER_PORT;

			String message = new String(
					registrationAcknowledgementPacket.getData(), 0,
					registrationAcknowledgementPacket.getLength());

			if (message.equals(Constant.SUCCESS_CONNECTION_ACK)) {
				System.out
						.println("Media successfully connected to district server.");
			} else {
				System.out.println("Media did not connect to district server.");
				mediaViewToHeadSocket.close();
				return;
			}
		} catch (IOException e) {
			System.out.println("Media cannot aquire port. Shutting down.");
			return;
		}
		receivePacketing();

	}

	public void receivePacketing() {

		try {
			mediaViewToHeadSocket.setSoTimeout(0);
		} catch(IOException s) {
			System.out.println("Could not modify socket properties to communicate with head. Shutting down...");
			return;
		}
		
		while (true) {
			byte[] recieveByte = new byte[Constant.DATAGRAM_BUFFER_SIZE];

			DatagramPacket updatedResults = new DatagramPacket(recieveByte,
					recieveByte.length);

			try {
				mediaViewToHeadSocket.receive(updatedResults);

				String resultString = new String(updatedResults.getData(), 0,
						updatedResults.getLength());

				for (String c : resultString
						.split(Constant.CANDIDATES_STRING_DELIMITER)) {
					if (c.length() > 0) {
						String[] params = c.split(Constant.DATA_DELIMITER);

						if (partiesNames.contains(params[0])) {
							textFields.get(partiesNames.indexOf(params[0]))
									.setText(
											params[0] + Constant.DATA_DELIMITER
													+ " " + params[1]);
						} else {
							JTextField tempTextField = new JTextField(params[0]
									+ Constant.DATA_DELIMITER + " " + params[1]);

							partiesNames.add(params[0]);
							tempTextField.setEditable(false);
							tempTextField.setVisible(true);
							textFields.add(tempTextField);

							scores.add(tempTextField);
						}

						scrollBox.setViewportView(scores);
						chart.addBar(params[0], Integer.parseInt(params[1]));
					}
				}

				System.out.println("Received packet " + resultString);

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Could not connect to district server.");
			}
		}

	}

	@Override
	protected void paintComponent(Graphics g) {
		// determine longest bar

		int max = Integer.MIN_VALUE;
		for (Integer value : bars.values()) {
			max = Math.max(max, value);
		}

		// paint bars
		if (bars.size() > 0) {
			int width = (getWidth() / bars.size()) - 2;
			int x = 1;
			for (String name : bars.keySet()) {

				int value = bars.get(name);
				int height = (int) ((getHeight() - 5) * ((double) value / max));
				g.setColor(Color.getColor(name));
				g.fillRect(x, getHeight() - height, width, height);
				g.setColor(Color.getColor(name));
				g.drawRect(x, getHeight() - height, width, height);
				x += (width + 2);
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(bars.size() * 10 + 2, 50);
	}

	public void setUp() {
		JFrame frame = new JFrame("Bar Chart");

		scores = new JPanel(new GridLayout(0, 1));

		scrollBox = new JScrollPane(scores);

		scrollBox.setPreferredSize(new Dimension(10, 100));

		scrollBox.setVisible(true);
		partiesNames = new ArrayList<String>();
		textFields = new ArrayList<JTextField>();

		frame.setLayout(new BorderLayout());

		chart = new BarChart();
		frame.setResizable(true);
		frame.add(scrollBox, BorderLayout.NORTH);

		frame.getContentPane().add((chart), BorderLayout.SOUTH);

		frame.setPreferredSize(new Dimension(250, 350));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}
}
