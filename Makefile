build: demo-client generate-env build-mvn demo-server

IMAGENAME_CLIENT = virgilsecurity/mariadb-demo-client
IMAGENAME_SERVER = virgilsecurity/mariadb-demo-server

define tag_docker
  @if [ "$(TRAVIS_BRANCH)" = "master" -a "$(TRAVIS_PULL_REQUEST)" = "false" ]; then \
    docker tag $(1) $(1):stable; \
  fi
  @if [ "$(TRAVIS_BRANCH)" != "master" ]; then \
    docker tag $(1) $(1):$(TRAVIS_BRANCH); \
  fi
endef


demo-client:
	cd client && docker build -t $(IMAGENAME_CLIENT) .

generate-env:
	cd server && generate-env.sh

build-mvn:
	cd server && mvn clean package
	
demo-server:
	cd server && docker build -t $(IMAGENAME_SERVER) .

docker-tag:
	@echo
	@echo MARK: tag_docker depend of branch
	$(call tag_docker, $(IMAGENAME_CLIENT))
	$(call tag_docker, $(IMAGENAME_SERVER))

docker-push:
	@echo
	@echo MARK: push image to dockerhub
	docker push $(IMAGENAME_CLIENT)
	docker push $(IMAGENAME_SERVER)

