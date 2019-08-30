## VAST Ad Server

VAST Ad Server is a Java project which implements a VAST ad server with 
basic functionality. You can read more about VAST [here](https://www.iab.com/guidelines/digital-video-ad-serving-template-vast/)
but strictly speacking VAST defines an XML standard which publishers and advertisers use to communicate and perform auctions.
The XML contains http addresses to be used for reporting tracking events (an ad was viewed, paused etc.) as well as links to the video files.

###Build instructions
Please make sure you have [Maven](https://maven.apache.org) installed.

1. Place the project source code files in a folder.
2. run in terminal `mvn package`
3. run in terminal `mvn exec:java`

The server is now running on port `8080`.   