
docker buildx build --platform linux/amd64 -t ubuntu-all-tools:jdk21-adm64 .
docker buildx build --platform linux/arm64 -t ubuntu-all-tools:jdk21-arm64 .