#!/bin/sh

export COMPOSE_FILE_PATH=${PWD}/target/classes/docker/docker-compose.yml

if [ -z "${M2_HOME}" ]; then
  export MVN_EXEC="mvn"
else
  export MVN_EXEC="${M2_HOME}/bin/mvn"
fi

start() {
    docker volume create alfresco-promena-lib-transformer-rendition-acs-volume
    docker volume create alfresco-promena-lib-transformer-rendition-db-volume
    docker volume create alfresco-promena-lib-transformer-rendition-ass-data-volume
    docker volume create alfresco-promena-lib-transformer-rendition-ass-conf-volume
    docker-compose -f $COMPOSE_FILE_PATH up --build -d
}

start_acs() {
    docker-compose -f $COMPOSE_FILE_PATH up --build -d alfresco-promena-lib-transformer-rendition-acs
}

down() {
    if [ -f $COMPOSE_FILE_PATH ]; then
        docker-compose -f $COMPOSE_FILE_PATH down -v
    fi
}

purge() {
    docker volume rm -f alfresco-promena-lib-transformer-rendition-acs-volume
    docker volume rm -f alfresco-promena-lib-transformer-rendition-db-volume
    docker volume rm -f alfresco-promena-lib-transformer-rendition-ass-data-volume
    docker volume rm -f alfresco-promena-lib-transformer-rendition-ass-conf-volume
}

build() {
    docker-compose -f $COMPOSE_FILE_PATH kill alfresco-promena-lib-transformer-rendition-acs
    yes | docker-compose -f $COMPOSE_FILE_PATH rm -f alfresco-promena-lib-transformer-rendition-acs
    $MVN_EXEC -DskipTests=true clean package
}

tail() {
    docker-compose -f $COMPOSE_FILE_PATH logs -f
}

tail_all() {
    docker-compose -f $COMPOSE_FILE_PATH logs --tail="all"
}

prepare_test() {
    $MVN_EXEC -DskipTests=true verify
}

test() {
    $MVN_EXEC -DskipTests=true verify
}

case "$1" in
  build_start)
    down
    build
    start
    tail
    ;;
  build_start_it_supported)
    down
    build
    prepare_test
    start
    tail
    ;;
  reload)
    build
    start_acs
    tail
    ;;
  start)
    start
    tail
    ;;
  stop)
    down
    ;;
  purge)
    down
    purge
    ;;
  tail)
    tail
    ;;
  build_test)
    down
    build
    prepare_test
    start
    test
    tail_all
    down
    ;;
  test)
    test
    ;;
  *)
    echo "Usage: $0 {build_start|build_start_it_supported|reload|start|stop|purge|tail|build_test|test|}"
esac