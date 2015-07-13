package ru.justnero.jetlauncher.swing;

import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import static java.lang.Math.*;
import javax.swing.JPanel;
import ru.justnero.jetlauncher.util.UtilSwing;

public class JetProgressBar extends JPanel {
    
    private float percentValue = 0.0F;
    private String percentText = "00%";
   
    public JetProgressBar() {
        super();
        setSize(220,46);
        setBorder(null);
        setLayout(null);
    }
    
    public void setProgress(float value) {
        percentValue = min(max(0.0F,value),100.0F);
        int percent = round(percentValue);
        StringBuilder sb = new StringBuilder();
        if(percent < 10) {
            sb.append("0");
        }
        sb.append(percent);
        sb.append("%");
        percentText = sb.toString();
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        int gradientH = height - 1;
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0,15,UtilSwing.colorGradientLightTop,0,45,UtilSwing.colorGradientLightBottom,false);
        g2.setPaint(gp);
        g2.fillRect(0,15,width,30);
        gp = new GradientPaint(0,15,UtilSwing.colorGradientMainTop,0,45,UtilSwing.colorGradientMainBottom,false);
        g2.setPaint(gp);
        g2.fillRect(0,15,round(width/100.0F*percentValue),30);
        g2.setColor(UtilSwing.colorShadow);
        g2.drawLine(0,gradientH,width,gradientH);
        
        g2.setColor(UtilSwing.colorTextLight);
        g2.fillRect((width-60)/2-1,0,60,15);
        gp = new GradientPaint(0,0,UtilSwing.colorGradientLightTTop,0,30,UtilSwing.colorGradientLightTBottom,false);
        g2.setPaint(gp);
        g2.fillRect((width-60)/2-1,0,60,30);
        g2.setColor(UtilSwing.colorTShadow);
        g2.drawLine((width-60)/2-1,30,(width-60)/2+59,30);
        
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2.setFont(UtilSwing.getFont(2,18.0F));
        FontMetrics fm = getFontMetrics(g2.getFont());
        g2.setColor(UtilSwing.colorTextDark);
        g2.drawString(percentText,(width-fm.stringWidth(percentText))/2,15+(30-fm.getAscent())/2);
    }
    
}
