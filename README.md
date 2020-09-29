# Thesis Project - P2Cstore
My project thesis and P2Cstore paper

## Index:
- [Configurations](#configurations)
- [User Guide](#user-guide)
- [Configure AWS S3](#configure-aws-s3)
- [Configure GCP](#configure-gcp)
- [Project Structure](#project-structure)
- [Credits](#credits)
- [Acknowledgements](#acknowledgements)
- [License](#license)

P2Cstore works on Linux and macOS<br>
If something doesn’t work, please [file an issue](https://github.com/QualiChain/P2Cstore/issues/new).<br>

## Configurations

To be able to connect to the network one has to have the inet ip enable on the machine.
To achieve that perform the following steps:

    $ sudo nano /etc/hosts
    
Once inside the file comment the line that has:

    127.0.1.1   ...
    
By simple adding a #, as such:

    #127.0.1.1   ...    

* [Return to Index](#index)

## User Guide

To better understand how to use and interact with this software we created a wiki [User Guide](https://github.com/QualiChain/P2Cstore/blob/master/User-Guide-P2Cstore.md) – How to use the P2Cstore.

* [Return to Index](#index)

## Configure AWS S3

To configure AWS S3 buckets use the following tutorial: [AWS S3 Buckets set up tutorial](https://docs.aws.amazon.com/AmazonS3/latest/user-guide/create-configure-bucket.html).

* [Return to Index](#index)

## Configure GCP

To configure GCP storage buckets use the following tutorial: [GCP Storage Buckets set up tutorial](https://cloud.google.com/storage/docs/creating-buckets).

* [Return to Index](#index)

## Project Structure

The present project as the following struture:

```
p2cstore
├── README.md
└── storage-system-blockchain
    ├── bss-client
    │   ├── dependency-reduced-pom.xml
    │   ├── lib
    │   │   ├── gson-2.8.6.jar
    │   │   ├── hamcrest-core-1.3.jar
    │   │   ├── java-multibase-v1.0.1.jar
    │   │   ├── java-multihash-1.2.1.jar
    │   │   └── junit-4.13.jar
    │   ├── main
    │   │   └── java
    │   │       └── com
    │   │           └── thesis
    │   │               └── inesc
    │   │                   ├── BSSConfig.java
    │   │                   ├── BssUser.java
    │   │                   ├── Client.java
    │   │                   ├── ClientMain.java
    │   │                   ├── CloudStorage
    │   │                   │   ├── CloudBlobContext.java
    │   │                   │   └── CloudConnectivity.java
    │   │                   ├── Commands
    │   │                   │   ├── AddCommand.java
    │   │                   │   ├── CommandContent.java
    │   │                   │   ├── Command.java
    │   │                   │   ├── DeleteCommand.java
    │   │                   │   ├── GetCommand.java
    │   │                   │   ├── GetSharedCommand.java
    │   │                   │   └── UpdateCommand.java
    │   │                   ├── CommunicationHandler.java
    │   │                   ├── Communication.java
    │   │                   ├── DHTAdapted.java
    │   │                   ├── Display.java
    │   │                   ├── Exceptions
    │   │                   │   ├── CouldNotConnectToBootstrapNodeException.java
    │   │                   │   ├── CouldNotReadLineException.java
    │   │                   │   ├── DHTFailureException.java
    │   │                   │   ├── NoSuchFileFoundException.java
    │   │                   │   └── WrongInputException.java
    │   │                   ├── kademliadht
    │   │                   │   ├── DefaultConfiguration.java
    │   │                   │   ├── dht
    │   │                   │   │   ├── DHT.java
    │   │                   │   │   ├── GetParameter.java
    │   │                   │   │   ├── JKademliaStorageEntry.java
    │   │                   │   │   ├── KadContent.java
    │   │                   │   │   ├── KademliaDHT.java
    │   │                   │   │   ├── KademliaStorageEntry.java
    │   │                   │   │   ├── KademliaStorageEntryMetadata.java
    │   │                   │   │   ├── StorageEntryMetadata.java
    │   │                   │   │   └── StoredContentManager.java
    │   │                   │   ├── exceptions
    │   │                   │   │   ├── ContentExistException.java
    │   │                   │   │   ├── ContentNotFoundException.java
    │   │                   │   │   ├── KadServerDownException.java
    │   │                   │   │   ├── RoutingException.java
    │   │                   │   │   └── UnknownMessageException.java
    │   │                   │   ├── JKademliaNode.java
    │   │                   │   ├── KadConfiguration.java
    │   │                   │   ├── KademliaNode.java
    │   │                   │   ├── KadServer.java
    │   │                   │   ├── KadStatistician.java
    │   │                   │   ├── message
    │   │                   │   │   ├── AcknowledgeMessage.java
    │   │                   │   │   ├── ConnectMessage.java
    │   │                   │   │   ├── ConnectReceiver.java
    │   │                   │   │   ├── ContentLookupMessage.java
    │   │                   │   │   ├── ContentLookupReceiver.java
    │   │                   │   │   ├── ContentMessage.java
    │   │                   │   │   ├── DeleteContentMessage.java
    │   │                   │   │   ├── DeleteContentReceiver.java
    │   │                   │   │   ├── KademliaMessageFactory.java
    │   │                   │   │   ├── MessageFactory.java
    │   │                   │   │   ├── Message.java
    │   │                   │   │   ├── NodeLookupMessage.java
    │   │                   │   │   ├── NodeLookupReceiver.java
    │   │                   │   │   ├── NodeReplyMessage.java
    │   │                   │   │   ├── Receiver.java
    │   │                   │   │   ├── SimpleMessage.java
    │   │                   │   │   ├── SimpleReceiver.java
    │   │                   │   │   ├── StoreContentMessage.java
    │   │                   │   │   ├── StoreContentReceiver.java
    │   │                   │   │   └── Streamable.java
    │   │                   │   ├── node
    │   │                   │   │   ├── KademliaId.java
    │   │                   │   │   ├── KeyComparator.java
    │   │                   │   │   └── Node.java
    │   │                   │   ├── operation
    │   │                   │   │   ├── BucketRefreshOperation.java
    │   │                   │   │   ├── ConnectOperation.java
    │   │                   │   │   ├── ContentLookupOperation.java
    │   │                   │   │   ├── ContentRefreshOperation.java
    │   │                   │   │   ├── DeleteOperation.java
    │   │                   │   │   ├── KadRefreshOperation.java
    │   │                   │   │   ├── NodeLookupOperation.java
    │   │                   │   │   ├── Operation.java
    │   │                   │   │   ├── PingOperation.java
    │   │                   │   │   └── StoreOperation.java
    │   │                   │   ├── routing
    │   │                   │   │   ├── Contact.java
    │   │                   │   │   ├── ContactLastSeenComparator.java
    │   │                   │   │   ├── JKademliaBucket.java
    │   │                   │   │   ├── JKademliaRoutingTable.java
    │   │                   │   │   ├── KademliaBucket.java
    │   │                   │   │   └── KademliaRoutingTable.java
    │   │                   │   ├── Statistician.java
    │   │                   │   └── util
    │   │                   │       ├── HashCalculator.java
    │   │                   │       ├── RouteLengthChecker.java
    │   │                   │       └── serializer
    │   │                   │           ├── JsonDHTSerializer.java
    │   │                   │           ├── JsonRoutingTableSerializer.java
    │   │                   │           ├── JsonSerializer.java
    │   │                   │           └── KadSerializer.java
    │   │                   ├── NodeComputer.java
    │   │                   ├── SaveNodeState.java
    │   │                   ├── StorageValues.java
    │   │                   ├── Terminal.java
    │   │                   ├── TerminalOptions.java
    │   │                   └── Utilities
    │   │                       ├── DHTUtilities.java
    │   │                       ├── FilesUtilities.java
    │   │                       ├── HashFunction.java
    │   │                       ├── PoSChallenge.java
    │   │                       ├── PoSUtilities.java
    │   │                       ├── PropertiesUtilitites.java
    │   │                       ├── ScheduledPoS.java
    │   │                       └── StorageUtilities.java
    │   ├── pom.xml
    │   ├── Resources
    │   │   ├── authorizedPeers.properties
    │   │   └── config.properties
    │   └── test
    │       └── java
    │           └── com
    │               └── thesis
    │                   └── inesc
    │                       ├── CreateBootstrapNodeClientTest.java
    │                       └── UtilitiesTests
    │                           ├── GetFileDataTest.java
    │                           ├── GetFileHashTest.java
    │                           ├── GetFileInputstreamTest.java
    │                           ├── TrimTest.java
    │                           └── VerifyIfFileExistsTest.java
    ├── bss-launch
    ├── pom.xml
    └── runFedora.sh

```

* [Return to Index](#index)


## Credits

This work was initially developed in the scope of the master's thesis of [@MarceloFRSilva](https://github.com/MarceloFRSilva).

The continuous development of this project is what makes it alive and it is only  possible due to all the people who [contribute](https://github.com/QualiChain/P2Cstore/graphs/contributors)<br>

* [Return to Index](#index)

## Acknowledgements

We are grateful to the authors of existing related projects used in the development of this one:

- [@JoshuaKissoon](https://github.com/JoshuaKissoon)

Thank you very much for your Kademlia DHT open source project.

* [Return to Index](#index)

## License

Type license here.

* [Return to Index](#index)
