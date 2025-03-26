# E6data-Hackathon
Duration: 6 hours 
Challenge: Build a custom query engine to process a dataset of millions of taxi trip records. The engine must support filtering, projection, aggregation, and group-by operations without relying on external data processing engines (e.g., DuckDB, Polars, Pandas, Spark, Hadoop, etc.) for reading the JSON file.

Important:

File Reader Requirement: The file reading code must be completely custom built. While you can use external libraries for processing the data after the file is read, you are not allowed to use them for parsing or reading the JSON files.

Dataset Download: You can download the dataset from the following link (make
sure to unzip it before use): taxi-trips-data.json.zip

The only restriction is that the JSON file reading and parsing code must be written from scratch

Query 1: Overall Record Count
Objective:
Count the total number of records in the dataset.
Query:
SELECT COUNT(*) AS total_trips
FROM dataset;

Query 2: Trip Distance Filter with Payment Type Grouping
Objective:
Filter trips with a Trip_distance greater than 5 miles. Group by Payment_type and compute:
Count of trips per payment type
Average fare amount per payment type
Total tip amount per payment type
Query:
SELECT
 Payment_type,
 COUNT(*) AS num_trips,
 AVG(Fare_amount) AS avg_fare,
 SUM(Tip_amount) AS total_tip
FROM dataset
WHERE Trip_distance > 5
GROUP BY Payment_type;

Query 3: Store-and-Forward Flag and Date Filter with Vendor Grouping
Objective:
Filter trips with:
Store_and_fwd_flag equal to 'Y' (store and forward trips)
Pickup date in January 2024 (between '2024-01-01' and '2024-01-31' inclusive)
Group by VendorID and compute:
Count of trips per vendor
Average passenger count per vendor
Query:
SELECT
 VendorID,
 COUNT(*) AS trips,
 AVG(Passenger_count) AS avg_passengers
FROM dataset
WHERE Store_and_fwd_flag = 'Y' AND tpep_pickup_datetime >= '2024-01-01'
 AND tpep_pickup_datetime < '2024-02-01'
GROUP BY VendorID;

Query 4: Daily Statistics for January 2024
Objective:
Extract the date from tpep_pickup_datetime and for January 2024:
Group trips by day
For each day, compute:
Total number of trips
Average number of passengers
Average trip distance
Average fare amount
Total tip amount
Query:
SELECT
 CAST(tpep_pickup_datetime AS DATE) AS trip_date,
 COUNT(*) AS total_trips,
 AVG(Passenger_count) AS avg_passengers,
 AVG(Trip_distance) AS avg_distance,
 AVG(Fare_amount) AS avg_fare,
 SUM(Tip_amount) AS total_tip
FROM dataset
WHERE tpep_pickup_datetime >= '2024-01-01'
 AND tpep_pickup_datetime < '2024-02-01'
GROUP BY CAST(tpep_pickup_datetime AS DATE)
ORDER BY trip_date ASC;

# Implementation

download the database - taxi-trips-data.json
Run the following commands in terminal to execute
javac JasonFileReader.java 
javac JasonFileReader ...\taxi-trips-data.json <query1 to query4>

Running the shell file
chmod +x ..../../run.sh
./run.sh




