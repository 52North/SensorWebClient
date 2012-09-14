Old/outdated dependencies are not deployed in 52n Maven repositories. Due to a current 
refactoring phase it is not intended to do this (yet). However, these dependencies lay 
in a project related file repository which will be installed in your local Maven
repository during project's install phase.

The Sensor Web Client project relies heavily on the 52n-oxf framework which also stands 
before a refactoring phase. To install its old/outdated dependencies, you have to check 
it out and install the project (with its dependencies) manually.

0) Checkout + mvn install
  https://svn.52north.org/svn/swe/incubation/ox-framework-2.0/52n-oxf/trunk/

1) Checkout + mvn install
  https://svn.52north.org/svn/swe/main/Clients/SensorWebClient/trunk/
  

Optionally:
n) XML Bindings (available from 52n Maven repository)
  https://svn.52north.org/svn/swe/incubation/ox-framework-2.0/52n-common-xml/trunk/