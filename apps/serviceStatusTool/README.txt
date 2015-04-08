
DATASOURCE CONFIGURATION

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

Copy file ojdbc6.jar from [project_source]/lib/api and place it under [Jboss Home]/standalone/deployments

=============================================================================================================================================