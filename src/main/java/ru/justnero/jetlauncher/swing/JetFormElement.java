package ru.justnero.jetlauncher.swing;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import ru.justnero.jetlauncher.util.UtilSwing;

public class JetFormElement<T extends JComponent> extends JPanel {
    
    private final T _hidden;
    private final JLabel _placeholder;

    public JetFormElement(T hidden,String placeholder) {
        super();
        setSize(220,31);
        setLayout(null);
        setBorder(null);
        
        _hidden = hidden;
        _placeholder = placeholder != null ? new JLabel(placeholder) : null;
        
        boolean isComboBox = _hidden instanceof JetComboBox;
        _hidden.setSize(isComboBox ? 220 : 190,30);
        _hidden.setLocation(isComboBox ? 0 : 15,0);
        _hidden.setOpaque(false);
        _hidden.setBorder(null);
        _hidden.setFont(UtilSwing.getFont(1,18.0F));
        _hidden.setForeground(UtilSwing.colorTextDark);
        if(isComboBox) {
            ((JetComboBox) _hidden).init();
        }
        add(_hidden);
        
        if(_placeholder != null) {
            _placeholder.setSize(190,30);
            _placeholder.setLocation(15,0);
            _placeholder.setOpaque(false);
            _placeholder.setBorder(null);
            _placeholder.setFont(UtilSwing.getFont(1,18.0F));
            _placeholder.setForeground(UtilSwing.colorTextForm);
            _placeholder.setVisible(false);
            add(_placeholder);
        }
    }
    
    public T getHidden() {
        return _hidden;
    }

    @Override
    public void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        int gradientH = height - 1;
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0,0,UtilSwing.colorGradientLightTop,0,gradientH,UtilSwing.colorGradientLightBottom,false);
        g2.setPaint(gp);
        g2.fillRect(0,0,width,gradientH);
        g2.setColor(UtilSwing.colorShadow);
        g2.drawLine(0,gradientH,width,gradientH);
        if(_hidden instanceof JPasswordField) {
            _placeholder.setVisible(((JPasswordField) _hidden).getPassword().length == 0);
        } else if(_hidden instanceof JTextField) {
            _placeholder.setVisible(((JTextField)     _hidden).getText().isEmpty());
        }
    }

}
