echo releasing fluxtion
mvn --batch-mode jgitflow:release-start jgitflow:release-finish -Dmaven.javadoc.failOnError=false -Prelease