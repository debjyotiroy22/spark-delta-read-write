# spark-delta-read-write

This jar can read and write delta files by running inside Databricks platform, as well as running on on other Spark cluster. The code will generate a dataframe from random numbers and it will write it in delta format to given output path.

## Dependencies - 
* Scala 2.12
* Spark 3.0.1
* Delta_core 2.1.1


## Setup
### How to build the jar - mvn clean install
1. Clone the repository.

   ```shell
   $ git clone git@github.com:debjyotiroy22/spark-delta-read-write
   $ cd spark-delta-read-write
   ```

2. Build the jar

   ```shell
   $ mvn clean install
   ```

 ### How to run the jar on local Spark cluster, outside of Databricks
 1. Go to the jar directory

   ```shell
   $ cd target
   ```
   There are 2 jars in the target directory, which we need to run the job - spark-dbr-cli-1.0-SNAPSHOT.jar and spark-dbr-cli-1.0-SNAPSHOT-jar-with-dependencies.jar. If you copy these jars to other path then mention the path as prefix in the next command.
   
2. Run this command to write delta files on local path or hdfs.
 
   ```shell
   spark-submit --packages io.delta:delta-core_2.12:2.1.1 --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension" --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog" --jars spark-dbr-cli-1.0-SNAPSHOT-jar-with-dependencies.jar --class com.databricks.DeltaWriteTest spark-dbr-cli-1.0-SNAPSHOT.jar <OUTPUT PATH> 
   ```
 If you are writing to hdfs then the path needs to have prefix "hdfs:/"
 
3. Run the command to read the data. It will print the dataframe on console.

  ```shell
   spark-submit --packages io.delta:delta-core_2.12:2.1.1 --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension" --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog" --jars spark-dbr-cli-1.0-SNAPSHOT-jar-with-dependencies.jar --class com.databricks.DeltaReadTest spark-dbr-cli-1.0-SNAPSHOT.jar <DELTA FILES PATH> 
   ```
 
 ### How to run the jar on Databricks(AWS) platform using DBCLI from laptop
 1. Upload the jar to Databricks file system. The depedency jar is not required as Databricks cluster already has those libraries.

   ```shell
   $ dbfs cp spark-dbr-cli-1.0-SNAPSHOT.jar dbfs:<USER DIRECTORY>
   ```
 
 2. Run this command to create the delta write job.
 
   ```shell
   export DATABRICKS_TOKEN=<TOKEN>
   
   curl -n \
-X POST --header "Authorization: Bearer $DATABRICKS_TOKEN" -H 'Content-Type: application/json' -d \
'{
     "name": "spark_write_delta_job_demo",
     "new_cluster": {
        "spark_version": "11.3.x-cpu-ml-scala2.12",
        "node_type_id": "i3en.xlarge",
        "aws_attributes": {"availability": "ON_DEMAND"},
        "num_workers": 2
       },
    "spark_submit_task": {
       "parameters": [ 
         "--class",
         "com.databricks.DeltaWriteTest",
         "dbfs:/<USER DIRECTORY>/spark-dbr-cli-1.0-SNAPSHOT.jar",
         "<OUTPUT PATH>"
         ]
       }
}' https://<databricks-instance>/api/2.0/jobs/create
   ```
 
 3. Once you run the last command, you will get a job id in return. Then run this command to run the job.

   ```shell
   curl -n \
-X POST --header "Authorization: Bearer $DATABRICKS_TOKEN" -H 'Content-Type: application/json' \
-d '{ "job_id": <JOB ID> }' https://<databricks-instance>/api/2.0/jobs/run-now   
   ```
   
4. Run this command to create the delta read job.  

```shell
   curl -n \
-X POST --header "Authorization: Bearer $DATABRICKS_TOKEN" -H 'Content-Type: application/json' -d \
'{
     "name": "spark_write_delta_job_demo",
     "new_cluster": {
        "spark_version": "11.3.x-cpu-ml-scala2.12",
        "node_type_id": "i3en.xlarge",
        "aws_attributes": {"availability": "ON_DEMAND"},
        "num_workers": 2
       },
    "spark_submit_task": {
       "parameters": [ 
         "--class",
         "com.databricks.DeltaReadTest",
         "dbfs:/<USER DIRECTORY>/spark-dbr-cli-1.0-SNAPSHOT.jar",
         "<OUTPUT PATH>"
         ]
       }
}' https://<databricks-instance>/api/2.0/jobs/create
   ```
   
5. Similary you will get job id in return. Use the same command as #3 to run the job.


 ### Install spark into laptop
 1. Install brew
   
   ```shell
   $ /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```
 2. Install Apache Spark
 
   ```shell
   $ brew install apache-spark
   ```

### Instakk DBCLI into laptop
Here is documentation [link](https://docs.databricks.com/dev-tools/cli/index.html#set-up-the-cli) to install DB CLI in laptop.


