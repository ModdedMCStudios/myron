pipeline {
  agent any
  environment {
    MAVEN_USER = credentials('jenkins-nexus-maven-user')
    MAVEN_PASS = credentials('jenkins-nexus-maven-pass')
  }
  stages {
    stage('Build') {
      steps {
        timeout(time: 10, unit: 'MINUTES') {
          sh '''#!/bin/bash

if [[ "$GIT_BRANCH" = origin/tags/* ]]; then e
    export VERSIONING_GIT_TAG=${GIT_BRANCH#origin/tags/};
else 
    export VERSIONING_GIT_BRANCH=${GIT_BRANCH#origin/};
fi'''
          sh '''chmod +x gradlew
./gradlew build'''
        }

      }
    }

    stage('Gradle Publish') {
      steps {
        sh '''#!/bin/bash

if [[ "$GIT_BRANCH" != origin/pull/* ]]; then
    ./gradlew publish
fi'''
      }
    }

  }
}