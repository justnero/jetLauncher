package ru.justnero.jetlauncher.swing;

import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import ru.justnero.jetlauncher.util.UtilSwing;

public class Panel04Settings extends JetPanel {
    
    private static final int panelWidth  = 400;
    private static final int panelHeight = 250;
    private static final int panelLeft   = 0;
    private static final int panelTop    = 180;
    
    private static final Memory[] memory = new Memory[] {
            new Memory(256,"256 МиБ"),
            new Memory(512,"512 МиБ"),
            new Memory(1024,"1024 МиБ"),
            new Memory(2048,"2048 МиБ"),
            new Memory(4096,"4096 МиБ"),
        };
    
    public Panel04Settings(JetFrame parent) {
        super(parent);
    }

    @Override
    protected void init() {
        setLocation(panelLeft,panelTop);
        setSize(panelWidth,panelHeight);
        
        JetLabel noticeL1 = new JetLabel("Указание объёма памяти, больше доступного,");
        noticeL1.setLocation((panelWidth-noticeL1.getWidth())/2,10);
        add(noticeL1);
        
        JetLabel noticeL2 = new JetLabel("приведёт к аварийному завершению работы игры");
        noticeL2.setLocation((panelWidth-noticeL2.getWidth())/2,24);
        add(noticeL2);
        
        final JetFormElement<JetComboBox<Memory>> memoryF = new JetFormElement<JetComboBox<Memory>>(new JetComboBox<Memory>(memory),null);
        memoryF.setLocation((panelWidth-memoryF.getWidth())/2,100);
        add(memoryF);
        
        memoryF.getHidden().setSelectedItem(getMemory(_parent.handler.getMemoryLimit()));
        memoryF.getHidden().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    Memory item = (Memory) e.getItem();
                    _parent.handler.setMemoryLimit(item.capacity);
                }
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
    
    private Memory getMemory(int capacity) {
        for(Memory element : memory) {
            if(element.capacity == capacity) {
                return element;
            }
        }
        return null;
    }
    
    public static class Memory {
        
        private final int capacity;
        private final String displayName;
        
        public Memory(int capacity, String displayName) {
            this.capacity = capacity;
            this.displayName = displayName;
        }
        
        @Override
        public final String toString() {
            return this.displayName;
        }
        
    }

}
