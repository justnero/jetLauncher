package ru.justnero.jetlauncher.swing;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import ru.justnero.jetlauncher.Main;

import static ru.justnero.jetlauncher.util.UtilLog.*;
import ru.justnero.jetlauncher.util.UtilSwing;

public class ImageButton extends JButton {
    
    
    public ImageButton(String url) {
        ImageIcon img = null;
        try {
            img = new ImageIcon(ImageIO.read(Main.class.getResource(url)));
        } catch(IOException ex) {
            error("Error loading image button: ",url);
            error(ex);
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setSize(img.getIconWidth(),img.getIconHeight());
        setMargin(new Insets(0,0,0,0));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(true);
        setOpaque(false);
        setIcon(img);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                Point point = getLocation();
                point.translate(0,1);
                setLocation(point);
            }
            
            @Override
            public void mouseReleased(MouseEvent evt) {
                Point point = getLocation();
                point.translate(0,-1);
                setLocation(point);
            }
        });
    }
    
}
