package observers.vis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class GraphicsUtil {
    public static void drawString(Graphics g, String str, double x, double y, Font font, Color c) {
        g.setColor(c);
        g.setFont(font);
        g.drawString(str, (int)x, (int)y);
    }

    public static void drawRect(Graphics g, double x, double y, double w, double h, Color c) {
        g.setColor(c);
        g.drawRect((int)x, (int)y, (int)w, (int)h);
    }

    public static void fillRect(Graphics g, double x, double y, double w, double h, Color c) {
        g.setColor(c);
        g.fillRect((int)x, (int)y, (int)w, (int)h);
    }
}
