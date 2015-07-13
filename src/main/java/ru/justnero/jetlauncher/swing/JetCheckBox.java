package ru.justnero.jetlauncher.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import javax.imageio.ImageIO;

import javax.swing.JCheckBox;
import ru.justnero.jetlauncher.Main;
import static ru.justnero.jetlauncher.util.UtilLog.error;

import ru.justnero.jetlauncher.util.UtilSwing;

public class JetCheckBox extends JCheckBox {
    
    protected Image checkIcon;
    
    public JetCheckBox() {
        super();
        setSize(20,20);
        setLayout(null);
        setBorder(null);
        setForeground(Color.WHITE);
        setFont(UtilSwing.getFont(1,18.0F));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        try {
            checkIcon = Toolkit.getDefaultToolkit().createImage(
//                    new FilteredImageSource(
                            ImageIO.read(
                                    Main.class.getResource("theme/panel04form.checkmark.png")
                            ).getSource()//,
//                            new UtilSwing.TransparentFilter()
//                    )
            );
        } catch(Exception ex) {
            setBackground(UtilSwing.colorTextDark);
            error("Can`t load check icon, using improvised version");
            error(ex);
        }
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
        if(isSelected()) {
            if(checkIcon != null) {
                g2.drawImage(checkIcon,2,2,16,16,this);
            } else {
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
                g2.setColor(UtilSwing.colorTextNormal);
                g2.fillOval(5,4,10,10);
            }
        }
    }
    
}
