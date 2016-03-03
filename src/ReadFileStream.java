import javax.swing.*;
    import javax.swing.filechooser.FileFilter;
    import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Utente on 19/01/2016.
 */
public class ReadFileStream implements ReadStream
{
    private String currLine;
    private String separator;
    private String currTimeSeriesName;
    private double currTime;
    private double currValue;
    private BufferedReader fileRead;
    public ReadFileStream(String separator)
    {

        JFileChooser fileopen = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("txt files", "txt");
        fileopen.addChoosableFileFilter(filter);
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION)
        {
            File file = fileopen.getSelectedFile();
            try
            {
                fileRead = new BufferedReader(new FileReader(file));
            }catch(IOException | NullPointerException Err)
            {
                System.out.println(Err.getMessage());
                System.exit(1);
            }
        }

        this.separator=separator;

    }

    /**
     *
     * @return
     */
    public boolean readLine()
    {

        try
        {
             currLine=fileRead.readLine();
        } catch (IOException | NullPointerException e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return currLine!=null;
    }

    /**
     *
     * @param newSource
     */
    public void setSource(String newSource)
    {
        this.closeStream();
        try
        {
            fileRead = new BufferedReader(new FileReader(newSource));
        }
        catch(IOException Err)
        {
            System.out.println(Err.getMessage());
            System.exit(1);
        }


    }
    public void closeStream()
    {
        try
        {
            fileRead.close();
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    public String getCurrLine()
    {
        return this.currLine;
    }
    public void setSeparator(String separator)
    {
        this.separator=separator;
    }
    public String getSeparator()
    {
        return this.separator;
    }

    /**
     *
     * @return
     */
    public boolean splitLine()
    {
        if(this.currLine.equals(""))
            return false;
        String parts[]= getCurrLine().split(this.getSeparator());

        if(parts.length==3)         // i'll throw away all input format with strange length after split input line
        {
            this.currTimeSeriesName = parts[0];      // here we are sure that the length of line format is right, but we have to test only the number format of object
            try
            {
                currValue = Double.parseDouble(parts[1]);
                currTime = Double.parseDouble(parts[2]);
                return true;
            } catch (NumberFormatException error)
            {
                return false;
            }
        }
        else
            return false;
    }


    public String getCurrTimeSeriesName()
    {
        return currTimeSeriesName;
    }

    public void setCurrTimeSeriesName(String currTimeSeriesName)
    {
        this.currTimeSeriesName = currTimeSeriesName;
    }

    public double getCurrTime()
    {
        return currTime;
    }

    public void setCurrTime(double currTime)
    {
        this.currTime = currTime;
    }

    public double getCurrValue()
    {
        return currValue;
    }

    public void setCurrValue(double currValue)
    {
        this.currValue = currValue;
    }
}
