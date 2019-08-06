#!/usr/bin/env bash

rm -fr repository/*
sbt clean compile test publish
./jfrog bt u /Users/evg/work/pbson/repository/ twistedlogic/pbson/pbson/v0.0.14-rc1 ru/twistedlogic/pbson_2.12/0.0.14-rc1/
#./jfrog bt u /Users/evg/work/pbson/repository/ twistedlogic/pbson/pbson/v0.0.13 ru/twistedlogic/pbson_2.13/0.0.13/