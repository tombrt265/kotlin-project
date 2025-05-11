# [TODO]: Change the shell according to your needs

set shell := ["powershell"]

default:
  just --list

build:
  if ($env:OS -eq "Windows_NT") { \
    & .\mvnw.cmd clean package \
  } else { \
    ./mvnw clean package \
  }

mutation-testing:
  if ($env:OS -eq "Windows_NT") { \
    & .\mvnw.cmd package pitest:mutationCoverage \
  } else { \
    ./mvnw package pitest:mutationCoverage \
  }

setup-maven-wrapper:
  if ($env:OS -ne "Windows_NT") { \
    chmod +x ./mvnw \
  } else { \
    echo "No chmod needed on Windows" \
  }

reset-maven-wrapper:
  if ($env:OS -ne "Windows_NT") { \
    chmod -x ./mvnw
