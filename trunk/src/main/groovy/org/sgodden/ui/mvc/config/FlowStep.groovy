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
package org.sgodden.ui.mvc.config

/**
 * A step in a flow.
 * @author sgodden
 */
class FlowStep {
     
     Set < String > availableResolutions = new HashSet < String >()
     String controllerMethod
     String controllerName
     String description
     String endState
     String[] entryControllerMethods
     String[] exitControllerMethods
     String name
     Map < String, String > subFlowInputParameters
     String subFlowName
     Map < String, String > subFlowOutputParameters
     Transition[] transitions
     Map < String, Set< Transition > > transitionsByName
     String viewName
     
     public void setTransitions(Transition[] transitions) {
         this.transitions = transitions
         transitionsByName = new HashMap < String, Set < Transition > >()
         for (t in transitions) {
             if ( t.on == null ) {
                 throw new IllegalArgumentException("The 'on' attribute of a transition must be specified")
             }
             availableResolutions.add(t.on)
             Set< Transition > sub
             if (transitionsByName.get(t.on) == null) {
                 sub = new HashSet < Transition >()
                 transitionsByName.put(t.on, sub)
             }
             else {
                 sub = transitionsByName.get(t.on)
             }
             sub.add(t)
         }
     }
     
     public Set < Transition > getTransitions(String on) {
         if (transitionsByName.get(on) != null) {
             return Collections.unmodifiableSet(transitionsByName.get(on))
         }
         else {
             return null
         }
     }
     
     public Set < String > getAvailableResolutions() {
         return Collections.unmodifiableSet(availableResolutions)
     }

}