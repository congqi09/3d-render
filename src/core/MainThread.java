package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;

import static java.lang.Math.min;

public class MainThread extends JFrame implements KeyListener {

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
    public static int avgSleepTime;
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

        LookupTables.init();
        Rasterizer.init();
        Camera.init(0, 0, 0);
        addKeyListener(this);

        // test a simple triangle
        Vector3D[] myTriangle = new Vector3D[3];
        // using the clockwise direction
        myTriangle[0] = new Vector3D(0, 1, 2);
        myTriangle[1] = new Vector3D(1, -1, 2);
        myTriangle[2] = new Vector3D(-1, -1, 2);

        while (true) {
            Camera.update();

            screen[0] = (163 << 16) | (216 << 8) | 239; // skyblue
            for (int i = 1; i < screen.length; i<<=1) {
                System.arraycopy(screen, 0, screen, i, min(i, screenSize - i));
            }

            Rasterizer.triangleVertices = myTriangle;
            Rasterizer.triangleColor = (255 << 16) | (128 << 8) | 0; // orange
            Rasterizer.renderType = 0;
            Rasterizer.rasterize();

            frameIndex++;

            if (frameIndex % 30 == 0) {
                thisTime = System.currentTimeMillis();
                framePerSecond = (int)(1000/((thisTime-lastTime)/30));
                avgSleepTime = sleepTime/30;
                lastTime = thisTime;
                sleepTime = 0;
            }
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
            g2.drawString("FPS: " + framePerSecond + "   " + "Thread Sleep: " + avgSleepTime + "ms   ", 5, 15);

            panel.getGraphics().drawImage(screenBuffer, 0, 0, this);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W')
            Camera.MOVE_FORWARD = true;
        else if (e.getKeyChar() == 's' || e.getKeyChar() == 'S')
            Camera.MOVE_BACKWARD = true;
        else if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A')
            Camera.SLIDE_LEFT = true;
        else if (e.getKeyChar() == 'd' || e.getKeyChar() == 'D')
            Camera.SLIDE_RIGHT = true;

        if (e.getKeyCode() == KeyEvent.VK_UP)
            Camera.LOOK_UP = true;
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
            Camera.LOOK_DOWN = true;
        else if (e.getKeyCode() == KeyEvent.VK_LEFT)
            Camera.LOOK_LEFT = true;
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            Camera.LOOK_RIGHT = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W')
            Camera.MOVE_FORWARD = false;
        else if (e.getKeyChar() == 's' || e.getKeyChar() == 'S')
            Camera.MOVE_BACKWARD = false;
        else if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A')
            Camera.SLIDE_LEFT = false;
        else if (e.getKeyChar() == 'd' || e.getKeyChar() == 'D')
            Camera.SLIDE_RIGHT = false;

        if (e.getKeyCode() == KeyEvent.VK_UP)
            Camera.LOOK_UP = false;
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
            Camera.LOOK_DOWN = false;
        else if (e.getKeyCode() == KeyEvent.VK_LEFT)
            Camera.LOOK_LEFT = false;
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            Camera.LOOK_RIGHT = false;
    }
}
