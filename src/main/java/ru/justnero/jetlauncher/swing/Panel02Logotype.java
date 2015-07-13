package ru.justnero.jetlauncher.swing;

import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;

import javax.imageio.ImageIO;

import ru.justnero.jetlauncher.Main;
import ru.justnero.jetlauncher.util.UtilSwing;

import static ru.justnero.jetlauncher.util.UtilLog.*;

public class Panel02Logotype extends JetPanel {
    
    private static final int panelWidth  = 400;
    private static final int panelHeight = 100;
    private static final int panelLeft   = 0;
    private static final int panelTop    = 30;

    public Panel02Logotype(JetFrame parent) {
        super(parent);
    }

    @Override
    protected void init() {
        setLocation(panelLeft,panelTop);
        setSize(panelWidth,panelHeight);
        setOpaque(false);
        try {
            bgImage = Toolkit.getDefaultToolkit().createImage(
                    new FilteredImageSource(
                            ImageIO.read(
                                    Main.class.getResource("theme/panel02logotype.bg.png")
                            ).getSource(),
                            new UtilSwing.TransparentFilter()
                    )
            );
        } catch(Exception ex) {
            setBackground(UtilSwing.colorTextDark);
            error("Can`t load background, using black");
            error(ex);
        }
        
        ImageButton nameBtn = new ImageButton("theme/panel02logotype.logo.png");
        nameBtn.setLocation((panelWidth-nameBtn.getWidth())/2,(panelHeight-nameBtn.getHeight())/2);
        nameBtn.setFocusPainted(false);
        nameBtn.addActionListener(new UtilSwing.ActionOpenLink("http://lz-craft.ru/")); // @TODO project site url
        add(nameBtn);
    }

}
