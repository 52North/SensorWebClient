This is the contribute.md of 52°North project [SensorWebClient](https://github.com/52North/SensorWebClient). Great to have you here! Here are a few ways you can help!

# Contribute.md

## Learn & listen
Best way to get started is to start with these:

* Mailing list: <http://52north.org/resources/mailing-list-and-forums/>
* IRC channel:  #52north on irc.freenode.net
* Website:  <http://52north.org/>
* Get involved in 52°North: <http://52north.org/about/get-involved/>

## Team Members
* Henning Bredel [@ridoo](https://github.com/ridoo)
* Jan Schulte, [@janschulte](http://github.com/janschulte)
* You?

## Contributor License Agreement

52°North requires all contributors to sign a contributors license agreement (CLA). This is not scary but essential for open source projects to live beyond projects and individual contributions.

Find all information on our licensing page at <http://52north.org/about/licensing/> and our CLA FAQ at <http://52north.org/about/licensing/cla-guidelines>.

## Adding new features

This section includes advice on how to build new features for the project & what kind of process it includes. 

* Create an issue if you want to discuss a certain issue (bug, missing feature, questions, etc.)
* Fork the repository and do pull requests if you added a feature or fixed a bug (once you have signed a CLA we are allowed to merge your code and add you as contributor)
* If you send in pull requests, please describe in detail what you are providing with the code
* Do unit testing of your code, do not hesitate to add unit tests to existing code (breaking dependencies would have to be discussed further)
* Find some code style settings exported from ecplipse in the `misc` folder

Don’t get discouraged! We estimate that the response time from the
maintainers is around: 2-4 working days

# Issue Tracker

https://github.com/52North/SensorWebClient/issues



# Translations

The SensorWebClient is able to switch languages. The [translations available](https://github.com/52North/SensorWebClient/tree/master/sensorwebclient-ui/src/main/resources/org/n52/client) can be found as resources within the user interface module. Add your translations as `messages_<your-language-code>.properties` and [tell the GWT module](https://github.com/52North/SensorWebClient/blob/3666ee1aa9fbd0ebc1801f58afb3848b3740d27a/sensorwebclient-ui/src/main/resources/org/n52/SensorWebClient.gwt.xml#L12-L13) that a further language is available.


# Documentation

https://wiki.52north.org/bin/view/SensorWeb/SensorWebClient


# Community 
This section includes ideas on how non-developers can help with the project. Here's a few examples:

* You can help us answer questions our users have [on the Sensor Web mailinglist](http://list.52north.org/mailman/listinfo/swe) or [Sensor Web forum](http://52north.org/resources/mailing-list-and-forums/)
* You can help write blog posts about the project
* Create an example of the project in real world by building something or
showing what others have built. 
* Write about other people’s projects based on this project. Show how
it’s used in daily life. Take screenshots and make videos! Share what you have done; we are interested!

# Still Puzzled?

* If you have further questions, contact: [Henning Bredel](mailto:h.bredel@52north.org) | [@ridoo](https://github.com/ridoo)
