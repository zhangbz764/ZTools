package demoTest2;

import Jama.Matrix;
import math.ZPCA;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project city_site_matching
 * @date 2021/11/4
 * @time 15:56
 */
public class TestPCA {
    public static void main(String[] args) {
        ZPCA pca = new ZPCA();
        double[][] primaryArray = {
//                {0,0},
//                {100,0},
//                {100,100},
//                {0,100}
                {-100,0},
                {100,-200},
                {200,-100},
                {0,100}
        };
        System.out.println("--------------------------------------------");
        System.out.println("原始数据: ");
        System.out.println(primaryArray.length + "行，" + primaryArray[0].length + "列");
        for (int i = 0; i < primaryArray.length; i++) {
            for (int j = 0; j < primaryArray[0].length; j++) {
                System.out.print(+primaryArray[i][j] + " \t");
            }
            System.out.println();
        }

        // 均值中心化后的矩阵
        double[][] averageArray = pca.changeAverageToZero(primaryArray);
        System.out.println("--------------------------------------------");
        System.out.println("均值0化后的数据: ");
        System.out.println(averageArray.length + "行，" + averageArray[0].length + "列");
        for (int i = 0; i < averageArray.length; i++) {
            for (int j = 0; j < averageArray[0].length; j++) {
                System.out.print((float) averageArray[i][j] + " \t");
            }
            System.out.println();
        }

        // 协方差矩阵
        double[][] varMatrix = pca.getVarianceMatrix(averageArray);
        System.out.println("---------------------------------------------");
        System.out.println("协方差矩阵: ");
        for (int i = 0; i < varMatrix.length; i++) {
            for (int j = 0; j < varMatrix[0].length; j++) {
                System.out.print((float) varMatrix[i][j] + "\t");
            }
            System.out.println();
        }

        // 特征值矩阵
        System.out.println("--------------------------------------------");
        System.out.println("特征值矩阵: ");
        double[][] eigenvalueMatrix = pca.getEigenvalueMatrix(varMatrix);

        // 特征向量矩阵
        System.out.println("--------------------------------------------");
        System.out.println("特征向量矩阵: ");
        double[][] eigenVectorMatrix = pca.getEigenVectorMatrix(varMatrix);

        // 主成分矩阵
        System.out.println("--------------------------------------------");
        Matrix principalMatrix = pca.getPrincipalComponent(primaryArray, eigenvalueMatrix, eigenVectorMatrix);
        System.out.println("主成分矩阵: ");
        principalMatrix.print(6, 2);

        // 降维后的矩阵
        System.out.println("--------------------------------------------");
        System.out.println("降维后的矩阵: ");
        Matrix resultMatrix = pca.getResult(primaryArray, principalMatrix);
        resultMatrix.print(10, 2);
    }
}
