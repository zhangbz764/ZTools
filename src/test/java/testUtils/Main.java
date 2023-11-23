package testUtils;

import gurobi.*;
import processing.core.PApplet;
import processing.core.PVector;

public class Main extends PApplet {
    public static void main(String[] args) {
        PApplet.main("testUtils.Main");
    }

    int num = 10;
    PVector[] ps = new PVector[num];
    int[] rs = new int[num];
    double[][] vs;
    double[] min;
    double[] areaSum = new double[5];
    int drawId = 0;

    @Override
    public void settings() {
        size(800, 800);
    }

    public void setup() {

        ps[0] = new PVector(400, 240);
        rs[0] = 40;
        ps[1] = new PVector(159, 258);
        rs[1] = 60;
        ps[2] = new PVector(320, 360);
        rs[2] = 50;
        ps[3] = new PVector(460, 400);
        rs[3] = 50;
        ps[4] = new PVector(550, 450);
        rs[4] = 40;
        ps[5] = new PVector(650, 600);
        rs[5] = 30;
        ps[6] = new PVector(700, 500);
        rs[6] = 50;
        ps[7] = new PVector(720, 300);
        rs[7] = 40;
        ps[8] = new PVector(520, 600);
        rs[8] = 60;
        ps[9] = new PVector(320, 600);
        rs[9] = 20;
//        try {
//            min = optiMinimum();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        for(int i=0;i<num;i++){
//            ps[i]=new PVector(random(200,700),random(200,700));
//            rs[i]=random(0,1)>0.5?20:40;
//        }
    }

    public void draw() {
        background(255);
        pushStyle();
        fill(0);
//        ellipse((float) min[0], (float) min[1], 2 * (float) min[2], 2 * (float) min[2]);
        popStyle();
        for (int i = 0; i < num; i++) {
//            println(ps[i],rs[i]);
//            ellipse((float) vs[i][0],(float)vs[i][1],2*(float)vs[i][2],2*(float)vs[i][2]);
//            ellipse(ps[i].x,ps[i].y,rs[i]*2,rs[i]*2);
            fill(255);
            ellipse(ps[i].x, ps[i].y, 10, 10);
        }

    }

    private double[] optiMinimum() throws GRBException {
        double[] vars_temp = new double[3];
        GRBEnv env = new GRBEnv();
        env.set("logFile", "Main.log");
        GRBModel model = new GRBModel(env);
        model.set(GRB.IntParam.NonConvex, 2);

        GRBVar x = model.addVar(0, width, 0, GRB.CONTINUOUS, "x");
        GRBVar y = model.addVar(0, height, 0, GRB.CONTINUOUS, "y");
        GRBVar r = model.addVar(0, width / 2, 0, GRB.CONTINUOUS, "r");

        GRBLinExpr obj = new GRBLinExpr();
        obj.addTerm(1, r);
        model.setObjective(obj, GRB.MINIMIZE);


        for (int i = 0; i < 10; i++) {
            GRBQuadExpr expr1 = new GRBQuadExpr();
            expr1.addTerm(1, r, r);
            //这两个是覆盖给定随机圆的
//            expr1.addTerm(-2*rs[i],r);
//            expr1.addConstant(rs[i]*rs[i]);

            expr1.addTerm(-1, x, x);
            expr1.addTerm(2 * ps[i].x, x);
            expr1.addConstant(-ps[i].x * ps[i].x);

            expr1.addTerm(-1, y, y);
            expr1.addTerm(2 * ps[i].y, y);
            expr1.addConstant(-ps[i].y * ps[i].y);

            model.addQConstr(expr1, GRB.GREATER_EQUAL, 0, "c" + String.valueOf(i));
        }

        model.optimize();
        vars_temp[0] = x.get(GRB.DoubleAttr.X);
        vars_temp[1] = y.get(GRB.DoubleAttr.X);
        vars_temp[2] = r.get(GRB.DoubleAttr.X);
        return vars_temp;
    }
}
