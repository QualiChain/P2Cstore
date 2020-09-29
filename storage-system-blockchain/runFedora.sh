#!/bin/bash

#cd ~/Desktop/thesis/blockchain-storage-system/bss-client;
cd bss-client/

if [ "$1" == "--bssNode" ]; then
  if [ "$#" == 4 ]; then
          mvn exec:java -Djava.net.preferIPv4Stack=true -Dexec.args="$1 $2 $3 $4"
        elif [ "$#" == 5 ]; then
          mvn exec:java -Djava.net.preferIPv4Stack=true -Dexec.args="$1 $2 $3 $4 $5"
        elif [ "$#" == 2 ]; then
          mvn exec:java -Djava.net.preferIPv4Stack=true -Dexec.args="$1 $2"
        else
          echo "Error: Wrong command. Try again."
        fi
elif [ "$1" == "--install" ]; then
  mvn clean install
elif [ "$1" == "--vagrantUp" ]; then
  cd Auxiliars/VagrantInfo
  vagrant up
  cd ../../
elif [ "$1" == "--vagrantSSH" ]; then
  cd Auxiliars/VagrantInfo
  vagrant ssh "$2"
  cd ../../
elif [ "$1" == "--vagrantHalt" ]; then
  cd Auxiliars/VagrantInfo
  vagrant halt
  cd ../../
elif [ "$1" == "--vagrantDestroy" ]; then
  cd Auxiliars/VagrantInfo
  vagrant destroy
  cd ../../
else
  if [ "$#" == 1 ]; then
          mvn exec:java -Djava.net.preferIPv4Stack=true -Dexec.args="$1"
  elif [ "$#" == 2 ]; then
          mvn exec:java -Djava.net.preferIPv4Stack=true -Dexec.args="$1 $2"
        elif [ "$#" == 3 ]; then
          mvn exec:java -Djava.net.preferIPv4Stack=true -Dexec.args="$1 $2 $3"
  elif [ "$#" == 4 ]; then
          mvn exec:java -Djava.net.preferIPv4Stack=true -Dexec.args="$1 $2 $3 $4"
        else
          echo "Error: Wrong command. Try again."
        fi
fi

cd ../;