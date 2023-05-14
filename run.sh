#!/bin/sh -x
java -Xms2g -Xmx2g \
  -XX:-DontCompileHugeMethods \
  -Dio.netty.buffer.checkAccessible=false \
  -DgreenApp.showtime=true \
  -jar target/green-app-1.0.0.jar