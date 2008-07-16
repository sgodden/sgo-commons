To execute the flow test app, something like:

mvn -e clean compile exec:java -Dswing.defaultlaf=com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel -Dgroovy.script.url=file:///home/sgodden/workspace/sgo-commons/src/main/groovy/ -Dexec.mainClass=org.sgodden.ui.mvc.swing.testapp.Main

This assumes you are running on jdk6-u10, and wish to use the nimbus look and feel.
