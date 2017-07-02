package co.aurasphere.scripts;

import java.io.*;
import javax.microedition.io.*;
import javax.bluetooth.*;

// If using this script with an Arduino, first connect the device using your PC settings (default passcode is 1234).
public class BluetoothClient {

	public void startClient() {
		try {
			String url = "btspp://98D3318041DE:1";
			StreamConnection con = (StreamConnection) Connector.open(url);
			OutputStream os = con.openOutputStream();
			InputStream is = con.openInputStream();
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader bufReader = new BufferedReader(isr);
			RemoteDevice dev = RemoteDevice.getRemoteDevice(con);

			if (con != null) {
				while (true) {
					// sender string
					System.out.println("Serverd:" + dev.getBluetoothAddress()
							+ "\r\n" + "Put your string" + "\r\n");
					String str = bufReader.readLine();
					char c = str.charAt(0);
					os.write(c);
					// reciever string
//					byte buffer[] = new byte[1024];
//					int bytes_read = is.read(buffer);
//					String received = new String(buffer, 0, bytes_read);
//					System.out.println("client received" + "from:"
//							+ dev.getBluetoothAddress());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		try {
			LocalDevice local = LocalDevice.getLocalDevice();
			System.out.println("Address:" + local.getBluetoothAddress()
					+ "\nName: " + local.getFriendlyName());
		} catch (Exception e) {
			System.err.print(e.toString());
		}
		try {
			RFCOMMClient ss = new RFCOMMClient();
			while (true) {
				ss.startClient();
			}
		} catch (Exception e) {
			System.err.print(e.toString());
		}
	}
}
