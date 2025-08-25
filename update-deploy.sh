#!/bin/bash
set -e

# Secret Manager から env を取得
mkdir -p $HOME/revox/secret
mkdir -p $HOME/revox-deploy/secret

# prod.env
gcloud secrets versions access latest --secret="revox_prod_env" --project=revoxprod > $HOME/revox/secret/prod.env
cp $HOME/revox/secret/prod.env $HOME/revox-deploy/secret/prod.env

# Vertex AI 用 JSON
gcloud secrets versions access latest --secret="revox_prod_vertex_sa_json" --project=revoxprod > $HOME/revox/secret/prod-vertexAI-sa.json
cp $HOME/revox/secret/prod-vertexAI-sa.json $HOME/revox-deploy/secret/prod-vertex-sa.json

# Google credential JSON（compute-sa-key.json を使用）
gcloud secrets versions access latest --secret="compute-sa-key-json" --project=revoxprod > $HOME/revox/secret/compute-sa-key.json

# Artifact Registry 認証
gcloud auth activate-service-account --key-file=$HOME/revox/secret/compute-sa-key.json
gcloud auth configure-docker us-west1-docker.pkg.dev --quiet

# revoxディレクトリが存在しない場合に作成
mkdir -p $HOME/revox

# 最新イメージをPull
docker pull us-west1-docker.pkg.dev/revoxprod/revox-repository/backend:latest

# 古いコンテナを停止して削除
docker-compose -f $HOME/revox-deploy/docker-compose-prod.yml down

# 新しいコンテナを起動
docker-compose -f $HOME/revox-deploy/docker-compose-prod.yml up -d
