# Kardam

Author: Georgios Damaskinos <georgios.damaskinos@epfl.ch>

Template code for [Asynchronous Byzantine Machine Learning (the case of SGD)](http://proceedings.mlr.press/v80/damaskinos18a.html) paper published in ICML 2018.

Deployment of Kardam on Android devices is possible via an [implementation on top of FLeet](https://github.com/gdamaskinos/fleet/blob/master/Server/src/main/java/utils/Kardam.java).

## Requirements

* Java 1.8
* Maven >= 3.6.1

## Basic usage

* compile: ```mvn clean install```
* run: ```mvn exec:java -Dexec.mainClass="example.RandomSGD"```


