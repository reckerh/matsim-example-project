package org.matsim.project;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.drt.optimizer.insertion.DrtInsertionSearchParams;
import org.matsim.contrib.drt.optimizer.insertion.ExtensiveInsertionSearchParams;
import org.matsim.contrib.drt.routing.DrtRoute;
import org.matsim.contrib.drt.routing.DrtRouteFactory;
import org.matsim.contrib.drt.run.*;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ModeParams;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup.SnapshotStyle;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultSelector;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultStrategy;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

public class RunDRTRebalancing{


    public static void main( String[] args ){

        //create config
        Config config;


        //load Config from args / load Config from given directory
        if ( args!=null && args.length>=1 ) {
            config = ConfigUtils.loadConfig( args );
        } else {
            config = ConfigUtils.loadConfig( "scenarios/drt-rebalancing-test/test.with-drt.config.xml" );
        }
        config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );


        //add / adjust MultiModeDrt config and Dvrp Config
        MultiModeDrtConfigGroup multiModeDrtConfig = ConfigUtils.addOrGetModule(config, MultiModeDrtConfigGroup.class);
        ConfigUtils.addOrGetModule(config, DvrpConfigGroup.class);
        DrtConfigs.adjustMultiModeDrtConfig(multiModeDrtConfig, config.planCalcScore(), config.plansCalcRoute());


        // create scenario, set route factory to Drt factory, load the scenario
        Scenario scenario = ScenarioUtils.createScenario( config ) ;
        scenario.getPopulation().getFactory().getRouteFactories().setRouteFactory( DrtRoute.class, new DrtRouteFactory() );
        ScenarioUtils.loadScenario( scenario );


        //create the controler
        Controler controler = new Controler( scenario ) ;


        //add Drvrp and MultiModeDrtModules to controler; activate DRT mode in QSim
        controler.addOverridingModule( new DvrpModule() ) ;
        controler.addOverridingModule( new MultiModeDrtModule( ) ) ;
        controler.configureQSimComponents(DvrpQSimComponents.activateAllModes(multiModeDrtConfig));


        controler.run() ;
    }

}
