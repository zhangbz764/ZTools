package testUtils;

import guo_cam.CameraController;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import render.processingGUI.BarChart;
import render.processingGUI.GUIManager;
import render.processingGUI.ImageButton;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2024/12/2
 * @time 16:28
 */
public class Test22ProcessingGUI extends PApplet {
    public static void main(String[] args) {
        PApplet.main(Test22ProcessingGUI.class.getName());
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private CameraController gcam;

    private GUIManager guiManager;

    private PImage[] pImages;
    private int imgCount = 0;

    public void setup() {
        surface.setLocation(500, 500);

        this.gcam = new CameraController(this);
        gcam.setPanButton(CameraController.MOUSE_RIGHTBUTTON);

        this.guiManager = new GUIManager();
        this.pImages = new PImage[3];
        pImages[0] = loadImage("src/test/resources/exampleImgs/Test19Skeleton3D.jpg");
        pImages[1] = loadImage("src/test/resources/exampleImgs/Test20CircleCoverLazy.jpg");
        pImages[2] = loadImage("src/test/resources/exampleImgs/Test17JtsOffsetLS.jpg");

        PFont font = createFont("src/test/resources/simhei.ttf", 32);

        guiManager.addButton("button")
                .setFunction(() -> function1("按钮1"))

                .setPosition(100, 100)
                .setSize(300, 60)

                .setFont(font, 25)
                .setLabel("按钮1")

                .setColorBackground(0xd3404040)
                .setColorActive(0xd3757575)
                .setColorLabel(0xffffffff)
        ;
        guiManager.addButton("button2")
                .setFunction(() -> function1("按钮2"))

                .setPosition(100, 200)
                .setSize(300, 60)

                .setFont(font, 25)
                .setLabel("按钮2")

                .setColorBackground(0xd3404040)
                .setColorActive(0xd3757575)
                .setColorLabel(0xffffffff)
        ;
        guiManager.addButton("button3")
                .setFunction(() -> function1("按钮3"))

                .setPosition(100, 300)
                .setSize(300, 60)

                .setFont(font, 25)
                .setLabel("按钮3")

                .setColorBackground(0xd3404040)
                .setColorActive(0xd3757575)
                .setColorLabel(0xffffffff)
        ;

        guiManager.addSlider("slider1")
                .setRange(0, 500)
                .setValue(100)
                .setFunction((cal) -> function2(cal))

                .setPosition(100, 400)
                .setSize(300, 60)

                .setFont(font, 25)
                .setLabel("滑条3")

                .setColorBackground(0xd3404040)
                .setColorForeground(0xd3525252)
                .setColorActive(0xd38b8b8b)
                .setColorLabel(0xffffffff)
        ;

        guiManager.addImageButton("imageButton1")
                .setImage(this.pImages[imgCount])
                .setFunction(() -> function3())

                .setPosition(500, 100)
                .setSize(400, 150)

                .setColorActive(0xd38b8b8b)
        ;

        guiManager.addImageButton("imageButton2")
                .setImages(this.pImages)
                .setFunction(() -> function4())

                .setPosition(500, 400)
                .setSize(300, 200)

                .setColorActive(0xd38b8b8b)
        ;

        guiManager.addBarChart("land_use_type_ratio")
                .setPosition(100, 700)
                .setSize(600, 250)

                .setChartRange(0, 10)
                .setChartData(
                        new String[]{"Res", "Edu", "Com", "Ind"},
                        new double[]{8.5, 7.8, 3.2, 0.6}
                )
                .setOriginLabel("0")
                .setXAxisLabel("TYPE")
                .setYAxisLabel("RATIO")
                .showInteger(true)

                .setFont(font, 20)

                .setColorBackground(0xd3404040)
                .setColorForeground(0xd38b8b8b)
                .setColorLabel(0xffffffff)
        ;

    }

    /* ------------- draw ------------- */

    public void draw() {
        background(52, 83, 69);
        gcam.drawSystem(100);

        gcam.begin2d();
        guiManager.draw(this);

        gcam.begin3d();
        ellipse(0, 0, 100, 100);

    }

    @Override
    public void mouseClicked() {
        guiManager.listenMouseClicked(mouseX, mouseY);
    }

    @Override
    public void mouseDragged() {
        guiManager.listenMouseDragged(mouseX, mouseY);
    }

    @Override
    public void mousePressed() {
        guiManager.listenMousePressed(mouseX, mouseY);
    }

    @Override
    public void mouseReleased() {
        guiManager.listenMouseReleased(mouseX, mouseY);
    }

    @Override
    public void keyPressed() {
        if (key == '1') {
            BarChart landUseTypeRatio = (BarChart) guiManager.getControllerByName("land_use_type_ratio");
            landUseTypeRatio.setChartData(
                    new String[]{"Res", "Edu", "Com", "Ind"},
                    new double[]{Math.random(), Math.random(), Math.random(), Math.random()}
            );
        }
    }

    /* ------------- functions ------------- */

    public void function1(String str) {
        System.out.println(str);
    }

    public void function2(double val) {
        System.out.println("slider  " + val);
    }

    public void function3() {
        imgCount = (imgCount + 1) % pImages.length;
        ImageButton imageButton1 = (ImageButton) this.guiManager.getControllerByName("imageButton1");
        imageButton1.setImage(pImages[imgCount]);
    }

    public void function4() {
        imgCount = (imgCount + 1) % pImages.length;
        ImageButton imageButton2 = (ImageButton) this.guiManager.getControllerByName("imageButton2");
        imageButton2.setImage(pImages[imgCount]);
    }
}
