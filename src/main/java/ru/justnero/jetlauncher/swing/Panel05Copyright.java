package ru.justnero.jetlauncher.swing;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.FilteredImageSource;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import ru.justnero.jetlauncher.Main;
import ru.justnero.jetlauncher.util.UtilSwing;

import static ru.justnero.jetlauncher.util.UtilLog.*;

public class Panel05Copyright extends JetPanel {
    
    public static int clickCounter = 0;
    
    private static final int panelWidth  = 400;
    private static final int panelHeight = 20;
    private static final int panelLeft   = 0;
    private static final int panelTop    = 430;
    
    private JetLabel copyL;
    
    public Panel05Copyright(JetFrame parent) {
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
                                    Main.class.getResource("theme/panel05copyright.bg.png")
                            ).getSource(),
                            new UtilSwing.TransparentFilter()
                    )
            );
        } catch(Exception ex) {
            setBackground(UtilSwing.colorTextDark);
            error("Can`t load background, using black");
            error(ex);
        }
        
        copyL = new JetLabel("jetLauncher © Nero");
        copyL.setLocation((panelWidth-copyL.getWidth())/2,0);
        add(copyL);
        
        copyL.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickCounter++;
                if(clickCounter >= 10) {
                    Panel04Load.toggleTardis(true);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }
    
    private void minimize() {
        _parent.setState(JFrame.ICONIFIED);
    }

}
