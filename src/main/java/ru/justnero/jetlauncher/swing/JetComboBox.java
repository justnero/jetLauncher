package ru.justnero.jetlauncher.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;

import ru.justnero.jetlauncher.util.UtilSwing;

public class JetComboBox<E> extends JComboBox<E> {
    
    private final JetLabel selectedL;
    private final JetComboBox _this;
    
    public JetComboBox(E[] list) {
        super(list);
        _this = this;
        setUI(new JetComboBoxUI());
        setRenderer(new JetComboBoxRenderer<E>());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        selectedL = new JetLabel(getSelectedItem() == null ? "Выберите сервер" : getSelectedItem().toString());
        addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(_this.getSelectedItem() == null) {
                    selectedL.setText("Выберите сервер");
                } else {
                    selectedL.setText(_this.getSelectedItem().toString());
                }
            }
        });
    }
    
    public void init() {
        selectedL.setFont(getFont());
        selectedL.setForeground(getForeground());
        selectedL.setSize(175,getHeight());
        selectedL.setLocation(15,(30-selectedL.getHeight())/2+2);
        add(selectedL);
    }

    @Override
    public void paintComponent(Graphics g) {}
    
    private class JetComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new BasicArrowButton(
                    BasicArrowButton.SOUTH,
                    UtilSwing.colorTextForm,
                    UtilSwing.colorTextForm,
                    UtilSwing.colorTextForm,
                    UtilSwing.colorTextForm
            ) {
                
                @Override
                public void paint(Graphics g) {
                    int w = getWidth();
                    int h = getHeight();
                    int size = Math.max(Math.min((h-4)/3,(w-4)/3),2);
                    Graphics2D g2 = (Graphics2D) g;
                    GradientPaint gp = new GradientPaint(0,0,UtilSwing.colorGradientDarkTop,0,h,UtilSwing.colorGradientDarkBottom,false);
                    g2.setPaint(gp);
                    g2.fillRect(0,0,w,h);
                    paintTriangle(g,(w-size)/2,(h-size)/2 + (getModel().isPressed() ? 1 : 0),size,direction,false);
                }
            };
            button.setBorderPainted(false);
            button.setSize(30,30);
            button.setForeground(UtilSwing.colorTextForm);
            return button;
        }
    }
    
    private class JetComboBoxRenderer<E> extends JetLabel implements ListCellRenderer<E> {
        
        public JetComboBoxRenderer() {
            super("Ошибка");
            setLeftOffset(15);
            setOpaque(true);
        }
        
        @Override
        public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if(isSelected) {
                setBackground(UtilSwing.colorTextDark);
                setForeground(UtilSwing.colorTextLight);
            } else {
                setBackground(UtilSwing.colorTextLight);
                setForeground(UtilSwing.colorTextDark);
            }
            setText(value.toString());
            setFont(UtilSwing.getFont(1,18.0F));
            return this;
        }
    }

}
