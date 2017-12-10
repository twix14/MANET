package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Peer implements Serializable {

	private static final long serialVersionUID = 4318690273286226312L;

	private DatagramSocket socket;
	private InetAddress ip;
	private int port;
	private List<byte[]> receiveArray;
	private List<Peer> neighbors;
	private BlockingQueue<Event> events;
	private Coordinate coord;

	public Peer(int port, InetAddress ip, Coordinate coord) {
		this.ip = ip;
		this.setPort(port);
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		receiveArray = new LinkedList<>();
		events = new LinkedBlockingQueue<>();
		this.setCoord(coord);
	}

	public void start() {
		new ReceiveThread().start();
		new SendThread().start();
		String viewPeer = getInetAddress();
		Event ev = new Event();
		String[] split = viewPeer.split(":");
		try {
			ev.setConnectTo(InetAddress.getByName(split[0]));
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		ev.setJoin(true);
		ev.setPortConnectTo(Integer.parseInt(split[1]));
		ev.setPeer(this);
		try {
			events.put(ev);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private String getInetAddress() {
		BufferedReader bf = null;
		File peers = new File("peers.txt");
		try {
			bf = new BufferedReader(new FileReader(peers));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		List<String> ips = new LinkedList<>();

		try {
			String line;

			while((line = bf.readLine()) != null){
				ips.add(line);
			}


		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int rand = new Random().nextInt(ips.size());
		String result = ips.get(rand);
		ips.remove(rand);

		BufferedWriter bw = null;
		File temp = new File("temp.txt");
		try {
			bw = new BufferedWriter(new FileWriter(temp));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			for(String ip : ips)
				bw.write(ip + System.lineSeparator());

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		peers.delete();
		temp.renameTo(new File("peers.txt"));

		return result;
	}

	public Peer(int port, InetAddress ip, Coordinate coord, boolean send) {
		this.ip = ip;
		this.setPort(port);
		this.coord = coord;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void addNeighbor(Peer p) {
		neighbors.add(p);
	}

	public Coordinate getCoord() {
		return coord;
	}

	public void setCoord(Coordinate coord) {
		this.coord = coord;
	}

	private class ReceiveThread extends Thread {

		public ReceiveThread() {

		}

		public void run() {
			byte[] receiveData = new byte[1024];
			DatagramPacket receive = new DatagramPacket(receiveData,
					receiveData.length);

			try {
				socket.receive(receive);
				Event ev = Event.deserializeBA(receive.getData());

				if(coord.checkDistance(ev.getPeer().getCoord()) || neighbors.size() <= 4) {
					if(ev.isJoin()) {
						addNeighbor(ev.getPeer());
					} else {
						//data
						//pub/sub
					}
				} else if (neighbors.size() == 5) {
					events.put(new Event("View of node is full"));
					//else discard
				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void publish(Event e) {
		try {
			events.put(e);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}


	/*
	 * TODO
	 * TODO
	 * TODO
	 * TODO
	 * TODO
	 */

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private class SendThread extends Thread {

		public SendThread() {

		}

		public void run() {
			while(true) {
				if(!events.isEmpty()) {
					byte[] sendData = new byte[1024];
					Event ev = null;
					try {
						ev = events.take();
						sendData = Event.serializeBA(ev);
					} catch (IOException | InterruptedException e1) {
						e1.printStackTrace();
					}
					
					DatagramPacket send = null;

					if(neighbors.isEmpty()) {
						send = new DatagramPacket(sendData, sendData.length, 
								ev.getConnectTo(), ev.getPortConnectTo());
					} else {
						for(Peer p : neighbors) {
							send = new DatagramPacket(sendData, sendData.length, 
									p.getIp(), p.getPort());
						}
					}
					
					try {
						socket.send(send);
					} catch (IOException e) {
						e.printStackTrace();
					}


				}
			}

		}

	}

}
