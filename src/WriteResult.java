import java.io.IOException;

/**
 * Created by marco on 03/02/16.
 */
public interface WriteResult
{

    public void write() throws IOException;
    public void connect();
    public void disconnect();


}