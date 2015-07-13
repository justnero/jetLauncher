package ru.justnero.jetlauncher.swing;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JButton;

import ru.justnero.jetlauncher.util.UtilSwing;

public class IconButton extends JButton {
    
    public IconButton(String text) {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));    
        setFont(UtilSwing.getFont(2,14.0F));
        setForeground(UtilSwing.colorTextNormal);
        setMargin(new Insets(0,0,0,0));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setText(text);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                setForeground(UtilSwing.colorTextHover);
            }
            
            @Override
            public void mouseExited(MouseEvent evt) {
                setForeground(UtilSwing.colorTextNormal);
            }
        });
    }
    
}
