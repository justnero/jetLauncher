package ru.justnero.jetlauncher;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static javax.swing.JOptionPane.*;

import ru.justnero.jetlauncher.swing.JetFrame;
import ru.justnero.jetlauncher.swing.Panel04Load;
import ru.justnero.jetlauncher.util.UtilCommon;
import ru.justnero.jetlauncher.util.UtilFile;

import static ru.justnero.jetlauncher.util.UtilHash.*;
import static ru.justnero.jetlauncher.util.UtilLog.*;

public class LauncherHandler {
    
    private final JetFrame launcherFrame;
    
    private int userID = -1;
    private String userName = null;
    private String session = null;
    private Game game = null;
    private Game games[] = new Game[]{};
    private ArrayList<String> toExtract = new ArrayList<String>();
    
    private int memoryLimit = 512;
    
    private LauncherHandler() {
        launcherFrame = new JetFrame(this,"LZ-Craft Launcher",JetFrame.AUTH); //@TODO project name
    }
    
    public static void start() {
        new LauncherHandler().init();
    }
    
    private void init() {
        readSettings();
        launcherFrame.setVisible(true);
    }
    
    public Game[] getGames() {
        if(games.length > 0) {
            return games;
        }
        Socket connection = Network.connect(1);
        try {
            DataInputStream in = new DataInputStream(connection.getInputStream());
            int code = in.readInt();
            switch(code) {
                case 404:
                    info("Games not found");
                    showMessageDialog(launcherFrame,"Невозможно получить список серверов","Ошибка",ERROR_MESSAGE);
                    break;
                case 200:
                    info("Games found");
                    int count = in.readInt();
                    games = new Game[count];
                    for(int i=0;i<count;i++) {
                        games[i] = new Game(in.readInt(),in.readUTF(),in.readUTF());
                    }
                    break;
                default:
                    info("Games unknown code ",String.valueOf(code));
                    showMessageDialog(launcherFrame,"Произошла неизвестная ошибка, отправьте логи администрации","Ошибка",ERROR_MESSAGE);
                    break;
            }
        } catch(Exception ex) {
            error("Unknown error in games");
            error(ex);
            showMessageDialog(launcherFrame,"Произошла неизвестная ошибка, отправьте логи администрации","Ошибка",ERROR_MESSAGE);
        }
        Network.disconnect(connection);
        return games;
    }
    
    public void handleAuth(String name, String password, Game game, boolean remember) {
        if(name.isEmpty()) {
            showMessageDialog(launcherFrame,"Логин не может быть пустой","Ошибка",ERROR_MESSAGE);
            return;
        }
        if(name.length() > 16) {
            showMessageDialog(launcherFrame,"Логин не может быть больше 16 символов","Ошибка",ERROR_MESSAGE);
            return;
        }
        if(password.isEmpty()) {
            showMessageDialog(launcherFrame,"Пароль не может быть пустой","Ошибка",ERROR_MESSAGE);
            return;
        }
        if(game == null) {
            showMessageDialog(launcherFrame,"Вы должны выбрать сервер","Ошибка",ERROR_MESSAGE);
            return;
        }
        
        Socket connection = Network.connect(2);
        try {
            DataInputStream in = new DataInputStream(connection.getInputStream());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeUTF(name);
            out.writeUTF(sha1(password));
            out.writeUTF(sha1(
                    sha1("If you can read this - contact Nero")
                  + sha1(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()))
            ));
            out.flush();
            int code = in.readInt();
            switch(code) {
                case 406:
                    info("User ",name," did not conifirm his mail");
                    showMessageDialog(launcherFrame,"Вы не подтвердили почту","Ошибка",ERROR_MESSAGE);
                    break;
                case 401:
                    info("User ",name," password did not match");
                    showMessageDialog(launcherFrame,"Пароль не подходит","Ошибка",ERROR_MESSAGE);
                    break;
                case 403:
                    String reason = in.readUTF();
                    info("User ",name,"is banned for: ",reason);
                    showMessageDialog(launcherFrame,"Вы забанены в лаунчере. По причине: "+reason,"Ошибка",ERROR_MESSAGE);
                    break;
                case 404:
                    info("User ",name," does not exists");
                    showMessageDialog(launcherFrame,"Пользователь с таким логином не найден","Ошибка",ERROR_MESSAGE);
                    break;
                case 505:
                    error("User ",name," launcher did not pass secondary check");
                    showMessageDialog(launcherFrame,"Ошибка в лаунчере","Ошибка",ERROR_MESSAGE);
                    System.exit(0);
                    break;
                case 200:
                    info("User ",name," has passed");
                    userID   = in.readInt();
                    session  = in.readUTF();
                    userName = in.readUTF();
                    rememberData(name,password,remember);
                    startLoad(game);
                    break;
                default:
                    info("User ",name," unknown code ",String.valueOf(code));
                    showMessageDialog(launcherFrame,"Произошла неизвестная ошибка, отправьте логи администрации","Ошибка",ERROR_MESSAGE);
                    break;
            }
        } catch(Exception ex) {
            error("Unknown error in user auth");
            error(ex);
            showMessageDialog(launcherFrame,"Произошла неизвестная ошибка, отправьте логи администрации","Ошибка",ERROR_MESSAGE);
        }
        Network.disconnect(connection);
    }
    
    private void startLoad(final Game selectedGame) {
        new Thread() {
            @Override
            public void run() {
                game = selectedGame;
                final Panel04Load panel = (Panel04Load) launcherFrame.changeContent(JetFrame.LOAD);
                panel.update("Проверка файлов","","",0);
                File dir = UtilFile.constructPath(game.name());
                if(!checkGame(dir)) {
                    panel.update("Очистка директории","","",5);
                    clearDir(dir);
                    panel.update("Загрузка файлов","","",10);
                    downloadGame(dir,panel);
                    panel.update("Загрузка библиотек","","",70);
                    downloadNatives(dir,panel);
                    panel.update("Распаковка","","",80);
                    extractGame(dir,panel);
                }
                panel.update("Запускаем клиент","","",99);
                runGame(dir);
            }
        }.start();
    }
    
    private boolean checkGame(File dir) {
        if(UtilFile.fileExists(dir.toPath()) && Files.isDirectory(dir.toPath())) {
            ArrayList<String> hashes = UtilFile.getDirHashes(dir);
            Collections.sort(hashes);
            StringBuilder sb = new StringBuilder();
            for(String temp : hashes) {
                sb.append(temp);
            }
            String hash = sha1(sb.toString());
            boolean result = false;
            Socket connection = Network.connect(3);
            try {
                DataInputStream in = new DataInputStream(connection.getInputStream());
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeInt(game.id());
                out.writeUTF(hash);
                out.flush();
                result = in.readBoolean();
            } catch(Exception ex) {
                error("Unknown error in client validation");
                error(ex);
                showMessageDialog(launcherFrame,"Произошла неизвестная ошибка, отправьте логи администрации","Ошибка",ERROR_MESSAGE);
            }
            Network.disconnect(connection);
            return result;
        } else {
            return false;
        }
    }
    
    private static boolean clearDir(File dir) {
        if(Files.exists(dir.toPath()) && Files.isDirectory(dir.toPath())) {
            int count = dir.listFiles().length;
            for(File file : dir.listFiles()) {
                String fileName = file.getName();
                if(file.isDirectory()) {
                    if(!fileName.equals("saves") &&
                       !fileName.equals("stats") &&
                       !fileName.equals("screenshots")) {
                        if(clearDir(file)) {
                            file.delete();
                            count--;
                        }
                    }
                } else {
                    if(!fileName.endsWith(".point") &&
                       !fileName.endsWith(".points") &&
                       !fileName.equals("keyconfig.txt") &&
                       !fileName.equals("options.txt") &&
                       !fileName.equals("optionsof.txt")) {
                        file.delete();
                        count--;
                    }
                }
            }
            return count <= 0;
        }
        return true;
    }
    
    private void downloadGame(File dir, Panel04Load panel) {
        toExtract.clear();
        long count = -1L;
        long size = 0L;
        String root = null;
        ArrayList<String> list = new ArrayList<String>();
        Socket connection = Network.connect(4);
        try {
            DataInputStream in = new DataInputStream(connection.getInputStream());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeInt(game.id());
            out.flush();
            int code = in.readInt();
            switch(code) {
                case 404:
                    info("Files for ",game.displayName()," does not exists");
                    showMessageDialog(launcherFrame,"Клиент для этого сервера не найден","Ошибка",ERROR_MESSAGE);
                    break;
                case 200:
                    info("Files for ",game.displayName()," found");
                    count = in.readLong();
                    size = in.readLong();
                    root = in.readUTF();
                    for(long i = 0;i<count;i++) {
                        String tmp = in.readUTF();
                        list.add(tmp);
                        if(tmp.endsWith(".jzip")) {
                            toExtract.add(tmp);
                            debug("Added to extract ",tmp);
                        }
                    }
                    break;
                default:
                    info("Files for ",game.displayName()," unknown code ",String.valueOf(code));
                    showMessageDialog(launcherFrame,"Произошла неизвестная ошибка, отправьте логи администрации","Ошибка",ERROR_MESSAGE);
                    break;
            }
        } catch(Exception ex) {
            error("Unknown error in game files list");
            error(ex);
            showMessageDialog(launcherFrame,"Произошла неизвестная ошибка, отправьте логи администрации","Ошибка",ERROR_MESSAGE);
        }
        Network.disconnect(connection);
        if(root != null) {
            long currentSize = 0L;
            for(String file : list) {
                currentSize += downloadFile(panel,dir,root,"",file,size,currentSize,10,70);
            }
        }
    }
    
    private void downloadNatives(File dir, Panel04Load panel) {
        long count = -1L;
        long size = 0L;
        String root = null;
        ArrayList<String> list = new ArrayList<String>();
        Socket connection = Network.connect(4);
        try {
            DataInputStream in = new DataInputStream(connection.getInputStream());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeInt(0);
            UtilCommon.OS platform = UtilCommon.getPlatform();
            if(platform == UtilCommon.OS.windows) {
                out.writeUTF("windows");
            } else if(platform == UtilCommon.OS.linux) {
                out.writeUTF("linux");
            } else if(platform == UtilCommon.OS.macos) {
                out.writeUTF("macos");
            } else if(platform == UtilCommon.OS.solaris) {
                out.writeUTF("solaris");
            } else {
                out.writeUTF("unknown");
            }
            out.flush();
            int code = in.readInt();
            switch(code) {
                case 404:
                    info("Natives for ",game.displayName()," does not exists");
                    showMessageDialog(launcherFrame,"Библиотеки для этого сервера не найден","Ошибка",ERROR_MESSAGE);
                    break;
                case 200:
                    info("Natives for ",game.displayName()," found");
                    count = in.readLong();
                    size = in.readLong();
                    root = in.readUTF();
                    for(long i = 0;i<count;i++) {
                        String tmp = in.readUTF();
                        list.add(tmp);
                        if(tmp.endsWith(".jzip")) {
                            toExtract.add("bin/natives/"+tmp);
                            debug("Added to extract bin/natives/",tmp);
                        }
                    }
                    break;
                default:
                    info("Natives for ",game.displayName()," unknown code ",String.valueOf(code));
                    showMessageDialog(launcherFrame,"Произошла неизвестная ошибка, отправьте логи администрации","Ошибка",ERROR_MESSAGE);
                    break;
            }
        } catch(Exception ex) {
            error("Unknown error in game natives list");
            error(ex);
            showMessageDialog(launcherFrame,"Произошла неизвестная ошибка, отправьте логи администрации","Ошибка",ERROR_MESSAGE);
        }
        Network.disconnect(connection);
        if(root != null) {
            long currentSize = 0L;
            for(String file : list) {
                currentSize += downloadFile(panel,dir,root,"bin/natives",file,size,currentSize,70,80);
            }
        }
    }
    
    private void extractGame(File dir, Panel04Load panel) {
        int i = 0;
        for(String fileName : toExtract) {
            debug("Start extracting ",fileName);
            File file = new File(dir,fileName);
            panel.update(null,file.getName(),"",Math.min(90,80+(10*(i/toExtract.size()))));
            if(UtilFile.NO_ERROR != UtilFile.extractFile(file.getParent(),file.toPath(),false,true)) {
                error("Error extracting file ",file.toString());
                showMessageDialog(launcherFrame,"Ошибка распаковки файла","Ошибка",ERROR_MESSAGE);
            }
        }
        toExtract.clear();
    }
    
    private void runGame(File dir) {
        Process process = getGameProcess(dir);
        launcherFrame.setVisible(false);
        if(process != null) {
            try {
                final InputStream stdin = process.getInputStream();
                final InputStream stderr = process.getErrorStream();
                info("@START Client output");
                (new InputStreamProcessor(stdin,0)).start();
                (new InputStreamProcessor(stderr,2)).start();
                process.waitFor();
                info("@END   Client output");
            } catch(Exception ex) {
                error("Error in game process");
                error(ex);
            }
        }
        info("Cleint exit code ",String.valueOf(process.exitValue()));
        System.exit(0);
    }
    
    private Process getGameProcess(File dir) {
        try {
            File bin = new File(dir,"bin");
            File nativesDir = new File(bin,"natives");
            ArrayList<String> params = new ArrayList<String>();
            params.add("java");
            params.add("-Xmx"+String.valueOf(memoryLimit)+"m");
            params.add("-XX:MaxPermSize=128m");
            params.add("-XX:+AggressiveOpts");
            params.add("-XX:+UseStringCache");
            params.add("-Dsun.java2d.noddraw=true");
            params.add("-Dsun.java2d.d3d=false");
            params.add("-Dsun.java2d.opengl=false");
            params.add("-Dsun.java2d.pmoffscreen=false");
            params.add("-Djava.library.path="+nativesDir.getAbsolutePath());
//            params.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
//            params.add("-Dfml.ignorePatchDiscrepancies=true");
            params.add("-cp");
            params.add(getLibraries(dir));
            params.add("net.minecraft.launchwrapper.Launch");

            params.add("--accessToken");
            params.add("1");
            params.add("--userProperties");
            params.add("{}");
            
            params.add("--tweakClass");
            params.add("cpw.mods.fml.common.launcher.FMLTweaker");

            params.add("--username");
            params.add(userName);
            
            params.add("--gameID");
            params.add(String.valueOf(game.id()));

            params.add("--sessionID");
            String sid = getClientSID();
            if(sid == null) {
                throw new Exception("Client SID not found");
            }
            params.add(sid);

            params.add("--windowTitle");
            params.add(game.displayName());
            debug(Arrays.toString(params.toArray(new String[params.size()])));
            ProcessBuilder pb = new ProcessBuilder(params);
            pb.directory(dir);
            return pb.start();
        } catch(Exception ex) {
            error(ex);
        }
        return null;
    }
    
    private String getLibraries(File dir) {
        String separator = UtilCommon.getPlatform() == UtilCommon.OS.windows ? ";" : ":"; 
        
        File binDir = new File(dir,"bin");
        File libsDir = new File(dir,"libs");
        File librariesJar = new File(binDir,"libraries.jar");
        
        StringBuilder sb = new StringBuilder();
        if(UtilFile.fileExists(librariesJar.toPath())) {
            sb.append(librariesJar.toString());
        }
        
        if(libsDir.exists() && libsDir.listFiles() != null) {
            for(File file : libsDir.listFiles()) {
                if(file.getName().endsWith(".jar")) {
                    if(sb.length() > 0) {
                        sb.append(separator);
                    }
                    sb.append(file.toString());
                }
            }
        }
        if(sb.length() > 0) {
            sb.append(separator);
        }
        sb.append(binDir.toString());
        sb.append(File.separatorChar);
        sb.append("minecraft.jar");
        
        return sb.toString();
    }
    
    private String getClientSID() throws IOException {
        String sid = null;
        Socket connection = Network.connect(6);
        DataInputStream in = new DataInputStream(connection.getInputStream());
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeUTF(userName);
        out.writeUTF(sha1(sha1(session+sha1("UO does not works :P"))));
        out.flush();
        int code = in.readInt();
        switch(code) {
            case 401:
                showMessageDialog(launcherFrame,"Сессия истекла. Перезапустите лаунчер","Ошибка",ERROR_MESSAGE);
                break;
            case 200:
                sid = in.readUTF();
                break;
            default:
                info("Client session for ",userName," unknown code ",String.valueOf(code));
                showMessageDialog(launcherFrame,"Произошла неизвестная ошибка, отправьте логи администрации","Ошибка",ERROR_MESSAGE);
                break;
        }
        in.close();
        out.close();
        connection.close();
        return sid;
    }
    
    private long downloadFile(Panel04Load panel, File dir, String root, String prefix, String fileName, long fullSize, long currentSize, int min, int max) {
        File file = new File(dir,prefix.isEmpty() ? fileName : prefix+File.separator+fileName);
        panel.update(null,file.getName(),null,-1);
        long counter = 0L;
        Socket connection = Network.connect(5);
        try {
            DataInputStream in = new DataInputStream(connection.getInputStream());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeUTF(root+File.separator+fileName);
            out.flush();
            int code = in.readInt();
            switch(code) {
                case 404:
                    info("File download ",prefix,"/",fileName," does not exists");
                    showMessageDialog(launcherFrame,"Файл клиента не найден","Ошибка",ERROR_MESSAGE);
                    break;
                case 200:
                    long fileSize = in.readLong();
                    file.getParentFile().mkdirs();
                    FileOutputStream fout = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n = 0;
                    while(-1 != (n = in.read(buffer))) {
                        fout.write(buffer,0,n);
                        counter += n;
                        int percent = Math.round(min+(max-min)*(counter+currentSize)/fullSize);
                        panel.update(null,
                                     null,
                                     getSizeString(counter,fileSize),
                                     Math.min(max,percent));
                    }
                    fout.close();
                    fout = null;
                    break;
                default:
                    info("File download ",prefix,"/",fileName," unknown code ",String.valueOf(code));
                    showMessageDialog(launcherFrame,"Произошла неизвестная ошибка, отправьте логи администрации","Ошибка",ERROR_MESSAGE);
                    break;
            }
        } catch(Exception ex) {
            error("Unknown error in file download");
            error(ex);
            showMessageDialog(launcherFrame,"Произошла неизвестная ошибка, отправьте логи администрации","Ошибка",ERROR_MESSAGE);
        }
        Network.disconnect(connection);
        return counter;
    }
    
    private String getSizeString(long size, long full) {
        double sizeD = (double) size;
        double fullD = (double) full;
        String measure = "Б";
        if(fullD/1024.0D >= 1.0D) {
            measure = "КиБ";
            sizeD = sizeD / 1024.0D;
            fullD = fullD / 1024.0D;
        }
        if(fullD/1024.0D >= 1.0D) {
            measure = "МиБ";
            sizeD = sizeD / 1024.0D;
            fullD = fullD / 1024.0D;
        }
        return String.format("%.1f/%.1f %s",sizeD,fullD,measure);
    }
    
    public void readSettings() {
        File settingsFile = UtilFile.constructPath(".settings.dat");
        if(!settingsFile.exists()) {
            return;
        }
        try {
            FileInputStream settingsStream = new FileInputStream(settingsFile);
            
            byte buf[] = new byte[Integer.SIZE / Byte.SIZE];
            settingsStream.read(buf);
            memoryLimit =  (buf[3] << (Byte.SIZE * 3));
            memoryLimit |= (buf[2] & 0xFF) << (Byte.SIZE * 2);
            memoryLimit |= (buf[1] & 0xFF) << (Byte.SIZE * 1);
            memoryLimit |= (buf[0] & 0xFF);
            
            settingsStream.close();
            if(UtilCommon.getPlatform() == UtilCommon.OS.windows) {
                Runtime.getRuntime().exec("attrib +H "+settingsFile.getAbsolutePath());
            }
        } catch(Exception ex) {
            error("Error writing settings file");
            error(ex);
        }
    }
    
    public void writeSettings() {
        File settingsFile = UtilFile.constructPath(".settings.dat");
        try {
            if(settingsFile.exists()) {
                if(UtilCommon.getPlatform() == UtilCommon.OS.windows) {
                    Runtime.getRuntime().exec("attrib -H "+settingsFile.getAbsolutePath());
                }
                settingsFile.delete();
            }
            FileOutputStream settingsStream = new FileOutputStream(settingsFile);
            
            byte[] buf = new byte[Integer.SIZE / Byte.SIZE];
            buf[3] = (byte) (memoryLimit >> Byte.SIZE * 3);
            buf[2] = (byte) (memoryLimit >> Byte.SIZE * 2);   
            buf[1] = (byte) (memoryLimit >> Byte.SIZE);   
            buf[0] = (byte)  memoryLimit;
            settingsStream.write(buf);
            
            settingsStream.flush();
            settingsStream.close();
            if(UtilCommon.getPlatform() == UtilCommon.OS.windows) {
                Runtime.getRuntime().exec("attrib +H "+settingsFile.getAbsolutePath());
            }
        } catch(Exception ex) {
            error("Error writing settings file");
            error(ex);
        }
    }
    
    public String memberData() {
        File memoryFile = UtilFile.constructPath("auth.dat");
        File keyFile = UtilFile.constructPath(".key.dat");
        
        if(!keyFile.exists() || !memoryFile.exists()) {
            return "";
        }
        
        byte remember[];
        byte keyBytes[] = new byte[8];
        try {
            FileInputStream keyStream = new FileInputStream(keyFile);
            keyStream.read(keyBytes);
            keyStream.close();
        } catch(IOException ex) {
            error("Error reading key file");
            error(ex);
            return "";
        }
        try {
            FileInputStream memoryStream = new FileInputStream(memoryFile);
            int length = memoryStream.read();
            remember = new byte[length];
            memoryStream.read(remember);
            memoryStream.close();
        } catch(IOException ex) {
            error("Error reading memory file");
            error(ex);
            return "";
        }
        try {
            SecretKeySpec key = new SecretKeySpec(keyBytes,"DES");

            Cipher c = Cipher.getInstance("DES");
            c.init(Cipher.DECRYPT_MODE,key);
            remember = c.doFinal(remember);
        } catch(Exception ex) {
            error(ex);
            return "";
        }
        return new String(remember);
    }
    
    private void rememberData(String name, String password, boolean remember) {
        File memoryFile = UtilFile.constructPath("auth.dat");
        File keyFile = UtilFile.constructPath(".key.dat");
        
        byte[] toMemory = (name+(remember ? ";"+password : "")).getBytes();
        byte keyBytes[] = new byte[8];
        try {
            SecureRandom rnd = new SecureRandom();
            rnd.nextBytes(keyBytes);
        
            SecretKeySpec key = new SecretKeySpec(keyBytes,"DES");

            Cipher c = Cipher.getInstance("DES");
            c.init(Cipher.ENCRYPT_MODE,key);
            toMemory = c.doFinal(toMemory);
        } catch(Exception ex) {
            error(ex);
            return;
        }
        try {
            if(keyFile.exists()) {
                Runtime.getRuntime().exec("attrib -H "+keyFile.getAbsolutePath());
                keyFile.delete();
            }
            FileOutputStream keyStream = new FileOutputStream(keyFile);
            keyStream.write(keyBytes);
            keyStream.flush();
            keyStream.close();
            if(UtilCommon.getPlatform() == UtilCommon.OS.windows) {
                Runtime.getRuntime().exec("attrib +H "+keyFile.getAbsolutePath());
            }
        } catch(IOException ex) {
            error("Error writing key file");
            error(ex);
            return;
        }
        try {
            if(memoryFile.exists()) {
                Runtime.getRuntime().exec("attrib -H "+memoryFile.getAbsolutePath());
                memoryFile.delete();
            }
            FileOutputStream memoryStream = new FileOutputStream(memoryFile);
            memoryStream.write(toMemory.length);
            memoryStream.write(toMemory);
            memoryStream.flush();
            memoryStream.close();
            if(UtilCommon.getPlatform() == UtilCommon.OS.windows) {
                Runtime.getRuntime().exec("attrib +H "+memoryFile.getAbsolutePath());
            }
        } catch(IOException ex) {
            error("Error writing memory file");
            error(ex);
        }
    }
    
    public void setMemoryLimit(int memory) {
        memoryLimit = memory;
        writeSettings();
    }
    
    public int getMemoryLimit() {
        return memoryLimit;
    }
    
    public static class Game {
        
        private final int id;
        private final String name;
        private final String displayName;
        
        public Game(int id, String name, String displayName) {
            this.id = id;
            this.name = name;
            this.displayName = displayName;
        }
        
        public final int id() {
            return this.id;
        }
        
        public final String name() {
            return this.name;
        }
        
        public final String displayName() {
            return this.displayName;
        }
        
        @Override
        public final String toString() {
            return this.displayName;
        }
        
    }
    
    public static class InputStreamProcessor extends Thread {
        
        private final InputStream stream;
        private final int level;
        
        public InputStreamProcessor(InputStream is, int lvl) {
            stream = is;
            level = lvl;
        }
        
        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(stream);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while((line = br.readLine()) != null) {
                    switch(level) {
                        default:
                        case 0:
                            info(line);
                            break;
                        case 1:
                            warning(line);
                            break;
                        case 2:
                            error(line);
                            break;
                        case 3:
                            debug(line);
                            break;
                            
                    }
                }
            } catch(IOException ex) {
                error(ex);
            }
        }
        
    }
    
}
