import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Utente on 19/01/2016.
 */
public class TimeSeriesValueWindow {
    private String name;
    private double sumOfDecay = 0.0;
    private double sumOfWeight = 0.0;
    private double currRelevance;
    private double currTime;
    private static double lambda = 1.0;
    private double currTrend;
    private int windowNumber = 0;
    private double windowCountElement = 0;
    private BufferedWriter writeSerie;
    private int priority = 0;
    private int pathSubDir;
    private int hashValue;
    private TreeMap<Double, Double> windowList = new TreeMap<Double, Double>();

    public TimeSeriesValueWindow(String name) {
        String split[]=Main.hashTimeSeriesName(name).split("\t");
        this.name = name;
        this.hashValue=Integer.parseInt(split[0]);
        this.pathSubDir=Integer.parseInt(split[1]);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSumOfDecay() {
        return sumOfDecay;
    }

    public void setSumOfDecay(double sumOfDecay) {
        this.sumOfDecay = sumOfDecay;
    }

    public double getSumOfWeight() {
        return sumOfWeight;
    }

    public void setSumOfWeight(double sumOfWeight) {
        this.sumOfWeight = sumOfWeight;
    }

    public double getCurrRelevance() {
        return currRelevance;
    }

    public void setCurrRelevance(double currRelevance) {
        this.currRelevance = currRelevance;
    }

    public double getCurrTime() {
        return currTime;
    }

    public void setCurrTime(double currTime) {
        this.currTime = currTime;
    }

    public double getCurrTrend() {
        return currTrend;
    }

    public void setCurrTrend(double currTrend) {
        this.currTrend = currTrend;
    }

    public double getWindowCountElement() {
        return windowCountElement;
    }

    public void setWindowCountElement(double windowCountElement) {
        this.windowCountElement = windowCountElement;
    }

    public TreeMap<Double, Double> getWindowList() {
        return windowList;
    }

    public void setWindowList(TreeMap<Double, Double> windowList) {
        this.windowList = windowList;
    }

    public void addListElement(double time, double relevance) {
        windowList.put(time, relevance);
        priority = (int) time;

    }

    /**
     * Computa il trend e passa come parametro l'osservazione che completa la finestra
     */
    public void clearListElement() {
        this.windowList.clear();
    }

    public void computeTrend(double time, double relevance) throws IOException {
        File file = new File(Main.path +File.separator +Main.firstDirTimeSeriesPath(this.name)+File.separator +Main.hashTimeSeriesName(this.name)+File.separator+ this.name + File.separator + "Result.txt");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        writeSerie = new BufferedWriter(new FileWriter(file, true));
        this.windowNumber++;
        String graphics = "------------";
        double sumDecayWindow = 0.0;
        double sumWeightWindow = 0.0;
        writeSerie.append(graphics + "Computo Trend Della TimeSeries " + this.name + " Finestra numero " + this.windowNumber + graphics);
        writeSerie.newLine();
        for (Map.Entry<Double, Double> entry : windowList.entrySet()) {
            sumDecayWindow = sumDecayWindow + entry.getValue() * Math.pow(Math.E, -(lambda * (time - entry.getKey())));
            sumWeightWindow = sumWeightWindow + Math.pow(Math.E, -(lambda * (time - entry.getKey())));
            writeSerie.append("Time : " + entry.getKey() + " Relevance " + entry.getValue());
            writeSerie.newLine();
        }
        writeSerie.append("Time : " + time + " Relevance " + relevance);
        sumDecayWindow = sumDecayWindow + relevance;
        sumWeightWindow = sumWeightWindow + 1;
        this.sumOfDecay = this.sumOfDecay + sumDecayWindow;
        this.sumOfWeight = this.sumOfWeight + sumWeightWindow;
        this.currTrend = relevance - (this.sumOfDecay / this.sumOfWeight);
        writeSerie.newLine();
        writeSerie.append("Media pesata " + (this.sumOfDecay / this.sumOfWeight) + " sum of decay " + this.sumOfDecay + " sum of weight : " + this.sumOfWeight);
        writeSerie.newLine();
        writeSerie.append(graphics + " Fine Finestra TimeSeries " + this.name + graphics);
        writeSerie.newLine();
        writeSerie.close();


    }

    public int getWindowNumber() {
        return windowNumber;
    }

    public void setWindowNumber(int windowNumber) {
        this.windowNumber = windowNumber;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPathSubDir() {
        return pathSubDir;
    }

    public void setPathSubDir(int pathSubDir) {
        this.pathSubDir = pathSubDir;
    }

    public int getHashValue() {
        return hashValue;
    }

    public void setHashValue(int hashValue) {
        this.hashValue = hashValue;
    }
}
