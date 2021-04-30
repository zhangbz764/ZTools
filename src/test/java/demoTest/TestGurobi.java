package demoTest;

import gurobi.*;
import math.ZMath;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * test gurobi
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/13
 * @time 17:05
 */
public class TestGurobi extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

//    PeasyCam cam;

    private int cellWidth;
    private int cellHeight;

    private List<Integer[]> optimizeResult;
    private List<Integer[]> colors;

    public void setup() {
//        cam = new PeasyCam(this, 300);

        cellHeight = (int) (this.height * 0.01);
        cellWidth = (int) (this.width * 0.01);

        optimizeResult = new ArrayList<>();

        buildOptimizer();

        colors = new ArrayList<>();
        for (int i = 0; i < optimizeResult.size(); i++) {
            colors.add(new Integer[]{ZMath.randomInt(0, 255), ZMath.randomInt(0, 255), ZMath.randomInt(0, 255)});
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        // draw grid
        strokeWeight(5);
        for (int i = 0; i < cellHeight; i++) {
            for (int j = 0; j < cellWidth; j++) {
                line(i * 100, j * 100, i * 100, (j + 1) * 100);
                line(i * 100, j * 100, (i + 1) * 100, j * 100);
                line((i + 1) * 100, (j + 1) * 100, (i + 1) * 100, j * 100);
                line((i + 1) * 100, (j + 1) * 100, i * 100, (j + 1) * 100);
            }
        }

        // draw result
        strokeWeight(1);
        for (int i = 0; i < optimizeResult.size(); i++) {
            fill(colors.get(i)[0], colors.get(i)[1], colors.get(i)[2]);
            rect(optimizeResult.get(i)[0] * 100, optimizeResult.get(i)[1] * 100, 100, 100);
            rect((optimizeResult.get(i)[0] + 1) * 100, (optimizeResult.get(i)[1] + 1) * 100, 100, 100);
            rect(optimizeResult.get(i)[0] * 100, (optimizeResult.get(i)[1] + 1) * 100, 100, 100);
            rect((optimizeResult.get(i)[0] - 1) * 100, (optimizeResult.get(i)[1] + 1) * 100, 100, 100);
        }
        fill(0);
        textSize(20);
        translate(0, 0, 5);
        for (int i = 0; i < optimizeResult.size(); i++) {
            text(1, optimizeResult.get(i)[0] * 100 + 50, optimizeResult.get(i)[1] * 100 + 50);
        }
    }

    public void buildOptimizer() {
        System.out.println("********* optimizing *********" + "\n");
        try {
            // Create empty environment, set options, and start
            GRBEnv env = new GRBEnv(true);
            env.set("logFile", "mip1.log");
            env.start();

            // Create empty model
            GRBModel model = new GRBModel(env);

            // Create variables
            GRBVar[][] vars = new GRBVar[cellWidth][cellHeight];
            for (int i = 0; i < vars.length; i++) {
                for (int j = 0; j < vars[i].length; j++) {
                    vars[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "var" + i + j);
                }
            }

            // Set objective
            GRBLinExpr expr = new GRBLinExpr();
            for (int i = 0; i < vars.length; i++) {
                for (int j = 0; j < vars[i].length; j++) {
                    expr.addTerm(1.0, vars[i][j]);
                }
            }
            model.setObjective(expr, GRB.MAXIMIZE);

            // Add constraint
            int count = 0;
            for (int i = 1; i < vars.length - 1; i++) {
                for (int j = 1; j < vars[i].length - 1; j++) {

                    expr = new GRBLinExpr();
                    expr.addTerm(1.0, vars[i][j]);
                    expr.addTerm(1.0, vars[i][j + 1]);
                    expr.addTerm(1.0, vars[i + 1][j + 1]);
                    expr.addTerm(1.0, vars[i - 1][j + 1]);
                    model.addConstr(expr, GRB.LESS_EQUAL, 1, "cons" + i + j);
                    count++;
                }
            }
            System.out.println(count);

            // Optimize model
            model.optimize();

            System.out.println("\n" + "******* result *******");
            for (int i = 0; i < vars.length; i++) {
                for (int j = 0; j < vars[i].length; j++) {
                    if (vars[i][j].get(GRB.DoubleAttr.X) == 1) {
                        System.out.println("base: "
                                + vars[i][j].get(GRB.StringAttr.VarName)
                                + " [" + i + "][" + j + "]"
                        );
                        optimizeResult.add(new Integer[]{i, j});
                    }
                }
            }
            System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

            // Dispose of model and environment
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.out.println(
                    "Error code: "
                            + e.getErrorCode()
                            + ". "
                            + e.getMessage()
            );
        }
    }

    /* ------------- templateT ------------- */

//    public class TemplateT {
//        int[] color;
//        int[] base;
//
//        public TemplateT() {
//            color = new int[]{ZMath.randomInt(0, 255), ZMath.randomInt(0, 255), ZMath.randomInt(0, 255)};
//        }
//
//        public void addTerm_obj(GRBLinExpr expr, GRBVar x) {
//            expr.addTerm(1.0, x);
//        }
//
//        public void addTerm_cons(GRBLinExpr expr, int i, int j, GRBVar[][] vars, GRBModel model, int xSize, int ySize) {
//            base = new int[]{i, j};
//            expr = new GRBLinExpr();
//            expr.addTerm(1.0, vars[i][j]);
//            expr.addTerm(1.0, vars[i + 1][j]);
//            expr.addTerm(1.0, vars[i + 1][j - 1]);
//            expr.addTerm(1.0, vars[i + 1][j + 1]);
//            model.addConstr(expr, GRB.LESS_EQUAL, 1, "cons");
//        }
//
//        public void draw(PApplet app, float x, float y, float w, float h) {
//            app.pushStyle();
//            app.fill(color[0], color[1], color[2]);
//            app.rect(base[0] * 100, base[1] * 100, 100, 100);
//            app.rect((base[0] + 1) * 100, base[1] * 100, 100, 100);
//            app.rect((base[0] + 1) * 100, (base[1] - 1) * 100, 100, 100);
//            app.rect((base[0] + 1) * 100, (base[1] + 1) * 100, 100, 100);
//            app.popStyle();
//        }
//    }
}