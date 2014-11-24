# Automatic Classification of Article Abstracts using Mahout   #


## Requirements ##

Java 8 (requires JAVA_HOME to be set) see [here](wiki/Installing Java 8)

Hadoop v1.2.1

Mahout v0.9

###Create base directory###
```
#!linux

mkdir ~/semantic
cd  ~/semantic
```


### Install Hadoop 1.2.1 ###



```
#!linux

wget http://mirrors.ukfast.co.uk/sites/ftp.apache.org/hadoop/common/hadoop-1.2.1/hadoop-1.2.1.tar.gz
tar -xzvf hadoop-1.2.1.tar.gz

export HADOOP_HOME=~/semantic/hadoop-1.2.1
export PATH=$PATH:$HADOOP_HOME/bin
```
### Install Mahout 0.9 ###


```
#!linux

wget http://archive.apache.org/dist/mahout/0.9/mahout-distribution-0.9.tar.gz
tar -xzvf mahout-distribution-0.9.tar.gz

export MAHOUT_HOME=~/semantic/mahout-distribution-0.9
export PATH=$PATH:$MAHOUT_HOME/bin
```

### Download the training sets ###


```
#!linux

mkdir ~/semantic/data
cd ~/semantic/data

// The article training set
wget  https://www.dropbox.com/s/jgghdpr7t7to91e/BMCAll-train.tsv

// The tweet training set
wget  https://www.dropbox.com/s/eui0tet2eqzig5d/tweets-train.tsv
wget  https://www.dropbox.com/s/rpf08lvanec47sr/tweets-test-set.tsv
wget  https://www.dropbox.com/s/0hq7xeahaf1hchw/tweets-to-classify.tsv

// NAICs training set
wget https://www.dropbox.com/s/d458cz842lfonfn/2012_NAICS_Index_File.csv

```

### Download the latest classifier .jar ###

```
#!linux


mkdir ~/semantic/lib
cd ~/semantic/lib
wget https://www.dropbox.com/s/x7xoq4hdn4kaojw/classifier-2.0.1.jar
```