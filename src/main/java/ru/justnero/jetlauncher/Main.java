package ru.justnero.jetlauncher;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import ru.justnero.jetlauncher.util.UtilLog;
import ru.justnero.jetlauncher.util.UtilFile;
import ru.justnero.jetlauncher.util.UtilSwing;
import ru.justnero.jetlauncher.util.UtilCommon;

import static ru.justnero.jetlauncher.util.UtilLog.*;

public class Main {
    
    public static void main(final String[] args) {
        preInit();
        
        init();
        
        postInit();
    }
    
    private static void preInit() {
        Thread thr = new Thread(InitSplash._instance,"Init Splash");
        thr.start();
    }
    
    private static void init() {
        System.setProperty("java.net.preferIPv4Stack","true");
        try {
            UtilLog.out = new PrintStream(new FileOutputStream(UtilFile.constructPath("launcher.log"),false),true);
        } catch(FileNotFoundException ex) {
            UtilLog.out = System.out;
            error("Can`t initialize log stream");
            error(ex);
        }
        info("Checking launcher to be up to date...");
        if(!Network.isUpToDate()) {
            warning("Whoa, I am outdated, not to good for me");
            info("I will try to update myself - if it not works tell my creator to fix it");
            update();
            System.exit(0);
        } else {
            info("Look`s like I am up to date, let`s go further");
        }
        info("Checking launcher to be in the right directory...");
        if(!UtilFile.isRightLocation()) {
            warning("Whoa, I am not where I supposed to be, need to fix this");
            info("I will use update to fix it - if it not works tell my creator to fix it");
            update();
            System.exit(0);
        } else {
            info("Look`s like I am here, go, go, go...");
        }
        info("Checking launcher is already launched...");
        if(!UtilCommon.lock(25600)) {
            warning("Whoa, I detected another copy of me, that`s not the deal i want");
            System.exit(0);
        } else {
            info("I am the only one, continue loading");
        }
        UtilSwing.initFonts(Main.class.getResource("theme/fonts/OpenSans-Regular.ttf"),Main.class.getResource("theme/fonts/OpenSans-ExtraBold.ttf"));
        LauncherHandler.start();
    }
    
    private static void update() {
        Path path = Paths.get(UtilFile.constructPath(".updater.jar").toURI());
        info("Updater download started");
        UtilFile.downloadFile("http://lz-craft.ru/public/system/.updater.jar",path,1024); // @TODO project url
        info("Download complete");
        info("It is time to run updater");
        try {
            Process process = Runtime.getRuntime().exec(new String[]{
                "java",
                "-jar",
                "\""+path.toString()+"\"",
            });
            if(process == null) {
                throw new Exception("Error from the process");
            }
            process.waitFor();
        } catch (Exception ex) {
            error(ex);
            error("Failed to run updater");
            System.exit(0);
        }
    }
    
    private static void postInit() {
        InitSplash._instance.die();
    }
    
}
