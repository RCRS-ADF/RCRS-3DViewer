#!/bin/sh

cd `dirname $0`

echo "viewer.jarsdir `pwd`/lib/rescuecore2" > viewer3d.cfg 
echo "viewer.configdir `pwd`/bin/data/config" >> viewer3d.cfg 

