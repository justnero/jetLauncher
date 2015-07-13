package ru.justnero.jetlauncher.util;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Comparator;

import static ru.justnero.jetlauncher.util.UtilLog.*;

public class UtilCommon {
    
    public static void openBrowser(String url) {
        try {
            URI uri = new URL(url).toURI();
            Desktop.getDesktop().browse(uri);
        } catch(Exception ex) {
            error(ex);
        }
    }
    
    public static boolean lock(int port) {
        try {
            final ServerSocket socket = new ServerSocket(port);
            Thread t = new Thread("lockHandler "+String.valueOf(port)) {
                @Override
                public void run() {
                    while(true) {
                        try {
                            socket.accept();
                            Thread.sleep(10L);
                        } catch(Exception ex) {}
                    }
                }
            };
            t.setDaemon(true);
            t.start();
            return true;
        } catch(IOException ex) {
            return false;
        }
    }

    public static OS getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        if(osName.contains("win")) return OS.windows;
        if(osName.contains("mac")) return OS.macos;
        if(osName.contains("solaris")) return OS.solaris;
        if(osName.contains("sunos")) return OS.solaris;
        if(osName.contains("linux")) return OS.linux;
        if(osName.contains("unix")) return OS.linux;
        return OS.unknown;
    }
    
    public static enum OS {
        linux, solaris, windows, macos, unknown;
    }
    
    public static class ValueSorterPair implements Comparator<Pair<String,String>> {
        @Override
        public int compare(Pair<String, String> a,Pair<String, String> b) {
            if(a.right.compareToIgnoreCase(b.right) >= 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }
    
    public static class Pair<L,R> {

        public L left;
        public R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public int hashCode() { 
            return left.hashCode() ^ right.hashCode(); 
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) 
                return false;
            if (!(o instanceof Pair)) 
                return false;
            Pair pair = (Pair) o;
            return this.left.equals(pair.left) && this.right.equals(pair.right);
        }

    }
    
}
