package render.processingGUI;

import processing.core.PApplet;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac2024-island-demo
 * @date 2024/11/8
 * @time 8:40
 */
public class Button extends Controller {
    private float duration = 0.5f;                // 动画总时长0.5秒
    private  float transitionTime = 0;            // 当前过渡时间
    private boolean isClicked = false;           // 是否被点击的标志

    private FunctionInterfaceButton myFunction;

    /* ------------- constructor ------------- */

    public Button(String name) {
        super(name);
        super.guiType = "Button";
    }

    /* ------------- member function ------------- */

    @Override
    public void enableMouseClickEvent(float mouseX, float mouseY) {
        // check boundary
        if (mouseX >= position[0] && mouseX <= position[0] + size[0] && mouseY >= position[1] && mouseY <= position[1] + size[1]) {
            isClicked = true;     // 开始动画
            transitionTime = 0;   // 重置过渡时间
            myFunction.execute();
        }
    }

    @Override
    public void enableMouseDragEvent(float mouseX, float mouseY) {

    }

    @Override
    public void enableMousePressEvent(float mouseX, float mouseY) {

    }

    @Override
    public void enableMouseReleaseEvent(float mouseX, float mouseY) {

    }

    /* ------------- setter & getter ------------- */

    public Button setFunction(FunctionInterfaceButton myFunction) {
        this.myFunction = myFunction;
        return this;
    }

    /* ------------- draw ------------- */

    @Override
    public void draw(PApplet app) {
        app.pushStyle();

        if (isClicked) {
            transitionTime += (float) (1.0 / (duration * app.frameRate)); // 计算过渡的比例
            if (transitionTime > 1) {
                transitionTime = 0;  // 重置过渡时间
                isClicked = false;   // 结束动画
            }
        }
        // 根据过渡时间计算当前颜色
        int currentColor;
        if (isClicked) {
            if (transitionTime <= 0.5) {
                // 第一段，红到蓝
                currentColor = app.lerpColor(colorBackground, colorActive, transitionTime * 2);
            } else {
                // 第二段，蓝到红
                currentColor = app.lerpColor(colorActive, colorBackground, (float) ((transitionTime - 0.5) * 2));
            }
        } else {
            currentColor = colorBackground; // 默认显示红色
        }

        // button rectangle
        app.noStroke();
        app.fill(currentColor);
        app.rect(position[0], position[1], size[0], size[1]);

        // label
        app.fill(colorLabel);
        app.textAlign(app.CENTER, app.CENTER);
        if (font != null) {
            app.textFont(font);
        }
        app.textSize(fontSize);
        app.text(label, position[0] + size[0] * .5f, position[1] + size[1] * .5f);

        app.popStyle();
    }
}
