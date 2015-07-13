package ru.justnero.jetlauncher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import static ru.justnero.jetlauncher.util.UtilHash.sha1;
import static ru.justnero.jetlauncher.util.UtilLog.*;

public class Network {
    
    public static boolean isUpToDate() {
        try {
            String hash = sha1(
                    sha1("If you can read this - contact Nero")
                  + sha1(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()))
            );
            Socket connection = Network.connect(0);
            DataInputStream in = new DataInputStream(connection.getInputStream());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeUTF(hash);
            out.flush();
            boolean answer = in.readInt() == 200;
            Network.disconnect(connection);
            return answer;
        } catch(Exception ex) {
            error("Error while checking launcher up to date");
            error(ex);
            return false;
        }
    }
    
    public static Socket connect(int packet) {
        return connect(packet,false);
    }
    
    public static Socket connect(int packet, boolean forceFlushID) {
        Socket socket = null;
        try {
            socket = new Socket(InetAddress.getByName("auth.lz-craft.ru"),15705); // @TODO project auth server
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeInt(packet);
            if(forceFlushID) {
                out.flush();
            }
        } catch(IOException ex) {
            error(ex);
        }
        return socket;
    }
    
    public static void disconnect(Socket connection) {
        try {
            if(connection != null && connection.isConnected() && !connection.isClosed()) {
                connection.close();
            }
        } catch(IOException ex) {
            error(ex);
        }
    }
    
    public static Socket getSocket(String host,int port) {
        try {
            return new Socket(InetAddress.getByName(host),port);
        } catch(IOException ex) {
            error(ex);
            return null;
        }
    }

}
