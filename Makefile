ROOT=$(shell pwd)

war:
	mvn package

clean:
	mvn clean
	
deploy:
	scp -P 22022 target/DEIISPipeline.war grid.anc.org:/tmp
	ssh -p 22022 grid.anc.org "mv /tmp/DEIISPipeline.war /usr/share/tomcat/server-1/webapps"

all: war clean deploy

run:
	docker run -d -p 8080:8080 --name tomcat -v $(ROOT)/target:/var/lib/tomcat7/webapps lappsgrid/tomcat7:1.2.1

stop:
	docker rm -f tomcat

test:
	@echo "Dir is $(ROOT)"

