# redireco-proxy-server-java
A proxy server for redire.co webhooks

## Purpose
The main purpose of this application is to assist developers in bypassing Cross-Origin Resource Sharing (CORS) restrictions when testing webhook responses using redire.co.

By design it can be used to proxy any RESTful HTTP requests made from browser-based JavaScript to avoid them being blocked by CORS restrictions.

## Usage
Start the proxy server with the following command:
`java -jar redireco-proxy-server.jar`

The default port of the server is `8765`. 

To proxy a request simply direct your request to the proxy server with the following url:

`http://localhost:8765/proxy?url={destination-url}`

Your request will then be rebuilt and sent to the `destination-url`. All custom headers and parameters will be included. The response will then be passed back to your calling function.

## Options

There are several options available to customize the server:

| Command | Description |
| --- | --- |
| -d,--debug | Optional. Whether to output debug information. |
| -p,--port \<arg\> | Optional. The port this proxy server listens on. Default is `8765`. |
| -u,--url \<arg\> | Optional. The hard-coded URL this proxy server forwards to. If set then url-param will be ignored. Default is `null`. |
| -up,--url-param \<arg\> | Optional. The request parameter that contains the URL to forward to. Default is `url`. |
