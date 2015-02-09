
SimplyREST is a project in Scala and Spray for checking the behavior of your APIs with parallel calls.

-----------------------------------Your API Details In src/test/resources/data.json----------------------------------------

You can make CRUD(POST,READ,PU and Delete) operations to your APIs by specifying the url,no of individual methods call headers and query params in src/test/resources/data.json.

Like -
"url": "http://api.duckduckgo.com/" //you API endpoint

"NoOfGETCalls": 5 //no of GET calls to your API

"Post": [ { "data": "test" } ] //Post method data

"Headers": [ { "Host" :"api.duckduckgo.com" } ] // Specify Request headers in Headers object

Similarly QueryParams in "QueryParams" object


---------------------For Changing The REST Client Details Modify src/test/resources/application.conf--------------

For making x number of parallel calls to you API change 'max-connections' field of 'host-connector' to x.

You can change many configurations in src/test/resources/application.conf all details of which are written in the src/test/resources/application.conf file itself

------------------------------------------------Thank You---------------------------------------------------------------------
