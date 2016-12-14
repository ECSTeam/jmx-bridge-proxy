# PCF JMX Bridge REST Proxy

This Spring Boot application uses [Jolokia](http://jolokia.org) to present a REST
interface to the Pivotal Cloud Foundry JMX Bridge, and obfuscates the need to
add target information to every request.

## Building

`mvn clean package`

## Running in Pivotal Cloud Foundry

Get the necessary of the JMX Provider VM by

1. Log into PCF Ops Manager
1. Click on the `JMX Bridge` tile
1. Click on the Status tab
1. Get the IP Address from the `JMX Provider` row
1. Click on the Credentials tab
1. Under `JMX Provider`, click the `Link to Credentials` for `Credentials` and get the username and password.

```
cf push jmx-bridge-proxy -p target/jmx-bridge-proxy-0.0.1-SNAPSHOT.jar --no-start
cf set-env jmx-bridge-proxy JMX_BRIDGE_HOST <JMX Provider IP>
cf set-env jmx-bridge-proxy JMX_BRIDGE_USER <JMX User>
cf set-env jmx-bridge-proxy JMX_BRIDGE_PASSWORD <JMX Password>
cf start jmx-bridge-proxy
```
