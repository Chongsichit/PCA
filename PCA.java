import Jama.Matrix;

import java.io.*;
import java.util.*;

public class PCA {

    private static double[][] centralization (double[][] matrix){
        int n = matrix.length;
        int m = matrix[0].length;
        double [][] average0matrix = new double[n][m];
        double [] average = new double[m];
        double[] sum = new double[m];
        for ( int j = 0 ; j < m ; j++){  // j = columns
            for ( int i = 0 ; i < n ; i++){  // i = rows
                sum[j] += matrix[i][j];
            }
            average[j] = sum[j]/n;}
        for(int j = 0; j<m; j++){
            for(int i = 0 ; i < n ; i++){
                average0matrix[i][j]=matrix[i][j]-average[j];
            }
        }

    return average0matrix;
    } //使樣本均值為零，方便計算

    private static double[][] covarianceMatrix(double[][] matrix){
        int m = matrix[0].length; //m為總變量數 n*m matrix
        int n = matrix.length; //n為總行數
        double [][] result = new double[m][m];
        for ( int i = 0; i < m; i++){
            for ( int j = 0; j < m; j++){
                double temp = 0;
                for ( int k = 0; k < n; k++){
                    temp += matrix[k][i] * matrix[k][j];
                }
                result[i][j] = temp / (n-1) ;
            }
        }

        return result;
    }//求出協方差矩陣

    private static double[][] eigenValueMatrix(double[][] matrix){ //求矩陣的特徵值
        Matrix matrix1 = new Matrix(matrix);
        double[][] result = matrix1.eig().getD().getArray();


        return result;
    } //求出特徵值矩陣

    private static double[][] eigenVectorMatrix(double[][] matrix){
        Matrix matrix1 = new Matrix(matrix);
        double [][] result = matrix1.eig().getV().getArray();

        return result;
    } //求出特徵向量矩陣

    private static double[][] principalMatrix(double[][] eigenvalue,double [][] eigenvector){
        double [][] result = new double[2][];
        HashMap<Double,double[]> hashMap = new HashMap<>();

        Matrix matrix = new Matrix(eigenvector);
        double[][] tranEigenVector = matrix.transpose().getArray(); //轉置矩陣方便進行排序和處理


        List<Double> eigenvalueList = new ArrayList<>(); //用於排序

        for (int i = 0; i < eigenvalue.length; i++){
            for(int j = 0; j< eigenvalue[i].length; j++){
                if(i==j){
                    double eigenValue = eigenvalue[i][j];
                    double[] eigenVector = tranEigenVector[i];
                    hashMap.put(eigenValue,eigenVector);
                    eigenvalueList.add(eigenValue);  //將特徵值所對應的特徵向量配在一起
                }
            }
        }


        Collections.reverse(eigenvalueList); //大至小排序

        for (int i = 0; i < result.length ; i++){
            double[] temp = hashMap.get(eigenvalueList.get(i));
            result[i] = temp;
        }


        return result;
    }

    private static double[][] getResult(double[][] primary, double[][] principal) {
        Matrix primaryMatrix = new Matrix(primary);
        Matrix principalMatrix = new Matrix(principal);
        Matrix matrix1 = primaryMatrix.times(principalMatrix.transpose());
        double[][]result= matrix1.getArray();
        return result;
    }

    private static double[][] inputData(String path) throws IOException {

        double [][] result1 = new double[60000][784];
        FileInputStream fileInputStream = new FileInputStream(path);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        int k=0;
        while((line = bufferedReader.readLine())!= null){
            String[] split = line.split(":");
            String[] split2 = split[1].split(" ");
            double[] temp = new double[split2.length];
            for ( int i = 0; i< temp.length; i++){
                temp[i]=Double.parseDouble(split2[i]);
            }
            result1[k]=temp;
            k++;
        }
        return result1;
    }

    private static void outputData(String path, double[][] matrix) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        for(int i = 0; i < matrix.length; i++){
//            bufferedWriter.write(i+":");
            for (int j =0;  j<matrix[i].length; j++){
                bufferedWriter.write(matrix[i][j] +",");
            }
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
    }

    public static void main(String[] args) throws IOException {
        String path = "C:\\Users\\ASUS\\Desktop\\mnist\\pixel";
        String path2 = "C:\\Users\\ASUS\\Desktop\\mnist\\Q1A";
        String path3 = "C:\\Users\\ASUS\\Desktop\\mnist\\Q1A_2";
       double[][] matrix = {{7,4,3,4},{4,1,8,3},{6,3,5,2},{8,3,2,10},{4,5,0,9},{1,3,2,5},{6,6,3,2},{8,3,3,6}}; //Homework data
//      double[][] matrix = inputData(path);
        System.out.println("原矩陣：");
        for(int i=0;i<matrix.length;i++){
            System.out.println(Arrays.toString(matrix[i]));
        }
        double[][]  average0matrix = centralization(matrix);
        System.out.println("去中心化矩陣：");
        for(int i=0;i<average0matrix.length;i++){
            System.out.println(Arrays.toString(average0matrix[i]));
        }
        double [][] covmatrix = covarianceMatrix(average0matrix);
        System.out.println("協方差矩陣：");
        for(int i=0;i<average0matrix.length;i++){
            System.out.println(Arrays.toString(average0matrix[i]));
        }
        double[][] eigenDmatrix = eigenValueMatrix(covmatrix);
        System.out.println("特徵值矩陣：");
        for(int i=0;i<eigenDmatrix.length;i++){
            System.out.println(Arrays.toString(eigenDmatrix[i]));
        }
        double[][] eigenVmatrix = eigenVectorMatrix(covmatrix);
        System.out.println("特徵向量矩陣：");
        for(int i=0;i<eigenVmatrix.length;i++){
            System.out.println(Arrays.toString(eigenVmatrix[i]));
        }
        double[][] principalMatrix = principalMatrix(eigenDmatrix,eigenVmatrix);
        System.out.println("主成分矩陣：");
        for(int i=0;i<principalMatrix.length;i++){
            System.out.println(Arrays.toString(principalMatrix[i]));
        }
        outputData(path2,principalMatrix);
        double [][] result = getResult(average0matrix,principalMatrix);
        System.out.println("已降維矩陣：");
        for(int i=0;i<result.length;i++){
            System.out.println(Arrays.toString(result[i]));
        }
        outputData(path3,result);
    }
}
