package ru.justnero.jetlauncher.swing;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import ru.justnero.jetlauncher.util.UtilSwing;

public class Panel03Action extends JetPanel {
    
    private static final int panelWidth  = 400;
    private static final int panelHeight = 50;
    private static final int panelLeft   = 0;
    private static final int panelTop    = 130;
    private final JetLabel actionL;
    private String actionS;
    
    public Panel03Action(JetFrame parent,String act) {
        super(parent,false);
        
        setLocation(panelLeft,panelTop);
        setSize(panelWidth,panelHeight);
        
        actionL = new JetLabel();
        actionS = act;
        add(actionL);
        
        init();
    }

    @Override
    protected void init() {
        actionL.setForeground(Color.BLACK);
        actionL.setFont(UtilSwing.getFont(1,18.0F));
        actionL.setText(actionS);
        actionL.setLocation((panelWidth-actionL.getWidth())/2,(panelHeight-actionL.getHeight())/2);
    }
    
    public void changeAction(String act) {
        actionS = act;
        init();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0,0,UtilSwing.colorGradientLightTop,0,panelHeight,UtilSwing.colorGradientLightBottom,false));
        g2.fillRect(0,0,panelWidth,panelHeight);
        g.setColor(UtilSwing.colorShadow);
        g.drawLine(0,0,400,0);
    }

}
