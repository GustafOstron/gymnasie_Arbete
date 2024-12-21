package src;

import src.Neurals.NeuralNetwork;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;

public class testDataBaseConnection {
    private static final String url = "jdbc:mysql://localhost:3306/gymnasieprojekt";
    private static final String username = "java";
    private static final String password = "password";

    public static void main(String[] args) throws IOException, SQLException {
        writeNetworkToDB(new NeuralNetwork(16,5,100,4));
        //tessst(6);
    }
    public static int writeNetworkToDB(NeuralNetwork nnw) throws IOException, SQLException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(nnw);
        Blob blob = new SerialBlob(byteArrayOutputStream.toByteArray());
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO networks(inputs, outputs, nrHiddenLayers, hiddenLayerSize, saveFile) VALUES(?,?,?,?,?)";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, nnw.getNeuralLayers().get(0).getNeurons().size());
            pst.setInt(2, nnw.getNeuralLayers().get(nnw.getNeuralLayers().size()-1).getNeurons().size());
            pst.setInt(3, (nnw.getNeuralLayers().size()-2));
            pst.setInt(4, nnw.getNeuralLayers().get(1).getNeurons().size());
            pst.setBlob(5, blob);
            pst.execute();
            sql = "SELECT ID FROM networks ORDER BY ID DESC LIMIT 1";
            pst = connection.prepareStatement(sql);
            pst.executeQuery();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
        return -1;
    }
    static void tessst(int nr){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO test(NR) VALUE ("+nr+")";
            PreparedStatement pst = connection.prepareStatement(sql);
            System.out.println(pst.toString());
            pst.execute();

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }
}
