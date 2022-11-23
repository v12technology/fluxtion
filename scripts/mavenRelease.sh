echo releasing fluxtion
mvn --batch-mode external.atlassian.jgitflow:jgitflow-maven-plugin:1.0-m5.1:release-finish -Prelease -Dmaven.javadoc.failOnError=false -DpushReleases=false -Dfluxtion.github.user=${{ secrets.FLUXTION_GITHUB_USER }} -Dfluxtion.github.password=${{ secrets.FLUXTION_GITHUB_TOKEN }}
