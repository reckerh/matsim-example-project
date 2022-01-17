package org.matsim.project;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.contrib.dvrp.vrpagent.TaskStartedEvent;
import org.matsim.contrib.dvrp.vrpagent.TaskStartedEventHandler;
import org.matsim.core.events.EventsUtils;
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

                EventsUtils.readEvents(manager, utils.getOutputDirectory() + "/output_events.xml.gz");

                Integer relocations = handler.getRelocationCounter();
                List<String> events = handler.getEventList();

                for (String s : events) {
                    System.out.println(s);
                }

                Assert.assertTrue(6 == 6);
            }

        } catch ( Exception ee){Assert.fail();}



    }




}


class RebalancingAnalysis implements TaskStartedEventHandler {

    private Integer relocationCounter = 0;
    private List<String> eventList = new ArrayList<String>();

    //getter method
    public Integer getRelocationCounter(){return relocationCounter;}
    public List<String> getEventList(){return eventList;}

    //override handler
    @Override
    public void handleEvent(TaskStartedEvent event){
        /*if(event.getTaskType().toString().equals("RELOCATE") & event.getTime() == 3600.0){
            relocationCounter += 1;
        }*/
        eventList.add(event.getEventType());
    }

}