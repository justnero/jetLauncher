package ru.justnero.jetlauncher.swing;

import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import ru.justnero.jetlauncher.Main;

import ru.justnero.jetlauncher.util.UtilSwing;

public class Panel04Load extends JetPanel {
    
    private static Panel04Load instance = null;
    private static boolean tardisVisible = false;
    
    private static final int panelWidth  = 400;
    private static final int panelHeight = 250;
    private static final int panelLeft   = 0;
    private static final int panelTop    = 180;
    
    private JLabel tardisL;
    private JetLabel noticeL1;
    private JetLabel noticeL2;
    private JetLabel stateL;
    private JetLabel currentL;
    private JetLabel subdataL;
    private JetProgressBar progressBar;
    
    public Panel04Load(JetFrame parent) {
        super(parent);
        instance = this;
    }

    @Override
    protected void init() {
        setLocation(panelLeft,panelTop);
        setSize(panelWidth,panelHeight);
                
        Icon tardisI = new ImageIcon(Main.class.getResource("theme/tardis.gif"));
        tardisL = new JLabel(tardisI);
        tardisL.setSize(tardisL.getPreferredSize());
        tardisL.setLocation(300,10);
        tardisL.setVisible(tardisVisible);
        add(tardisL);
        
        noticeL1 = new JetLabel("Подготовка к запуску");
        noticeL1.setLocation((panelWidth-noticeL1.getWidth())/2,10);
        add(noticeL1);
        
        noticeL2 = new JetLabel("Терпение, друг мой");
        noticeL2.setLocation((panelWidth-noticeL2.getWidth())/2,24);
        add(noticeL2);
        
        stateL = new JetLabel();
        stateL.setFont(UtilSwing.getFont(2,18.0F));
        stateL.setForeground(UtilSwing.colorTextForm);
        stateL.setSize(stateL.getPreferredSize());
        stateL.setLocation((panelWidth-stateL.getWidth())/2,65);
        add(stateL);
        
        currentL = new JetLabel();
        currentL.setFont(UtilSwing.getFont(1,18.0F));
        currentL.setSize(currentL.getPreferredSize());
        currentL.setLocation((panelWidth-currentL.getWidth())/2,100);
        add(currentL);
        
        subdataL = new JetLabel();
        subdataL.setFont(UtilSwing.getFont(1,18.0F));
        subdataL.setSize(subdataL.getPreferredSize());
        subdataL.setLocation((panelWidth-subdataL.getWidth())/2,130);
        add(subdataL);
        
        progressBar = new JetProgressBar();
        progressBar.setLocation((panelWidth-progressBar.getWidth())/2,185);
        add(progressBar);
    }
    
    public static void toggleTardis(boolean state) {
        tardisVisible = state;
        if(instance != null) {
            instance.tardisL.setVisible(state);
        }
    }
    
    public void update(final String state, final String current, final String subdata, final int progress) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(state != null) {
                    stateL.setText(state);
                    stateL.setLocation((panelWidth-stateL.getWidth())/2,65);
                    stateL.repaint();
                }

                if(current != null) {
                    currentL.setText(current);
                    currentL.setLocation((panelWidth-currentL.getWidth())/2,100);
                    currentL.repaint();
                }

                if(subdataL != null) {
                    subdataL.setText(subdata);
                    subdataL.setLocation((panelWidth-subdataL.getWidth())/2,130);
                    subdataL.repaint();
                }

                if(progress >= 0) {
                    progressBar.setProgress(progress);
                    progressBar.repaint();
                }
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(UtilSwing.colorTextLight);
        g.fillRect(0,0,400,250);
        g.setColor(UtilSwing.colorShadow);
        g.drawLine(0,0,400,0);
        g.drawLine(0,249,400,249);
    }
    
}
