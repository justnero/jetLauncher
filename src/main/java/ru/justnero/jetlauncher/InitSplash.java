package ru.justnero.jetlauncher;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import static ru.justnero.jetlauncher.util.UtilLog.*;

public class InitSplash extends JWindow implements Runnable { 
    
    private static final Image bin = Toolkit.getDefaultToolkit().getImage(
            InitSplash.class.getResource("theme/loading.png"));
    public static InitSplash _instance = new InitSplash();
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(bin,0,0,this);
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                _instance.showsplash();
            }
        });
    }

    public void showsplash() {
        try {
            _instance.setSize(bin.getWidth(this),bin.getHeight(this));
            _instance.setLocationRelativeTo(null);
            _instance.setVisible(true);
        } catch (Exception ex) {
            error("Can`t show splash.");
            error(ex);
        }
    }
    
    public void die() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                _instance.dispose();
            }
        });
    }
} 

