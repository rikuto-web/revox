#!/bin/bash
set -e

# 最新イメージを取得
docker pull us-west1-docker.pkg.dev/revoxprod/revox-repository/backend:latest

# 古いコンテナを停止して削除
docker-compose -f /home/rikutoweb5/revox/docker-compose-prod.yml down

# 新しいコンテナを起動
docker-compose -f /home/rikutoweb5/revox/docker-compose-prod.yml up -d
