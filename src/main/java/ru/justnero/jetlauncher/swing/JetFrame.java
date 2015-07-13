package ru.justnero.jetlauncher.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import ru.justnero.jetlauncher.LauncherHandler;
import ru.justnero.jetlauncher.Main;
import ru.justnero.jetlauncher.util.UtilFile;

import static ru.justnero.jetlauncher.util.UtilLog.*;
import ru.justnero.jetlauncher.util.UtilSwing;

public class JetFrame extends JFrame {
    
    public static final int AUTH     = 1;
    public static final int LOAD     = 2;
    public static final int SETTINGS = 3;
    
    public final LauncherHandler handler;
    
    private int jetFramePosX = 0,
                jetFramePosY = 0,
                contentType;
    private static final int jetFrameWidth  = 400,
                             jetFrameHeight = 450;
    
    private JetPanel panel01,
                     panel02,
                     panel03,
                     panel04,
                     panel05;
    private ImageButton logo,
                        settings;
    
    public JetFrame(LauncherHandler lh, String title, int content) {
        super(title); 
        handler = lh;
        contentType = content;
        
        setLayout(null);
        setResizable(false);
        setUndecorated(true);
        setBackground(new Color(0,0,0,0));
        setSize(jetFrameWidth,jetFrameHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addMouseListener(new MousePressedAdapter());
        addMouseMotionListener(new MouseMoveAdapter());
        
        try {
            setIconImage(ImageIO.read(Main.class.getResource("theme/favicon.png")));
        } catch (IOException ex) {
            error("Error loading favicon");
            error(ex);
        }
        String actionText = "";
        switch(contentType) {
            case AUTH:
                actionText = "Авторизация";
                panel04 = new Panel04Auth(this);
                break;
            case LOAD:
                actionText = "Загрузка";
                panel04 = new Panel04Load(this);
                break;
            case SETTINGS:
                actionText = "Настройки";
                panel04 = new Panel04Auth(this);
                break;
        }
        
        add(panel01 = new Panel01Title(this));
        add(logo = getLogotype());
        add(panel02 = new Panel02Logotype(this));
        add(settings = getSettings());
        add(panel03 = new Panel03Action(this,actionText));
        add(panel04);
        add(panel05 = new Panel05Copyright(this));
    }
    
    public JetPanel changeContent(int content) {
        if(contentType != content) {
            contentType = content;
            remove(panel04);
            String actionText = "";
            switch(contentType) {
                case AUTH:
                    actionText = "Авторизация";
                    panel04 = new Panel04Auth(this);
                    break;
                case LOAD:
                    actionText = "Загрузка";
                    panel04 = new Panel04Load(this);
                    break;
                case SETTINGS:
                    actionText = "Настройки";
                    panel04 = new Panel04Settings(this);
                    break;
            }
            ((Panel03Action) panel03).changeAction(actionText);
            add(panel04);
            repaint();
        }
        return panel04;
    }
    
    private ImageButton getLogotype() {
        ImageButton logotypeBtn = new ImageButton("theme/logotype.png");
        logotypeBtn.setLocation(6,66);
        logotypeBtn.setFocusPainted(false);
        logotypeBtn.addActionListener(new UtilSwing.ActionOpenLink(UtilFile.getWorkDir().toURI().toString()));
        return logotypeBtn;
    }
    
    private ImageButton getSettings() {
        ImageButton settingsBtn = new ImageButton("theme/settings.png");
        settingsBtn.setLocation(330,156);
        settingsBtn.setFocusPainted(false);
        settingsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeContent(AUTH+SETTINGS-contentType);
            }
        });
        return settingsBtn;
    }
    
    private class MousePressedAdapter extends MouseAdapter {
        
        @Override
        public void mousePressed(MouseEvent e) {
            jetFramePosX = e.getX();
            jetFramePosY = e.getY();
        }
        
    }
    
    private class MouseMoveAdapter extends MouseAdapter {
        
        @Override
        public void mouseDragged(MouseEvent evt) {		
            setLocation(evt.getXOnScreen()-jetFramePosX,evt.getYOnScreen()-jetFramePosY);
        }
        
    }
    
}
