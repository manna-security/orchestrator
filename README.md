# orchestrator
This project houses the code that orchestrates the actual scanning pipeline. 
It integrates with kafka as the message bus, and uses utilities from the `common` project.

In order to build this project successfully, you must first install `common` using the instructions on that readme. 
Then, to install this project, just use `mvn clean install`.
