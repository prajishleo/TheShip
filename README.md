The Ship

**1. Build Docker images**

cd proxy-server

docker build -t proxy-server .

cd ../proxy-client

docker build -t proxy-client .

**2. Run proxy-server**

docker run -d --name proxy-server -p 9000:9000 proxy-server

**3. Run proxy-client**

docker run -d --name proxy-client --link proxy-server -p 8080:8080 proxy-client

**Test with curl**

curl -x http://localhost:8080 http://example.com/
