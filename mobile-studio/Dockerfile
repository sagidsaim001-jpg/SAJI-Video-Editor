FROM mcr.microsoft.com/devcontainers/python:1-3.11-bookworm
RUN apt-get update && apt-get install -y --no-install-recommends ffmpeg git libgl1 libglib2.0-0 && rm -rf /var/lib/apt/lists/*
WORKDIR /workspaces/SAJI-Video-Editor/mobile-studio
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
EXPOSE 7860
