package render.processingGUI;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac2024-island-demo
 * @date 2024/11/8
 * @time 8:38
 */
public class GUIManager {
    private List<Controller> controllers;

    /* ------------- constructor ------------- */

    public GUIManager() {
        this.controllers = new ArrayList<>();
    }

    /* ------------- member function: add controller ------------- */

    public Button addButton(String name) {
        Button button = new Button(name);
        this.controllers.add(button);
        return button;
    }

    public Slider addSlider(String name) {
        Slider slider = new Slider(name);
        this.controllers.add(slider);
        return slider;
    }

    public ImageButton addImageButton(String name) {
        ImageButton imageButton = new ImageButton(name);
        this.controllers.add(imageButton);
        return imageButton;
    }

    public BarChart addBarChart(String name) {
        BarChart barChart = new BarChart(name);
        this.controllers.add(barChart);
        return barChart;
    }

    /* ------------- member function: event listener ------------- */

    public void listenMouseClicked(float mouseX, float mouseY) {
        for (Controller controller : controllers) {
            controller.enableMouseClickEvent(mouseX, mouseY);
        }
    }

    public void listenMouseDragged(float mouseX, float mouseY) {
        for (Controller controller : controllers) {
            if (controller.guiType.equals("Slider")) {
                controller.enableMouseDragEvent(mouseX, mouseY);
            }
        }
    }

    public void listenMousePressed(float mouseX, float mouseY) {
        for (Controller controller : controllers) {
            if (controller.guiType.equals("Slider")) {
                controller.enableMousePressEvent(mouseX, mouseY);
            }
        }
    }

    public void listenMouseReleased(float mouseX, float mouseY) {
        for (Controller controller : controllers) {
            if (controller.guiType.equals("Slider")) {
                controller.enableMouseReleaseEvent(mouseX, mouseY);
            }
        }
    }

    /* ------------- setter & getter ------------- */

    public List<Controller> getControllers() {
        return controllers;
    }

    public Controller getControllerByName(String name) {
        for (Controller controller : controllers) {
            if (controller.getName().equals(name)) {
                return controller;
            }
        }
        return null;
    }

    /* ------------- draw ------------- */

    public void draw(PApplet app) {
        for (Controller controller : controllers) {
            controller.draw(app);
        }
    }
}
