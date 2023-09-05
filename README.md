# AWSJavaSandbox

- a place to try aws java sdk to create certificates

## instructions to run

0a.  prerequisite - have gradle installed (i tried latest gradle)
0b.  have java installed (i used jdk 11)
0c.  create a certificate signing request and place it into src/main/resources/csr_file.csr
  - first create private key
```console
openssl genpkey -algorithm RSA -out CLIENTKEY.KEY -pkeyopt rsa_keygen_bits:2048
```
  - then create csr
```console
openssl req -new -key CLIENTKEY.KEY -out csr_file.csr -subj “/C=DE/ST=BadenWuerttemberg/L=Maulburg/O=Endress+Hauser SE+Co.KG/OU=FWR30/CN=“TEST_SERIAL”
```
  - copy the file to src/main/resources
0d.  have aws cli installed
0e.  modify ~/.aws/credentials to make a profile with your ak and secret
0f.  modify ~/.aws/config to make profile with region
1.  build wrapper
```
gradle wrapper
```
2.  compile and run
```
gradlew run
```