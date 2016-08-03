package org.wso2.carbon.ml.siddhi.extension.streaming.samoa;

import com.github.javacliparser.ClassOption;
import com.github.javacliparser.FlagOption;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.Option;
import org.apache.samoa.moa.cluster.Clustering;
import org.apache.samoa.tasks.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.samoa.topology.impl.SimpleComponentFactory;
import org.apache.samoa.topology.impl.SimpleEngine;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by mahesh on 7/17/16.
 */
public class StreamingClusteringTaskBuilder {


    // TODO: clean up this class for helping ML Developer in SAMOA
    // TODO: clean up code from storm-impl

    // It seems that the 3 extra options are not used.
    // Probably should remove them
    private static final String SUPPRESS_STATUS_OUT_MSG = "Suppress the task status output. Normally it is sent to stderr.";
    private static final String SUPPRESS_RESULT_OUT_MSG = "Suppress the task result output. Normally it is sent to stdout.";
    private static final String STATUS_UPDATE_FREQ_MSG = "Wait time in milliseconds between status updates.";
    private static final Logger logger = LoggerFactory.getLogger(StreamingClusteringTaskBuilder.class);

    // public LinkedList<double[]>cepEvents;
    // public LinkedList<Clustering> samoaClusters;
    public ConcurrentLinkedQueue<double[]>cepEvents;
    public ConcurrentLinkedQueue<Clustering>samoaClusters ;
    public int numClusters=0;

    /**
     * The main method.
     *
     * @param args
     *          the arguments
     */

    public StreamingClusteringTaskBuilder(int numClusters, ConcurrentLinkedQueue<double[]> cepEvents, ConcurrentLinkedQueue<Clustering>samoaClusters){
        logger.info("StreamingClusteringTaskBuilder");
        this.numClusters = numClusters;
        this.cepEvents = cepEvents;
        this.samoaClusters = samoaClusters;

    }
    public static void main(String[] args) {
        logger.info("In Main");
        // ArrayList<String> tmpArgs = new ArrayList<String>(Arrays.asList(args));

        // args = tmpArgs.toArray(new String[0]);

        FlagOption suppressStatusOutOpt = new FlagOption("suppressStatusOut", 'S', SUPPRESS_STATUS_OUT_MSG);

        FlagOption suppressResultOutOpt = new FlagOption("suppressResultOut", 'R', SUPPRESS_RESULT_OUT_MSG);

        IntOption statusUpdateFreqOpt = new IntOption("statusUpdateFrequency", 'F', STATUS_UPDATE_FREQ_MSG, 1000, 0,
                Integer.MAX_VALUE);

        Option[] extraOptions = new Option[] { suppressStatusOutOpt, suppressResultOutOpt, statusUpdateFreqOpt };

        StringBuilder cliString = new StringBuilder();
        for (String arg : args) {
            logger.info(arg);
            cliString.append(" ").append(arg);
        }
        logger.debug("Command line string = {}", cliString.toString());
        System.out.println("Command line string = " + cliString.toString());


        Task task;
        try {
            task = ClassOption.cliStringToObject(cliString.toString(), Task.class, extraOptions);
            logger.info("Successfully instantiating {}", task.getClass().getCanonicalName());
        } catch (Exception e) {
            logger.error("Fail to initialize the task", e);
            System.out.println("Fail to initialize the task" + e);
            return;
        }

        task.setFactory(new SimpleComponentFactory());
        task.init();
        SimpleEngine.submitTopology(task.getTopology());
    }


    public void initTask(int numAttributes, int numClusters, int batchSize){
        String query="";
        query ="org.gsoc.samoa.streaming.clustering.MyClustering -i 100000 -s  (org.gsoc.samoa.streaming.streams.MyClusteringStream -K "+numClusters+" -a "+numAttributes+") -l (org.apache.samoa.learners.clusterers.simple.DistributedClusterer -l (org.apache.samoa.learners.clusterers.ClustreamClustererAdapter -l (org.apache.samoa.moa.clusterers.clustream.WithKmeans  -m 3 -k 3)))";
        logger.info("QUERY: "+query);
        String args[]={query};
        this.initClusteringTask(args);
    }

    public void initClusteringTask(String[] args) {
        logger.info("In Main");
        // ArrayList<String> tmpArgs = new ArrayList<String>(Arrays.asList(args));

        // args = tmpArgs.toArray(new String[0]);

        FlagOption suppressStatusOutOpt = new FlagOption("suppressStatusOut", 'S', SUPPRESS_STATUS_OUT_MSG);

        FlagOption suppressResultOutOpt = new FlagOption("suppressResultOut", 'R', SUPPRESS_RESULT_OUT_MSG);

        IntOption statusUpdateFreqOpt = new IntOption("statusUpdateFrequency", 'F', STATUS_UPDATE_FREQ_MSG, 1000, 0,
                Integer.MAX_VALUE);

        Option[] extraOptions = new Option[] { suppressStatusOutOpt, suppressResultOutOpt, statusUpdateFreqOpt };

        StringBuilder cliString = new StringBuilder();
        for (String arg : args) {
            logger.info(arg);
            cliString.append(" ").append(arg);
        }
        logger.debug("Command line string = {}", cliString.toString());
        System.out.println("Command line string = " + cliString.toString());


        Task task;
        try {
            task = ClassOption.cliStringToObject(cliString.toString(), Task.class, extraOptions);
            logger.info("Successfully instantiating {}", task.getClass().getCanonicalName());
        } catch (Exception e) {
            logger.error("Fail to initialize the task", e);
            System.out.println("Fail to initialize the task" + e);
            return;
        }
        logger.info("A");
        if(task instanceof StreamingClusteringTask){
            StreamingClusteringTask t = (StreamingClusteringTask) task;
            t.setCepEvents(this.cepEvents);
            t.setSamoaClusters(this.samoaClusters);
            t.setNumClusters(this.numClusters);

        }
        //logger.info("B");
        task.setFactory(new SimpleComponentFactory());
        //logger.info("C");
        task.init();
        //logger.info("D");
        SimpleEngine.submitTopology(task.getTopology());

        //logger.info("Simple ENgine Started");
    }





}
