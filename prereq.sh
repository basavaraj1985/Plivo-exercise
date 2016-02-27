#!/usr/bin/env bash
git clone git@github.com:basavaraj1985/MockServer.git
cd MockServer
git fetch --all
git checkout feature/plivo
mvn clean install