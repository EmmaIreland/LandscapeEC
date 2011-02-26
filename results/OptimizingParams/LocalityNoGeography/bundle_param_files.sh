#!/bin/bash

dir="LnoG_$1"

mkdir $dir
mv *__*.properties $dir
tar -zcvf $dir.tgz $dir
scp $dir.tgz xps1.essex.ac.uk:/tmp
