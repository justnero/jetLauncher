package ru.justnero.jetlauncher.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import ru.justnero.jetlauncher.Main;
import static ru.justnero.jetlauncher.util.UtilCommon.*;
import ru.justnero.jetlauncher.util.UtilCommon.OS;
import static ru.justnero.jetlauncher.util.UtilHash.sha1;

import static ru.justnero.jetlauncher.util.UtilLog.*;

public class UtilFile {
    
    public static final int NO_ERROR = 0;
    public static final int NO_FILE_ERROR = 1;
    public static final int FILE_EXISTS_ERROR = 1;
    public static final int INPUT_ERROR = 3;
    public static final int PERMISSION_ERROR = 4;
    public static final int UNKNOWN_ERROR = 5;
    public static int bufferSize = 4096;
    public static Exception lastException = null;
    
    private static File workDir = null;
    
    public static boolean isRightLocation() {
        try {
            String thisDir = URLDecoder.decode(Main.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm(),"UTF-8");
            String rightDir = URLDecoder.decode(constructPath("jetLZ."+(getPlatform() == OS.windows ? "exe" : "jar")).toURI().toURL().toExternalForm(),"UTF-8"); // @TODO project name
            if(thisDir.equalsIgnoreCase(rightDir)) {
                return true;
            }
        } catch(MalformedURLException|UnsupportedEncodingException ex) {
            error("Error while checking directory");
            error(ex);
        }
        return false;
    }
    
    public static ArrayList<String> getDirHashes(File dir) {
        ArrayList<String> tmp_map = new ArrayList<String>();
        for(File file : dir.listFiles()) {
            if(file.isDirectory()) {
                tmp_map.addAll(getDirHashes(file));
            } else {
                String fileName = file.getName();
                if(fileName.endsWith(".exe") || 
                   fileName.endsWith(".jar") || 
                   fileName.endsWith(".zip") || 
                   fileName.endsWith(".class")) {
                    tmp_map.add(sha1(file));
                }
            }
        }
        return tmp_map;
    }
    
    public static File constructPath(String... args) {
        StringBuilder sb = new StringBuilder();
        for(String arg : args) {
            if(sb.length() > 0) {
                sb.append(File.separatorChar);
            }
            sb.append(arg);
        }
        return new File(getWorkDir(),sb.toString());
    }
    
    public static File getWorkDir() {
        if(workDir == null)
            workDir = getWorkDir("jetLZ");
        return workDir;
    }

    private static File getWorkDir(String applicationName) {
        String userHome = System.getProperty("user.home",".");
        File workingDirectory;
        switch (getPlatform().ordinal()) {
            case 0:
            case 1:
                workingDirectory = new File(userHome,'.'+applicationName+'/');
                break;
            case 2:
                String applicationData = System.getenv("APPDATA");
                if(applicationData != null) {
                    workingDirectory = new File(applicationData,"."+applicationName+'/');
                } else {
                    workingDirectory = new File(userHome,'.'+applicationName+'/');
                }
                break;
            case 3:
                workingDirectory = new File(userHome,"Library/Application Support/"+applicationName+'/');
                break;
            default:
                workingDirectory = new File(userHome,applicationName+'/');
        }
        if((!workingDirectory.exists()) && (!workingDirectory.mkdirs()))
            throw new RuntimeException("The working directory could not be created: "+workingDirectory);
        return workingDirectory;
    }
    
    public static int downloadFile(String strURL, Path path, int buffSize) {
        if(fileExists(path)) {
            return FILE_EXISTS_ERROR;
        }
        try {
            Files.createDirectories(path.getParent());
            URL connection = new URL(strURL);
            HttpURLConnection urlconn;
            urlconn = (HttpURLConnection) connection.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.connect();
            try(InputStream in = urlconn.getInputStream(); OutputStream writer = new FileOutputStream(path.toFile())) {
                byte buffer[] = new byte[buffSize == 0 ? bufferSize : buffSize];
                int c = in.read(buffer);
                while(c > 0) {
                    writer.write(buffer, 0, c);
                    c = in.read(buffer);
                }
                writer.flush();
            }
        } catch (IOException ex) {
            lastException = ex;
            debug("Can`t download file from:",strURL," to:",path.toString());
            error(ex);
            return UNKNOWN_ERROR;
        }
        return NO_ERROR;
    }
    
    public static int extractFile(String root,Path filePath,boolean cleanBefore,boolean deleteAfter) {
        if(!fileExists(filePath)) {
            return NO_FILE_ERROR;
        }
        BufferedOutputStream dest = null;
        try {
            Path rootPath = Paths.get(root);
            if(cleanBefore) {
                Files.walkFileTree(rootPath,new DeleteWalker());
                Files.deleteIfExists(rootPath);
            }
            if(!fileExists(rootPath)) {
                Files.createDirectories(rootPath);
            }
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(filePath.toFile())));
            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {
                if(fileExists(new File(root,entry.getName()).toPath())) {
                    continue;
                }
                debug("Extracting: ",entry.getName());
                int count;
                byte data[] = new byte[bufferSize];
                if(entry.isDirectory()) {
                    Files.createDirectories(Paths.get(root,entry.getName()));
                } else {
                    int lastSeparator = entry.getName().lastIndexOf('/');
                    if (lastSeparator != -1) {
                        if(!fileExists(new File(root,entry.getName().substring(0,lastSeparator)).toPath())) {
                            Files.createDirectories(Paths.get(root,entry.getName().substring(0,lastSeparator)));
                        }
                    }
                    FileOutputStream fos = new FileOutputStream(root + "/" + entry.getName());
                    dest = new BufferedOutputStream(fos);
                    while((count = zis.read(data)) != -1) {
                        dest.write(data,0,count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            zis.closeEntry();
            zis.close();
            if(deleteAfter) {
                Files.delete(filePath);
            }
        } catch(Exception ex) {
            lastException = ex;
            error(ex);
            return UNKNOWN_ERROR;
        }
        return NO_ERROR;
    }
    
    public static boolean fileExists(String path) {
        return fileExists(Paths.get(path));
    }
    
    public static boolean fileExists(Path path) {
        return Files.exists(path);
    }
    
    public static boolean isDirectory(String path) {
        return isDirectory(Paths.get(path));
    }
    
    public static boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }
    
    public static class DeleteWalker implements FileVisitor<Path> {

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }

    }
    
}
