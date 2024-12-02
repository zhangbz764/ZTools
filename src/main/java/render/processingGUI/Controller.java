package render.processingGUI;

import processing.core.PApplet;
import processing.core.PFont;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac2024-island-demo
 * @date 2024/11/8
 * @time 8:39
 */
public abstract class Controller {
    protected String guiType = "";

    protected int[] position = new int[]{0, 0};
    protected int[] size = new int[]{0, 0};
    private String name;
    protected String label;

    protected PFont font;
    protected float fontSize = 12;

    protected int colorBackground;
    protected int colorForeground;
    protected int colorActive;
    protected int colorLabel;

    /* ------------- constructor ------------- */

    public Controller(String name) {
        this.name = name;
    }

    /* ------------- member function ------------- */

    public abstract void enableMouseClickEvent(float mouseX, float mouseY);

    public abstract void enableMouseDragEvent(float mouseX, float mouseY);

    public abstract void enableMousePressEvent(float mouseX, float mouseY);

    public abstract void enableMouseReleaseEvent(float mouseX, float mouseY);

    /* ------------- setter & getter ------------- */

    public Controller setPosition(int x, int y) {
        this.position = new int[]{x, y};
        return this;
    }

    public Controller setSize(int width, int height) {
        this.size = new int[]{width, height};
        return this;
    }

    public Controller setLabel(String label) {
        this.label = label;
        return this;
    }

    public Controller setFont(PFont font, float size) {
        this.font = font;
        this.fontSize = size;
        return this;
    }

    public Controller setColorBackground(int color) {
        this.colorBackground = color;
        return this;
    }

    public Controller setColorForeground(int color) {
        this.colorForeground = color;
        return this;
    }

    public Controller setColorActive(int color) {
        this.colorActive = color;
        return this;
    }

    public Controller setColorLabel(int color) {
        this.colorLabel = color;
        return this;
    }

    public String getName() {
        return name;
    }

    /* ------------- draw ------------- */

    public abstract void draw(PApplet app);

}
