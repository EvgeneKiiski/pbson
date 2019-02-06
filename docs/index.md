
[![Build status](https://img.shields.io/circleci/project/github/EvgeneKiiski/pbson/master.svg?style=flat)](https://circleci.com/gh/EvgeneKiiski/pbson/tree/master)
[![Coverage Status](https://coveralls.io/repos/github/EvgeneKiiski/pbson/badge.svg?branch=master)](https://coveralls.io/github/EvgeneKiiski/pbson?branch=master)

# PBson - Pure BSON

pbson is a BSON library for Scala.

The goal of this library is to create at compile-time the boilerplate necessary to encode and decode of a certain type.
The pbson provides generic codec derivation using [Shapeless](https://github.com/milessabin/shapeless).    

pbson can derive bson encoder and decoder:

``` BsonEncoder[T] : T => BsonValue ```

``` BsonDecoder[T] : BsonValue => Either[BsonError, T]``

<a name="quick-start"></a>

{% include_relative quickstart.md %}