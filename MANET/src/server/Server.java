package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Server {

	//args[0] - port
	//args[1] - myIp

	public static void main(String[] args) {
		new Server(args);

	}

	public Server(String[] args) {
		int networkSize = removeSelf(args);
		Peer p = null;
		try {
			p = new Peer(Integer.parseInt(args[0]), 
					InetAddress.getByName(args[1]), networkSize);
		} catch (NumberFormatException | UnknownHostException e) {
			e.printStackTrace();
		}
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Node started!");
		try {
			System.out.println("'start' to add myself to another view");
			System.out.println("'publish' 'eventype' 'message'");
			System.out.println("Type of eventTypes - ");
			System.out.println("House on fire");
			System.out.println("Fire in the streets");
			System.out.println("Injured people");
			System.out.println("Coms line needs fixing");
			System.out.println("Cars surrounded by fire");
			System.out.println("There's some damsel in distress");
			System.out.println("'subscribe' 'eventype'");
			while(true) {
				String s = sc.nextLine();
				
				
				if(s.contains("start")) {
					p.start();
				} else if(s.contains("publish")) {
					Event e = new Event();
					String[] split = s.split(" ");
					e.setType(EventType.valueOf(split[1]));
					e.setMessage(split[2]);
					e.setCounter(0);
					p.publish(e);
					System.out.println("Event published!");
				} else if(s.contains("subscribe")) {
					String[] split = s.split(" ");
					p.subscribe(EventType.valueOf(split[1]));
					System.out.println("EventType subscribed!");
				} else if(s.contains("move")){
					p.move();
				}else	{
					System.out.println("Try again!");
					
					System.out.println("'start' to add myself to another view");
					System.out.println("'publish' 'eventype' 'message'");
					System.out.println("Type of eventTypes - ");
					System.out.println("House on fire");
					System.out.println("Fire in the streets");
					System.out.println("Injured people");
					System.out.println("Coms line needs fixing");
					System.out.println("Cars surrounded by fire");
					System.out.println("There's some damsel in distress");
					System.out.println("'subscribe' 'eventype'");
				}
			}

		} finally {
			sc.close();
		}
	}


	private int removeSelf(String[] args) {
		File peers = new File("peers.txt");
		File temp = new File("temp.txt");
		BufferedReader bf = null;
		BufferedWriter bw = null;
		int networkSize = 0;
		//ya

		try {
			bf = new BufferedReader(new FileReader(peers));
			bw = new BufferedWriter(new FileWriter(temp));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			String line;
			while((line = bf.readLine()) != null){
				if(!line.equals(args[1] + ":" + args[0])) {
					bw.write(line + System.lineSeparator());
					networkSize++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bf.close();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		peers.delete();
		temp.renameTo(new File("peers.txt"));
		return networkSize;
	}
}