#!/bin/bash 

###################################################################
# Script Name   :
# Description   :
# Args          : 
# Author       	: Michel Héon PhD
# Institution   : Université du Québec à Montréal
# Copyright     : Université du Québec à Montréal (c) 2022
# Email         : heon.michel@uqam.ca
###################################################################
export SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd -P)"
source $SCRIPT_DIR/../../../releng/org.vivoweb.dspacevivo.installer/00-env.sh
export PATH=$LOC_SCRIPT_DIR:$PATH
export INSTALLER_HOME=$RELENG/org.vivoweb.dspacevivo.installer
export VOCAB_HOME=$BUNDLES/org.vivoweb.dspacevivo.model.ontologie
export METAMODEL_HOME=$BUNDLES/org.vivoweb.dspacevivo.model.openapi
export TRANSFORM_HOME=$BUNDLES/org.vivoweb.dspacevivo.transformation
export DSPACEVIVO_HOME=$BUNDLES/org.vivoweb.dspacevivo.vivo

export PRJ_CP=$DSPACEVIVO_PKG_HOME/target/classes:$VOCAB_HOME/target/classes:$METAMODEL_HOME/target/classes:$TRANSFORM_HOME/target/classes:$LIB/*
#export TRANSFORM_ITEM=$DSPACEVIVO_PKG_HOME/target/org.vivoweb.dspacevivo-0.0.1-SNAPSHOT.jar
export JVM_ARGS=""



