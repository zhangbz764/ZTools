package render.processingGUI;

import processing.core.PApplet;

/**
 * description
 *
 * @author zbz_lennovo
 * @project cdac2024-island-demo
 * @date 2024/11/8
 * @time 10:29
 */
public class Slider extends Controller {
    private double value;
    private double min;
    private double max;

    private double ratio;

    private FunctionInterfaceSlider myFunction;
    private int currentColor;

    private float duration = 0.35f;              // 每段动画的总时长 0.5 秒
    private float transitionTime = 0;            // 当前过渡时间
    private boolean isMousePressed = false;      // 鼠标按下的状态

    /* ------------- constructor ------------- */

    public Slider(String name) {
        super(name);
        super.guiType = "Slider";
        this.currentColor = colorForeground;
    }

    /* ------------- member function ------------- */

    @Override
    public void enableMouseClickEvent(float mouseX, float mouseY) {
        // check boundary
        if (mouseX >= position[0] && mouseX <= position[0] + size[0] && mouseY >= position[1] && mouseY <= position[1] + size[1]) {
            float delta = mouseX - position[0];
            this.ratio = delta / size[0];
            this.value = min + (max - min) * ratio;
            myFunction.execute(value);
        }
    }

    @Override
    public void enableMouseDragEvent(float mouseX, float mouseY) {
        // check boundary
        if (mouseX >= position[0] && mouseX <= position[0] + size[0] && mouseY >= position[1] && mouseY <= position[1] + size[1]) {
            //this.currentColor = colorActive;
            float delta = mouseX - position[0];
            this.ratio = delta / size[0];
            this.value = min + (max - min) * ratio;
            myFunction.execute(value);
        }
    }

    @Override
    public void enableMousePressEvent(float mouseX, float mouseY) {
        if (mouseX >= position[0] && mouseX <= position[0] + size[0] && mouseY >= position[1] && mouseY <= position[1] + size[1]) {
            isMousePressed = true;
        }
    }

    @Override
    public void enableMouseReleaseEvent(float mouseX, float mouseY) {
        //this.currentColor = colorForeground;
        isMousePressed = false;
    }

    /* ------------- setter & getter ------------- */

    public Slider setRange(double min, double max) {
        this.min = min;
        this.max = max;
        this.ratio = value / (max - min);
        return this;
    }

    public Slider setValue(double val) {
        this.value = val;
        this.ratio = value / (max - min);
        return this;
    }

    public Slider setFunction(FunctionInterfaceSlider myFunction) {
        this.myFunction = myFunction;
        return this;
    }

    @Override
    public Slider setSize(int width, int height) {
        super.size = new int[]{width, height};
        return this;
    }

    /* ------------- draw ------------- */

    @Override
    public void draw(PApplet app) {
        app.pushStyle();

        // slider background rectangle
        app.noStroke();
        app.fill(colorBackground);
        app.rect(position[0], position[1], size[0], size[1]);

        // 更新过渡时间
        if (isMousePressed) {
            // 鼠标按下，颜色从红渐变到蓝
            transitionTime += (float) (1.0 / (duration * app.frameRate));
            if (transitionTime > 1) {
                transitionTime = 1; // 达到完全蓝色
            }
        } else {
            // 鼠标松开，颜色从蓝渐变回红
            transitionTime -= (float) (1.0 / (duration * app.frameRate));
            if (transitionTime < 0) {
                transitionTime = 0; // 恢复到完全红色
            }
        }

        // 根据过渡时间计算当前颜色
        currentColor = app.lerpColor(colorForeground, colorActive, transitionTime);

        // slider value rectangle
        app.fill(currentColor);
        app.rect(position[0], position[1], size[0] * (float) ratio, size[1]);

        // label
        app.fill(colorLabel);
        app.textAlign(app.LEFT, app.BOTTOM);
        if (font != null) {
            app.textFont(font);
        }
        app.textSize(fontSize);
        app.text(label, position[0], position[1] - fontSize * .3f);

        // value
        app.textAlign(app.LEFT, app.CENTER);
        app.text(String.format("%.2f", value), position[0] + size[1] * .5f, position[1] + size[1] * .5f);

        app.popStyle();
    }
}
