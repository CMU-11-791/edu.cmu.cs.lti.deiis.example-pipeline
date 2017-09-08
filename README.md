# DEIIS : Example QA pipeline

This is an example of a collection of web services that can be deployed to a LAPPS grid.  In this example all services are bundled as a single war file.
 
## Build the project

```
mvn clean package

```

## Start the services

```bash
mvn jetty:run
```

After Jetty has started the services should be available at http://localhost:8080/example-pipeline/services/*.  See the `pipeline.lsd` script for examples of creating service clients and invoking the services.


## Run a pipeline

The `pipeline.lsd` script can be used to run each service on the src/main/resources/booth.txt data.  

```bash
lsd pipeline.lsd
```

If you do not have the LSD (Lappsgrid Services DSL) interperter installed you can download it from [http://downloads.lappsgrid.org/lsd-latest.tgz](http://downloads.lappsgrid.org/lsd-latest.tgz).  Unpack the archive and copy the jar and lsd file somewhere on your $PATH; /usr/local/bin is a good location on MacOS or Linux.  Windows users will have to write their own .bat or .cmd file to launch the lsd executable.
 
## Services

1. PrepareData
	- adds question and answer annotations
	- strips metadata from the text
	- wraps in a LIF container

1. Tokenizer
	- white space tokenizer
	- does not handle punctuation
	- expects every sentence to be terminated by a single punctuation mark.

1. NGramAnnotator
	- generates unigrams, bigrams, or trigrams
    - set N in the constructor or in a data parameter
    
1. NGramScorer
	- generates a score for each answer based on ngram overlap with the question

1. Ranker
	- ranks answers based on the value of a given feature
	- the feature name can be provided as a runtime parameter.

1. Evaluator
	- calculates precision, recall, and F-measure for the top N responses.
	- N can be provided as a run time parameter or calculated from the candidate answers.

1. Extractor
    - extracts the Evaluator results and writes them in a CSV format.
    
    