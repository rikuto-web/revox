#!/bin/bash
set -e

# Secret Manager から env を取得
gcloud secrets versions access latest --secret="revox-prod-env" --project=revoxprod > /home/rikutoweb5/revox/secret/prod.env

# Vertex AI 用 JSON
gcloud secrets versions access latest --secret="prod-vertexAI-sa-json" --project=revoxprod > /home/rikutoweb5/revox/secret/prod-vertexAI-sa.json

# Google credential JSON
gcloud secrets versions access latest --secret="google-credential-prod-json" --project=revoxprod > /home/rikutoweb5/revox/secret/google-credential.prod.json

# Artifact Registry 認証
gcloud auth configure-docker us-west1-docker.pkg.dev --quiet

# revoxディレクトリが存在しない場合に作成
mkdir -p /home/rikutoweb5/revox

# 最新イメージをPull
sudo docker pull us-west1-docker.pkg.dev/revoxprod/revox-repository/backend:latest

# 古いコンテナを停止して削除
sudo docker-compose -f /home/rikutoweb5/revox/docker-compose-prod.yml down

# 新しいコンテナを起動
sudo docker-compose -f /home/rikutoweb5/revox/docker-compose-prod.yml up -d
