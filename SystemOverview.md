## VAST Ad Server

VAST Ad Server is a Java project which implements a VAST ad server with 
basic functionality. You can read more about VAST [here](https://www.iab.com/guidelines/digital-video-ad-serving-template-vast/)
but strictly speacking VAST defines an XML standard which publishers and advertisers use to communicate and perform auctions.
The XML contains http addresses to be used for reporting tracking events (an ad was viewed, paused etc.) as well as links to the video files.

This VAST ad server **auction** is completely dynamic and works by connecting publishers and advertisers based on content category and CPM.  

**Entities**:
 1. Publisher - the entity which offers ad placement for advertisers.
 2. Advertiser - the entity which provides ads for publishers.

**Impression** is one ad view.
**Cost CPM** is the cost of 1000 impressions while **Price CPM** is the price an advertiser is willing to
pay for 1000 impressions (read more [here](https://en.wikipedia.org/wiki/Cost_per_impression)).
Each entity has a **category** which describes the type of content offered/required, e.g. sports, news etc.
**Budget** is defined as the maximum amount of impressions an advertiser is willing to make.
**vastUrl** is a URL which points to advertisers XML file.

The server is implemented as REST API using Spring Boot 2.1.6 framework.

###System components

#### Scheduled tasks
The server has 3 **scheduled tasks** which send JMS messages to JMS listener.
The tasks are:
1. CHECK_OUT_OF_BUDGET_ADVERTISERS - the task runs at a set interval to make sure that advertisers don't run out of their budget.
2. AGGREGATE_STATS - the task runs at a set interval to aggregate the stats of `stats` table. The aggregated stats table only represents the data for the current day. This data can be sent via a REST endpoint.
3. CLEAR_AGGREGATED_STATS_TABLE - the task runs each day at midnight to clear the aggregated stats table of yesterday.

#### Database
Postgres database is used for the project. All db operations are performed by the class `DBHandler`.
There're 4 **tables**:
1. `publishers` - contains data on publishers.
2. `advertisers` - contains data on advertisers.
3. `stats` - contains data on auctions.
4. `aggregatedstats` - contains auction data only for today.

`DBHandler` uses `JdbcTemplate` which the core class in Spring JDBC package to perform
all operation on the db. In addition, `sqlStatements` package contains utilities classes to build
SQL queries in the rest of the application.

#### JMS
ActiveMQ implementation of JMS is used in the server. It's used to perform scheduled tasks which can be time/resources intensive tasks.
JMS service is implemented using `JmsListener` Spring component, which is non-blocking.

#### REST API
All actions which relate to creating/changing/listing/deleting VAST entities, starting
auctions and receiving reports are accessed via REST API endpoints. The **complete list of API endpoints** and their description can be found in Postman documentation [here](https://documenter.getpostman.com/view/4351524/SVYuqH2U?version=latest#a28671b5-0874-45ea-a7cb-11642b39429d).
REST API supports CORS standard, specifically, any domain can send request to the `/auction` endpoint and therefore start an auction.
However, only single domain is allowed to send requests regarding entities (in development mode it's `localhost:3000`).

REST API is implemented by controllers in `controllers` package. Each entity has its own controller.
There're also dedicated controllers for auctions, reports and tracking events.

#### VAST Entities
VAST Entities are reporesented by their respective classes in `JSONObjects` package. In addition, if a JSON object
needs to be returned as part of http response it will also be in this package. This is because object serialization
in responses is performed by `Jackson` Spring package which requires a JSON object to be represented by a class.

#### Error Handling
Several custom error handlers are used in the project. `ApiError` is used in order to communicate errors in http responses.
Other error handlers are used for specific exceptions.

#### XML Parsing
VAST tags are simple URLs which point to an XML file hosted in some storage location (e.g. CDN).
Therefore, in order to get and parse the XML it needs to be obtained first.
`VastUrlToXmlConverter` does just that. Once we receive an XML as a string `VASTXMLParserBuilder` parses the string into a DOM object.
This is done in order to add our tracking events on top of the already existing ones.

#### Config
All configuration details and constants are kept in `config` package. `Routes` contain the definition of all REST API endpoints,
`SQLConstants` contain the definition of tables and other relevant data while `VastConfig` contains the most general configurations.
