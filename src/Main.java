
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by Marco Garlet on 19/01/2016.
 */

public class Main {


    private static TreeMap<String, TimeSeriesValueWindow> executionValueOrientedTree = new TreeMap<String, TimeSeriesValueWindow>();
    private static TreeMap<String, WriteResult> writeContextTree = new TreeMap<String, WriteResult>();
    private static TreeMap<Integer,Integer> hashKey=new TreeMap<>();
    private static int windowLimit = 3;
    public static String path = "/Volumes/SAMSUNG 1/RisultatiTheseus";
    private static int executionLimit = 10000;


    public static void miningTheseus(TimeSeriesValueWindow series, double time, double value) throws IOException
    {

        series.setWindowCountElement(series.getWindowCountElement() + 1);
        if (series.getWindowCountElement() == windowLimit) // se l'elemento che sarebbe nella finestra ma non è ancora inserito in lista è l'ultimo computa il trend
        {
            // computa le somme di decadimento , le somme peso e calcola il trend.
            series.computeTrend(time, value);
            if (series.getCurrTrend() > 0)
                System.out.println("Trend " + series.getName() + " in crescita");
            else if (series.getCurrTrend() == 0)
                System.out.println("Trend " + series.getName() + " costante");
            else
                System.out.println("Trend " + series.getName() + " in calo");
            series.setWindowCountElement(0);
            series.clearListElement();
        }
        else
        {
            // aggiungi i valori della finestra in attesa di essere visualizzati.
            series.addListElement(time, value);
            System.out.println("<>Time series: " + series.getName() + " AGGIUNGO SENZA COMPUTARE time :" + time + " relevance " + value + "<>" + series.getWindowCountElement());

        }
        series.setCurrTime(time);
        series.setCurrRelevance(value);

    }
    public static String hashTimeSeriesName(String name)
    {
        int hash = 7;
        for (int i = 0; i < name.length(); i++) {
            hash = hash*31 + name.charAt(i);
        }
        if(!hashKey.containsKey(hash))
        {
            hashKey.put(hash,1);
        }
        else
        {
            hashKey.replace(hash,hashKey.get(hash)+1);
        }
        return ""+hash+"\t"+hashKey.get(hash);
    }
    public static String firstDirTimeSeriesPath(String name)
    {
        int index=name.charAt(0);
        String firstFolder;
        if((index>=65&&index<=90)||(index>=97)&&(index<=122))
            firstFolder=""+name.charAt(0);
        else
        {
            if(index>=48&&index<=57)
                firstFolder="_numbers";
            else
                firstFolder="_symbols";
        }
        return firstFolder;
    }

    public static boolean hasContextFile(String name) {

        File context = new File(path + File.separator +firstDirTimeSeriesPath(name)+File.separator+hashTimeSeriesName(name) +File.separator +name + File.separator + hashTimeSeriesName(name) + "Log.txt");
        return context.exists();
    }

    public static void main(String args[]) throws IOException {
        int chunkLimit = 0,diskAccess=0;
        double currTime, currValue;
        String currName;
        ReadFileStream readSource = new ReadFileStream("\t");
        TimeSeriesValueWindow newElement;
        WriteFile writeContextElement;

        while (readSource.readLine() == true)
        {

            if (readSource.splitLine())
            {
                chunkLimit++;
                currTime = readSource.getCurrTime();
                currValue = readSource.getCurrValue();
                currName = readSource.getCurrTimeSeriesName();
                System.out.println("\n!!!!!!!!OSSERVAZIONE"+currName+"Tempo "+currTime+" Valore "+currValue+"OSSERVAZIONE!!!!!!!!\n");
                if (!executionValueOrientedTree.containsKey(readSource.getCurrTimeSeriesName()))
                {
                    newElement = new TimeSeriesValueWindow(currName);

                    if (hasContextFile(currName))
                    {
                        writeContextElement = new WriteFile();
                        writeContextElement.loadFromFile(new File(path + File.separator +firstDirTimeSeriesPath(currName)+ File.separator + hashTimeSeriesName(currName) +File.separator +currName + File.separator + hashTimeSeriesName(currName) + "Log.txt"), currName, newElement);
                    } else
                    {
                        writeContextElement = new WriteFile(newElement);
                    }
                    writeContextTree.put(currName, writeContextElement);
                    executionValueOrientedTree.put(currName, newElement);

                }
                miningTheseus(executionValueOrientedTree.get(currName), currTime, currValue);

                if (chunkLimit==executionLimit)
                {
                    // remove some time series from memory and write on a file
                    for (Map.Entry<String, WriteResult> entry : writeContextTree.entrySet())
                    {
                        entry.getValue().connect();
                        entry.getValue().write();
                        entry.getValue().disconnect();

                    }
                    executionValueOrientedTree.clear();
                    writeContextTree.clear();
                    System.out.println("Strutture dati dimensione: " + executionValueOrientedTree.size());
                    chunkLimit = 0;


                }



            }




        }
        for (Map.Entry<String, WriteResult> entry : writeContextTree.entrySet()) {
            entry.getValue().connect();
            entry.getValue().write();
            entry.getValue().disconnect();

        }
        executionValueOrientedTree.clear();
        writeContextTree.clear();


    }
}
