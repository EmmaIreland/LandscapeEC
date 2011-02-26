#!/bin/bash

dir=$1

echo "$dir.R.gz"

scp xps1.essex.ac.uk:/tmp/$dir.R.gz .
gunzip $dir.R.gz
ruby EDA.rb $dir.R

