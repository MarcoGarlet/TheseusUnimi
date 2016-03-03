/**
 * Created by Utente on 19/01/2016.
 */
public interface ReadStream
{
    public boolean readLine();    //  get next item
    public void setSource(String newSource);    //  set stream source
    public void closeStream();

}
