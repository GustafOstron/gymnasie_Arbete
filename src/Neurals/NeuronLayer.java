package Neurals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NeuronLayer implements Serializable {
    List<Double> outputs;
    List<Neuron> neurons;
    static Random rand = new Random();

    NeuronLayer(int nrNeurons, int nrPreviousLayerNeurons){
        outputs = Arrays.asList(new Double[nrNeurons]);
        neurons = new ArrayList<>();
        for (int i = 0; i < nrNeurons; i++) {
            neurons.add(new Neuron(nrPreviousLayerNeurons));
        }
    }

    public List<Double> getOutputs() {
        return outputs;
    }
    public List<Double> getAndCalculateOutputs(List<Double> inputs) {
        for (int i = 0; i < neurons.size(); i++) {
            outputs.set(i, neurons.get(i).getAndCalculateOutput(inputs));
        }
        return outputs;
    }

    public void mutate(){
        neurons.get(getRandomNumber(neurons.size())).mutate();
    }

    public static int getRandomNumber(int max) {
        return (int) (Math.random() * (max));
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }
}
