ROOT=$(shell pwd)

war:
	mvn package

clean:
	mvn clean
	
run:
	docker run -d -p 8080:8080 --name tomcat -v $(ROOT)/target:/var/lib/tomcat7/webapps lappsgrid/tomcat7:1.2.1

stop:
	docker rm -f tomcat


