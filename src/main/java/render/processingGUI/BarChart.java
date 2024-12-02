package render.processingGUI;

import processing.core.PApplet;

import java.util.Arrays;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac2024-island-demo
 * @date 2024/11/10
 * @time 17:20
 */
public class BarChart extends Controller {
    private String[] dataLabel;
    private double[] dataValue;

    private String originLabel = "";
    private String xAxisLabel = "";
    private String yAxisLabel = "";

    private double min;
    private double max;

    private float spanPerData;
    private float paddingPerData;
    private float widthPerData;
    private float[] heightData;

    private boolean showInteger;

    /* ------------- constructor ------------- */

    public BarChart(String name) {
        super(name);
        super.guiType = "BarChart";
    }

    /* ------------- member function ------------- */

    @Override
    public void enableMouseClickEvent(float mouseX, float mouseY) {

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

    public BarChart setChartRange(double min, double max) {
        this.min = min;
        this.max = max;
        return this;
    }

    public BarChart setChartData(String[] dataLabel, double[] dataValue) {
        this.dataLabel = dataLabel;
        this.dataValue = dataValue;

        this.spanPerData = (float) size[0] / dataLabel.length;
        this.widthPerData = spanPerData * 0.6f;
        this.paddingPerData = (spanPerData - widthPerData) * .5f;
        this.heightData = new float[dataLabel.length];
        for (int i = 0; i < dataLabel.length; i++) {
            // in case label and data are not even
            if (dataValue.length > i) {
                heightData[i] = size[1] * (float) (dataValue[i] / (max - min));
            } else {
                heightData[i] = 0;
            }
        }

        System.out.println(spanPerData);

        System.out.println(Arrays.toString(heightData));

        return this;
    }

    public BarChart showInteger(boolean bool) {
        this.showInteger = bool;
        return this;
    }

    @Override
    public BarChart setPosition(int x, int y) {
        super.position = new int[]{x, y};
        return this;
    }

    @Override
    public BarChart setSize(int width, int height) {
        super.size = new int[]{width, height};
        return this;
    }

    public BarChart setOriginLabel(String originLabel) {
        this.originLabel = originLabel;
        return this;
    }

    public BarChart setXAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
        return this;
    }

    public BarChart setYAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
        return this;
    }

    /* ------------- draw ------------- */

    @Override
    public void draw(PApplet app) {
        app.pushStyle();

        // chart background
        app.noStroke();
        app.fill(colorBackground);
        app.rect(position[0], position[1], size[0], size[1]);

        // data bar
        for (int i = 0; i < heightData.length; i++) {
            app.fill(colorForeground);
            app.rect(position[0] + i * spanPerData + paddingPerData, position[1] + size[1] - heightData[i], widthPerData, heightData[i]);
        }

        // data label
        if (font != null) {
            app.textFont(font);
        }
        app.textSize(fontSize);
        app.fill(colorLabel);
        for (int i = 0; i < dataLabel.length; i++) {
            app.textAlign(app.CENTER, app.TOP);
            app.text(dataLabel[i], position[0] + i * spanPerData + paddingPerData + widthPerData * .5f, position[1] + size[1] + fontSize * .3f);

            // data value
            app.textAlign(app.CENTER, app.BOTTOM);
            if (showInteger) {
                app.text((int) Math.round(dataValue[i]), position[0] + i * spanPerData + paddingPerData + widthPerData * .5f, position[1] + size[1] - heightData[i] - fontSize * .3f);
            } else {
                app.text(String.format("%.2f", dataValue[i]), position[0] + i * spanPerData + paddingPerData + widthPerData * .5f, position[1] + size[1] - heightData[i] - fontSize * .3f);
            }
        }

        // axis
        app.textAlign(app.RIGHT, app.TOP);
        app.text(originLabel, position[0] - fontSize * .3f, position[1] + size[1] + fontSize * .3f);
        app.text(yAxisLabel, position[0] - fontSize * .3f, position[1]);
        app.textAlign(app.LEFT, app.TOP);
        app.text(xAxisLabel, position[0] + size[0] + fontSize * .3f, position[1] + size[1] + fontSize * .3f);

        app.stroke(colorForeground);
        app.strokeWeight(2);
        app.line(position[0], position[1], position[0], position[1] + size[1]);
        app.line(position[0], position[1] + size[1], position[0] + size[0], position[1] + size[1]);

        app.popStyle();
    }
}
