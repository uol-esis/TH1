#!/bin/sh

# 0. check if git and docker compose are installed

if ! [ -x "$(command -v git)" ]; then
  echo 'Error: git is not installed.' >&2
  exit 1
fi

if ! [ -x "$(command -v docker)" ]; then
  echo 'Error: docker is not installed.' >&2
  exit 1
fi

if ! [ -x "$(command -v docker compose)" ]; then
  echo 'Error: docker compose is not installed.' >&2
  exit 1
fi

# 1. pull the latest state of the project

git pull origin main

# 2. build with docker

cd docker
docker compose down # stop the running containers
docker compose up --build -d # build and run the containers