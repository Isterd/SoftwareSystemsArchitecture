package com.example.architecture;

import com.example.architecture.messages.ExperimentParamsRequest;
import com.example.architecture.messages.ExperimentResponse;
import com.example.architecture.services.Simulation;

import java.io.FileWriter;
import java.io.IOException;

public class ArchitectureApp {

    public static void main(String[] args) throws IOException {

        int[] devArr = new int[]{
                300, 140, 50
        };
        int[] buffArr = new int[]{
                10, 40, 120
        };
        int bufMin = 10, bufMax = 200;
        int devMin = 50, devMax = 300;
        double alpha = 0.4, beta = 0.7, lambda = 0.0025;
        FileWriter writer = new FileWriter("example.txt", true);
        int src = 10000;
        int N0 = src * 100;
        for (int dev : devArr) {
            for (int buf : buffArr) {
                Simulation simulation = new Simulation();
                simulation.startWork(new ExperimentParamsRequest(src, dev, buf, N0, alpha, beta, lambda));
                double p0 = simulation.getP0();
                ExperimentResponse resp = simulation.countResults();
                if (p0 <= 0.95) {
                    //записывать каждый раз
                    String ans = src + "; " + dev + "; " + buf + "; " + p0 + "; " +
                            resp.getTpreb() + "; " + resp.getKisp() + ";\n";
                    writer.write(ans);
                    writer.flush();
                    System.out.println("+");
                } else if (p0 > 0.95) {
                    break;
                }
            }
        }
        writer.close();
    }

    private static double countAccuracy(double p0, double p1) {
        return Math.abs(p0 - p1) / p0;
    }
}
