package core;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;

public class MainThread extends JFrame {

    public static int screen_w = 1024;
    public static int screen_h = 682;
    public static int half_screen_w = screen_w / 2;
    public static int half_screen_h = screen_h / 2;

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

            int r_skyblue = 163, g_skyblue = 216, b_skyblue = 239;
            int r_orange = 255, g_orange = 128, b_orange = 0;

            // rolling skyblue to orange
            for (int i = 0; i < screen_w; i++) {
                int p = (i+frameIndex*8) % screen_w;
                float t1 = Math.abs((float) (half_screen_w - p)/half_screen_w);
                float t2 = 1f - t1;
                int r = (int)(r_orange*t1 + r_skyblue*t2);
                int g = (int)(g_orange*t1 + g_skyblue*t2);
                int b = (int)(b_orange*t1 + b_skyblue*t2);
                for (int j = 0; j < screen_h; j++) {
                    screen[i+j*screen_w] = (r << 16) | (g << 8) | b;
                }
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
