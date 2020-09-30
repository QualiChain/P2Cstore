# Welcome to the User Guide of P2Cstore.

## Quick overview

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

## How to compile the code

As previously stated the present demonstration is for Linux computers, namely Ubuntu-based Linux and Fedora-based Linux.

The examples provided are for these operating systems because the code was developed in a Ubuntu-based environment and it was tested on a Fedora-based environment.

**The command to compile the code on a Ubuntu-based machine:**

    $ source bss-launch --install

**The command to compile the code on a Fedora-based machine:**

    $ ./runFedora.sh --install

## How to run the code

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

## Example of system usage with images

