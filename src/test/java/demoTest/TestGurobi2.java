package demoTest;

import gurobi.*;
import processing.core.PApplet;

/**
 * test Gurobi 2
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/9
 * @time 2:22
 */
public class TestGurobi2 extends PApplet {

    @Override
    public void setup() {
        buildOptimizer();
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
            GRBVar[] vars = new GRBVar[5];
            for (int i = 0; i < vars.length; i++) {
                vars[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "var" + i);
            }

            // Set objective
            GRBLinExpr expr = new GRBLinExpr();
            for (int i = 0; i < vars.length; i++) {
                expr.addTerm(1.0, vars[i]);
            }
            model.setObjective(expr, GRB.MINIMIZE);


            // Add constraint
            expr = new GRBLinExpr();
            expr.addTerm(1.0, vars[0]);
            model.addConstr(expr, GRB.GREATER_EQUAL, 1, "cons0");
            expr = new GRBLinExpr();
            expr.addTerm(1.0, vars[0]);
            expr.addTerm(1.0, vars[1]);
            model.addConstr(expr, GRB.GREATER_EQUAL, 1, "cons1");
            expr = new GRBLinExpr();
            expr.addTerm(1.0, vars[0]);
            expr.addTerm(1.0, vars[2]);
            model.addConstr(expr, GRB.GREATER_EQUAL, 1, "cons2");
            expr = new GRBLinExpr();
            expr.addTerm(1.0, vars[1]);
            expr.addTerm(1.0, vars[2]);
            expr.addTerm(1.0, vars[3]);
            model.addConstr(expr, GRB.GREATER_EQUAL, 1, "cons3");
            expr = new GRBLinExpr();
            expr.addTerm(1.0, vars[2]);
            expr.addTerm(1.0, vars[3]);
            model.addConstr(expr, GRB.GREATER_EQUAL, 1, "cons4");
            expr = new GRBLinExpr();
            expr.addTerm(1.0, vars[2]);
            expr.addTerm(1.0, vars[3]);
            expr.addTerm(1.0, vars[4]);
            model.addConstr(expr, GRB.GREATER_EQUAL, 1, "cons5");
            expr = new GRBLinExpr();
            expr.addTerm(1.0, vars[2]);
            expr.addTerm(1.0, vars[4]);
            model.addConstr(expr, GRB.GREATER_EQUAL, 1, "cons6");

            // Optimize model
            model.optimize();

            System.out.println("\n" + "******* result *******");

            System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));
            for (int i = 0; i < vars.length; i++) {
                System.out.println(vars[i].get(GRB.DoubleAttr.X));
            }
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
}
