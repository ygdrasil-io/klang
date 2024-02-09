
#docker build -t ubuntu-clang-16 -f ./ubuntu-clang-16 .
#docker build -t ubuntu-java-21 -f ./ubuntu-java-21 .
#docker build -t ubuntu-all-tools -f ./ubuntu-all-tools .
docker build -t ubuntu-qemu -f ./ubuntu-qemu .

#docker run -it --privileged qemu-image