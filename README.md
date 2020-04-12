# Button Processor
Receives events and stores them in an Oracle Cloud NoSQL Table

## Authentication to Oracle Cloud
For local development I am authenticating via fn environment variables:

``` sh
fn cf a <app> <key> <value>
ex. fn cf a helloworld-app REGION us-phoenix-1
```

## test.sh usage
``` sh
./test.sh <num-requests> <fn-app-name> <fn-function-name>
ex: ./test.sh 5 helloworld-app helloworld-func
```
