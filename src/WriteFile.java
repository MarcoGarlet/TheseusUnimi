/**
 * Created by marco on 03/02/16.
 */
import java.io.*;
import java.util.Map;

public class WriteFile implements WriteResult
{

    private BufferedWriter bwSerie;
    private TimeSeriesValueWindow serie;
    private File file;
    public WriteFile(TimeSeriesValueWindow serie)  // crea la cartella con il file contenente tutti i valori delle variabili locali
    {
        /*
        String separator[]=Main.hashTimeSeriesName(serie.getName()).split("\t");
        int hash=Integer.parseInt(separator[0]);
        this.serie.setPathSubDir(Integer.parseInt(separator[1]));
        this.serie=serie;*/
        file=new File(Main.path+File.separator +Main.firstDirTimeSeriesPath(serie.getName())+File.separator+hash+File.separator+serie.getName()+File.separator+hash+"Log.txt");
        if(file.exists())
           file.delete();
        else
        {
            file.getParentFile().mkdirs();
        }
        try
        {
            file.createNewFile();

        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }

    }
    public WriteFile()
    {

    }
    public void write() throws IOException {
        // scrivi tutti i campi
        System.out.println("Time series nel metodo write "+serie.getName());
        bwSerie.write("name:\t"+serie.getName()+"\n");
        bwSerie.write("sum of decay:\t"+serie.getSumOfDecay()+"\n");
        bwSerie.write("sum of weight:\t"+serie.getSumOfWeight()+"\n");
        bwSerie.write("current relevance:\t"+serie.getCurrRelevance()+"\n");
        bwSerie.write("current time:\t"+serie.getCurrTime()+"\n");
        bwSerie.write("current trend:\t"+serie.getCurrTrend()+"\n");
        bwSerie.write("window number:\t"+serie.getWindowNumber()+"\n");
        bwSerie.write("window count element:\t"+serie.getWindowCountElement()+"\n");
        bwSerie.write("path sub dir:\t"+serie.getPathSubDir());
        bwSerie.write("-Window List-\n");
        for(Map.Entry<Double,Double> entry : serie.getWindowList().entrySet())
        {
            bwSerie.write("time:\t"+entry.getKey()+"\tvalue:\t"+entry.getValue()+"\n");

        }
       // bwSerie.write("-End Window List");


    }
    public void connect()
    {
        try
        {
            bwSerie=new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    public void disconnect()
    {
        try
        {
            bwSerie.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    public void loadFromFile(File existingFile,String name,TimeSeriesValueWindow seriesToUpdate)
    {

        this.file=existingFile;
        String line="";
        boolean startList=false;
        BufferedReader readFile = null;
        try
        {
            readFile = new BufferedReader(new FileReader(file.getAbsoluteFile()));
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        try
        {
            while((line=readFile.readLine())!=null)
            {
                if(line.equals("-Window List-"))
                {
                    System.out.println("\ncapisco che inizia una lista\n");
                    startList=true;
                    continue;
                }
                String parts[]= line.split("\t");
                if(startList)
                {


                    seriesToUpdate.addListElement(Double.parseDouble(parts[1]),Double.parseDouble(parts[3]));

                }
                else
                {

                    switch(parts[0])
                    {
                        case "name:":
                            seriesToUpdate.setName(parts[1]);
                            break;
                        case "sum of decay:":
                            seriesToUpdate.setSumOfDecay(Double.parseDouble(parts[1]));
                            break;
                        case "sum of weight:":
                            seriesToUpdate.setSumOfWeight(Double.parseDouble(parts[1]));
                            break;
                        case "current relevance:":
                            seriesToUpdate.setCurrRelevance(Double.parseDouble(parts[1]));
                            break;
                        case "current time:":
                            seriesToUpdate.setCurrTime(Double.parseDouble(parts[1]));
                            break;
                        case "current trend:":
                            seriesToUpdate.setCurrTrend(Double.parseDouble(parts[1]));
                            break;
                        case "window number:":
                            seriesToUpdate.setWindowNumber(Integer.parseInt(parts[1]));
                            break;
                        case "window count element:":
                            seriesToUpdate.setWindowCountElement(Double.parseDouble((parts[1])));
                            break;
                        case "path sub dir:":
                            seriesToUpdate.setPathSubDir(Integer.parseInt(parts[1]));
                            break;
                    }

                }
            }
            readFile.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        this.serie=seriesToUpdate;
        file=new File(Main.path +File.separator +Main.firstDirTimeSeriesPath(serie.getName())+ File.separator + Main.hashTimeSeriesName(serie.getName()) +File.separator +serie.getName()+ File.separator + Main.hashTimeSeriesName(serie.getName()) + "Log.txt");
    }



}
