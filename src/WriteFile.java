/**
 * Created by marco on 03/02/16.
 */
import java.io.*;
import java.util.Map;

public class WriteFile implements WriteResult
{

    private BufferedWriter bwLog;
    private BufferedWriter bwResult;
    private BufferedReader readList;
    private TimeSeriesValueWindow serie;
    private File fileLog;
    private File fileResult;
    private int collisionNumber;
    private int hash;
    private String firstDir;
    public WriteFile(TimeSeriesValueWindow serie) throws IOException  // crea la cartella con il file contenente tutti i valori delle variabili locali
    {

        hash=Main.hashTimeSeriesName(serie.getName());
        collisionNumber=0;  // collision number, i'll set only if i discover list file
        this.serie=serie;
        firstDir= Main.firstDirTimeSeriesPath(serie.getName());
        boolean foundCollision=false;
        /*
        * Now i'm going to check in hash dir the list of collision to detect weather collision occurs
        * */
        File listFile=new File(Main.path+File.separator+firstDir+File.separator+hash+File.separator+"CollisionList.txt");
        BufferedWriter listWriter;
        if(listFile.exists())   // if list file doesn't exist i'm going to create it
        {
            try
            {
                readList=new BufferedReader(new FileReader(listFile));
            } catch (FileNotFoundException e)
            {
                System.out.println(e.getMessage());
                System.exit(1);
            }
            String line;
            while((line=readList.readLine())!=null)
            {
                String token[]=line.split("\t");
                collisionNumber=Integer.parseInt(token[1]);
                if(token[0].equals(serie.getName()))
                {
                    foundCollision=true;
                    break;
                }

            }
        }
        // else i've to create listFile
        else
        {
            listFile.getParentFile().mkdirs();
            listFile.createNewFile();
        }
        if(foundCollision==false)
        {
            collisionNumber++;
            listWriter = new BufferedWriter(new FileWriter(listFile, true));
            listWriter.append(serie.getName() + "\t" + collisionNumber);
        }

        // here i can create subdirectory and put my time series file
        // the same path of listFile+collisionNumber
        fileLog=new File(Main.path+File.separator +firstDir+File.separator+hash+File.separator+collisionNumber+File.separator+hash+"Log.txt");
        if(fileLog.exists())
        {
            this.loadFromFile(fileLog,serie.getName(),serie);
            fileLog.delete();
        }
        else
            fileLog.mkdirs();

        fileLog.createNewFile();
        // now i set result file
        fileResult = new File(Main.path + File.separator + firstDir + File.separator + hash + File.separator + collisionNumber + File.separator + "Result.txt");
        if (!fileResult.exists())
        {
            fileResult.createNewFile();
        }
        this.connect();



    }
    public void writeWindowResult(TimeSeriesValueWindow trendToWrite,double lastTime, double lastRelevance)throws IOException
    {
        String graphics = "------------";
        bwResult.append(graphics + "Computo Trend Della TimeSeries " + trendToWrite.getName() + " Finestra numero " + trendToWrite.getWindowNumber() + graphics);
        bwResult.newLine();
        for (Map.Entry<Double, Double> entry : trendToWrite.getWindowList().entrySet())
        {
            bwResult.append("Time : " + entry.getKey() + " Relevance " + entry.getValue());
            bwResult.newLine();
        }
        bwResult.append("Time : " + lastTime + " Relevance " + lastRelevance);
        bwResult.newLine();
        bwResult.append("Media pesata " + (trendToWrite.getSumOfDecay() / trendToWrite.getSumOfWeight()) + " sum of decay " + trendToWrite.getSumOfDecay() + " sum of weight : " + trendToWrite.getSumOfWeight());
        bwResult.newLine();
        bwResult.append(graphics + " Fine Finestra TimeSeries " + trendToWrite.getName() + graphics);
        bwResult.newLine();

    }
    public void writeLog() throws IOException {
        // scrivi tutti i campi
        System.out.println("Time series nel metodo write "+serie.getName());
        bwLog.write("name:\t"+serie.getName()+"\n");
        bwLog.write("sum of decay:\t"+serie.getSumOfDecay()+"\n");
        bwLog.write("sum of weight:\t"+serie.getSumOfWeight()+"\n");
        bwLog.write("current relevance:\t"+serie.getCurrRelevance()+"\n");
        bwLog.write("current time:\t"+serie.getCurrTime()+"\n");
        bwLog.write("current trend:\t"+serie.getCurrTrend()+"\n");
        bwLog.write("window number:\t"+serie.getWindowNumber()+"\n");
        bwLog.write("window count element:\t"+serie.getWindowCountElement()+"\n");
        bwLog.write("-Window List-\n");
        for(Map.Entry<Double,Double> entry : serie.getWindowList().entrySet())
        {
            bwLog.write("time:\t"+entry.getKey()+"\tvalue:\t"+entry.getValue()+"\n");

        }


    }
    public void connect()
    {
        try
        {
            bwLog=new BufferedWriter(new FileWriter(fileLog.getAbsoluteFile()));
            bwResult=new BufferedWriter(new FileWriter(fileResult));
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
            bwLog.close();
            bwResult.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    public void loadFromFile(File existingFile,String name,TimeSeriesValueWindow seriesToUpdate)
    {

        this.fileLog=existingFile;
        String line="";
        boolean startList=false;
        BufferedReader readFile = null;
        try
        {
            readFile = new BufferedReader(new FileReader(fileLog.getAbsoluteFile()));
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
        fileLog=new File(Main.path +File.separator +firstDir+ File.separator + hash +File.separator +collisionNumber+ File.separator + hash + "Log.txt");
    }



}
