#!/bin/bash

cd $1

files=`find . -maxdepth 1 -type f ! -name "*yaml*" ! -name "*~" | grep -v /[.]`

for i in $files
do
    cp $i $i-yaml
    sed -i "n;n;d;" $i-yaml
    sed -i "$!N;s_\n_: _g" $i-yaml
    sed -i "s_--- # __g" $i-yaml
    sed -i "s_\s_,_g" $i-yaml
    sed -i "s_:,_: _g" $i-yaml
    sed -i "s_,_, _g" $i-yaml
    sed -i "s_Corners, __g" $i-yaml
done