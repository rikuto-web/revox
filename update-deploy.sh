#!/bin/bash
set -e

# Secret Manager から env を取得
gcloud secrets versions access latest --secret="revox_prod_env" --project=revoxprod > $HOME/revox/secret/prod.env

# Vertex AI 用 JSON
gcloud secrets versions access latest --secret="revox_prod_vertex_sa_json" --project=revoxprod > $HOME/revox/secret/prod-vertexAI-sa.json

# Google credential JSON
gcloud secrets versions access latest --secret="revox_google_credentials_prod_json" --project=revoxprod > $HOME/revox/secret/google-credential.prod.json

# Artifact Registry 認証
gcloud auth configure-docker us-west1-docker.pkg.dev --quiet

# revoxディレクトリが存在しない場合に作成
mkdir -p $HOME/revox

# 最新イメージをPull
sudo docker pull us-west1-docker.pkg.dev/revoxprod/revox-repository/backend:latest

# 古いコンテナを停止して削除
sudo docker-compose -f $HOME/revox/docker-compose-prod.yml down

# 新しいコンテナを起動
sudo docker-compose -f $HOME/revox/docker-compose-prod.yml up -d
