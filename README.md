[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

# QualiChain P2Cstore Project

![web][logo]

[logo]: https://web.ist.utl.pt/~ist180970/assets/img/qualichain-logo.png
A webapp for the Qualichain Portuguese PoC. The Qualichain Portuguese PoC is about the interaction between a higher education organization, IST (or Técnico Lisboa), and a recruiting organization, AMA. A full version of the PoC source code is provided in another repository (https://github.com/QualiChain/consortium). 

This repository presents an alternative app to be executed by a recruiting organization. The difference is that the Recruiting Module of the other repository is a standalone app that has to be installed before executed, whereas the Recruiting Webapp is, as the name suggests, a web application. 

## Index:
- [System Requirements](#system-requirements)
- [Setup](#setup)
- [On P2Cstore](#on-p2cstore)
- [Compile](#compile)
- [Run](#run)
- [Credits](#credits)
- [Acknowledgements](#acknowledgements)
- [License](#license)

P2Cstore works on Linux and macOS<br>
If something doesn’t work, please [file an issue](https://github.com/QualiChain/P2Cstore/issues/new).<br>

## System Requirements
* Git
* Java 1.8
* Maven

The operating system supported for the given examples is Linux. However, considering that the only thing necessary to run the program is Java and Maven it should work on Windows and macOS.

It is important that the system has 512MB of RAM or more.

## Setup

1. To be able to connect to the network one has to have the inet ip enable on the machine.

To achieve that perform the following steps:

    $ sudo nano /etc/hosts
    
Once inside the file comment the line that has:

    127.0.1.1   ...
    
By simple adding a #, as such:

    #127.0.1.1   ...    

2. Fork and clone the projcet

3. In case one wants to use clouds it has to [Configure AWS S3](#configure-aws-s3) and [Configure GCP](#configure-gcp). Afterwards it is necessary to add the credentials to the Resources/config.properties file on the project.

* [Return to Index](#index)

## On P2Cstore

### It is important to first know and understand which are the system participants:

* **The Storage peers** - Nodes that store content from other nodes and participate in the routing algorithm. Storage peers can perform all the operations on the system.
* **The Readers** - Nodes that do not store content from other nodes nor have to give storage space to the system. This type of participant can only read from the system.
* **Cloud providers** - Comercial cloud providers like AWS and GCP.

### Now we will describe what are the main operations available:

* **Add operation** - Storage peers can add content to the system using this operation.
* **Get operation** - Storage peers and Readers can read content from the system using this operation.
* **Delete operation** - Storage peers can delete content from the system using this operation.
* **Update operation** - Storage peers can update content from the system using this operation.

There are also a few auxiliary operations available, below we will describe how to find them.

## Compile

As previously stated the present demonstration is for Linux computers, namely Ubuntu-based Linux and Fedora-based Linux.

The examples provided are for these operating systems because the code was developed in a Ubuntu-based environment and it was tested on a Fedora-based environment.

**The command to compile the code on a Ubuntu-based machine:**

    $ source bss-launch --install

**The command to compile the code on a Fedora-based machine:**

    $ ./runFedora.sh --install

## Run

### BootStrap Node

It is important to understand that for the system to function properly it is necessary to have a set of BootStrap Nodes to which the regular nodes will connect to interact with the system and know its state. So these are the nodes that must be connected first.

Therefore...

**The command to run the program on a Ubuntu-based machine for the BootStrap Node:**
 
    $ source bss-launch --bssNode BootStrapNode0    #In which 0 can be 0-3

**The command to run the program on a Fedora-based machine for the BootStrap Node:**

    $ ./runFedora.sh --bssNode BootStrapNode0    #In which 0 can be 0-3

### Storage Peers

For the storage peers the command is a little bit different.

**The command to run the program on a Ubuntu-based machine for Storage Peers:**

    $ source bss-launch --storagePeer nodeID  
    
    #If nodeID is empty, a new node is created, if it is not the system will launch the node with that nodeID

**The command to run the program on a Fedora-based machine for Storage Peers:**
    
    $ ./runFedora.sh --storagePeer nodeID  
    
    #If nodeID is empty, a new node is created, if it is not the system will launch the node with that nodeID

### Readers 

For readers we do the following:

**The command to run the program on a Ubuntu-based machine for Readers:**

     $ source bss-launch get-shared ownerID/file_key
     
     # In which ownerID and file_key is shared with the reader by a storage peer

**The command to run the program on a Fedora-based machine for Readers:**

     $ ./runFedora.sh get-shared ownderID/file_key
     
     # In which ownerID and file_key is shared with the reader by a storage peer


## Credits

The continuous development of this project is what makes it alive and it is only  possible due to all the people who [contribute](https://github.com/QualiChain/P2Cstore/graphs/contributors)<br>

* [Return to Index](#index)

## Acknowledgements

This work was initially developed in the scope of the master's thesis of [@MarceloFRSilva](https://github.com/MarceloFRSilva) who developed the first version of this work.

We are grateful to the authors of existing related projects used in the development of this one:

- [@JoshuaKissoon](https://github.com/JoshuaKissoon)

Thank you very much for your Kademlia DHT open source project.

* [Return to Index](#index)

## License

[License: MIT](https://opensource.org/licenses/MIT)

* [Return to Index](#index)
