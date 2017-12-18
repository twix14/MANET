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

		if(args.length == 2) {
			try {
				p = new Peer(Integer.parseInt(args[0]), 
						InetAddress.getByName(args[1]), networkSize);
			} catch (NumberFormatException | UnknownHostException e) {
				e.printStackTrace();
			}
		} else if (args.length == 4) {
			try {
				p = new Peer(Integer.parseInt(args[0]), 
						InetAddress.getByName(args[1]), networkSize, args[2], args[3]);
			} catch (NumberFormatException | UnknownHostException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Try again with the right commands, please :D");
			System.exit(0);
		}

		Scanner sc = new Scanner(System.in);
		System.out.println("Node started!");
		try {
			System.out.println("'start' to add myself to another view");
			System.out.println("'publish' 'eventype' 'message'");
			System.out.println("Type of eventTypes - ");
			System.out.println("HOUSE_FIRE");
			System.out.println("STREET_FIRE");
			System.out.println("INJURY");
			System.out.println("BROKEN_CABLE_LINE");
			System.out.println("CAR_STRANDED");
			System.out.println("DAMSEL_IN_DISTRESS");
			System.out.println("'subscribe' 'eventype'");
			System.out.println("'move' to randomly start moving");
			System.out.println("'stop' to stop moving");
			while(true) {
				String s = sc.nextLine();
				String[] split = s.split(" ");

				if(split[0].equals("start")) {
					p.start();
				} else if(split[0].equals("publish")) {
					Event e = new Event();

					StringBuilder sb = new StringBuilder();
					for(int i = 2; i < split.length; i++) {
						sb.append(split[i] + " ");
					}
					try {
						e.setType(EventType.valueOf(split[1]));
						e.setMessage(sb.toString());
						e.setCounter(0);
						p.publish(e);
						System.out.println("Event published!");
					} catch (IllegalArgumentException e1) {
						System.out.println("EventType doesn't exist, try again");
						continue;
					}

				} else if(split[0].equals("subscribe")) {
					try {
						p.subscribe(EventType.valueOf(split[1]));
						System.out.println("EventType subscribed!");
					} catch (IllegalArgumentException e) {
						System.out.println("EventType doesn't exist, try again");
						continue;
					}

				} else if(split[0].equals("move")){
					p.move();
				} else if (split[0].equals("stop")) {
					p.stop();
				}	else {
					System.out.println("Try again!");
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