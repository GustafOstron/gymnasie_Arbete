package Neurals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Neuron implements Serializable {
    List<Double> weights;
    double bias, output;
    static Random rand = new Random();

    Neuron(int nrInputs){
        weights = new ArrayList<>();
        for (int i = 0; i < nrInputs; i++) {
            weights.add(rand.nextDouble());
        }
        this.bias = 0;
    }
    Neuron(int nrInputs , double bias){
        weights = new ArrayList<>();
        for (int i = 0; i < nrInputs; i++) {
            weights.add(rand.nextDouble());
        }
        this.bias = bias;
    }

    Neuron(List<Double> weights){
        this.weights = weights;
        this.bias = 0;
    }

    Neuron(List<Double> weights, double bias){
        this.weights = weights;
        this.bias = bias;
    }

    public List<Double> getWeights() {
        return weights;
    }

    public void setWeights(List<Double> weights) {
        this.weights = weights;
    }

    public double getOutput() {
        return output;
    }

    public double getAndCalculateOutput(List<Double> inputs){
        double tot = 0;
        for (int i = 0; i < inputs.size(); i++) {
            tot += inputs.get(i) * weights.get(i);
        }
        return tot * 1/inputs.size();
    }

    public void mutate(){
        weights.set(getRandomNumber(weights.size()-1), rand.nextDouble());
    }

    public static int getRandomNumber(int max) {
        return (int) (Math.random() * (max));
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }
}
