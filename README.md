# Vertumnus

Vertumnus is the name for the Roman god of transformation and change ([see Wikipedia](http://en.wikipedia.org/wiki/Vertumnus)).

This is a **Live Update** software written in Java. The Vertumnus-Maker uploads the software package.
The Vertumnus(-Client) downloads the package for the next version. The user can decide whether to update
to the newest version (e.g. 1.1.0 to 2.0.1) or to keep to the major version (e.g. 1.1.0 to 1.2.3).

## Development

Eclipse Kepler is used for programming. So the repository contains the Eclipse files.
Vertumnus-Maker depends on Vertumnus.

Vertumnus needs webspace with FTP access. For unit testing you need the file /ftp.xml.
Content: `<ftp p="..." u="..." s="..." path="..."/>`

* p = FTP password
* u = FTP user name
* s = FTP server name
* path = FTP server path, usually begins with /

After clone this repository into Eclipse you must execute the target "eclipse" of vertumnus-maker/make.xml to load the needed JARs from internet.

## Roadmap

* easier ftp.xml format
* Translation of messages to English language (NLS)
* Translation of code and file names to English language; comments
* new feature: Bugfix line
* Documentation
