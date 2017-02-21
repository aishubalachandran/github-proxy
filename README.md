# github-proxy

Proxy service for all Github v3 [APIs](https://developer.github.com/v3/) with caching support using Redis.
The service APIs are developed using the [Dropwizard](http://www.dropwizard.io/1.0.6/docs/) framework.


### Pre-requisites

The service requires the following:

* Java 1.8
* Maven 3.2.1 or above 
* [Redis](https://redis.io/). Follow the instructions given [here](https://redis.io/download) for the setup. 


### Installation

```sh
$ git clone git@github.com:aishubalachandran/github-proxy.git
$ cd github-proxy
$ mvn clean install
```

### How To Run

After you have successfully built the package, you need to do the following -

* Set up github api token as an environment variable named - __GITHUB_API_TOKEN__.
  Example: 
  
  ```sh
  $ export GITHUB_API_TOKEN=<your_token_here>
  ```
  
* Run the redis server using 
  ```sh
  $ src/redis-server
  ```
  The Redis server by default runs on port __6379__.
  
* To start the service:
  
  ```sh
  $ cd github-proxy
  $ java -jar target/githubproxy-0.0.1-SNAPSHOT.jar server src/main/config/configuration.yml
  ```
  By default, the service runs on port __8080__. To run the server on a different port, use the following - 
  
  ```sh
  $  java -Ddw.server.applicationConnectors[0].port=9090 -Ddw.server.adminConnectors[0].port=9091 -jar target/githubproxy-0.0.1-SNAPSHOT.jar server src/main/config/configuration.yml 
  ```
  
### Healthcheck

After the service has started successfully, you can monitor the service using the healthcheck endpoint. 
Please note: By default, the healthcheck runs on port __8081__.

```sh
$ curl -X GET "http://localhost:8081/healthcheck"
```

To modify the healthcheck port, run the service using the following command - 
```sh
$  java -Ddw.server.adminConnectors[0].port=9091 -jar target/githubproxy-0.0.1-SNAPSHOT.jar server src/main/config/configuration.yml
```


### Endpoints

The service exposes the following endpoints:

```sh
curl -X GET 'http://localhost:8080/'
curl -X GET 'http://localhost:8080/orgs/netflix/repos'
curl -X GET 'http://localhost:8080/orgs/netflix/members'
curl -X GET 'http://localhost:8080/view/top/5/open_issues'
curl -X GET 'http://localhost:8080/view/top/5/watchers'
curl -X GET 'http://localhost:8080/view/top/10/forks'
curl -X GET 'http://localhost:8080/view/top/5/stars'
curl -X GET 'http://localhost:8080/view/top/5/last_updated'
```
For all other Github endpoints use :

```sh
curl -X GET 'http://localhost:8080/<same_path_as_the_actual_github_api>'
```

### Caching

Currently, the Redis API response cache is refreshed every __15 minutes__.
