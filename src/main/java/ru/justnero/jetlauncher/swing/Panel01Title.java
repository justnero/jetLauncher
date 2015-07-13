package ru.justnero.jetlauncher.swing;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.FilteredImageSource;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import ru.justnero.jetlauncher.Main;

import static ru.justnero.jetlauncher.util.UtilLog.*;
import ru.justnero.jetlauncher.util.UtilSwing;

public class Panel01Title extends JetPanel {
    
    private static final int panelWidth  = 400;
    private static final int panelHeight = 30;
    private static final int panelLeft   = 0;
    private static final int panelTop    = 0;
    
    public Panel01Title(JetFrame parent) {
        super(parent);
    }

    @Override
    protected void init() {
        setLocation(panelLeft,panelTop);
        setSize(panelWidth,panelHeight);
        setOpaque(false);
        
        try {
            bgImage = Toolkit.getDefaultToolkit().createImage(
                    new FilteredImageSource(
                            ImageIO.read(
                                    Main.class.getResource("theme/panel01title.bg.png")
                            ).getSource(),
                            new UtilSwing.TransparentFilter()
                    )
            );
        } catch(Exception ex) {
            setBackground(UtilSwing.colorTextDark);
            error("Can`t load background, using black");
            error(ex);
        }
        
        IconButton minimizeBtn = new IconButton("_");
        minimizeBtn.setLocation(panelWidth-52,1);
        minimizeBtn.setSize(minimizeBtn.getPreferredSize());
        minimizeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                minimize();
            }
        });
        add(minimizeBtn);
        
        IconButton closeBtn = new IconButton("X");
        closeBtn.setLocation(panelWidth-32,4);
        closeBtn.setSize(closeBtn.getPreferredSize());
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });
        add(closeBtn);
    }
    
    private void minimize() {
        _parent.setState(JFrame.ICONIFIED);
    }

}
