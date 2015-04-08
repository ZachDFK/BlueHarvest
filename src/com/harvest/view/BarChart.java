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


public class BarChart extends JPanel
{
	private Map<String, Integer> bars =
            new LinkedHashMap<String, Integer>();
	private DatagramSocket mediaViewToHeadSocket;
	private int headServerPort;
	private InetAddress headServerAddress;
	private JPanel scores;
	private ArrayList<String> partiesNames;
	private ArrayList<JTextField> textFields;
	public void addBar(String name, int value)
	{
		bars.put(name, value);
		repaint();
	}
	
	public void establichConnectionWithHeadServer(){
		Scanner input = new Scanner(System.in);
		boolean successInput = false;
	
		try {
			mediaViewToHeadSocket = new DatagramSocket();
			
			byte[] payload = Constant.HEAD_SERVER_MEDIA_REGISTRATION_CODE.getBytes();

			DatagramPacket registrationPacket;
			DatagramPacket registrationAcknowledgementPacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
		
			String ip;
			while(!successInput) {
				System.out.println("Enter the IP of the head server");
				ip = input.nextLine();
				
				// Register polling station to district server
				registrationPacket = new DatagramPacket(payload, payload.length,
						InetAddress.getByName(ip), Constant.HEAD_SERVER_PORT);
				mediaViewToHeadSocket.send(registrationPacket);
	
				// Receive acknowledgment from district server
				registrationAcknowledgementPacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
				mediaViewToHeadSocket.setSoTimeout(Constant.DATAGRAM_TIMEOUT);
				
				try {
					mediaViewToHeadSocket.receive(registrationAcknowledgementPacket);
					successInput = true;
				} catch(SocketTimeoutException e) {
					System.out.println("Could not connect to district server.");
					successInput = false;
				}
			}

			headServerAddress = registrationAcknowledgementPacket.getAddress();
			headServerPort = Constant.HEAD_SERVER_PORT;
			
			String message = new String(registrationAcknowledgementPacket.getData(), 0, registrationAcknowledgementPacket.getLength());

			if (message.equals(Constant.SUCCESS_CONNECTION_ACK)) {
				System.out.println("Media successfully connected to district server.");
			} else {
				System.out.println("Media did not connect to district server.");
				mediaViewToHeadSocket.close();
				return;
			}
		}catch (IOException e) {
			System.out.println("Media cannot aquire port. Shutting down.");
			return;
		}
		receivePacketing();
			
		
	}
	public void receivePacketing(){
		while(true){
			byte[] recieveByte= new byte[Constant.DATAGRAM_BUFFER_SIZE];
			
			DatagramPacket updatedResults = new DatagramPacket(recieveByte, recieveByte.length);
			try {
				mediaViewToHeadSocket.receive(updatedResults);
				
				String resultString = new String(updatedResults.getData(),0,updatedResults.getLength());
				
				
//				for(String c:resultString.split(Constant.CANDIDATES_STRING_DELIMITER)){
//					if(c.length() >0){
//						String[] params = c.split(Constant.DATA_DELIMITER);
//						this.addBar(params[0],Integer.parseInt(params[1]));
//
//						JTextField tempTextField = new JTextField();
//						if(partiesNames.contains(params[0])){
//							tempTextField = textFields.get(partiesNames.indexOf(params[0]));
//							
//						}
//						else{
//							tempTextField.setText(params[0] + Constant.DATA_DELIMITER + " " + params[1] );
//							
//						}
//						
//				
//						
//						int cand1Votes = 1;
//						int cand2Votes = 3;
//						int cand3Votes = 6;
//						String party1 = "Liberal";
//						String party2;
//						JTextField cand1 = new JTextField(party1 + ":" + " " +Integer.toString(cand1Votes));
//						JTextField cand2 = new JTextField(Integer.toString(cand2Votes));
//						JTextField cand3 = new JTextField(Integer.toString(cand3Votes));
//						cand1.setEditable(false);
//						cand2.setEditable(false);
//						cand3.setEditable(false);
//					}
//				}
//				
				System.out.println("Received packet "+ resultString);
//				
			} catch( IOException e) {
				System.out.println("Could not connect to district server.");
			}
		}
		
	}
	@Override
	protected void paintComponent(Graphics g)
	{
		// determine longest bar
		
		int max = Integer.MIN_VALUE;
		for (Integer value : bars.values())
		{
			max = Math.max(max, value);
		}
		
		// paint bars
		
		int width = (getWidth() / bars.size()) - 2;
		int x = 1;
		for (String name : bars.keySet())
		{
			int value = bars.get(name);
			int height = (int) 
                            ((getHeight()-5) * ((double)value / max));
			g.setColor(Color.getColor(name));
			g.fillRect(x, getHeight() - height, width, height);
			g.setColor(Color.black);
			g.drawRect(x, getHeight() - height, width, height);
			x += (width + 2);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(bars.size() * 10 + 2, 50);
	}
	public void setUp(){
//		int cand1Votes = 1;
//		int cand2Votes = 3;
//		int cand3Votes = 6;
//		String party1 = "Liberal";
//		String party2;
//		JTextField cand1 = new JTextField(party1 + ":" + " " +Integer.toString(cand1Votes));
//		JTextField cand2 = new JTextField(Integer.toString(cand2Votes));
//		JTextField cand3 = new JTextField(Integer.toString(cand3Votes));
//		cand1.setEditable(false);
//		cand2.setEditable(false);
//		cand3.setEditable(false);
		JFrame frame = new JFrame("Bar Chart");
		
		scores = new JPanel(new GridLayout(0,1));
		
		
		JScrollPane scrollBox = new JScrollPane(scores);
		
		
		scrollBox.setViewportView(scores);
		scrollBox.setPreferredSize(new Dimension(10, 100));
		
		partiesNames = new ArrayList<String>();
		
		
		scrollBox.setVisible(true);
		
		
		
		
		frame.setLayout(new BorderLayout());
		
		
		
		BarChart chart = new BarChart();
		
//		chart.addBar(Color.red, cand1Votes);
//		chart.addBar(Color.blue, cand2Votes);
//		chart.addBar(Color.black, cand3Votes);
//		chart.addBar("black",10);
//		chart.addBar("blue",5);
	chart.addBar("red",2);
	
		frame.setResizable(false);
		frame.add(scrollBox, BorderLayout.NORTH);
		//frame.add(parties, BorderLayout.CENTER);
		frame.getContentPane().add((chart), BorderLayout.SOUTH);
		
		frame.setPreferredSize(new Dimension(250,350));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
	}
	public static void main(String[] args)
	{
		new BarChart().setUp();
	}
}
