package testUtils;

import gurobi.*;
import processing.core.PApplet;
import processing.core.PVector;

public class Multi extends PApplet{
    public static void main(String[] args) {
        PApplet.main("testUtils.Multi");
    }
    int num=500;
    PVector[] ps=new PVector[num];
    double[][]min;

    @Override
    public void settings() {
        size(800, 800);
    }

    public void setup(){
        for(int i=0;i<num;i++){
            ps[i]=new PVector(random(100,700),random(100,700));
        }
//        try {
//            min=optiMulti();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        for(int i=0;i<min.length;i++){
//            println(min[i][0],min[i][1],min[i][2]);
//        }
    }

    public void draw(){
        background(255);

        noFill();
        stroke(0,0,255);
//        for(int i=0;i<min.length;i++){
//            ellipse((float) min[i][0],(float)min[i][1],2*(float)min[i][2],2*(float)min[i][2]);
//
//            pushStyle();
//            fill(255,0,0);
//            ellipse((float) min[i][0],(float)min[i][1],8,8);
//            popStyle();
//        }

        pushStyle();
        for(int i=0;i<num;i++){
            fill(0);
            ellipse(ps[i].x,ps[i].y,8,8);
        }
        popStyle();
    }

    private double[][] optiMulti() throws GRBException {

        GRBEnv env=new GRBEnv();
        env.set("logFile","Main.log");
        GRBModel model=new GRBModel(env);
        model.set(GRB.IntParam.NonConvex,2);
        model.set(GRB.DoubleParam.TimeLimit,60);
        int number=4;
        GRBVar[] x=new GRBVar[number];
        GRBVar[] y=new GRBVar[number];

        GRBVar mxR=model.addVar(0,width,0,GRB.CONTINUOUS,"mxR");

        GRBVar[] r=new GRBVar[number];
        GRBVar[][] OR = new GRBVar[num][number];//或约束

        int maxR=400;
        for(int j=0;j< number;j++){
            x[j]=model.addVar(0,width,0,GRB.CONTINUOUS,"x");
            y[j]=model.addVar(0,height,0,GRB.CONTINUOUS,"y");
            r[j]=model.addVar(0,maxR,0,GRB.CONTINUOUS,"r");

        }

        //或约束
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < number; j++) {
                OR[i][j] = model.addVar(0,1,0,GRB.BINARY,"or");
            }
        }
        model.set(GRB.IntAttr.ModelSense,GRB.MINIMIZE);
        //Object
        GRBLinExpr obj1=new GRBLinExpr();
        obj1.addTerm(1,mxR);
        model.setObjectiveN(obj1,0,10,1,100,0.01,"MaxRMin");

        GRBLinExpr obj2=new GRBLinExpr();
        for(int j=0;j<number;j++){
            obj2.addTerm(1,r[j]);
        }
        model.setObjectiveN(obj2,1,9,1,100,0.01,"AllMin");

        //Constraints
        int M=maxR*maxR;
        model.addGenConstrMax(mxR,r,0,"maxRconstraint");
        for(int i=0;i<num;i++){
            GRBLinExpr expr2=new GRBLinExpr();
            GRBQuadExpr expr1;
            for(int j=0;j< number;j++){
                expr1=new GRBQuadExpr();
                expr2.addTerm(1,OR[i][j]);//r

                expr1.addTerm(1,x[j],x[j]);
                expr1.addTerm(-2*ps[i].x,x[j]);
                expr1.addConstant(ps[i].x*ps[i].x);

                expr1.addTerm(1,y[j],y[j]);
                expr1.addTerm(-2*ps[i].y,y[j]);
                expr1.addConstant(ps[i].y*ps[i].y);

                expr1.addTerm(-1,r[j],r[j]);
                expr1.addConstant(-M);

                expr1.addTerm(M,OR[i][j]);//r
                model.addQConstr(expr1,GRB.LESS_EQUAL,0,"c"+String.valueOf(i));
            }
            model.addConstr(expr2,GRB.GREATER_EQUAL,1,"exist");
        }

        model.optimize();

        double[][]vars_temp=new double[number][3];
        for(int i=0;i<number;i++){
            vars_temp[i][0]=x[i].get(GRB.DoubleAttr.X);
            vars_temp[i][1]=y[i].get(GRB.DoubleAttr.X);
            vars_temp[i][2]=r[i].get(GRB.DoubleAttr.X);
        }

        return  vars_temp;
    }


}
