
DATASOURCE CONFIGURATION

First way:

Edit the standalone.xml file in the configuration folder.

Add the following under <datasources> ,  replacing <HOST>:<PORT>:<SID> and [PASSWORD] according to your environment:

                <datasource jta="false" jndi-name="java:/jdbc/amn" pool-name="AMN" enabled="true" use-ccm="false">
                    <connection-url>jdbc:oracle:thin:@<HOST>:<PORT>:<SID></connection-url>
                    <driver-class>oracle.jdbc.OracleDriver</driver-class>
                    <driver>ojdbc6.jar</driver>
                    <security>
                        <user-name>AMN</user-name>
                        <password>[PASSWORD]</password>
                    </security>
                    <validation>
                        <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.oracle.OracleValidConnectionChecker"/>
                        <validate-on-match>false</validate-on-match>
                        <background-validation>false</background-validation>
                        <stale-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.oracle.OracleStaleConnectionChecker"/>
                        <exception-sorter class-name="org.jboss.jca.adapters.jdbc.extensions.oracle.OracleExceptionSorter"/>
                    </validation>
                    <statement>
                        <share-prepared-statements>false</share-prepared-statements>
                    </statement>
                </datasource>

Copy file ojdbc6.jar to [Jboss Home]/standalone/deployments


Second way:

Edit the standalone.xml file in the configuration folder.

Create a file path structure under the JBOSS_HOME/modules/ directory like JBOSS_HOME/modules/com/oracle/main.
Copy file ojdbc6.jar to JBOSS_HOME/modules/com/oracle/main
In the main/ subdirectory, create a module.xml file like to the example below:

					<?xml version="1.0" encoding="UTF-8"?>
					<module xmlns="urn:jboss:module:1.0" name="com.oracle">
					  <resources>
					    <resource-root path="ojdbc6.jar"/>
					  </resources>
					  <dependencies>
					    <module name="javax.api"/>
					    <module name="javax.transaction.api"/>
					  </dependencies>
					</module>

Start the Server.
Start the Management CLI. (JBOSS_HOME/bin/jboss-cli.sh).
Connect to a managed JBoss AS 7 instance ([disconnected /] connect).
Run the following CLI command to add the JDBC driver module as a driver:
/subsystem=datasources/jdbc-driver=oracle:add(driver-name=oracle,driver-module-name=com.oracle,driver-xa-datasource-class-name=oracle.jdbc.xa.client.OracleXADataSource)

Add the following under <datasources> ,  replacing <HOST>:<PORT>:<SID> and [PASSWORD] according to your environment:

					<datasource jta="false" jndi-name="java:/jdbc/amn" pool-name="AMN" enabled="true" use-ccm="false">
					    <connection-url>jdbc:oracle:thin:@<HOST>:<PORT>:<SID></connection-url>
					    <security>
					        <user-name>AMN</user-name>
					        <password>[PASSWORD]</password>
					    </security>
					    <validation>
					        <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.oracle.OracleValidConnectionChecker"/>
					        <validate-on-match>false</validate-on-match>
					        <background-validation>false</background-validation>
					        <stale-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.oracle.OracleStaleConnectionChecker"/>
					        <exception-sorter class-name="org.jboss.jca.adapters.jdbc.extensions.oracle.OracleExceptionSorter"/>
					    </validation>
					    <statement>
					        <share-prepared-statements>false</share-prepared-statements>
					    </statement>
					</datasource>

=============================================================================================================================================