package ru.justnero.jetlauncher.swing;

import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

import ru.justnero.jetlauncher.util.UtilSwing;

public class JetButton extends JButton {
    
    private final JetLabel textL;
    
    public JetButton(String text) {
        super(text);
        
        setSize(220,31);
        setLayout(null);
        setBorder(null);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        textL = new JetLabel(text);
        textL.setBorder(null);
        textL.setOpaque(false);
        textL.setFont(UtilSwing.getFont(1,18.0F));
        textL.setForeground(UtilSwing.colorTextNormal);
        textL.setSize(textL.getPreferredSize());
        textL.setLocation(
                (getWidth()  - textL.getWidth())  / 2,
                (getHeight() - textL.getHeight()) / 2);
        add(textL);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        int gradientH = height - 1;
        GradientPaint gp = new GradientPaint(0,0,UtilSwing.colorGradientMainTop,0,gradientH,UtilSwing.colorGradientMainBottom,false);
        g2.setPaint(gp);
        g2.fillRect(0,0,width,gradientH);
        g2.setColor(UtilSwing.colorShadow);
        g2.drawLine(0,gradientH,width,gradientH);
        textL.setLocation(
                (width  - textL.getWidth())  / 2,
                (height - textL.getHeight()) / 2 + (getModel().isPressed() ? 1 : 0)
        );
    }

}
