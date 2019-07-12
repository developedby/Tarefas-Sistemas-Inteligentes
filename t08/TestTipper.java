package tipperusingapi;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

import net.sourceforge.jFuzzyLogic.rule.Variable;



/**
 *
 * @author tacla
 */
public class TestTipper {
    public static void main(String[] args) throws Exception {
        // Load from 'FCL' file
        String fileName = "./tipper.fcl";
        FIS fis = FIS.load(fileName,true);

        // Error while loading?
        if( fis == null ) { 
            System.err.println("Can't load file: '" + fileName + "'");
            return;
        }

        // Show 
        JFuzzyChart.get().chart(fis);

        // Set inputs
        fis.setVariable("service", 5);
        fis.setVariable("food", 7.5);

        // Evaluate
        fis.evaluate();

        // Show output variable's chart
        Variable tip = fis.getVariable("tip");
        
        JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);

        // Print ruleSet
        System.out.println(fis);
        
        // print membership degree for output terms
        System.out.println("cheap="+tip.getMembership("cheap"));
        System.out.println("average="+tip.getMembership("average"));
        System.out.println("generous="+tip.getMembership("generous"));
    }
}
