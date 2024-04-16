package core;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;

import static java.lang.Math.min;

public class MainThread extends JFrame {

    public static int screen_w = 1024;
    public static int screen_h = 682;
    public static int half_screen_w = screen_w / 2;
    public static int half_screen_h = screen_h / 2;
    public static int screenSize = screen_w * screen_h;

    public static JPanel panel;
    public static int[] screen;
    public static BufferedImage screenBuffer;

    public static int frameIndex;
    public static int frameInterval = 33; //ms
    public static int sleepTime;
    public static int framePerSecond;
    public static long lastDraw;
    public static double thisTime, lastTime;

    public static void main(String[] args) {
        new MainThread();
    }

    public MainThread() {

        setTitle("Java Soft Renderer");
        panel = (JPanel) this.getContentPane();
        panel.setPreferredSize(new Dimension(screen_w, screen_h));
        panel.setMinimumSize(new Dimension(screen_w, screen_h));
        panel.setLayout(null);

        setResizable(false);
        pack();
        setVisible(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        screenBuffer = new BufferedImage(screen_w, screen_h, BufferedImage.TYPE_INT_RGB);
        DataBuffer dest = screenBuffer.getRaster().getDataBuffer();
        screen = ((DataBufferInt)dest).getData();

        while (true) {

            screen[0] = (163 << 16) | (216 << 8) | 239; // skyblue
            for (int i = 1; i < screen.length; i<<=1) {
                System.arraycopy(screen, 0, screen, i, min(i, screenSize - i));
            }

            frameIndex++;

            if (frameIndex % 30 == 0) {
                thisTime = System.currentTimeMillis();
                framePerSecond = (int)(1000/((thisTime-lastTime)/30));
                lastTime = thisTime;
            }
            sleepTime = 0;
            while (System.currentTimeMillis() - lastDraw < frameInterval) {
                try {
                    Thread.sleep(1);
                    sleepTime++;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            lastDraw = System.currentTimeMillis();

            // draw the analytics numbers
            Graphics2D g2 = (Graphics2D) screenBuffer.getGraphics();
            g2.setColor(Color.BLACK);
            g2.drawString("FPS: " + framePerSecond + "   " + "Thread Sleep: " + sleepTime + "ms   ", 5, 15);

            panel.getGraphics().drawImage(screenBuffer, 0, 0, this);
        }
    }
}
