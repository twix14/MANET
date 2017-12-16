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
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Peer implements Serializable {

	private static final long serialVersionUID = 4318690273286226312L;

	private transient List<Peer> neighbors;
	private transient List<String> messages;
	private transient BlockingQueue<Event> events;
	private transient DatagramSocket socket;
	private transient PubSub pubsub;

	private InetAddress ip;
	private int port;
	private Coordinate coord;
	private int NETWORK_DIAMETER;

	public Peer(int port, InetAddress ip, Coordinate coord,
			int network) {
		this.ip = ip;
		this.setPort(port);
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		pubsub = new PubSub();
		NETWORK_DIAMETER = network;
		messages = new LinkedList<>();
		neighbors = new LinkedList<>();
		events = new LinkedBlockingQueue<>();
		this.setCoord(coord);
		new ChangeLocation().start();
		new ReceiveThread().start();
		new SendThread().start();
	}

	public void start(){
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

	public void subscribe(EventType type) {
		PubSub.printSub(type);
		pubsub.getSubscriptions().add(type);
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
	
	private class ChangeLocation extends Thread {
		
		public void run() {
			while(true) {
				try {
					int lng = coord.getLng();
					int lat = coord.getLat();
					Random rad = new Random();
					int option = rad.nextInt(8);
					switch(option) {
						case 0: //variate latitude positive, longitude stays the same
							coord.setLat(lat++);
							break;
						case 1: //variate latitude negative, longitude stays the same
							coord.setLat(lat--);
							break;
						case 2: //variate longitude positive, latitude stays the same
							coord.setLng(lng++);
							break;
						case 3: //variate longitude negative, latitude stays the same
							coord.setLng(lng--);
							break;
						case 4: //variate longitude negative, latitude positive
							coord.setLng(lng--);
							coord.setLat(lat++);
							break;
						case 5: //variate longitude positive, latitude negative
							coord.setLng(lng--);
							coord.setLat(lat++);
							break;
						case 6: //variate longitude positive, latitude positive
							coord.setLng(lng++);
							coord.setLat(lat++);
							break;
						case 7: //variate longitude negative, latitude negative
							coord.setLng(lng--);
							coord.setLat(lat--);
							break;
					}
					System.out.println("Moved from (" + lat + "," + lng + ") to "
							+ " (" + coord.getLat() + "," + coord.getLng() + ")");
					
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	private class ReceiveThread extends Thread {

		public ReceiveThread() {

		}

		public void run() {
			while(true) {
				byte[] receiveData = new byte[1024];
				DatagramPacket receive = new DatagramPacket(receiveData,
						receiveData.length);

				try {
					socket.receive(receive);
					Event ev = Event.deserializeBA(receive.getData());
					System.out.println("Data received");

					if(coord.checkDistance(ev.getPeer().getCoord()) && neighbors.size() <= 4) {
						if(ev.isJoin()) {
							addNeighbor(ev.getPeer());
							System.out.println("Added Neighbor - " + ev.getPeer().getIp() + " to my view");
						} else {

							if(!messages.contains(ev.getMessage())) {
								messages.add(ev.getMessage());
								if(pubsub.getSubscriptions().contains(ev.getType()))
									PubSub.printPub(ev);

								int currentCounter = ev.getCounter();
								if(currentCounter < NETWORK_DIAMETER) {
									//forward message to send and increase counter
									ev.setCounter(currentCounter++);
									events.put(ev);
								} else
									System.out.println("Message discarded, since the "
											+ "number of hops surpassed the network diameter");
							}


						}
					} else if (neighbors.size() == 5) {
						events.put(new Event("View of node is full"));
						System.out.println("My view is full");
						//else discard
					}else{
						System.out.println("the event was discarded due to distance");
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
	}

	public void publish(Event e) {
		PubSub.printPub(e);
		try {
			pubsub.getPublishings().add(e);
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
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					DatagramPacket send = null;

					if(ev.isJoin()) {
						try {
							sendData = Event.serializeBA(ev);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						send = new DatagramPacket(sendData, sendData.length, 
								ev.getConnectTo(), ev.getPortConnectTo());
						System.out.println("Trying to add myself to " + ev.getConnectTo() + " view");

						try {
							socket.send(send);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						double div = ((double)neighbors.size())/2;
						int nNodes = (int) Math.ceil(div);
						List<Integer> rands = new LinkedList<>();

						Random rand = new Random();
						for(int i = 0; i < nNodes;) {

							while(true) {
								int randNum = rand.nextInt(neighbors.size());
								if(!rands.contains(randNum)) {
									rands.add(randNum);
									Peer neighbor = neighbors.get(randNum);
									ev.setPeer(neighbor);
									try {
										sendData = Event.serializeBA(ev);
									} catch (IOException e1) {
										e1.printStackTrace();
									}
									send = new DatagramPacket(sendData, sendData.length,
											neighbor.getIp(), neighbor.getPort());
									System.out.println("Event sent to neighbor " +
											neighbor.getIp());
									try {
										socket.send(send);
									} catch (PortUnreachableException e) {
										neighbors.remove(randNum);
										e.printStackTrace();
										System.err.println("Node left the view");
									} catch (IOException e) {
										e.printStackTrace();
									} 
									break;
								}
							}

						}
						System.out.println("All events were forwaded!");
					}

				}
			}
		}
	}
}
