package co.aurasphere.scripts;

/*
 * MIT License
 *
 * Copyright (c) 2017 Donato Rimenti
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Simple UDP server implementation. This program has been designed to be run
 * from command line.
 * 
 * The server can work in 3 modes:
 * 
 * <ul>
 * <li>redirect, the incoming packets will be logged and redirected to the
 * address and port specified as the first two arguments. This is the default
 * mode</li>
 * <li>echo, the incoming packets will be sent back to the sender at the same
 * address and port. This mode is enabled by passing 0 as the first argument and
 * then any value</li>
 * <li>man-in-the-middle, in this mode the server will redirect the packets as
 * in redirect mode and send the reply received to the original server. Please
 * note that the implementation of this mode is really naive and assumes that
 * each even packet is a reply, so multiple requests within a short window of
 * time may cause issues.</li>
 * </ul>
 * 
 * The server takes 3 or 4 arguments:
 * 
 * <ol>
 * <li>the binding port for the server</li>
 * <li>the address where the incoming packets will be forwarded to</li>
 * <li>the port where the incoming packets will be forwarded to</li>
 * <li>(optional) enables "man-in-the-middle" mode. This argument can be any
 * integer. If the value is 0 or not specified, this mode will be disabled,
 * otherwise it will enabled. In this mode, the server will capture a packet,
 * send it to the forwarding address and port, get the reply and forward it back
 * to the first request originator</li>
 * </ol>
 * 
 * @author Donato Rimenti
 *
 */
public class UdpInterceptorServer {

	/**
	 * Socket used to send and receive data.
	 */
	private static DatagramSocket serverSocket;

	/**
	 * Constant which represents a byte with value 0.
	 */
	private final static byte ZERO_BYTE = Byte.parseByte("0");

	/**
	 * Starts the server. Once started, the server will listen for incoming
	 * connections and reply accordingly to the given configuration.
	 *
	 * @param serverPort
	 *            the port where the server will be listening
	 * @param forwardingAddress
	 *            the address where the server will redirect the incoming
	 *            packets
	 * @param forwardingPort
	 *            the port where the server will redirect the incoming packets
	 * @param manInTheMiddleMode
	 *            whether to work in man-in-the-middle mode or not
	 * @throws IOException
	 */
	public static void startServer(InetAddress forwardingAddress, Integer forwardingPort, boolean manInTheMiddleMode)
			throws IOException {
		// Address where to send the packets.
		InetAddress returnAddress = forwardingAddress;
		Integer returnPort = forwardingPort;

		// Variables used by the man-in-the-middle mode.
		InetAddress oldAddress = null;
		int oldPort = 0;
		int mimCounter = 0;

		// Application main loop. Listens for incoming connections and
		// forwards them accordingly to the current configuration.
		while (true) {
			// Gets the incoming packet.
			byte[] receiveData = new byte[1024];
			DatagramPacket incomingPacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				serverSocket.receive(incomingPacket);
			} catch (IOException e) {
				// Don't rethrow this, so if an exception occurs for one
				// packet, the application still runs.
				e.printStackTrace();
			}

			// Logs the received packet to standard output.
			log(incomingPacket, true);

			// Creates a new packet.
			byte[] data = incomingPacket.getData();

			// If no return address has been passed as argument, the packet is
			// just sent back to the original sender each time.
			if (forwardingAddress == null) {
				returnAddress = incomingPacket.getAddress();
				returnPort = incomingPacket.getPort();
			}

			// Handles the forward according to the current mode.
			if (manInTheMiddleMode) {

				// This counter thing is very naive and it doesn't take into
				// account the fact that multiple requests may be sent before
				// any response is received but it'll do for this simple server.
				if (mimCounter % 2 == 0) {
					// Even packets go through the destination
					// normally.
					// Save the originator address and port
					// to forward later there.
					oldAddress = incomingPacket.getAddress();
					oldPort = incomingPacket.getPort();

					sendPacket(data, returnAddress, returnPort);
				} else {
					// Odd packets get sent back to the first sender.
					sendPacket(data, oldAddress, oldPort);
				}

				// Increments the counter.
				mimCounter++;

			} else {
				// Sends the packet to the address and port specified at
				// constructor time.
				sendPacket(data, returnAddress, returnPort);
			}
		}
	}

	/**
	 * Logs a packet.
	 * 
	 * @param packet
	 *            the packet to log
	 * @param incoming
	 *            whether the packet is incoming (true) or outcoming (false)
	 */
	private static void log(DatagramPacket packet, boolean incoming) {
		String direction = incoming ? "received" : "sent";
		String logHeader = "-------- UDP packet " + direction + " --------";
		System.out.println(logHeader);
		System.out.println("RAW data : " + byteArrayToPrettyString(packet.getData()));
		System.out.println("Data : " + new String(packet.getData()));
		System.out.println("Port: " + packet.getPort());
		System.out.println("Address : " + packet.getAddress());
		System.out.println("Socket Address: " + packet.getSocketAddress());
		System.out.println("Length : " + packet.getLength());
		System.out.println("Offset : " + packet.getOffset());
		System.out.println("-------------------------------------");
		System.out.println();
	}

	/**
	 * Sends a packet to the specified address.
	 * 
	 * @param data
	 *            the data to send
	 * @param destinationAddress
	 *            the address where to send the packet
	 * @param destinationPort
	 *            the port where to send the packet
	 */
	private static void sendPacket(byte[] data, InetAddress destinationAddress, int destinationPort) {
		// Gets the effective length of a packet by getting subtracting the
		// number of trailing 0 bytes from the packet size.
		int packetLength = data.length - 1;
		// Decrements the counter while the value of the read byte is zero.
		while (data[packetLength] == ZERO_BYTE) {
			packetLength--;
		}
		packetLength++;

		// Sends the packet.
		DatagramPacket packet = new DatagramPacket(data, packetLength, destinationAddress, destinationPort);
		try {
			serverSocket.send(packet);
			log(packet, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts a byte array into a string with each byte separated by a
	 * whitespace.
	 * 
	 * @param data
	 *            the byte array to convert
	 * @return a string representing the byte array
	 */
	private static String byteArrayToPrettyString(byte[] data) {
		StringBuilder builder = new StringBuilder();
		for (byte b : data) {
			builder.append(b).append(' ');
		}
		return builder.toString();
	}

	/**
	 * Main method for the UDP server. The server can work in 3 modes:
	 * 
	 * <ul>
	 * <li>redirect, the incoming packets will be logged and redirected to the
	 * address and port specified as the first two arguments. This is the
	 * default mode</li>
	 * <li>echo, the incoming packets will be sent back to the sender at the
	 * same address and port. This mode is enabled by passing 0 as the first
	 * argument and then any value</li>
	 * <li>man-in-the-middle, in this mode the server will redirect the packets
	 * as in redirect mode and send the reply received to the original server.
	 * Please note that the implementation of this mode is really naive and
	 * assumes that each even packet is a reply, so multiple requests within a
	 * short window of time may cause issues.</li>
	 * </ul>
	 *
	 * @param args
	 *            <ol>
	 *            <li>the binding port for the server</li>
	 *            <li>the address where the incoming packets will be forwarded
	 *            to</li>
	 *            <li>the port where the incoming packets will be forwarded
	 *            to</li>
	 *            <li>(optional) enables "man-in-the-middle" mode. This argument
	 *            can be any integer. If the value is 0 or not specified, this
	 *            mode will be disabled, otherwise it will enabled. In this
	 *            mode, the server will capture a packet, send it to the
	 *            forwarding address and port, get the reply and forward it back
	 *            to the first request originator</li>
	 *            </ol>
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		int serverBindingPort = 0;
		InetAddress forwardingAddress = null;
		boolean manInTheMiddleMode = false;
		int forwardingPort = 0;
		// First argument is the binding port.
		switch (args.length) {
		case 4:
			// Fourth argument enables man in the middle mode if not 0.
			if (!args[4].equals("0")) {
				System.out.println("Working in man-in-the-middle mode");
				manInTheMiddleMode = true;
			}
		case 3:
			// First argument is the binding port.
			System.out.println("Binding to port: " + args[0]);
			serverBindingPort = Integer.valueOf(args[0]);

			// Creates the server.
			serverSocket = new DatagramSocket(serverBindingPort);

			// Second and third arguments are the forwarding address and port.
			System.out.println("Forwarding captured packets to: " + args[1] + ":" + args[2]);
			String forwardingAddressArg = args[1];
			if (!args[1].equals("0")) {
				forwardingAddress = InetAddress.getByName(forwardingAddressArg);
			}
			forwardingPort = Integer.valueOf(args[2]);

			break;
		// Other cases are not accepted.
		default:
			System.out
					.println("Usage: <binding_port> (<forward_address> | 0) <forward_port> [man-in-the-middle-mode].");
			System.out.println("Passing a <forward_address> 0 will enable echo mode.");
			System.out.println(
					"[man-in-the-middle-mode] is an integer which can be 0 for disabled or any other value for enabled (0 is the default value).");

			System.exit(1);
			break;
		}

		// Starts up the server with the selected configuration.
		startServer(forwardingAddress, forwardingPort, manInTheMiddleMode);
	}

}