package ru.justnero.jetlauncher.swing;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import ru.justnero.jetlauncher.util.UtilSwing;

public class JetLabel extends JLabel {
    
    private int  topOffset = 0;
    private int leftOffset = 0;
    
    public JetLabel(String text) {
        super(text);
        
        setForeground(Color.BLACK);
        setFont(UtilSwing.getFont(1,12.0F));
        setSize(getPreferredSize());
    }

    public JetLabel() {
        super();
    }
    
    @Override
    public void setText(String text) {
        super.setText(text);
        setSize(getPreferredSize());
        repaint();
    }
    
    public void setLeftOffset(int x) {
        leftOffset = x;
    }
    
    public void setTopOffset(int y) {
        topOffset = y;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        if(isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0,0,getWidth(),getHeight());
        }
        g.setColor(getForeground());
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.setFont(getFont());
        FontMetrics fm = this.getFontMetrics(getFont());
        g.drawString(getText(),leftOffset,fm.getAscent()+topOffset);
    }
    
}
