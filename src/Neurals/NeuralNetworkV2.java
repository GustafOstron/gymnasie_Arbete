package src.Neurals;

import java.io.Serializable;
import java.util.Random;

public class NeuralNetworkV2 implements Serializable {
    Random rand = new Random();
    NeuronLayerV2[] layers;
    public NeuralNetworkV2(int nrInputs, int nrHiddenLayers, int hiddenLayerSize, int nrOutputs){
        layers = new NeuronLayerV2[nrHiddenLayers+2];
        layers[0] = new NeuronLayerV2(hiddenLayerSize, nrInputs, rand);
        for (int i = 0; i <nrHiddenLayers-1 ; i++) {
            layers[i+1] = new NeuronLayerV2(hiddenLayerSize, hiddenLayerSize, rand);
        }
        layers[layers.length-1] = new NeuronLayerV2(nrOutputs, hiddenLayerSize, rand);
    }

    public void mutate(int mutationRate){
        for (int i = 0; i < layers.length; i++) {
            layers[i].mutate(rand, mutationRate);
        }
    }

    public double[] getOutput(double[] input){
        double[] previousOutput = input;
        for (int i = 0; i < layers.length; i++) {
            previousOutput = layers[i].getOutput(previousOutput);
        }
        return previousOutput;
    }
}

class NeuronLayerV2{
    NeuronV2[] neurons;
    NeuronLayerV2(int size, int previousLayerSize, Random rand){
        neurons = new NeuronV2[size];
    }

    void mutate(Random rand, double mutationRate){
        for(NeuronV2 n: neurons){
            n.mutate(rand, mutationRate);
        }
    }

    protected double[] getOutput(double[] inputs){
        double[] out = new double[neurons.length];
        for (int i = 0; i < neurons.length; i++) {
            for (int j = 0; j < neurons[i].getWeights().length; j++) {
                out[i] += inputs[j] * neurons[i].getWeights()[j];
            }
            out[i] = (out[i]/17)+neurons[i].getBias();
        }
        return out;
    }
}

class NeuronV2{
    private double[] weights;
    private double bias;
    NeuronV2(int previousLayerSize, Random rand){
        weights = new double[previousLayerSize];
        for (int i = 0; i < previousLayerSize; i++) {
            weights[i] = rand.nextDouble();
            bias = rand.nextDouble();
        }
    }

    public double[] getWeights() {
        return weights;
    }

    public double getBias() {
        return bias;
    }

    void mutate(Random rand, double mutationRate){
        for (int i = 0; i < weights.length; i++) {
            if (rand.nextDouble() > mutationRate) weights[i] = rand.nextDouble();
        }
        if (rand.nextDouble() > mutationRate) bias = rand.nextDouble();
    }
}