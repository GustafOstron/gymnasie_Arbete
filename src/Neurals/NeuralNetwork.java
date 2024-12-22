package src.Neurals;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class NeuralNetwork implements Serializable {
    Random rand = new Random();
    NeuronLayer[] layers;
    public NeuralNetwork(int nrInputs, int nrHiddenLayers, int hiddenLayerSize, int nrOutputs){
        layers = new NeuronLayer[nrHiddenLayers+1];
        layers[0] = new NeuronLayer(hiddenLayerSize, nrInputs, rand);
        for (int i = 0; i <nrHiddenLayers-1 ; i++) {
            layers[i+1] = new NeuronLayer(hiddenLayerSize, hiddenLayerSize, rand);
        }
        layers[layers.length-1] = new NeuronLayer(nrOutputs, hiddenLayerSize, rand);
    }

    public NeuralNetwork(NeuralNetwork nn){
        layers = new NeuronLayer[nn.layers.length];
        for (int i = 0; i < nn.layers.length; i++) {
            layers[i] = new NeuronLayer(nn.layers[i]);
        }
    }

    public void mutate(double mutationRate){
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

    public static NeuralNetwork readNeuralNetworkFromFile(File file) throws IOException, ClassNotFoundException{
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        NeuralNetwork s = (NeuralNetwork)in.readObject();
        in.close();
        return s;
    }
    public void writeToFile(File file) throws IOException {
        FileOutputStream fout = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fout);
        out.writeObject(this);
        out.close();
    }
}

class NeuronLayer implements Serializable{
    Neuron[] neurons;
    NeuronLayer(int size, int previousLayerSize, Random rand){
        neurons = new Neuron[size];
        for (int i = 0; i < size; i++) {
            neurons[i] = new Neuron(previousLayerSize, rand);
        }
    }

    NeuronLayer(NeuronLayer nl){
        neurons = new Neuron[nl.neurons.length];
        for (int i = 0; i < nl.neurons.length; i++) {
            neurons[i] = new Neuron(nl.neurons[i]);
        }
    }

    void mutate(Random rand, double mutationRate){
        for(Neuron n: neurons){
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

class Neuron implements Serializable{
    private double[] weights;
    private double bias;
    Neuron(int previousLayerSize, Random rand){
        weights = new double[previousLayerSize];
        for (int i = 0; i < previousLayerSize; i++) {
            weights[i] = rand.nextDouble();
            bias = rand.nextDouble();
        }
    }

    Neuron(Neuron n){
        this.weights = Arrays.copyOf(n.weights, n.weights.length);
        this.bias = n.bias;
    }

    public double[] getWeights() {
        return weights;
    }

    public double getBias() {
        return bias;
    }

    void mutate(Random rand, double mutationRate){
        for (int i = 0; i < weights.length; i++) {
            if (rand.nextDouble() < mutationRate) weights[i] = rand.nextDouble();
        }
        if (rand.nextDouble() < mutationRate) bias = rand.nextDouble();
    }
}
