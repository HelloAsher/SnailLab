package LOG4J;

import org.apache.log4j.Logger;

/**
 * Created by Asher on 2016/12/1.
 */
public class Log4jTest{
    public static Logger logger = Logger.getLogger(Log4jTest.class);
    public static void main(String[] args) throws Exception{

        while(true){
            //Thread.sleep(5000);
            logger.debug("this is debug message!");
            logger.warn("this is warn message!");
            logger.info("this is info message!");
            logger.error("this is error message!");
        }

    }
}
