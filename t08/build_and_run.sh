#!/bin/bash
javac -classpath jFuzzyLogic.jar:../pck_busca:. sistema/*.java && java -classpath jFuzzyLogic.jar:../pck_busca:. sistema.Main
