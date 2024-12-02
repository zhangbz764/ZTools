package render.processingGUI;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac2024-island-demo
 * @date 2024/11/8
 * @time 8:40
 */
public class ImageButton extends Controller {
    private float fadeInDuration = 0.1f;           // 淡入时长 0.3 秒
    private float fadeOutDuration = 0.3f;          // 淡出时长 0.7 秒
    private float transitionTime = 0;            // 当前过渡时间
    private boolean isClicked = false;           // 是否被点击的标志
    private boolean isAnimating = false;         // 是否正在进行动画
    private boolean fadingIn = true;

    private FunctionInterfaceButton myFunction;

    private PImage[] image;

    /* ------------- constructor ------------- */

    public ImageButton(String name) {
        super(name);
        super.guiType = "Button";
    }

    /* ------------- member function ------------- */

    @Override
    public void enableMouseClickEvent(float mouseX, float mouseY) {
        // check boundary
        if (mouseX >= position[0] && mouseX <= position[0] + size[0] && mouseY >= position[1] && mouseY <= position[1] + size[1]) {
            if (!isAnimating) {
                isAnimating = true;    // 开始动画
                fadingIn = true;       // 从淡入开始
                transitionTime = 0;    // 重置过渡时间
            }
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

    public ImageButton setFunction(FunctionInterfaceButton myFunction) {
        this.myFunction = myFunction;
        return this;
    }

    public ImageButton setImage(PImage image) {
        this.image = new PImage[]{image};
        return this;
    }

    public ImageButton setImages(PImage... image) {
        this.image = image;
        return this;
    }

    @Override
    public ImageButton setSize(int width, int height) {
        super.size = new int[]{width, height};
        return this;
    }

    /* ------------- draw ------------- */

    @Override
    public void draw(PApplet app) {
        app.pushStyle();

        // image
        app.image(image[0], position[0], position[1], size[0], size[1]);


        // 如果动画正在进行
        if (isAnimating) {
            // 计算当前的透明度（alpha）
            float alpha;
            if (fadingIn) {
                // 淡入阶段，从透明到不透明
                alpha = PApplet.map(transitionTime, 0, 1, 0, 255);
                transitionTime += (float) (1.0 / (fadeInDuration * app.frameRate)); // 按淡入时长更新过渡时间
            } else {
                // 淡出阶段，从不透明到透明
                alpha = PApplet.map(transitionTime, 0, 1, 255, 0);
                transitionTime += (float) (1.0 / (fadeOutDuration * app.frameRate)); // 按淡出时长更新过渡时间
            }

            // 绘制带透明度的矩形
            // button rectangle
            app.noStroke();
            app.fill(colorActive, alpha);
            app.rect(position[0], position[1], size[0], size[1]);

            // 如果过渡完成，进入下一个阶段
            if (transitionTime >= 1) {
                transitionTime = 0;  // 重置过渡时间

                if (fadingIn) {
                    // 淡入完成，开始淡出
                    fadingIn = false;
                } else {
                    // 淡出完成，停止动画
                    isAnimating = false;
                }
            }
        }


//        // label
//        app.fill(colorLabel);
//        app.textAlign(app.CENTER, app.CENTER);
//        if (font != null) {
//            app.textFont(font);
//        }
//        app.textSize(fontSize);
//        app.text(label, position[0] + size[0] * .5f, position[1] + size[1] * .5f);

        app.popStyle();
    }
}
