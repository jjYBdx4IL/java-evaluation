package tests.java.lang;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class DowngradeViaCast {
    class Basic {
        public String prop;
    }
    class BasicUpgrade extends Basic {
        public String upgradedProp;
    }
    
    @Test
    public void testSimpleCastAndListCast() {
        // Downgrade of basic classes is ok:
        BasicUpgrade basicUpgrade = new BasicUpgrade();
        Basic basic = (Basic) basicUpgrade;
        basic.prop = "123";
        basicUpgrade.upgradedProp = "456";

        List<BasicUpgrade> listOfBasicUpgrades = new ArrayList<BasicUpgrade>();
        listOfBasicUpgrades.add(basicUpgrade);
                
        // simple cast does not even compile: (List<Basic>) listOfBasicUpgrades
        
        // let's try something different:
        List<? extends Basic> listOfBasics = listOfBasicUpgrades;
        assertEquals("123", listOfBasics.get(0).prop);
        assertEquals("456", ((BasicUpgrade)listOfBasics.get(0)).upgradedProp);
        
        // Hooray!
    }
    
}
