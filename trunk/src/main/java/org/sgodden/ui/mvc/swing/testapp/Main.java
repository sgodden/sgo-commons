/* =================================================================
 # This library is free software; you can redistribute it and/or
 # modify it under the terms of the GNU Lesser General Public
 # License as published by the Free Software Foundation; either
 # version 2.1 of the License, or (at your option) any later version.
 #
 # This library is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 # Lesser General Public License for more details.
 #
 # You should have received a copy of the GNU Lesser General Public
 # License along with this library; if not, write to the Free Software
 # Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 #
 # ================================================================= */
package org.sgodden.ui.mvc.swing.testapp;


import groovy.util.GroovyScriptEngine;
import java.net.URL;
import javax.swing.JFrame;

import org.sgodden.ui.mvc.Flow;
import org.sgodden.ui.mvc.FlowFactory;
import org.sgodden.ui.mvc.FrontController;
import org.sgodden.ui.mvc.TitledContainer;
import org.sgodden.ui.mvc.impl.FlowImpl;
import org.sgodden.ui.mvc.swing.ContainerImpl;

/**
 * Provides a simple swing test app for the MVC framework.
 * 
 * @author sgodden
 *
 */
public class Main {

    private static GroovyScriptEngine groovyScriptEngine;

    static {
        try {
            groovyScriptEngine = new GroovyScriptEngine(
                    new URL[]{new URL(System.getProperty("groovy.script.url"))});
        } catch (Exception e) {
            throw new Error("Error initialsing groovy script engine", e);
        }
    }
	
	public static void main(String[] args) {
		
		FlowImpl flow = (FlowImpl)
                getGroovyObjectInstance("org.sgodden.ui.mvc.swing.testapp.flow1.Flow1");
        flow.setFlowFactory(new FlowFactoryImpl());
	
		ContainerImpl container = new ContainerImpl();
		FrontController front = new FrontController(container, flow);
		
		TitledJFrame frame = new TitledJFrame();
        front.setTitledContainer(frame);

		frame.getContentPane().add(container);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setVisible(true);
		
	}

    /**
     * Returns a new instance of the class defined in the specified
     * groovy script.
     * @param scriptName the groovy script name.
     * @return a new instance of the class defined in the script.
     */
    private static Object getGroovyObjectInstance(String scriptName) {
        Object ret = null;
        try {
            Class clazz = groovyScriptEngine.loadScriptByName(scriptName);
            ret = clazz.newInstance();
        } catch (Exception e) {
            throw new Error("Error creating groovy object instance for" +
                    " script name: " + scriptName, e);
        }

        return ret;
    }

    private static class FlowFactoryImpl implements FlowFactory {

        public Flow makeFlow(String flowName) {
            if (flowName.equals("flow2")) {
                return (Flow) getGroovyObjectInstance(
                        "org.sgodden.ui.mvc.swing.testapp.flow2.Flow2");
            }
            else {
                throw new IllegalArgumentException("Unknown flow: " + flowName);
            }
        }

    }

    private static class TitledJFrame extends JFrame
            implements TitledContainer {
    }

}
