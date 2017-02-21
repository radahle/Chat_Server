package Core;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by Shohaib on 27.01.2017.
 */
public class FileHandler {

    /**
     * This method will Save Entry log from server that is related to client entry
     * @param text Accept text it needs to save. In this case it will be Entry log,
     *        so it will ask for ListView entry log
     */
    public static void saveEntryLog(String text){

        try {
            PrintWriter printWriter = new PrintWriter("ClientEntryLog.txt");
            printWriter.write(text);
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will Save Server chat log from server.
     * @param text Accept text it needs to save. In this case it will be Server chat log,
     *        so it will ask for ListView server log
     */
    public static void saveServerLog(String text){

        try {
            PrintWriter printWriter = new PrintWriter("ServerLog.txt");
            printWriter.write(text);
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
