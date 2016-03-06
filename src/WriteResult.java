import java.io.IOException;

/**
 * Created by marco on 03/02/16.
 */
public interface WriteResult
{

     void writeLog() throws IOException;
     void writeWindowResult(TimeSeriesValueWindow timeSeries, double lastTimeWindow, double lastValueWindow) throws IOException;
     void connect();
     void disconnect();


}
