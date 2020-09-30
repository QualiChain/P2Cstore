# Welcome to the Auxiliar P2Cstore Setup and Run

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

