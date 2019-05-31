#!/usr/bin/env bash

rm -fr repository/*
sbt clean compile test publish
./jfrog bt u /Users/evg/work/pbson/repository/ twistedlogic/pbson/pbson/v0.0.12 ru/twistedlogic/pbson_2.12/0.0.12/