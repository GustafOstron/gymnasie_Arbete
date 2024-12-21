package Neurals;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NeuralNetwork implements Serializable {
    List<Double> outputs;
    List<NeuronLayer> neuralLayers;
    Random rand = new Random();

    public NeuralNetwork(int nrInputs, int nrHiddenLayers, int hiddenLayerSize, int nrOutputs){
        outputs = new ArrayList<>();
        neuralLayers = new ArrayList<>();
        neuralLayers.add(new NeuronLayer(nrInputs, nrInputs));
        for (int i = 0; i < nrHiddenLayers; i++) {
            neuralLayers.add(new NeuronLayer(hiddenLayerSize, neuralLayers.get(i).neurons.size()));
        }
        neuralLayers.add(new NeuronLayer(nrOutputs, neuralLayers.get(neuralLayers.size()-1).neurons.size()));
    }
    public NeuralNetwork(String filePath){
        NeuralNetwork nnwNew = readNeuralNetworkFromFile(new File(filePath));
        this.outputs = nnwNew.outputs;
        this.neuralLayers = nnwNew.neuralLayers;
    }
    NeuralNetwork(File file){
        this.outputs = readNeuralNetworkFromFile(file).outputs;
        this.neuralLayers = readNeuralNetworkFromFile(file).neuralLayers;
    }

    public List<Double> getOutputs(List<Double> inputs) {
        return outputs;
    }

    public List<Double> getAndCalculateOutputs(List<Double> inputs) {
        neuralLayers.get(0).getAndCalculateOutputs(inputs);
        for (int i = 1; i < neuralLayers.size(); i++) {
            neuralLayers.get(i).getAndCalculateOutputs(neuralLayers.get(i-1).getOutputs());
        }
        outputs = neuralLayers.get(neuralLayers.size()-1).getOutputs();
        return outputs;
    }

    public List<NeuronLayer> getNeuralLayers() {
        return neuralLayers;
    }

    public void setNeuralLayers(List<NeuronLayer> neuralLayers) {
        this.neuralLayers = neuralLayers;
    }

    public void mutate(int mutations){
        for (int i = 0; i < mutations; i++) {
            mutate();
        }
    }

    public void mutate(){
        neuralLayers.get(getRandomNumber(neuralLayers.size()-1)).mutate();
    }

    public static int getRandomNumber(int max) {
        return (int) (Math.random() * (max));
    }

    public static NeuralNetwork readNeuralNetworkFromFile(File file){
        try{
            //Creating stream to read the object
            ObjectInputStream in=new ObjectInputStream(new FileInputStream(file));
            NeuralNetwork s = (NeuralNetwork)in.readObject();
            in.close();
            return s;
        }catch(Exception e){
            System.out.println(e);
            return new NeuralNetwork(16, 2,10,4);
        }
    }
    public boolean writeToFile(String filePath){
        return writeToFile(new File(filePath));
    }
    public boolean writeToFile(File file){
        try {
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(this);
            out.close();
            return true;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }
}
