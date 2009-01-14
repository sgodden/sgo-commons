package org.sgodden.ui.mvc.config;

import org.testng.annotations.Test;

@Test
public class FlowStepTest {
    
    public void testNullTransitions() {
        FlowStep step = new FlowStep();
        // this used to cause a NPE
        step.getAvailableResolutions();
    }

}
