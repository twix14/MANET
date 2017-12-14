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
		removeSelf(args);
		Peer p = null;
		try {
			p = new Peer(Integer.parseInt(args[0]), 
					InetAddress.getByName(args[1]), 
					new Coordinate(args[2], args[3]));
		} catch (NumberFormatException | UnknownHostException e) {
			e.printStackTrace();
		}

		Scanner sc = new Scanner(System.in);
		System.out.println("Peer started!");
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


	private void removeSelf(String[] args) {
		File peers = new File("peers.txt");
		File temp = new File("temp.txt");
		BufferedReader bf = null;
		BufferedWriter bw = null;

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
				if(!line.equals(args[1] + ":" + args[0]))
					bw.write(line + System.lineSeparator());
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
	}
}