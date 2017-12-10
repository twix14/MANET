package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Server {

	//args[0] - port
	//args[1] - myIp
	
	public static void main(String[] args) {
		new Server(args);
		
	}
	
	public Server(String[] args) {
		Peer p = null;
		try {
			p = new Peer(Integer.parseInt(args[0]), 
					InetAddress.getByName(args[1]), 
					new Coordinate(args[2], args[3]));
		} catch (NumberFormatException | UnknownHostException e) {
			e.printStackTrace();
		}
		
		Scanner sc = new Scanner(System.in);
		try {
			
			while(true) {
				String s = sc.nextLine();
				if(s.equals("start")) {
					p.start();
				}
			}
			
		} finally {
			sc.close();
		}
	}

}
