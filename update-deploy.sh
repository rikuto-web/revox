#!/bin/bash
set -e

# スクリプト自身のディレクトリを基準にパスを設定
BASE_DIR="$(cd "$(dirname "$0")/../revox" && pwd)"
SECRET_DIR="$BASE_DIR/secret"

mkdir -p "$SECRET_DIR"

# Secret Manager から env を取得
gcloud secrets versions access latest --secret="revox-prod-env" --project=revoxprod > "$SECRET_DIR/prod.env"

# Vertex AI 用 JSON
gcloud secrets versions access latest --secret="prod-vertexAI-sa-json" --project=revoxprod > "$SECRET_DIR/prod-vertexAI-sa.json"

# Google credential JSON
gcloud secrets versions access latest --secret="google-credential-prod-json" --project=revoxprod > "$SECRET_DIR/google-credential.prod.json"

# Artifact Registry 認証
gcloud auth configure-docker us-west1-docker.pkg.dev --quiet

# 最新イメージをPull
sudo docker pull us-west1-docker.pkg.dev/revoxprod/revox-repository/backend:latest

# 古いコンテナを停止して削除
sudo docker-compose -f "$BASE_DIR/docker-compose-prod.yml" down

# 新しいコンテナを起動
sudo docker-compose -f "$BASE_DIR/docker-compose-prod.yml" up -d
