package ru.justnero.jetlauncher.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RGBImageFilter;
import java.net.URL;

import static ru.justnero.jetlauncher.util.UtilLog.*;

public class UtilSwing {
    
    public static final Color colorTextHover           = Color.decode("0x0099FF"); // @TODO project color
    public static final Color colorGradientMainTop     = Color.decode("0x4387FD"); // @TODO project color
    public static final Color colorGradientMainBottom  = Color.decode("0x4683EA"); // @TODO project color
    
    public static final Color colorTextNormal          = Color.decode("0xFFFFFF");
    public static final Color colorTextLight           = Color.decode("0xFEFEFE");
    public static final Color colorTextDark            = Color.decode("0x000000");
    public static final Color colorTextForm            = Color.decode("0x888888");
    public static final Color colorShadow              = Color.decode("0xCECECE");
    public static final Color colorGradientLightTop    = Color.decode("0xEFEFEF");
    public static final Color colorGradientLightBottom = Color.decode("0xF1F1F1");
    public static final Color colorGradientDarkTop     = Color.decode("0xC2C2C2");
    public static final Color colorGradientDarkBottom  = Color.decode("0xC4C4C4");
    
    public static final Color colorTShadow              = new Color(206,206,206,128);
    public static final Color colorGradientLightTTop    = new Color(239,239,239,128);
    public static final Color colorGradientLightTBottom = new Color(241,241,241,128);
    
    
    public static Font fontDefaultPlain;
    public static Font fontDefaultBold;
    
    public static void initFonts(URL defaultPlain, URL defaultBold) {
        try {
            debug("Initializing default plain font ",defaultPlain.getPath());
            fontDefaultPlain = Font.createFont(Font.TRUETYPE_FONT,defaultPlain.openStream());
        } catch (Exception ex) {
            debug("Error, use Helvetica instead");
            debug(ex);
            fontDefaultPlain = new Font("Helvetica",Font.PLAIN,16);
        }
        try {
            debug("Initializing default bold font ",defaultPlain.getPath());
            fontDefaultBold = Font.createFont(Font.TRUETYPE_FONT,defaultBold.openStream());
        } catch (Exception ex) {
            debug("Error, use Helvetica instead");
            debug(ex);
            fontDefaultBold = new Font("Helvetica",Font.BOLD,16);
        }
    }
    
    public static Font getFont(int fontID,float size) {
        Font font = null;
        switch(fontID) {
            case 1:
                font = fontDefaultPlain;
                break;
            case 2:
                font = fontDefaultBold;
                break;
        }
        return getFont(font,size);
    }
    
    private static Font getFont(Font font,float size) {
        return font == null ? null : font.deriveFont(size);
    }
    
    public static class ActionOpenLink implements ActionListener {
        public String link;

        public ActionOpenLink(String url) {
            link = url;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                UtilCommon.openBrowser(link);
            } catch (Exception ex) {
                error("Error opening link: "+link);
                error(ex);
            }
        }
    }
    
    public static class TransparentFilter extends RGBImageFilter {
        @Override
        public final int filterRGB(int x, int y, int rgb) {
            if((rgb | 0xFF000000) == 0xFFFFFFFF) {
                return 0x00FFFFFF & rgb;
            } else {
                return rgb;
            }
        }
    }
    
}
