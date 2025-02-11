package src;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;

public class testJcuda {
    public static void main(String[] args) {

        // Initialize JCublas
        JCublas.cublasInit();

        // Define matrices A, B, C
        int n = 2; // 2x2 matrices
        float[] A = {1, 2, 3, 4}; // Matrix A (Row-major)
        float[] B = {5, 6, 7, 8}; // Matrix B
        float[] C = new float[n * n]; // Result matrix

        // Allocate memory on GPU
        Pointer d_A = new Pointer();
        Pointer d_B = new Pointer();
        Pointer d_C = new Pointer();
        JCublas.cublasAlloc(n * n, Sizeof.FLOAT, d_A);
        JCublas.cublasAlloc(n * n, Sizeof.FLOAT, d_B);
        JCublas.cublasAlloc(n * n, Sizeof.FLOAT, d_C);

        // Copy matrices from CPU to GPU
        JCublas.cublasSetVector(n * n, Sizeof.FLOAT, Pointer.to(A), 1, d_A, 1);
        JCublas.cublasSetVector(n * n, Sizeof.FLOAT, Pointer.to(B), 1, d_B, 1);

        // Perform matrix multiplication: C = A * B
        float alpha = 1.0f, beta = 0.0f;
        JCublas.cublasSgemm('n', 'n', n, n, n, alpha, d_A, n, d_B, n, beta, d_C, n);

        // Copy result from GPU to CPU
        JCublas.cublasGetVector(n * n, Sizeof.FLOAT, d_C, 1, Pointer.to(C), 1);

        // Print the result matrix
        System.out.println("Result matrix:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(C[i * n + j] + " ");
            }
            System.out.println();
        }

        // Free GPU memory
        JCublas.cublasFree(d_A);
        JCublas.cublasFree(d_B);
        JCublas.cublasFree(d_C);

        // Shutdown JCublas
        JCublas.cublasShutdown();
    }
}
