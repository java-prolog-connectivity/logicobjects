#!/bin/bash

DEPLOY_URL='file://repository'
echo Deploying to $DEPLOY_URL

#jpl
mvn deploy:deploy-file -Dfile=jpl-3.1.4-alpha.jar -DpomFile=jpl-3.1.4-alpha.pom -Durl=$DEPLOY_URL

#guava-reflection
mvn deploy:deploy-file -Dfile=guava-reflection-0.1.3-SNAPSHOT.jar -DpomFile=guava-reflection-0.1.3-SNAPSHOT.pom -Durl=$DEPLOY_URL
