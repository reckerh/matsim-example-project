package org.matsim.project;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.contrib.drt.util.DrtEventsReaders;
import org.matsim.contrib.dvrp.vrpagent.TaskStartedEvent;
import org.matsim.contrib.dvrp.vrpagent.TaskStartedEventHandler;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.testcases.MatsimTestUtils;
import scala.Int;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class RunDRTRebalancingTest {

    @Rule
    public MatsimTestUtils utils = new MatsimTestUtils();

    @Test
    public void main() {

        final String inputConfig = "scenarios/drt-rebalancing-test/test.with-drt.config.xml";

        try{
            String [] args = {
                    inputConfig,
                    "--config:controler.outputDirectory", utils.getOutputDirectory()
            };

            RunDRTRebalancing.main(args);

            {
                var manager = EventsUtils.createEventsManager();
                var handler = new RebalancingAnalysis();
                manager.addHandler(handler);

                MatsimEventsReader eventsReader = DrtEventsReaders.createEventsReader(manager);
                eventsReader.readFile(utils.getOutputDirectory() + "/output_events.xml.gz");

                Integer relocations = handler.getRelocationCounter();

                Assert.assertTrue(relocations == 6);
            }

        } catch ( Exception ee){Assert.fail();}



    }




}


class RebalancingAnalysis implements TaskStartedEventHandler {

    //create counter Integer
    private Integer relocationCounter = 0;

    //getter method
    public Integer getRelocationCounter(){return relocationCounter;}

    //override empty handler
    @Override
    public void handleEvent(TaskStartedEvent event){
        if(event.getTaskType().name().equals("RELOCATE") && event.getTime()==3600.0){
            relocationCounter += 1;
        }
    }

}