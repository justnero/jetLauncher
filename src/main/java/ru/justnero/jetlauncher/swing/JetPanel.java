package ru.justnero.jetlauncher.swing;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public abstract class JetPanel extends JPanel {
    
    protected final JetFrame _parent;
    protected Image bgImage;
    
    public JetPanel(JetFrame parent) {
        super(null);
        setOpaque(false);
        _parent = parent;
        init();
    }
    
    public JetPanel(JetFrame parent, boolean autoInit) {
        super(null);
        setOpaque(false);
        _parent = parent;
        if(autoInit) {
            init();
        }
    }
    
    protected abstract void init();

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(bgImage,0,0,this);
    }
    
}
