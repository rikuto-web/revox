#!/bin/bash
set -e

# Artifact Registryの認証情報を設定
gcloud auth configure-docker us-west1-docker.pkg.dev --quiet

# revoxディレクトリが存在しない場合に作成
mkdir -p /home/rikutoweb5_gmail_com/revox

# Artifact Registryから最新のイメージをプル
sudo docker pull us-west1-docker.pkg.dev/revoxprod/revox-repository/backend:latest

# 古いコンテナを停止して削除
sudo docker-compose -f /home/rikutoweb5_gmail_com/revox/docker-compose-prod.yml down

# 新しいコンテナを起動
sudo docker-compose -f /home/rikutoweb5_gmail_com/revox/docker-compose-prod.yml up -d