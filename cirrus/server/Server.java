package cirrus.server;

import cirrus.server.AntiVirus;
import cirrus.server.Flagger;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
        //TODO process arguments
        //TODO create new Server class instance, which will create listeners...

        AntiVirus f = new Flagger();

		SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
		SSLServerSocket welcomeSocket = (SSLServerSocket)factory.createServerSocket(6789);
		
		while(true) {
			SSLSocket connectionSocket = (SSLSocket)welcomeSocket.accept();
			
			DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());
			
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			
			int numFilesToReceive = inFromClient.readInt();
			for (int i = 0; i < numFilesToReceive; i++) {
				BufferedReader line = new BufferedReader(new InputStreamReader(inFromClient));
				String name = line.readLine();

				FileOutputStream output = new FileOutputStream("./" + name);
				
				long length = inFromClient.readLong();
				
				byte[] buffer = new byte[65536];
				int read = 0;
				int offset = 0;
				
				while (offset < length) {
					read = inFromClient.read(buffer, 0, 65536);
					output.write(buffer, 0, read);
					
					offset += read;
				}
				output.close();
				
				outToClient.writeBytes("Read " + offset + " bytes.\n");

                outToClient.writeBytes(f.scan(name) + "\n");
			}
			
			//outToClient.writeBytes("Read " + numFilesToReceive + " files.\n");
			
		}
	}

}
