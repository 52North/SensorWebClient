====
    Copyright (C) ${inceptionYear}-${latestYearOfContribution} 52°North Initiative for Geospatial Open Source
    Software GmbH

    This program is free software; you can redistribute it and/or modify it under
    the terms of the GNU General Public License version 2 as publishedby the Free
    Software Foundation.

    If the program is linked with libraries which are licensed under one of the
    following licenses, the combination of the program with the linked library is
    not considered a "derivative work" of the program:

        - Apache License, version 2.0
        - Apache Software License, version 1.0
        - GNU Lesser General Public License, version 3
        - Mozilla Public License, versions 1.0, 1.1 and 2.0
        - Common Development and Distribution License (CDDL), version 1.0

    Therefore the distribution of the program linked with libraries licensed under
    the aforementioned licenses, is permitted by the copyright holders if the
    distribution is compliant with both the GNU General Public License version 2
    and the aforementioned licenses.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
    PARTICULAR PURPOSE. See the GNU General Public License for more details.
====

How to get Sensor Web Client running/debugging as Web Project under eclipse:

1) In Eclipse, open your project's properties (Alt+Enter or right-click, Properties)
2) Under Google | Web Toolkit, likewise select the version of the GWT SDK you're using. 
   As with the App Engine SDK, install it via the Eclipse plugin update site if needed.
3) Under Google | Web Application, check the "This project has a WAR directory" box and 
   point it to your project's src/main/webapp directory. This is the standard WAR source 
   folder for Maven Web projects. Be sure that the "Launch and deploy from this directory" 
   box is NOT checked.
4) Under Java Build Path, select the Order and Export tab and move all Maven dependencies 
   to the BOTTOM. Otherwise, GPE will see the App Engine and GWT SDKs from the Maven repo 
   on the build path and complain that they are not valid. This is because GPE expects a 
   specific SDK structure used to enable other tooling.
5) Also under Java Build Path, select the Source tab and ensure that the Build output 
   directory is enabled and pointing to target/your-project-name/WEB-INF/classes. If you 
   created the project with mvn eclipse:eclipse, this should be done for you automatically.
6) Finally, and this is very important, the first time you launch your project using 
   Run As | Web Application (or Debug), you will be prompted to select the war directly. 
   This is NOT src/main/webapp, but rather the WAR output folder, which is 
   target/your-project-name. If you make a mistake, simply go to Run | Run Configurations... 
   and remove any old configurations for the project. GPE will then ask you again next time 
   you try to Run As | Web Application.
