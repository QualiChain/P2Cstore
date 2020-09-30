[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

# QualiChain P2Cstore Project

![web][logo]

[logo]: https://web.ist.utl.pt/~ist180970/assets/img/qualichain-logo.png
A webapp for the Qualichain Portuguese PoC. The Qualichain Portuguese PoC is about the interaction between a higher education organization, IST (or Técnico Lisboa), and a recruiting organization, AMA. A full version of the PoC source code is provided in another repository (https://github.com/QualiChain/consortium). 

This repository presents an alternative app to be executed by a recruiting organization. The difference is that the Recruiting Module of the other repository is a standalone app that has to be installed before executed, whereas the Recruiting Webapp is, as the name suggests, a web application. 

## Index:
- [System Requirements](#system-requirements)
- [Setup and Run](#setup-and-run)
- [Configure AWS S3](#configure-aws-s3)
- [Configure GCP](#configure-gcp)
- [Project Structure](#project-structure)
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

## Setup and Run

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

Type license here.

* [Return to Index](#index)
