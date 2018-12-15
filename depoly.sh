#!/bin/sh

CWD=`pwd`

cd ../synthetic/docs
make html

cd $CWD
cp -r ../synthetic/docs/build/html/* .

git add *
git commit -am "automatic deploy"
git push
