package ru.justnero.jetlauncher.swing;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import ru.justnero.jetlauncher.LauncherHandler.Game;
import ru.justnero.jetlauncher.util.UtilSwing;

public class Panel04Auth extends JetPanel {
    
    private static final int panelWidth  = 400;
    private static final int panelHeight = 250;
    private static final int panelLeft   = 0;
    private static final int panelTop    = 180;
    
    public Panel04Auth(JetFrame parent) {
        super(parent);
    }

    @Override
    protected void init() {
        setLocation(panelLeft,panelTop);
        setSize(panelWidth,panelHeight);
        
        JetLabel noticeL1 = new JetLabel("Никому не говорите свой пароль");
        noticeL1.setLocation((panelWidth-noticeL1.getWidth())/2,10);
        add(noticeL1);
        
        JetLabel noticeL2 = new JetLabel("и не передавайте данные из клиента");
        noticeL2.setLocation((panelWidth-noticeL2.getWidth())/2,24);
        add(noticeL2);
        
        String member = _parent.handler.memberData();
        String userName = "";
        String password = "";
        if(!member.isEmpty()) {
            String splited[] = member.split(";");
            userName = splited[0];
            if(splited.length == 2) {
                password = splited[1];
            }
        }
        
        final JetFormElement<JTextField> loginF = new JetFormElement<JTextField>(new JTextField(userName),"Логин...");
        loginF.setLocation((panelWidth-loginF.getWidth())/2,50);
        add(loginF);
        
        final JetFormElement<JPasswordField> passwordF = new JetFormElement<JPasswordField>(new JPasswordField(password),"Пароль...");
        passwordF.setLocation((panelWidth-passwordF.getWidth())/2,100);
        
        final JetCheckBox checkBoxBtn = new JetCheckBox();
        checkBoxBtn.setLocation((panelWidth-passwordF.getWidth()-checkBoxBtn.getWidth())/2,100-checkBoxBtn.getHeight()/2);
        checkBoxBtn.setSelected(!password.isEmpty());
        
        add(checkBoxBtn);
        add(passwordF);
        
        final JetFormElement<JetComboBox<Game>> serverF = new JetFormElement<JetComboBox<Game>>(new JetComboBox<Game>(_parent.handler.getGames()),null);
        serverF.setLocation((panelWidth-serverF.getWidth())/2,150);
        add(serverF);
        
        final JetButton loginB = new JetButton("Войти");
        loginB.setLocation((panelWidth-loginB.getWidth())/2,200);
        add(loginB);
        
        loginB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _parent.handler.handleAuth(
                        loginF.getHidden().getText(),
                        new String(passwordF.getHidden().getPassword()),
                        (Game) serverF.getHidden().getSelectedItem(),
                        checkBoxBtn.isSelected()
                );
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
